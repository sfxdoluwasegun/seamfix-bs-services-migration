package com.sf.biocapture.ws.vtu;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.ws.WebServiceException;

import nw.commons.BCrypt;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.StringType;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.ds.TokenGenerator.OTPStatus;
import com.sf.biocapture.entity.OTPStatusRecord;
import com.sf.biocapture.entity.Setting;
import com.sf.biocapture.entity.enums.OtpStatusRecordTypeEnum;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.entity.vtu.BundleType;
import com.sf.biocapture.entity.vtu.VTUTransactionLogs;
import com.sf.biocapture.entity.vtu.enums.TransactionStatusEnum;
import com.sf.biocapture.entity.vtu.enums.TransactionTypeEnum;
import com.sf.biocapture.ilog.ILog;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.otp.OtpDS;
import com.sf.vas.airtimevend.mtn.dto.VendDto;
import com.sf.vas.airtimevend.mtn.dto.VendResponseDto;
import com.sf.vas.airtimevend.mtn.enums.MtnVtuVendStatusCode;
import com.sf.vas.airtimevend.mtn.soapartifacts.Vend;
import com.sf.vas.airtimevend.mtn.soapartifacts.VendResponse;

import org.hibernate.Criteria;

/**
 * VTU Data Service Class
 * @author Nnanna
 * @since 6 Nov 2017, 11:21:17
 */
@Stateless
public class VtuDS extends DataService {

	private static final String BUNDLES_CACHE_KEY = "BUNDLE-TYPES";

	@Inject
	BioCache cache;
	
	@Inject
	AccessDS accessDS;
	
	@Inject
    OtpDS otpDS;
	
	@Inject
	MTNVTUWrapperService mtnVTUService;
	
	@Inject
	JmsSender jmsSender;
	
	private static final String VTU_REQUEST_TYPE = "VTU_VENDING";
	
	private boolean isRequestValid(VTUVendingRequest req){
		if(StringUtils.isBlank(req.getVtuNumber())
				|| StringUtils.isBlank(req.getSubscriberNumber())
				|| req.getAmount() == 0.0
				|| StringUtils.isBlank(req.getClientTxnID())
				|| StringUtils.isBlank(req.getTxnType())
				|| StringUtils.isBlank(req.getAgentEmail())
				|| StringUtils.isBlank(req.getOtp())
				|| StringUtils.isBlank(req.getDeviceId())){
			return false;
		}
		return true;
	}
	
