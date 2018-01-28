package com.sf.biocapture.ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.jboss.resteasy.util.Base64;

import com.google.gson.Gson;
import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.app.SettingsCache;
import com.sf.biocapture.emailapi.EmailApiAuthenticator;
import com.sf.biocapture.entity.KitMarker;
import com.sf.biocapture.entity.KycBroadcast;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.Outlet;
import com.sf.biocapture.entity.Setting;
import com.sf.biocapture.entity.audit.AccessLog;
import com.sf.biocapture.entity.audit.VersionLog;
import com.sf.biocapture.entity.enums.KycPrivilege;
import com.sf.biocapture.entity.enums.LoginStatusEnum;
import com.sf.biocapture.entity.enums.OtpStatusRecordTypeEnum;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.enums.SimSwapModes;
import com.sf.biocapture.entity.enums.VersionType;
import com.sf.biocapture.entity.onboarding.AgentFingerprint;
import com.sf.biocapture.entity.onboarding.OnboardingStatus;
import com.sf.biocapture.entity.security.KMRole;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.entity.security.PasswordPolicy;
import com.sf.biocapture.util.map.Coordinate;
import com.sf.biocapture.ws.HeaderIdentifier;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import com.sf.biocapture.ws.access.AccessResponse;
import com.sf.biocapture.ws.access.AccessResponseData;
import com.sf.biocapture.ws.access.AgentOnboardingResponseData;
import com.sf.biocapture.ws.access.FetchPrivilegesResponse;
import com.sf.biocapture.ws.access.FingerLoginResponse;
import com.sf.biocapture.ws.access.ForgotPasswordResponse;
import com.sf.biocapture.ws.access.NodeData;
import com.sf.biocapture.ws.access.PasswordResetResponse;
import com.sf.biocapture.ws.access.SettingsResponse;
import com.sf.biocapture.ws.access.pojo.LoginCacheItem;
import com.sf.biocapture.ws.heartbeatstatus.GeoFenceDS;
import com.sf.biocapture.ws.onboarding.AgentFingerprintPojo;
import com.sf.biocapture.ws.otp.OtpDS;
import com.sf.biocapture.ws.tags.ClientRefRequest;
import com.sf.biocapture.ws.vtu.BundleTypeDTO;
import com.sf.biocapture.ws.vtu.VtuDS;
import java.util.Arrays;

