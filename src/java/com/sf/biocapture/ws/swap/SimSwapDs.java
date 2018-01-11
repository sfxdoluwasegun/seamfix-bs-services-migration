package com.sf.biocapture.ws.swap;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.agl.integration.AgilityResponse;
import com.sf.biocapture.agl.integration.GetSubscriberDetails;
import com.sf.biocapture.app.BiodataCache;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.CrmPushStatus;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.MsisdnDetail;
import com.sf.biocapture.entity.audit.SimSwapLog;
import com.sf.biocapture.entity.enums.CrmStatus;
import com.sf.biocapture.entity.enums.SfxCrmTypes;
import com.sf.biocapture.entity.enums.SimSwapStatus;
import com.sf.biocapture.proxy.BioData;

import nw.orm.core.exception.NwormQueryException;
import org.hibernate.criterion.Restrictions;

/**
 * 
 * @author Nnanna
 * @since 24/11/2016
 */
@Stateless
public class SimSwapDs extends DataService {

	@Inject
	BiodataCache cache;

	@Inject
	GetSubscriberDetails sDetails;

	public void createSimSwapLog(String deviceId,SimSwapRequest req){
            try {                
		String uniqueId = req.getUniqueId() == null ? req.getTag() + "-" + defaultSdf.format(new Date()) : req.getUniqueId();

		SimSwapLog log = new SimSwapLog();
		log.setActive(true);
		log.setAgentEmail(req.getAgentEmail());
		log.setMacAddress(req.getMacAddress());
		log.setMsisdn(req.getMsisdn());
		log.setOrderNumber(req.getOrderNumber());
		log.setPuk(req.getPuk());
		log.setSimSerial(req.getSimSerial());
		log.setSwapStatus(SimSwapStatus.PENDING);
		log.setSwapTime(new Timestamp(req.getSwapTime()));
		log.setTag(req.getTag());
		log.setUniqueId(uniqueId);
                EnrollmentRef enrollmentRef = null;
                if(deviceId != null && !deviceId.isEmpty()){
                    enrollmentRef = dbService.getByCriteria(EnrollmentRef.class, Restrictions.eq("deviceId", deviceId.toUpperCase()));
                }
                log.setRef(enrollmentRef);
                if(req.getSubscriberPassport() != null){
			log.setSubscriberPassport(Base64.getDecoder().decode(req.getSubscriberPassport().replaceAll("\\s*", "")));
		}
		dbService.create(log);
		
		//get old unique id from cache
		String oldUniqueId = getOldUniqueId(req.getMsisdn());
		logger.debug("Old unique ID retrieved for sim swap: " + oldUniqueId);
		
		//get msisdn detail
		MsisdnDetail md = getMsisdnDetail(req.getMsisdn(), oldUniqueId);

		//create crm push status
		CrmPushStatus pushStatus = new CrmPushStatus();
		pushStatus.setUniqueId(uniqueId);
		pushStatus.setCrmCreateTimestamp(new Timestamp(System.currentTimeMillis()));
		pushStatus.setCrmType(SfxCrmTypes.SIM_SWAP_FORM);
		pushStatus.setMsisdnDetail(md);
		pushStatus.setSkip(false);
		pushStatus.setPushStatus(CrmStatus.NOT_SENT);
		
		dbService.create(pushStatus);
            } catch (NwormQueryException e) {
                logger.error("", e);
            }
	}
	
	/**
	 * gets the old unique id from cache
	 * @return
	 */
	private String getOldUniqueId(String msisdn){
		String key = "SIMSWAP-" + msisdn + "-";
		BioData bd = cache.getItem(key, BioData.class);
		if(bd == null){
			logger.debug("MSISDN unique ID not found in cache for SIM SWAP");
			AgilityResponse ar = sDetails.getSubscriberDetails(msisdn, null, msisdn + "-" + defaultSdf.format(new Date()));
			bd = ar.getBioData();
		}
		
		return bd.getUniqueId();
	}
	
	private MsisdnDetail getMsisdnDetail(String msisdn, String oldUniqueId){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("msisdn", msisdn);
		parameters.put("uniqueId", oldUniqueId);
		
		String hql = "select md from MsisdnDetail md, BasicData bd, UserId ud "
				+ "where md.msisdn = :msisdn "
				+ "and md.basicData.id = bd.id "
				+ "and bd.userId.id = ud.id "
				+ "and ud.uniqueId = :uniqueId";
		
		return dbService.getByHQL(hql, parameters, MsisdnDetail.class);
	}
}