	public VTUResponse doVending(VTUVendingRequest req){
		//validate request
		if(!isRequestValid(req)){
			return new VTUResponse(ResponseCodeEnum.INVALID_INPUT);
		}
		
		//validate transaction type
		try{
			TransactionTypeEnum.valueOf(req.getTxnType().trim());
		}catch(IllegalArgumentException ex){
			logger.error("Invalid transaction type sent: {}", req.getTxnType());
			return new VTUResponse(ResponseCodeEnum.INVALID_INPUT, "Invalid transaction type");
		}
		
		//validate that bundle id exists for DATA vending
		if(TransactionTypeEnum.valueOf(req.getTxnType().trim()).equals(TransactionTypeEnum.DATA)){
			if(req.getBundleId() == null){
				return new VTUResponse(ResponseCodeEnum.INVALID_INPUT, "Bundle ID is missing from request");
			}
		}
		
		//validate device by kit tag or device ID
		if(!kitExists(req.getKitTag(), req.getDeviceId())){
			return new VTUResponse(ResponseCodeEnum.KIT_NOT_FOUND);
		}
		
		//get user
		KMUser user = getUser(req.getAgentEmail());
		if(user == null){
			return new VTUResponse(ResponseCodeEnum.USER_NOT_FOUND);
		}
		
		//validate vtu number
		if(!isVTUValid(req.getVtuNumber(), user.getPk())){
			return new VTUResponse(ResponseCodeEnum.INVALID_VTU_NUMBER);
		}
		
		//validate OTP
		OTPStatusRecord record = getOTPRecord(req.getVtuNumber(), req.getOtp());
		if(record == null){
			return new VTUResponse(ResponseCodeEnum.INVALID_INPUT, "No unused OTP exists for entered VTU number");
		}
		
		OTPStatus status = validateOTP(record, req.getVtuNumber(), req.getOtp());
		int code = otpDS.getOTPCode(status);
		VTUResponse resp = new VTUResponse();
		switch (code) {
		case 1:
			resp.setCode(ResponseCodeEnum.SUCCESS);
			resp.setDescription("The specified OTP is valid.");
			break;
		case 2:
			updateOTPRecord(record);
			resp.setCode(ResponseCodeEnum.FAILED_AUTHENTICATION);
			resp.setDescription("This otp has expired.");
			break;
		case 3:
			resp.setCode(ResponseCodeEnum.FAILED_AUTHENTICATION);
			resp.setDescription("No match was found for the specified OTP.");
			break;
		case 4:
			resp.setCode(ResponseCodeEnum.INVALID_INPUT);
			resp.setDescription("OTP is invalid.");
			break;
		}
		logger.debug("OTP status: {}", resp.getCode());
		if(!resp.getCode().equals(ResponseCodeEnum.SUCCESS.getCode())){
			return resp;
		}
		
		//get latest sequence
		long currentSequence = getLatestSequence();
		
		//do vending
		VendDto vendDto = new VendDto();
		vendDto.setAmount((int) req.getAmount());
		vendDto.setDestMsisdn(req.getSubscriberNumber());
		vendDto.setOrigMsisdn(req.getVtuNumber());
		vendDto.setSequence(currentSequence + 1);
		vendDto.setTariffTypeId(mtnVTUService.getTariffTypeId(TransactionTypeEnum.valueOf(req.getTxnType())));
		
		VendResponseDto vendResponseDto = null;
		try{
			vendResponseDto = mtnVTUService.sendVendRequest(vendDto);
		}catch(WebServiceException ex){
			logger.error("Error in connecting to VTU vending service:", ex);
			return new VTUResponse(ResponseCodeEnum.ERROR);
		}
		
		MtnVtuVendStatusCode vendStatusCode = vendResponseDto.getStatusCode();
		if(vendStatusCode == null){
			logger.debug("No vend status code from MTN: {}", req.getVtuNumber());
			return new VTUResponse(ResponseCodeEnum.ERROR, "Vending failed");
		}
		
		//queue logs for auditing
		String requestXml = marshalObject(Vend.class, vendDto.toVend());
		String responseXml = marshalObject(VendResponse.class,vendResponseDto.getVendResponse());
		logger.debug("VTU request xml: {}", requestXml);
		logger.debug("VTU response xml: {}", responseXml);
		ILog aLog = logActivity(requestXml, responseXml, vendStatusCode.getResponseCode(), vendStatusCode.getResponseDescription(), 
				req.getVtuNumber(), VTU_REQUEST_TYPE, req.getSubscriberNumber());
		jmsSender.queueIntegrationLog(aLog);
		
		//generate transaction logs
		currentSequence = vendResponseDto.getUsedSequence();
		VTUTransactionLogs log = getTransactionLog(req, currentSequence);
		log.setUser(user);
		log.setVendStatusCode(vendStatusCode.getResponseCode());
		if(vendStatusCode != null && MtnVtuVendStatusCode.SUCCESSFUL.equals(vendStatusCode)){
			log.setTxnStatus(TransactionStatusEnum.SUCCESSFUL);
			
			resp.setCode(ResponseCodeEnum.SUCCESS);
			resp.setDescription(ResponseCodeEnum.SUCCESS.getDescription());
		}else{
			log.setTxnStatus(TransactionStatusEnum.FAILED);
			resp.setCode(ResponseCodeEnum.UNSUCCESSFUL_TRANSACTION);
			resp.setDescription(ResponseCodeEnum.UNSUCCESSFUL_TRANSACTION.getDescription() + vendStatusCode.getResponseDescription());
		}
		if(vendResponseDto.getVendResponse() != null && vendResponseDto.getVendResponse().getTxRefId() != null){
			logger.debug("MTN Txn ID: {}", vendResponseDto.getVendResponse().getTxRefId());
			log.setTxnID(vendResponseDto.getVendResponse().getTxRefId());
		}else{
			logger.debug("Client Txn ID: {}", req.getClientTxnID());
			log.setTxnID(req.getClientTxnID());
		}
		try{
			getDbService().create(log);
		}catch(HibernateException ex){
			logger.error("Error in generating transaction logs: {}", ex);
			return new VTUResponse(ResponseCodeEnum.ERROR);
		}
		
		//update OTP
		updateOTPRecord(record);
		
		//update vending sequence in Settings table
		updateVendingSequence(currentSequence);
		
		return resp;
	}
	
	private void updateVendingSequence(long lastSequence){
		Setting setting = accessDS.getDbService().getByCriteria(Setting.class, 
				Restrictions.eq("name", SettingsEnum.VENDING_SEQUENCE.getName()));
		long nextSequence = lastSequence + 1;
		if(setting != null){
			setting.setValue(String.valueOf(nextSequence));
			getDbService().update(setting);
		}else{
			setting = new Setting();
			setting.setName(SettingsEnum.VENDING_SEQUENCE.getName());
			setting.setDescription(SettingsEnum.VENDING_SEQUENCE.getDescription());
			setting.setValue(String.valueOf(nextSequence));
			getDbService().create(setting);
		}
	}
	