import nw.orm.core.exception.NwormQueryException;
import nw.orm.core.query.QueryFetchMode;
import nw.orm.core.query.QueryModifier;
import nw.orm.core.query.QueryParameter;
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AccessDS extends DataService {

	private final String BLACKLIST_KEY = "XLIST-";
	private final String LOGIN_KEY = "LOGINTOKEN";
	private final String AUTHENTICATION_KEY = "#D8CK>HIGH<LOW>#";
	private final String DEFAULT_KM_SERVLET_URL = "http://kycmanager.mtnnigeria.net:8070/KycManagerMTN-portlet/PasswordUpdate";
	private static final String ACTIVE = "active";
	private static final String OTP_ERROR_MESSAGE = "An error occurred while generating OTP. Please try again later.";
	private static final String NODEDATA = "NODEDATA-";

	@Inject
	private BioCache cache;

	@Inject
	private SettingsCache settingsCache;

	@Inject
	private OtpDS otpDS;

	@Inject
	VtuDS vtuDS;

	@Inject 
	private KannelSMS kSms;

	@Inject
	GeoFenceDS geoFenceDS;

	public static void main(String[] args) {
		System.out.println(new Date().getTime());
	}


	public AccessResponse doPasswordReset(String email, String password){
		AccessResponse ar = new AccessResponse();

		//check if any fields are empty
		if(isEmpty(email) || isEmpty(password)){
			ar.setStatus(-1);
			ar.setMessage("Required fields are missing");
		}else{
			KMUser user = getUser(email);

			if(user != null){
                            //update KMUser                                        
                            user.setPassword(password);
                            user.setLastPasswordChange(new Timestamp(new Date().getTime()));
                            user.setClientFirstLogin(new Timestamp(new Date().getTime()));
                            dbService.update(user);

                            ar.setStatus(0);
                            ar.setMessage("Password reset successful");

                            logger.info("Password reset successful!!!!! for email: " + email);
			}else{
				logger.info("User with email " + email + " was not found!!!");
				ar.setStatus(-1);
				ar.setMessage("User not found");
			}
		}
		return ar;
	}

	private HttpResponse makeHttpRequest(String serverUrl, Long orbitaId, String password){
		HttpResponse response = null;
		try {
			HttpClient hc = new DefaultHttpClient();
			logger.info("***** the kyc manager server url is : " + serverUrl);
			HttpPost hp = new HttpPost( serverUrl );//URLEncoder.encode(serverUrl,"UTF-8"));
			hp.setHeader("sc-auth-key", AUTHENTICATION_KEY);

			List<NameValuePair> nvps = new ArrayList<>();
			nvps.add(new BasicNameValuePair("orbitaId", orbitaId.toString()));
			nvps.add(new BasicNameValuePair("password", password));

			hp.setEntity(new UrlEncodedFormEntity(nvps));

			response = hc.execute(hp);

			return response;
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception ", e);
		}catch (IOException e) {
			logger.error("Exception ", e);
		}
		return response;
	}

//	@SuppressWarnings("deprecation")
//	private PasswordResetResponse doKMPassordReset(Long orbitaId, String password){
//		String serverUrl = getSettingValue("KM_SERVLET_URL", DEFAULT_KM_SERVLET_URL);
//
//		//Execute HTTP Post Request
//		HttpResponse response = makeHttpRequest(serverUrl, orbitaId, password);
//		try {
//			if( response != null ){
//				int responseCode = response.getStatusLine().getStatusCode();
//				if(responseCode == 200){
//					String resp = convertStreamToString(response.getEntity().getContent());
//					return new Gson().fromJson(resp, PasswordResetResponse.class);
//				}else{
//					logger.debug("***** connecting to kyc manager returned a status code of : " + responseCode);
//				}                        
//			}else{
//				logger.debug("*** response from kyc manager is null...");
//			}
//
//		} catch (IOException | UnsupportedOperationException e) {
//			logger.error("An exception was thrown attempting to call km for password reset: ", e);
//		}
//
//		return null;
//	}

	private String convertStreamToString(InputStream is) {

		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String checkBlacklistStatus(String tag, String mac, String deviceId) {
		String bItem = cache.getItem(BLACKLIST_KEY + tag, String.class);
		String blacklisted = "N";
		if (bItem == null) {
			logger.debug("tag: " + tag + ", mac address: " + mac + ", deviceId: " + deviceId);
			if (tag == null && mac == null && deviceId == null) {
				return "Y";
			}
			Session session = null;
			try {
				session = dbService.getSessionService().getManagedSession();
				Criteria criteria = session.createCriteria(Node.class, "n");
				criteria.createAlias("n.enrollmentRef", "ref");
				Disjunction disjunction = Restrictions.disjunction();
				if (deviceId != null && !deviceId.isEmpty()) {
					disjunction.add(Restrictions.eq("ref.deviceId", deviceId.toUpperCase()));
				} else {
					if (mac != null && !mac.isEmpty()) {
						disjunction.add(Restrictions.eq("n.macAddress", mac).ignoreCase());
					}
					if (tag != null && !tag.isEmpty()) {
						disjunction.add(Restrictions.eq("ref.code", tag).ignoreCase());
					}
				}
				criteria.add(disjunction);
				criteria.setProjection(Projections.property("n.blacklisted"));
				criteria.setMaxResults(1);
				Object blisted = criteria.uniqueResult();
				blacklisted = blisted != null && (Boolean) blisted ? "Y" : "N";
			} catch (HibernateException he) {
				logger.error("", he);
			} finally {
				dbService.getSessionService().closeSession(session);
			}
			cache.setItem(BLACKLIST_KEY + tag, blacklisted, 60 * 60);
		} else {
			blacklisted = bItem;
		}
		return blacklisted;
	}       

	public String getAgentStatus(String email) {
		String deactivated = "N";
		if (email != null) {
			Boolean active = isActiveUser(email);
			if (active != null) {
				if (active) {
					deactivated = "N";
				} else {
					deactivated = "Y";
				}
			}
		}
		return deactivated;
	}

	private List<String> getUserRoles(Set<KMRole> roles){
		List<String> userRoles = new ArrayList<String>();
		if(roles != null && !roles.isEmpty()){
			for(KMRole kmr : roles){
				userRoles.add(kmr.getRole());
			}
		}
		return userRoles;
	}

	private PasswordPolicy getPasswordPolicy(){
		//check cache
		String ppKey = "PWD-POLICY";
		PasswordPolicy pp = cache.getItem(ppKey, PasswordPolicy.class);
		if(pp == null){
			//check db
			pp = dbService.getByCriteria(PasswordPolicy.class, Restrictions.eq("policyActive", true));
			if(pp != null){
				cache.setItem(ppKey, pp, 86400);
				logger.debug("==Active password policy found and cached!!!");
			}
		}else{
			logger.debug("==Active password policy retrieved from cache!!!");
		}
		return pp;
	}

	@SuppressWarnings("PMD") 
	public AccessResponse performLogin(KMUser user, String password, String tag, HeaderIdentifier headerIdentifier){
		logger.debug("User wants to login. Tag: " + tag);
		AccessResponse ar = new AccessResponse();
		AccessLog loginLog = new AccessLog();
		try {
			loginLog.setKitTag(tag);
			if (headerIdentifier != null) {
				loginLog.setMacAddress(headerIdentifier.getMac());
				loginLog.setRefDeviceId(headerIdentifier.getRefDeviceId());
				loginLog.setRealtimeDeviceId(headerIdentifier.getRealTimeDeviceId());
			}
			if (user == null) {
				ar.setStatus(-1);
				ar.setMessage("Invalid username or password entered.");
				loginLog.setLoginStatusEnum(LoginStatusEnum.FAILED_AUTH);
				String msg = jmsSender.queueLoginLog(loginLog);
				logger.info(msg);
				return ar;
			}
			loginLog.setUsername(user.getEmailAddress());

			PasswordPolicy pp = getPasswordPolicy();
			boolean isPasswordValid = user.isPasswordValid(password);

			// The line below sets the appropriate response message to the passed AccessResponse object.
			// If no message is returned, we consider this to have passed password policy, else, we return
			// For password expiration cases, we set the password syntax regex and guide and return it to client.

			checkCachedPasswordPolicy(user, pp, isPasswordValid, ar, loginLog); //checkPasswordPolicy(user, isPasswordValid, pp, ar);
			if (ar.getMessage() != null && !ar.getMessage().isEmpty()) {
				//login failed policy check
				return ar;
			}

			if (!isPasswordValid) {
				// The line below updates the failed logins status of the user.
				ar.setStatus(-1);
				ar.setMessage("Invalid username or password entered.");
				loginLog.setLoginStatusEnum(LoginStatusEnum.FAILED_AUTH);
				String msg = jmsSender.queueLoginLog(loginLog);
				logger.info(msg);
				return ar;
			}

			ar.setName(getUsername(user));

			//check if role-based login is enabled
			String roleBasedEnabled = appProps.getProperty("enable-role-based", "1"); //0 or 1
			if (roleBasedEnabled != null && roleBasedEnabled.equalsIgnoreCase("1")) {
				if (tag == null || (tag != null && tag.trim().equalsIgnoreCase("TAG_NOT_SET"))) {
					//it means the kit has not been tagged
					//so check if user has support role
					//				if(!user.hasRole(KycManagerRole.SUPPORT.name())){
					if (user.getPrivileges() != null && !user.hasPrivilege(KycPrivilege.TAGGING)) {
						logger.debug("User, " + user.getEmailAddress() + ", does not have TAGGING privilege");
						ar.setStatus(-1);
						ar.setMessage("Please contact support to tag this device");
						loginLog.setLoginStatusEnum(LoginStatusEnum.NO_TAG_PRIVILEGE);
						String msg = jmsSender.queueLoginLog(loginLog);
						logger.info(msg);
						return ar;
					}
				} else {
					//check if kit is blacklisted
					String bs = checkBlacklistStatus(tag, null,null);
					if (bs != null && bs.equalsIgnoreCase("Y")) {
						//kit is blacklisted
						ar.setStatus(-3);
						ar.setMessage("This kit is blacklisted. Please contact support");
						loginLog.setLoginStatusEnum(LoginStatusEnum.BLACKLISTED_KIT);
						String msg = jmsSender.queueLoginLog(loginLog);
						logger.info(msg);
						return ar;
					}
				}

				//check if agent is deactivated
				if (!user.isActive()) {
					ar.setStatus(-4);
					ar.setMessage("Your account was deactivated. Please contact support");
					loginLog.setLoginStatusEnum(LoginStatusEnum.IN_ACTIVE_ACCOUNT);
					String msg = jmsSender.queueLoginLog(loginLog);
					logger.info(msg);
					return ar;
				}

				ar.setRoles(getUserRoles(user.getRoles())); //retain this for legacy machines

				//if user has at least one privilege, then he can login
				if (user.getPrivileges() != null && !user.getPrivileges().isEmpty()) {
					ar = getSuccessResponseObj(ar, pp, null);
				} else {
					ar.setStatus(-1);
					ar.setMessage("User does not have the privileges required for login");
					loginLog.setLoginStatusEnum(LoginStatusEnum.NO_LOGIN_PRIVILEGE);
					String msg = jmsSender.queueLoginLog(loginLog);
					logger.info(msg);
					return ar;
				}
				if (user.getClientFirstLogin() != null) {
					ar.setFirstLogin(false);
				} else {
					ar = getSuccessResponseObj(ar, pp, true);
				}
				ar.setStatus(0);
				ar.setMessage("Login successful");
				updateSuccessfulLogin(user, loginLog);
			} else {
				updateSuccessfulLogin(user, loginLog);
				ar.setStatus(0);
				ar.setMessage("Login successful");
			}

		} catch (Exception e) {
			logger.error("Error while performing login", e);
		}
		return ar;
	}

	private AccessResponse updateUserPasswordPolicy(KMUser user, AccessResponse ar){
		user.setFailedLoginAttempts( user.getFailedLoginAttempts() + 1 );
		user.setLastFailedLogin( new Timestamp( new Date().getTime() ) );  
		getDbService().update(user);   

		ar.setStatus(-1);
		ar.setMessage("Invalid username or password entered.");

		return ar;
	}

	private AccessResponse getSuccessResponseObj(AccessResponse ar, PasswordPolicy pp, Boolean firstLogin){
		ar.setStatus(0);
		ar.setMessage("Login successful");
		if(pp != null){
			ar.setPasswordSyntaxRegex( pp.getRegex() );
			ar.setPasswordSyntaxGuide( pp.getUserMessage() );
		}
		if(firstLogin != null){
			ar.setFirstLogin(firstLogin);
		}
		return ar;
	}

	@Inject
	private JmsSender jmsSender;

	private void updateSuccessfulLogin(KMUser user, AccessLog loginLog){
		user.setLastSuccessfulLogin( new Timestamp( new Date().getTime() ) );
		logger.debug("==About to update user...PK = " + user.getPk());
		loginLog.setLoginStatusEnum(LoginStatusEnum.SUCCESS);
		String msg = jmsSender.queueLoginLog(loginLog);
		logger.info(msg);
	}

	/**
	 * Determines how many days records will stay on 
	 * the client before biosmart triggers a deletion
	 * @return
	 */
	private int getClientRecordsLifespan(){
		int duration = 0;
		String ls = cache.getItem("CLIENT-RECORDS-LIFESPAN", String.class);
		if(ls != null && !ls.isEmpty()){
			duration = Integer.valueOf(ls.trim());
		}else{
			//check db settings
			Setting setting = getDbService().getByCriteria(Setting.class, Restrictions.eq("name", "CLIENT-RECORDS-LIFESPAN").ignoreCase());
			if(setting != null && setting.getValue() != null){
				duration = Integer.valueOf(setting.getValue().trim());
				cache.setItem("CLIENT-RECORDS-LIFESPAN", duration + "", 60 * 60); //1hr
			}
		}

		logger.debug("Client records lifespan: " + duration);

		return duration;
	}

	@SuppressWarnings("unchecked")
	private SettingsResponse getGlobalSettings(){
		String defaultFalseValue = "false";
		SettingsResponse sr = new SettingsResponse();

		sr = getClientFieldSettings(sr);
		sr = getSimSwapSettings(sr);

		String r2 = "(abcde|bcdef|cdefg|defgh|efghi|fghij|ghijk|hijkl|ijklm|jklmn|klmno|lmnop|mnopq|nopqr|opqrs|pqrst|qrstu|rstuv|stuvw|tuvwx|uvwxy|vwxyz)";
		String heartBeatRate = getSettingValue("SC-HEARTBEAT-RATE","10");
		String maxMsisdn = getSettingValue("SC-MAX-MSISDN","5");
		String maxChildMsisdn = getSettingValue("MAX-CHILD-MSISDN","5");
		String regexOne = getSettingValue("REGEX-ONE","(\\\\w)\\\\1{3,}");
		String regexTwo = getSettingValue("REGEX-TWO",r2);
		String spoofData = getSettingValue("SC-SPOOF-DATA","1");
		String clientlockoutPeriod = getSettingValue("CLIENT-LOCKOUT-PERIOD", "30"); //in mins
		String clientAuditSyncInterval = getSettingValue("CLIENT-AUDIT-SYNC-INTERVAL", "30"); //in mins


		sr.setHeartbeatRate( (heartBeatRate == null) ? 10L : Long.valueOf(heartBeatRate) );//     appProps.getLong("sc-heartbeat-rate", 10L));
		sr.setMaxMsisdn( (maxMsisdn == null) ? 5 : Integer.valueOf(maxMsisdn) ); // appProps.getInt("sc-max-msisdn", 5));
		sr.setRegexOne( ((regexOne == null) || regexOne.isEmpty()) ? "(\\\\w)\\\\1{3,}" : regexOne);  //appProps.getProperty("regex-one", "(\\w)\\1{3,}"));
		sr.setRegexTwo( ((regexTwo == null) || regexTwo.isEmpty()) ? r2 : regexTwo ); //appProps.getProperty("regex-two", r2));
		sr.setSpoofData( ( (spoofData == null) ? 1 : Integer.valueOf(spoofData) ) == 1 );  //appProps.getInt("sc-spoof-data", 1) == 1);
		sr.setMaxChildMsisdn( (maxChildMsisdn == null) ? 5 : Integer.valueOf(maxChildMsisdn) ); // appProps.getInt("max-child-msisdn", 5));

		String defaultLocalIdTypes = "International Passport,Drivers License,National ID Card,Other";
		String defaultForeignIdTypes = "Residence Permit,Travel Document,National ID,National Drivers License";
		sr.setLocalIdTypes(getIdTypes("LOCAL_ID_TYPES", defaultLocalIdTypes));
		sr.setForeignIdTypes(getIdTypes("FOREIGN_ID_TYPES", defaultForeignIdTypes));
		sr.setClientRecordsLifespan(getClientRecordsLifespan());
		sr.setClientlockoutPeriod(Integer.valueOf(clientlockoutPeriod));
		sr.setClientAuditSyncInterval(Integer.valueOf(clientAuditSyncInterval));

		String signRegistration = getSettingValue("SIGN-REGISTRATION", defaultFalseValue);
		sr.setSignRegistration(Boolean.valueOf(signRegistration));
		String loginMode = getSettingValue("CLIENT-LOGIN-MODE", "FINGERPRINT, USERNAME");
		String loginModes [] = loginMode.split(",");
		if (loginModes != null) {
			List<String> modes = new ArrayList<>();
			for (String mode : loginModes) {
				modes.add(mode.trim());
			}
			sr.setLoginMode(modes);
		}
		String otpRequired = getSettingValue(SettingsEnum.OTP_REQUIRED);
		sr.setOtpRequired(Boolean.valueOf(otpRequired));

		String offlineMode = getSettingValue(SettingsEnum.LOGIN_OFFLINE);
		sr.setLoginOffline(Boolean.valueOf(offlineMode));

		String offlineValidationType = getSettingValue(SettingsEnum.LOGIN_OFFLINE_VALIDATION_TYPE);
		sr.setOfflineValidationType(offlineValidationType);

		String airtimeSalesMandatory = getSettingValue(SettingsEnum.AIRTIME_SALES_MANDATORY);
		sr.setAirtimeSalesMandatory(Boolean.valueOf(airtimeSalesMandatory));                

		String airtimeSalesURL = getSettingValue(SettingsEnum.AIRTIME_SALES_URL);
		sr.setAirtimeSalesURL(airtimeSalesURL);

		String minAcceptableCharacter = getSettingValue(SettingsEnum.MINIMUM_ACCEPTABLE_CHARACTER);
		sr.setMinimumAcceptableCharacter(Integer.valueOf(minAcceptableCharacter));

		String airtimeSalesEnabled = getSettingValue(SettingsEnum.ENABLE_VAS_MODULE);
		sr.setEnableVasModule(Boolean.valueOf(airtimeSalesEnabled));

		String availableUseCase = getSettingValue(SettingsEnum.AVAILABLE_USE_CASE);
		String availableUseCases[] = availableUseCase.split(",");
		if (availableUseCases != null) {
			List<String> useCases = new ArrayList<>();
			for (String uCase : availableUseCases) {
				useCases.add(uCase);
			}
			sr.setAvailableUseCases(useCases);
		}

		sr.setClientActivityLogBatchSize(parseSettingInteger(SettingsEnum.CLIENT_ACTIVITY_LOG_BATCH_SIZE));
		sr.setMaximumMsisdnAllowedPerRegistration(parseSettingInteger(SettingsEnum.MAXIMUM_MSISDN_ALLOWED_PER_REGISTRATION));

		//CLIENT SERVICE INTERVALS
		sr.setNotificationsChecker(parseSettingInteger(SettingsEnum.NOTIFICATION_CHECKER_INTERVAL));
		sr.setAgentBioSynchronizer(parseSettingInteger(SettingsEnum.AGENT_BIOSYNC_INTERVAL));
		sr.setAuditXmlSynchronizer(parseSettingInteger(SettingsEnum.AUDIT_XML_SYNC_INTERVAL));
		sr.setThresholdUpdater(parseSettingInteger(SettingsEnum.THRESHOLD_CHECKER_INTERVAL));
		sr.setActivationChecker(parseSettingInteger(SettingsEnum.ACTIVATION_CHECKER_INTERVAL));
		sr.setSynchronizer(parseSettingInteger(SettingsEnum.SYNCHRONIZER_INTERVAL));
		sr.setHarmonizer(parseSettingInteger(SettingsEnum.HARMONIZER_INTERVAL));
		sr.setSettingsService(parseSettingInteger(SettingsEnum.SETTINGS_INTERVAL));
		sr.setBlackLister(parseSettingInteger(SettingsEnum.BLACKLIST_CHECKER_INTERVAL));
		sr.setClientLocationCheckPopupInterval(parseSettingInteger(SettingsEnum.CLIENT_LOCATION_CHECK_POPUP_INTERVAL));
		sr.setUpdateDeviceIdInterval(parseSettingInteger(SettingsEnum.UPDATE_DEVICE_ID_INTERVAL));
		sr.setRequestShortcode(Integer.valueOf(getSettingValue(SettingsEnum.OFFLINE_REQUEST_SHORTCODE)));
		sr.setResponseShortcode(Integer.valueOf(getSettingValue(SettingsEnum.OFFLINE_RESPONSE_SHORTCODE)));

		//CHANGE PASSWORD CLIENT
		PasswordPolicy pp = getPasswordPolicy();
		if(pp != null){
			sr.setPasswordPolicy(pp.getUserMessage());
			sr.setPasswordRegex(pp.getRegex());
		}

		// CLIENT DYA ACCOUNT USE CASES
		String dyaAvaliableUseCase = getSettingValue(SettingsEnum.DYA_AVAILABLE_USE_CASE);
		String dyaAvailableUseCases[] = dyaAvaliableUseCase.split(",");
		if (dyaAvailableUseCases != null) {
			List<String> dyaUseCases = new ArrayList<>();
			for (String uCase : dyaAvailableUseCases) {
				dyaUseCases.add(uCase);
			}
			sr.setDiamondYellowAcctUseCase(dyaUseCases);
		}

		// CLIENT DYA ACCOUNT TYPE
		sr.setDyaAccountType(getSettingValue(SettingsEnum.DYA_ACCOUNT_TYPE));

		sr.setLocationMandatory(Boolean.valueOf(getSettingValue(SettingsEnum.LOCATION_MANDATORY)));

		//VTU
		String vtuVendingMandatory = getSettingValue(SettingsEnum.VTU_VENDING_MANDATORY);
		sr.setVtuVendingMandatory(Boolean.valueOf(vtuVendingMandatory));

		String vtuAvaliableTransactionType = getSettingValue(SettingsEnum.AVAILABLE_VTU_TRANSACTION_TYPE);
		String vtuAvaliableTransactionTypes[] = vtuAvaliableTransactionType.split(",");
		if (vtuAvaliableTransactionTypes != null) {
			List<String> txnTypes = new ArrayList<>();
                        txnTypes.addAll(Arrays.asList(vtuAvaliableTransactionTypes));
			sr.setTransactionTypes(txnTypes);
		}

		List<BundleTypeDTO> bundleTypes = vtuDS.loadBundleTypes();
		if(bundleTypes != null && !bundleTypes.isEmpty()){
			logger.debug("Number of bundle types found: {}", bundleTypes.size());
			sr.setBundleTypes(bundleTypes);
		}

		return sr;
	}

	private int parseSettingInteger(SettingsEnum setting){
		int finalVal = 0;
		try{
			finalVal = Integer.valueOf(getSettingValue(setting));
		}catch(NumberFormatException e){
			logger.error("NumberFormatException on retrieving setting: " + setting.getName());
			finalVal = Integer.valueOf(setting.getValue()); //use default instead
		}
		return finalVal;
	}

	public SettingsResponse getSimSwapSettings(SettingsResponse sr){
		//sim swap settings
		String modeOfValidation = getSettingValue("SIM_SWAP_VALIDATION_MODE", "FINGERPRINT_AND_QUESTIONNAIRE");
		String allowableFpFailures = getSettingValue("SIM_SWAP_ALLOWABLE_FP_FAILURES", "3");
		String noOfCheckedMsisdns = getSettingValue("SIM_SWAP_NO_CHECKED_MSISDNS", "3");//for frequently dialed numbers
		String qValidation = getSettingValue("SS_QUESTIONNAIRE_VALIDATION", "FIRST_NAME:MANDATORY "
				+ "LAST_NAME:MANDATORY DATE_OF_BIRTH:MANDATORY LGA_OF_ORIGIN:MANDATORY LRC_AMOUNT:MANDATORY "
				+ "ACCOUNT_ID:MANDATORY INVOICE_AMOUNT:MANDATORY");//for validating questionnaire

		//		logger.debug("Mapping for questionnaire validation: " + qValidation);

		//validate mode of validation
		SimSwapModes mode = SimSwapModes.valueOf(modeOfValidation);
		if(mode == null){
			logger.error("Invalid setting config for sim swap mode of validation: " + modeOfValidation);
			sr.setModeOfValidation("FINGERPRINT_AND_QUESTIONNAIRE"); //use default
		}else{
			sr.setModeOfValidation(modeOfValidation);
		}

		//validate no. of allowable fp failures
		try{
			Integer.valueOf(allowableFpFailures);
			sr.setAllowableFpFailures(allowableFpFailures);
		}catch(NumberFormatException ex){
			logger.error("Invalid setting config for sim swap number of allowable failed fingerprint validations: " + allowableFpFailures);
			sr.setAllowableFpFailures("3");
		}

		//validate no. of checked msisdns
		try{
			sr.setMatchedMsisdns(Integer.valueOf(noOfCheckedMsisdns));
		}catch(NumberFormatException ex){
			logger.error("Invalid setting config for number of checked msisdns for frequently-dialed numbers: " + noOfCheckedMsisdns);
			sr.setMatchedMsisdns(3);
		}

		//construct mapping for validating questionnaire
		HashMap<String, String> questionnaireValidation = null;
		if(qValidation != null){
			questionnaireValidation = new HashMap<String, String>();
			for(String keyVal : qValidation.split(" ")){
				try{
					String[] configSplit = keyVal.split(":");
					//validate the key
					questionnaireValidation.put(configSplit[0], configSplit[1]);
				}catch(ArrayIndexOutOfBoundsException ex){
					logger.error("Error in constructing questionnaire validation config: ", ex);
				}
			}
		}

		sr.setQuestionnaireValidation(questionnaireValidation);

		return sr;
	}

	/**
	 * Retrieves config which determines if a 
	 * client field is optional or mandatory
	 * @param sr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SettingsResponse getClientFieldSettings(SettingsResponse sr){
		HashMap<String, String> fieldValidation = null;
		String fvKey = "FIELD_VALIDATION_SETTINGS";

		//check if field validation map exists in cache
		Object val = settingsCache.getItem(fvKey, Object.class);
		if(val != null){
			fieldValidation = (HashMap<String, String>) val;
		}else{
			String fValidation = getSettingValue("CLIENT_FIELD_SETTINGS", "KYC_FORM:MANDATORY MODE_OF_IDENTIFICATION:OPTIONAL");//to determine if fields are optional or mandatory
			if(fValidation != null && !fValidation.isEmpty()){
				fieldValidation = new HashMap<String, String>();
				for(String keyVal : fValidation.split(" ")){
					try{
						String[] configSplit = keyVal.split(":");
						fieldValidation.put(configSplit[0], configSplit[1]);
					}catch(ArrayIndexOutOfBoundsException ex){
						logger.error("Error in constructing field validation config: ", ex);
					}
				}
				//add field validation map to cache
				settingsCache.setItem(fvKey, fieldValidation, 60 * 60); //1 hr
			}
		}

		sr.setClientFieldSettings(fieldValidation);

		return sr;
	}

	/**
	 * Retrieves the modes of identification to be sent
	 * to the client
	 * @param settingName, local or foreign settings
	 * @return
	 */
	private String getIdTypes(String settingName, String defaultValue){
		return getSettingValue(settingName,defaultValue);
	}

	public String getSettingValue(String settingName, String defaultValue){
		String val = settingsCache.getItem(settingName, String.class);
		if (val == null) {
			//check db settings
			QueryParameter queryParameter = new QueryParameter();
			queryParameter.setName("name");
			queryParameter.setValue(settingName);
			String query = "select s.value from setting s where s.name = :name ";
			List<String> values = getDbService().getBySQL(String.class, query, null, queryParameter);
			String settingValue = values != null && values.size() > 0 ? values.get(0) : null;
			if (settingValue != null) {
				val = settingValue.trim();
			} else {
				val = defaultValue;
				Setting setting = new Setting();
				setting.setName(settingName);
				setting.setValue(defaultValue);
				setting.setDescription(settingName);

				getDbService().create(setting);
			}
			settingsCache.setItem(settingName, val, 60 * 60); //1 hr
		}

		return val;
	}

	public String getSettingValue(SettingsEnum settingsEnum){
		String val = settingsCache.getItem(settingsEnum.getName(), String.class);
		if(val == null){
			//check db settings
			try {
				QueryParameter queryParameter = new QueryParameter();
				queryParameter.setName("name");
				queryParameter.setValue(settingsEnum.getName());
				String query = "select s.value from setting s where s.name = :name ";
				List<String> values = getDbService().getBySQL(String.class, query, null, queryParameter);
				String settingValue = values != null && values.size() > 0 ? values.get(0) : null;
				if (settingValue != null) {
					val = settingValue.trim();
				} else {
					val = settingsEnum.getValue();
					Setting setting = new Setting();
					setting.setName(settingsEnum.getName());
					setting.setValue(settingsEnum.getValue());
					setting.setDescription(settingsEnum.getDescription());

					getDbService().create(setting);
				}
			} catch (NwormQueryException e) {
				logger.error("", e);
			}
			settingsCache.setItem(settingsEnum.getName(), val, 60 * 60); //1 hr
		}

		return val;
	}

	/**
	 *
	 * @param deviceId
	 * @param ref
	 * @param type
	 * @param req
	 * @return
	 */
	public SettingsResponse getSettings(String deviceId,String ref, String type, ClientRefRequest req){

		SettingsResponse sr = getGlobalSettings();

		String key  = deviceId;
		if(StringUtils.isNotBlank(deviceId)){
			key = deviceId;
		}else{
			key = req.getMac();
		}
		NodeData nd = cache.getItem(NODEDATA + key, NodeData.class); 

		if(nd == null){
			// Retrieve node data from database
			nd = new NodeData();
			Map<String, Object> nodeData = getNodeData(req.getMac(),deviceId);
			if(nodeData.get("BROADCAST") != null){
				KycBroadcast kb = (KycBroadcast) nodeData.get("BROADCAST");
				nd.setServerMessage(kb.getMessage());
				sr.setServerMessage(kb.getMessage());
			}

			if(nodeData.get("NODE") != null){
				Node node = (Node) nodeData.get("NODE");

				if(node.getCorperateKit() != Boolean.TRUE){
					node.setCorperateKit(false); // just to ensure the value is false at the db layer
				}

				if(node.getLastInstalledUpdate() == null || (node.getLastInstalledUpdate() != null && !req.getPatchVersion().equals(node.getLastInstalledUpdate()))){
					node.setLastInstalledUpdate(req.getPatchVersion());
					node.setLastUpdated(new Date(req.getInstallDate().getTime()));

					node.setMachineManufacturer(req.getMachineManufacturer());
					node.setMachineModel(req.getMachineModel());
					node.setMachineOS(req.getMachineOS());
					node.setMachineSerialNumber(req.getMachineSerial());
					boolean success = dbService.update(node);
					logger.debug("Node update successful? " + success);

					VersionLog versionLog = new VersionLog();
					versionLog.setType(VersionType.KIT_VERSION);
					versionLog.setVersion(String.valueOf(req.getPatchVersion()));
					versionLog.setNode(node);
					dbService.create(versionLog);
				}

				nd.setCorporateKit(node.getCorperateKit());
				nd.setMacId(node.getMacAddress());
				Float version = node.getLastInstalledUpdate();
				nd.setPatchVersion(version);
				sr.setCorporateKit(nd.isCorporateKit());
                             
				confirmUpdateAvailable(sr,type,version,req , deviceId);

				// cache node data
				cache.setItem(NODEDATA + key, nd, 30 * 60);				
			}
		}else{
			sr.setServerMessage(nd.getServerMessage());
			sr.setCorporateKit(nd.isCorporateKit());

			confirmUpdateAvailable(sr,type,nd.getPatchVersion(),req , deviceId);
		}

		sr.setCode(ResponseCodeEnum.SUCCESS);
		sr.setDescription("Success");

		return sr;
	}
        
	private void confirmUpdateAvailable(SettingsResponse sr,String type,Float patchVersion,ClientRefRequest req , String deviceId){
		Float cv = 0f;
		boolean useList = false;
		try{
			cv = appProps.getFloat("SC_"+ type, 1.23f); // type DROID, WIN, LITE
			useList = appProps.getBool("use-update-kit-list", false);
		}catch(NumberFormatException ex){
			logger.error("", ex);
		}

		//		logger.debug("current version: " + cv);
		//		logger.debug("kit version: " + patchVersion );

		if( useList ){
                        Disjunction disjunction = Restrictions.disjunction();
                        disjunction.add(Restrictions.eq("macAddress", req.getMac().trim()).ignoreCase());
                        if(StringUtils.isNotBlank(deviceId)){
                            disjunction.add(Restrictions.eq("deviceId", deviceId.trim()).ignoreCase());
                        }
			KitMarker kMarker = getDbService().getByCriteria(KitMarker.class, disjunction);
			if( (kMarker == null) ){
				sr.setUpdateAvailable(false);
				return;
			}

			setUpdateAvailable(sr,type,patchVersion,cv);

		}else{
			setUpdateAvailable(sr,type,patchVersion,cv);
		}
	}

	private void setUpdateAvailable(SettingsResponse sr,String type,Float patchVersion,float cv){
		if( (patchVersion != null) && (cv > patchVersion) ){
			sr.setUpdateAvailable(true);
			sr.setUpdateVersion(appProps.getFloat("SC_"+ type, 1.23f)); // type DROID, WIN, LITE
		}else{
			sr.setUpdateAvailable(false);
		}
	}


	private Map<String, Object> getNodeData(String macId, String deviceId){

		Map<String, Object> nodeData = new HashMap<String, Object>();

		Session sxn = null;

		try{
			sxn = dbService.getSessionService().getManagedSession();
			Criteria nc = sxn.createCriteria(Node.class, "nd");
			nc.createAlias("nd.enrollmentRef", "ref");
			nc.setFetchMode("ref", FetchMode.EAGER);

			if(!StringUtils.isEmpty(deviceId)){      
				nc.add(Restrictions.eq("ref.deviceId", deviceId));
			}
			else{
				nc.add(Restrictions.eq("nd.macAddress", macId));
			}
			Object node = nc.uniqueResult();

			nodeData.put("NODE", node);
			nodeData.put("BROADCAST", null);

		}
		catch(NwormQueryException e){
			logger.error("", e);
		} finally {		
			dbService.getSessionService().closeSession(sxn);
		}
		//		KycBroadcast brd = cache.getItem("GLOBALBROADCAST", KycBroadcast.class);
		//		if(brd == null){
		//			Criteria bc = sxn.createCriteria(KycBroadcast.class);
		//			bc.add(Restrictions.eq("global", true));
		//			bc.add(Restrictions.eq(ACTIVE, true));
		//			Object broadcast = bc.uniqueResult();
		//			if(broadcast != null){
		//				nodeData.put("BROADCAST", broadcast);
		//				cache.setItem("GLOBALBROADCAST", broadcast, 48 * 60 * 60);
		//			}
		//
		return nodeData;
	}

	public void handleLicenseRequest(ClientRefRequest crr){

	}

	public AccessResponse authenticate(String auth) {
		AccessResponse ar = new AccessResponse();
		try {
			byte[] decodedParams = Base64.decode(auth);
			String params = new String(decodedParams);
			String uname = params.split(",")[0];
			String pw = params.split(",")[1];

			KMUser user = getUser(uname);
			if(user != null && user.isPasswordValid(pw) && user.isActive()){
				ar.setStatus(0);
				ar.setMessage(user.getPk() + "");
			}else{
				ar.setStatus(-1);
				ar.setMessage("Unknown credentials specified");
			}

		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			ar.setMessage("Unknown credentials specified");
			logger.error("Exception ", e);
		}
		return ar;
	}


	private void cacheFailedLoginAttempt(KMUser kUser, String cachedPolicyResult) {
		LoginCacheItem cacheItem = null;
		if (cachedPolicyResult == null) {
			//user no longer in cache or was never there because previous login was successful.
			cacheItem = new LoginCacheItem();
		} else {
			cacheItem = new LoginCacheItem(cachedPolicyResult);
		}
		cacheItem.setLoginDate(new Date());
		cacheItem.setAttempts(cacheItem.getAttempts() + 1);
		cacheItem.setUsername(kUser.getEmailAddress());
		cache.setItem(LOGIN_KEY + kUser.getEmailAddress().trim(), LoginCacheItem.to(cacheItem), appProps.getInt("login-session-timeout", 600000)); //cached for 10 minutes
	}

	private AccessResponse checkCachedPasswordPolicy(KMUser kUser, PasswordPolicy pp, boolean validLogin, AccessResponse ar, AccessLog loginLog) {
		if (pp == null) {
			return ar;
		}
		if (pp.getLockoutEnabled() && !validLogin) {
			if ((kUser.isLockedOut() != null) && kUser.isLockedOut()) {
				//attempt unlocking locked accounts
				Timestamp currentTime = new Timestamp(new Date().getTime());
				Timestamp lastFailedLogin = kUser.getLastFailedLogin();
				int difference = getTimeDifference(lastFailedLogin, currentTime, DatePart.MINUTE);
				if (difference < pp.getLockoutDuration()) {
					ar.setStatus(-1);
					ar.setMessage("Your lockout wait period has not expired. Please try again later.");
					loginLog.setLoginStatusEnum(LoginStatusEnum.LOCKED);
					jmsSender.queueLoginLog(loginLog);
					return ar;
				} else {
					String allowAdminUnlockOnly = getSettingValue("ALLOW-ADMIN-UNLOCK-ONLY", "true");
					if ((allowAdminUnlockOnly != null) && allowAdminUnlockOnly.equalsIgnoreCase("false")) {
						//unlock account
						kUser.setLockedOut(Boolean.FALSE);
						//add to queue for update 
						String msg = jmsSender.queueUserLogin(kUser);
						logger.info("queued locked user to be unlocked: " + msg);
						//proceed to the end for other checks
					}
				}
			} else {
				//we only needed to put this info in cache for users whose account are not locked yet
				String cachedPolicyResult = cache.getItem(LOGIN_KEY + kUser.getEmailAddress().trim(), String.class); //retrieve existing cached item
				cacheFailedLoginAttempt(kUser, cachedPolicyResult);//cache current check
				if (cachedPolicyResult != null) {
					//check if failed login attempt policy was violated
					LoginCacheItem cacheItem = new LoginCacheItem(cachedPolicyResult);
					if ((cacheItem.getAttempts() + 1) == pp.getMaximumFailure()) {
						ar.setStatus(-1);
						ar.setMessage("You have " + cacheItem.getAttempts() + " failed logins. Next failed attempt will lock your account.");
						loginLog.setLoginStatusEnum(LoginStatusEnum.FAILED_AUTH);
						jmsSender.queueLoginLog(loginLog);
						logger.info("queued login log for failed auth");
						return ar;
					} else if (cacheItem.getAttempts() >= pp.getMaximumFailure()) {
						// The line below marks the KMUser locked out status.
						ar.setStatus(-1);
						ar.setMessage("You have " + cacheItem.getAttempts() + " failed logins. Your account is now locked.");
						kUser.setLockedOut(Boolean.TRUE);
						kUser.setFailedLoginAttempts(cacheItem.getAttempts());
						kUser.setLastFailedLogin(new Timestamp(new Date().getTime()));
						String msg = jmsSender.queueUserLogin(kUser);
						logger.info("queued user to be locked: " + msg);

						loginLog.setLoginStatusEnum(LoginStatusEnum.LOCKOUT);
						jmsSender.queueLoginLog(loginLog);
						logger.info("queued login log for failed auth");
						cache.removeItem(LOGIN_KEY + kUser.getEmailAddress().trim()); //prepare cache for the next attempts
						return ar;
					}
				}
			}
		}

		if (pp.getPasswordExpirationEnabled() && validLogin) {
			// We are not checking for null here as there should always be a last password change date. Remember user first login?
			Timestamp lastLogin = kUser.getLastPasswordChange();
			Timestamp currentTime = new Timestamp(new Date().getTime());
			int difference = getTimeDifference(lastLogin, currentTime, DatePart.DAY);
			if (difference > pp.getMaximumAge()) {
				ar.setStatus(-7);
				ar.setName(getUsername(kUser));
				ar.setRoles(getUserRoles(kUser.getRoles()));
				ar.setMessage("Your password has expired. Please reset your password");
				ar.setPasswordSyntaxRegex(pp.getRegex());
				ar.setPasswordSyntaxGuide(pp.getUserMessage());
				loginLog.setLoginStatusEnum(LoginStatusEnum.EXPIRED_PASSWORD);
				jmsSender.queueLoginLog(loginLog);
				logger.info("queued login log for expired password");
				return ar;
			}
		}
		return ar;
	}

	private AccessResponse checkPasswordPolicy(KMUser kUser,boolean isPasswordValid, PasswordPolicy pp,AccessResponse ar) {

		if( (kUser == null) || (pp == null) || (ar == null) ){
			return ar;
		}

		if( pp.getLockoutEnabled() ){

			if( (kUser.isLockedOut() != null) && kUser.isLockedOut() ){
				Timestamp lastFailedLogin = kUser.getLastFailedLogin();
				Timestamp lastSuccessfulLogin = kUser.getLastSuccessfulLogin();
				if( (lastFailedLogin != null) && (lastSuccessfulLogin != null) && lastFailedLogin.after(lastSuccessfulLogin) ){ // This ensures that a person with a successful login after a failed login does not get logged out.

					Timestamp currentTime = new Timestamp(new Date().getTime());
					int difference = getTimeDifference(lastFailedLogin, currentTime, DatePart.MINUTE);
					if( difference < pp.getLockoutDuration() ){    
						ar.setStatus(-1);
						ar.setMessage("Your lockout wait period has not expired. Please try again later.");
						return ar;
					}else{
						String allowAdminUnlockOnly = getSettingValue("ALLOW-ADMIN-UNLOCK-ONLY", "true");
						if( (allowAdminUnlockOnly != null ) && allowAdminUnlockOnly.equalsIgnoreCase("false") ){
							kUser.setFailedLoginAttempts(0);// resets the failed login attempts.
							kUser.setLockedOut(Boolean.FALSE);
							//							getDbService().update(kUser);
						}
					}

				}
			}

			int failedLoginCount = kUser.getFailedLoginAttempts() == null ? 0 : kUser.getFailedLoginAttempts();
			if( failedLoginCount >= pp.getMaximumFailure() ){ 
				// The line below marks the KMUser locked out status.
				ar.setStatus(-1);
				ar.setMessage("You have " + pp.getMaximumFailure() + " failed logins. Your account is now locked.");
				kUser.setLockedOut(Boolean.TRUE);
				//				getDbService().update(kUser);
				return ar;
			}            
		}

		if( pp.getPasswordExpirationEnabled() ){

			if( !isPasswordValid ){
				return updateUserPasswordPolicy(kUser, ar);
			}

			// We are not checking for null here as there should always be a last password change date. Remember user first login?
			Timestamp lastLogin_ = kUser.getLastPasswordChange();
			Timestamp currentTime = new Timestamp(new Date().getTime());
			int difference = getTimeDifference(lastLogin_, currentTime, DatePart.DAY);
			if( difference > pp.getMaximumAge() ){   
				ar.setStatus(-7);
				ar.setName( getUsername(kUser) );
				ar.setRoles(getUserRoles(kUser.getRoles()));
				ar.setMessage("Your password has expired. Please reset your password");
				ar.setPasswordSyntaxRegex( pp.getRegex() );
				ar.setPasswordSyntaxGuide( pp.getUserMessage() );
				return ar;
			}            
		}

		return ar;
	}

	private String getUsername(KMUser kUser){
		String firstname = kUser.getFirstName() == null ? "" : kUser.getFirstName();
		String surname = kUser.getSurname() == null ? "" : kUser.getSurname();

		return firstname + " " + surname;
	}

	private void AccountLogoutException(String invalid_credentials) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	enum DatePart{DAY, HOUR, MINUTE,SECONDS, MILLISECONDS}

	private int getTimeDifference(Timestamp start, Timestamp end,DatePart datePart){

		if( (start == null) || (end == null) || (datePart == null) ) {
			return 0;
		}

		Long diffMs = end.getTime() - start.getTime();
		Long diffSec = diffMs / 1000;
		Long diffMin = diffSec / 60;
		Long diffHr = diffMin / 60;
		Long diffDays = diffHr / 24;

		switch( datePart ){
		case DAY :
			return diffDays.intValue();
		case HOUR :
			return diffHr.intValue();
		case MINUTE :
			return diffMin.intValue();
		case SECONDS :
			return diffSec.intValue();
		case MILLISECONDS :
			return diffMs.intValue();
		}

		return 0;
	}

	@SuppressWarnings("PMD") //needed to catch exception because a third party api
	public AccessResponseData requestLoginOtp(KMUser user, AccessResponse accessResponse) {
		AccessResponseData responseData = new AccessResponseData();
		responseData.setCode(ResponseCodeEnum.ERROR);
		try {
			if (user == null || user.getMobile() == null || user.getMobile().isEmpty()) {
				responseData.setCode(ResponseCodeEnum.FAILED_AUTHENTICATION);
				return responseData;
			}

			try {   //s + ":" + otpToken + ":" + otpExpiration;
			String resp = otpDS.generateOTP(OtpStatusRecordTypeEnum.LOGIN, user.getMobile());

			logger.debug("otp response=" + resp);
			String[] respSplit = resp.split(":");
			if (respSplit[0] != null) {

				//                boolean b = kSms.sendSMS(msisdn, "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes." );
				String message = "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes.";
				boolean sendSMS = kSms.sendSMS(user.getMobile(), message);
				if (sendSMS) {
					responseData.setCode(ResponseCodeEnum.SUCCESS);
					responseData.setDescription("OTP was generated successfully.");
					if (accessResponse != null) {
						responseData.setFirstLogin(accessResponse.isFirstLogin());
						responseData.setMsisdn(user.getMobile());
						responseData.setName(accessResponse.getName());
						responseData.setPasswordSyntaxGuide(accessResponse.getPasswordSyntaxGuide());
						responseData.setPasswordSyntaxRegex(accessResponse.getPasswordSyntaxRegex());
						responseData.setOtp(respSplit[1]);
						responseData.setOtpExpirationTime(respSplit[2]);
						responseData.setPrivileges(user.getPrivileges());
					}
				} else {
					responseData.setCode(ResponseCodeEnum.ERROR);
					responseData.setDescription("Failed to generate/send OTP. Try again later.");
				}
			} else {
				responseData.setCode(ResponseCodeEnum.ERROR);
				responseData.setDescription("An error occurred while generating OTP. Please try again later.");
			}

			return responseData;

			} catch (Exception e) {
				responseData.setCode(ResponseCodeEnum.ERROR);
				responseData.setDescription(OTP_ERROR_MESSAGE);
				logger.error("", e);
			}

			return responseData;

		} catch (ArrayIndexOutOfBoundsException e) {
			responseData.setCode(ResponseCodeEnum.ERROR);
			responseData.setDescription(OTP_ERROR_MESSAGE);
			logger.error("", e);
			return responseData;
		}

	}

	public AccessResponse handleVerifyOtp(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String mobile, String otp) { 
		AccessResponse accessResponse = new AccessResponse();

		if (otp == null || otp.isEmpty() || mobile == null || mobile.isEmpty()) {
			accessResponse.setStatus(-1);
			accessResponse.setMessage("Invalid username or phone number found.");
			return accessResponse; 
		}

		int code = 0;
		code = otpDS.verifyOtpStatus(otpStatusRecordTypeEnum, otp, mobile);
		String msg = "";
		switch (code) {
		case 1:
			msg = "The specified otp is valid.";
			break;
		case 2:
			msg = "This otp has expired.";
			break;
		case 3:
			msg = "There is no record associated the provided otp.";
			break;
		case 4:
			msg = "Both the msisdn and the OTP are required inputs. Note that msisdn must be 11 digits.";
			break;
		}
		accessResponse.setMessage(msg);
		accessResponse.setStatus(code);
		return accessResponse;
	}

	@SuppressWarnings("CPD-START")
	public AgentOnboardingResponseData handleValidateAgentEmailAddress(String emailAddress) {
		AgentOnboardingResponseData ar = new AgentOnboardingResponseData(ResponseCodeEnum.ERROR);
		try {
			KMUser user = doLightKMUserRetrieval(emailAddress);

			if (user == null) {
				ar.setCode(ResponseCodeEnum.FAILED_AUTHENTICATION);
				ar.setDescription("Invalid email address was entered.");
				return ar;
			}
			if (!user.isActive()) {
				ar.setCode(ResponseCodeEnum.INACTIVE_ACCOUNT);
				ar.setDescription("Your account was deactivated. Please contact support");
				return ar;
			}

			OnboardingStatus os = doLightOnboardingStatusRetrieval(user.getPk());
			if (os != null && os.isOnboarded()) {
				ar.setCode(ResponseCodeEnum.ALREADY_ONBOARDED);
				ar.setDescription("Agent already onboarded");
				return ar;
			}

			String resp = otpDS.generateOTP(OtpStatusRecordTypeEnum.EMAIL_VALIDATION, user.getMobile());
			String[] respSplit = resp.split(":");
			if (respSplit[0] != null) {
				String message = "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes.";
				boolean sendSMS = kSms.sendSMS(user.getMobile(), message);
				if (sendSMS) {
					ar.setCode(ResponseCodeEnum.SUCCESS);
					ar.setEmail(emailAddress);
					ar.setMsisdn(user.getMobile());
					ar.setFirstName(user.getFirstName());
					ar.setLastName(user.getSurname());
					ar.setDescription("OTP was successfully generated.");
				} else {
					ar.setCode(ResponseCodeEnum.ERROR);
					ar.setDescription("Failed to generate/send OTP. Try again later.");
				}
			} else {
				ar.setCode(ResponseCodeEnum.ERROR);
				ar.setDescription(OTP_ERROR_MESSAGE);
			}

		} catch (ArrayIndexOutOfBoundsException | IOException e) {
			logger.error("", e);
		}
		return ar;
	}


	private OnboardingStatus doLightOnboardingStatusRetrieval(Long userFk) {
		Session session = null;
		OnboardingStatus onboardingStatus = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			String query = "select o.pk as pk, o.ONBOARDED_ as onboarded  from onboarding_status o where o.user_fk = :userFk";
			SQLQuery sqlq = session.createSQLQuery(query);
			sqlq.setParameter("userFk", userFk);
			sqlq.addScalar("pk", new LongType());
			sqlq.addScalar("onboarded", new BooleanType());
			onboardingStatus = (OnboardingStatus) sqlq.setResultTransformer(Transformers.aliasToBean(OnboardingStatus.class)).uniqueResult();
		} catch (HibernateException | NwormQueryException e) {
			logger.error("", e);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return onboardingStatus;
	}

	private KMUser doLightKMUserRetrieval(String agentEmail) {
		Session session = null;
		KMUser user = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			String query = "select u.pk as pk, u.active as active, u.mobile as mobile, u.surname as surname, u.first_name as firstName, u.email_address as emailAddress from km_user u where u.email_address = :emailAddress";
			SQLQuery sqlq = session.createSQLQuery(query);
			sqlq.setParameter("emailAddress", agentEmail.toLowerCase());
			sqlq.addScalar("pk", new LongType());
			sqlq.addScalar("active", new BooleanType());
			sqlq.addScalar("surname", new StringType());
			sqlq.addScalar("mobile", new StringType());
			sqlq.addScalar("firstName", new StringType());
			sqlq.addScalar("emailAddress", new StringType());
			user = (KMUser) sqlq.setResultTransformer(Transformers.aliasToBean(KMUser.class)).uniqueResult();
		} catch (HibernateException | NwormQueryException e) {
			logger.error("", e);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return user;
	}

	/**
	 * Fetches agent fingerprints for matching
	 * on the client
	 * @param email
	 * @return
	 */
	public FingerLoginResponse fetchAgentFingerprints(String email){
		if(email == null || email.isEmpty()){
			return new FingerLoginResponse(ResponseCodeEnum.INVALID_INPUT, "No email address supplied");
		}

		KMUser user = getUser(email.trim());
		if(user == null){
			return new FingerLoginResponse(ResponseCodeEnum.FAILED_AUTHENTICATION, "No agent was found with entered email address");
		}

		if(!user.isActive()){
			return new FingerLoginResponse(ResponseCodeEnum.INACTIVE_ACCOUNT, "Your Account has been deactivated");
		}

		//		check if agent has been onboarded
		QueryModifier modifier = new QueryModifier(OnboardingStatus.class);
		modifier.addProjection(Projections.property("onboarded"));
		Boolean onboarded = dbService.getByCriteria(Boolean.class, modifier, Restrictions.eq("user.pk", user.getPk()));
		if(onboarded == null || !onboarded){
			return new FingerLoginResponse(ResponseCodeEnum.ONBOARDING_PENDING, "Agent has not been onboarded yet");
		}

		//		retrieve the agent's fingerprints from db
		List<AgentFingerprintPojo> fingers = new ArrayList<AgentFingerprintPojo>();
		QueryModifier fingerModifier = new QueryModifier(AgentFingerprint.class);
		QueryFetchMode fetchMode = new QueryFetchMode();
		fetchMode.setAlias("user");
		fetchMode.setFetchMode(FetchMode.LAZY);
		fingerModifier.addFetchMode(fetchMode);
		List<AgentFingerprint> afs = dbService.getListByCriteria(AgentFingerprint.class, fingerModifier, Restrictions.eq("user.pk", user.getPk()));
		if(afs != null && !afs.isEmpty()){
			for(AgentFingerprint af : afs){
				logger.debug("Adding finger: " + af.getFingerType().name());
				fingers.add(new AgentFingerprintPojo(af.getFingerType().name(), Base64.encodeBytes(af.getFingerprint())));
			}
		}

		FingerLoginResponse flr = new FingerLoginResponse(ResponseCodeEnum.SUCCESS, "Agent's fingerprints successfully retrieved");
		flr.setFingerprints(fingers);
		flr.setFirstName(user.getFirstName());
		flr.setSurname(user.getSurname());
		flr.setCached(isUserCacheable(user));
		flr.setEmail(email);

		flr.setPrivileges(user.getPrivileges());

		return flr;
	}

	public FetchPrivilegesResponse getPrivileges(String email){
		logger.info("Retrieving privileges for: " + email);
		FetchPrivilegesResponse resp = new FetchPrivilegesResponse();
		KMUser user = getUser(email);

		if(user == null){
			resp.setCode(ResponseCodeEnum.INVALID_INPUT);
			resp.setDescription("User with entered email address not found"); 
			return resp;
		}

		if(!user.isActive()){
			resp.setCode(ResponseCodeEnum.INACTIVE_ACCOUNT);
			resp.setDescription("User is blacklisted"); 
			return resp;
		}

		resp.setFirstName(user.getFirstName());
		resp.setSurname(user.getSurname());
		resp.setEmail(email);
		resp.setCached(isUserCacheable(user));
		resp.setPrivileges(user.getPrivileges());
		resp.setCode(ResponseCodeEnum.SUCCESS);
		resp.setDescription("Successfully retrieved user's privileges");

		return resp;
	}

	public boolean isUserCacheable(KMUser user){
		boolean cacheable = false;
		if(user != null){
			for(KMRole role : user.getRoles()){
				cacheable = true;
				if(role.getAdmin() == null || !role.getAdmin()){
					cacheable = false;
					break;
				}
			}
		}
		return cacheable;
	}

	public ForgotPasswordResponse sendTemporaryPassword(String email) {
		ForgotPasswordResponse response = new ForgotPasswordResponse();
		if (email == null) {
			response.setCode(ResponseCodeEnum.ERROR);
			response.setDescription("Required field is missing");
			return response;
		}

		Session session = null;
		KMUser user = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			String query = "select u.active as active, u.mobile as mobile,"
					+ " u.first_name as firstName, u.surname as surname, u.locked_out as lockedOut from km_user u"
					+ " where u.email_address =:emailAddress";
			SQLQuery sqlq = session.createSQLQuery(query);
			sqlq.setParameter("emailAddress", email.toLowerCase());
			sqlq.addScalar("active", BooleanType.INSTANCE);
			sqlq.addScalar("mobile", StringType.INSTANCE);
			sqlq.addScalar("firstName", StringType.INSTANCE);
			sqlq.addScalar("surname", StringType.INSTANCE);
			sqlq.addScalar("lockedOut", BooleanType.INSTANCE);
			user = (KMUser) sqlq.setResultTransformer(Transformers.aliasToBean(KMUser.class)).uniqueResult();
		} catch (NwormQueryException | HibernateException e) {
			logger.error("DB Exception for forgot password", e);

		} finally {
			dbService.getSessionService().closeSession(session);
		}
		if (user == null) {
			response.setCode(ResponseCodeEnum.INVALID_INPUT);
			response.setDescription("No agent found");
			return response;
		} else {
			if (!user.isActive()) {
				response.setCode(ResponseCodeEnum.INACTIVE_ACCOUNT);
				response.setDescription("Agent account is deactived, Please contact support");
				return response;
			}
			if (user.getLockedOut() != null && user.getLockedOut()) {
				response.setCode(ResponseCodeEnum.LOCKED_ACCOUNT);
				response.setDescription("Agent account is locked, Please contact support");
				return response;
			}
			if (user.getMobile() == null || user.getMobile().isEmpty()) {
				response.setCode(ResponseCodeEnum.FAILED_AUTHENTICATION);
				response.setDescription("No Mobile number found for this agent");
				return response;
			}

			String otpValue, otpRecord, otpExpirationTime;
			try {
				Map<String, String> otpResult = generateOTP(OtpStatusRecordTypeEnum.EMAIL_VALIDATION, user.getMobile());
				otpRecord = otpResult.get("otpRecord");
				otpValue = otpResult.get("otpValue");
				otpExpirationTime = otpResult.get("otpExpirationTime");
			} catch (ArrayIndexOutOfBoundsException ex) {
				response.setCode(ResponseCodeEnum.FAILED_OTP_GENERATION);
				response.setDescription("Otp generation failed");
				logger.error("forgot password Otp Array out of Bounds Exception", ex);
				return response;
			}
			if (otpRecord != null) {
				boolean sendSMS = sendOTPMessage(otpValue, otpExpirationTime, user.getMobile());
				if (sendSMS) {
					response.setCode(ResponseCodeEnum.SUCCESS);
					response.setDescription("Successfully generated OTP");
					response.setMsisdn(user.getMobile());
					response.setOtp(otpValue);
					response.setOtpExpirationTime(otpExpirationTime);
					String firstname = user.getFirstName() == null ? "" : user.getFirstName();
					String surname = user.getSurname() == null ? "" : user.getSurname();
					response.setName(firstname + " " + surname);
				} else {
					response.setCode(ResponseCodeEnum.FAILED_OTP_GENERATION);
					response.setDescription(OTP_ERROR_MESSAGE);
				}
			} else {
				response.setCode(ResponseCodeEnum.FAILED_OTP_GENERATION);
				response.setDescription(OTP_ERROR_MESSAGE);
			}
		}

		return response;
	}
	
	public boolean sendOTPMessage(String otpValue, String otpExpirationTime, String msisdn){
		String message = "Your one-time password is " + otpValue + ". It expires in " + otpExpirationTime + " minutes.";
		boolean sendSMS = false;
		try {
			sendSMS = kSms.sendSMS(msisdn, message);
		} catch (IOException ex) {
			logger.error("Exception while sending forgot password Otp", ex);
		}
		
		return sendSMS;
	}
	
	public Map<String, String> generateOTP(OtpStatusRecordTypeEnum type, String msisdn) throws ArrayIndexOutOfBoundsException {
		String otpValue, otpRecord, otpExpirationTime;
		String otpString = otpDS.generateOTP(type, msisdn);
		String[] otpSplit = otpString.split(":");
		otpRecord = otpSplit[0];
		otpValue = otpSplit[1];
		otpExpirationTime = otpSplit[2];
		
		Map<String, String> otpResult = new HashMap<String, String>();
		otpResult.put("otpRecord", otpRecord);
		otpResult.put("otpValue", otpValue);
		otpResult.put("otpExpirationTime", otpExpirationTime);
		
		return otpResult;
	}

	public ResponseData changeLoggedInPassword(String email, String oldPassword, String newPassword) {
		ResponseData response = new ResponseData();
		try {
			if (email == null || oldPassword == null || newPassword == null) {
				response.setCode(ResponseCodeEnum.ERROR);
				response.setDescription("Required fields are missing");
				return response;
			}

			KMUser user = getUser(email);

			if (user == null) {
				response.setCode(ResponseCodeEnum.ERROR);
				response.setDescription("No agent found");
				return response;

			} else {
				if (!user.isPasswordValid(oldPassword)) {
					response.setCode(ResponseCodeEnum.ERROR);
					response.setDescription("Invalid logged in Password ");
					logger.debug("password mismatch " + email);
					return response;

				} else {
                                    user.setLastPasswordChange(new Timestamp(new Date().getTime()));
                                    user.setPassword(newPassword);
                                    dbService.update(user);
                                    response.setCode(ResponseCodeEnum.SUCCESS);
                                    response.setDescription("Password change successful");
                                    logger.debug("Password change successful  " + email);
				}
			}
		} catch (NwormQueryException e) {
			logger.error("Password Changed failed", e);
			response.setCode(ResponseCodeEnum.ERROR);
			response.setDescription("Password change failed");
		}
		return response;
	}



	public AccessResponse testUsers(String email, String phone){
		String hql = "Update KMUser set mobile = '" + phone + "' Where emailAddress = '" + email + "'";
		getDbService().executeHQLUpdate(hql);

		AccessResponse ar = new AccessResponse();
		ar.setStatus(0);
		ar.setMessage("User update successful");

		return ar;
	}

	public Coordinate getOutletCoordinate(String mac, String deviceId) {
		String cacheName = ("OUTLET_COORDINATE" + mac).replace(" ", "");

		Coordinate coordinate = cache.getItem(cacheName, Coordinate.class);
		if(coordinate == null) {
			//check db
			Outlet outlet = geoFenceDS.getOutlet(mac, deviceId);
			if(outlet != null && outlet.getLatitude() != null && outlet.getLongitude() != null) {
				coordinate = new Coordinate(outlet.getLatitude(), outlet.getLongitude());
				cache.setItem(cacheName, coordinate, 60 * 60 * 2); //2 hrs
			}
		}
		return coordinate;
	}

	public EmailApiAuthenticator getEmailApiAuthenticator() {
		String userName = getSettingValue(SettingsEnum.EMAIL_NOTIFICATION_AUTHENTICATOR_USER_NAME);
		String password = getSettingValue(SettingsEnum.EMAIL_NOTIFICATION_AUTHENTICATOR_PASSWORD);
		Integer port = Integer.valueOf(getSettingValue(SettingsEnum.EMAIL_NOTIFICATION_AUTHENTICATOR_HOST_PORT));
		String hostName = getSettingValue(SettingsEnum.EMAIL_NOTIFICATION_AUTHENTICATOR_HOST_NAME);
		String fromTitle = getSettingValue(SettingsEnum.EMAIL_NOTIFICATION_AUTHENTICATOR_FROM_EMAIL);
		return new EmailApiAuthenticator(userName, password, port, hostName, fromTitle);
	}
}