	private VTUTransactionLogs getTransactionLog(VTUVendingRequest req, long sequence){
		VTUTransactionLogs log = new VTUTransactionLogs();
		log.setAmount(req.getAmount());
		if(req.getBundleId() != null){
			BundleType bundleType = getDbService().getByCriteria(BundleType.class, 
					Restrictions.idEq(Long.valueOf(req.getBundleId())));
			log.setBundleType(bundleType);
		}
		log.setClientTxnID(req.getClientTxnID());
		log.setDeviceId(req.getDeviceId());
		log.setKitTag(req.getKitTag());
		log.setSubscriberNumber(req.getSubscriberNumber());
		log.setTxnDate(new Date());
		log.setTxnType(TransactionTypeEnum.valueOf(req.getTxnType()));
		log.setVtuNumber(req.getVtuNumber());
		log.setSequence(sequence);
		return log;
	}
	
	private Long getLatestSequence(){
		return Long.parseLong(accessDS.getSettingValue(SettingsEnum.VENDING_SEQUENCE));
	}
	
	private OTPStatusRecord getOTPRecord(String vtuNumber, String otp){
		List<OTPStatusRecord> otps = otpDS.getOtpRecords(OtpStatusRecordTypeEnum.VTU_AGENT_VALIDATION, otp, vtuNumber.trim()); //otp could be null at this point
        if (otps != null && !otps.isEmpty()) {
        	//get the latest OTP record
            return otps.get(0);
        }
        
        return null;
	}
	
	private OTPStatus validateOTP(OTPStatusRecord record, String vtuNumber, String otp){
		OTPStatus otpStatus = OTPStatus.INVALID;
		
		//validate that otp has not expired
		if (System.currentTimeMillis() > record.getExpirationTime().getTime()) {
			//otp is expired
			otpStatus = OTPStatus.EXPIRED;
		} else {
			//validate the otp against the hashed otp
			if(!BCrypt.checkpw(otp.trim(), record.getOtp())){
				logger.debug("Invalid OTP entered for VTU number: {}", vtuNumber);
				return otpStatus;
			}else{
				otpStatus = OTPStatus.VALID;
			}
		}

		return otpStatus;
	}
	
	private void updateOTPRecord(OTPStatusRecord record){
		record.setOtpUsed(true);
        record.setTimeUsed(new Timestamp(new Date().getTime()));
        getDbService().update(record);
	}
	
	public VTUResponse validateVTUNumber(String vtuNumber, String agentEmail){
		logger.debug("==Validating VTU number==");
		
		//validate params
		if(StringUtils.isBlank(vtuNumber) || StringUtils.isBlank(agentEmail)){
			return new VTUResponse(ResponseCodeEnum.INVALID_INPUT);
		}
		
		//validate agent
		KMUser user = getUser(agentEmail);
		if(user == null){
			logger.debug("User not found in biosmart db: {}", agentEmail);
			return new VTUResponse(ResponseCodeEnum.USER_NOT_FOUND);
		}
		
		//validate vtu number
		if(!isVTUValid(vtuNumber, user.getPk())){
			return new VTUResponse(ResponseCodeEnum.INVALID_VTU_NUMBER);
		}
		
		//generate & send OTP to VTU number
		String otpValue, otpRecord, otpExpirationTime;
		try {
			Map<String, String> otpResult = accessDS.generateOTP(OtpStatusRecordTypeEnum.VTU_AGENT_VALIDATION, vtuNumber);
			otpRecord = otpResult.get("otpRecord");
			otpValue = otpResult.get("otpValue");
			otpExpirationTime = otpResult.get("otpExpirationTime");
			logger.debug("Generated OTP: {}, Expires: {} mins", otpValue, otpExpirationTime);
		} catch (ArrayIndexOutOfBoundsException ex) {
			logger.error("Exception in generating OTP for VTU validation:", ex);
			return new VTUResponse(ResponseCodeEnum.FAILED_OTP_GENERATION);
		}
		
		if(otpRecord != null){
			boolean sendOTP = accessDS.sendOTPMessage(otpValue, otpExpirationTime, vtuNumber);
			if (sendOTP) {
				return new VTUResponse(ResponseCodeEnum.SUCCESS, "Successfully generated OTP");
			}
		}
		
		return new VTUResponse(ResponseCodeEnum.FAILED_OTP_GENERATION, "OTP generation failed");
	}
	
	private boolean isVTUValid(String vtuNumber, long pk){
		Session session = null;
		try{
			session = dbService.getSessionService().getManagedSession();
			String query = "SELECT uv.pk FROM km_user_vtu uv, km_user u "
					+ "WHERE uv.active = 1 "
					+ "AND uv.vtu_number = :vtuNumber "
					+ "AND uv.user_fk = u.pk "
					+ "AND u.pk = :userPk";
			SQLQuery sqlq = session.createSQLQuery(query);
			sqlq.setMaxResults(1);
			sqlq.setParameter("vtuNumber", vtuNumber.trim());
			sqlq.setParameter("userPk", pk);
			return ((BigDecimal) sqlq.uniqueResult()) != null;
		} catch (HibernateException he) {
			logger.error("Unable to get user VTU status: ", he);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<BundleTypeDTO> loadBundleTypes(){
		List<BundleTypeDTO> bundles = (List<BundleTypeDTO>) cache.getItem(BUNDLES_CACHE_KEY);
		if(bundles == null){
			bundles = fetchBundleTypes();
			if(bundles != null && !bundles.isEmpty()){
				//cache bundle types
				cache.setItem(BUNDLES_CACHE_KEY, bundles, appProps.getInt("bundle-types-cache-interval", 21600));
			}
		}
		return bundles;
	}

	/**
	 * Fetches current list of bundle types
	 * available in the database
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BundleTypeDTO> fetchBundleTypes(){
		Session session = null;
		try{
			session = dbService.getSessionService().getManagedSession();
			String query = "SELECT bt.pk as bundleId, bt.bundle_name as name, bt.description as description, bt.amount as amount, vc.category_name as category "
					+ "FROM bundle_type bt, vtu_category vc WHERE bt.vtu_category_fk = vc.pk";
			
			SQLQuery sqlQuery = session.createSQLQuery(query);
			sqlQuery.addScalar("bundleId", StringType.INSTANCE);
			sqlQuery.addScalar("name", StringType.INSTANCE);
			sqlQuery.addScalar("description", StringType.INSTANCE);
			sqlQuery.addScalar("amount", DoubleType.INSTANCE);
			sqlQuery.addScalar("category", StringType.INSTANCE);
			
			return (List<BundleTypeDTO>) sqlQuery.setResultTransformer(Transformers.aliasToBean(BundleTypeDTO.class)).list();
		}catch(HibernateException e){
			logger.error("Unable to retrieve bundle types", e);
		}finally{
			dbService.getSessionService().closeSession(session);
		}
		
		return null;
	}
        
        /*
            returns transaction status for vtu vending
        */
        @SuppressWarnings("unchecked")
	public VTUResponse getVendingStatus(VTUVendingRequest request){
            
                if(StringUtils.isEmpty(request.getClientTxnID()) ||StringUtils.isEmpty(request.getDeviceId())||StringUtils.isEmpty(request.getKitTag()) ||
                        StringUtils.isEmpty(request.getVtuNumber())){
                    return new VTUResponse(ResponseCodeEnum.ERROR, "Invalid request");
                }
                
		Session session = null;
		try{
			session = dbService.getSessionService().getManagedSession();
                        Criteria criteria = session.createCriteria(VTUTransactionLogs.class);
                        criteria.add(Restrictions.eq("clientTxnID", request.getClientTxnID()));
                        criteria.add(Restrictions.eq("deviceId", request.getDeviceId()));
                        criteria.add(Restrictions.eq("kitTag", request.getKitTag()));
                        criteria.add(Restrictions.eq("vtuNumber", request.getVtuNumber()));
                        
			VTUTransactionLogs log = (VTUTransactionLogs) criteria.uniqueResult();
                        if(log == null){
                            return new VTUResponse(ResponseCodeEnum.RECORD_NOT_FOUND, "VTU Transaction record was not found");
                        }
                        
                        TransactionStatusEnum status = log.getTxnStatus();
                        if(TransactionStatusEnum.SUCCESSFUL.equals(status)){
                        	return new VTUResponse(ResponseCodeEnum.SUCCESS, "VTU Transaction Successful");
                        }
                        return new VTUResponse(ResponseCodeEnum.VTU_TRANSACTION_FAILED, "VTU Transaction not successful");
                        
		}catch(HibernateException e){
			logger.error("Unable to retrieve transaction status from vtu transaction log", e);
		}finally{
			dbService.getSessionService().closeSession(session);
		}
		
                return new VTUResponse(ResponseCodeEnum.ERROR, "Error retrieving transaction status");
	}
}
