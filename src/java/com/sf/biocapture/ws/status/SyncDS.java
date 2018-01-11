package com.sf.biocapture.ws.status;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.hibernate.criterion.Restrictions;

import nw.orm.core.query.QueryParameter;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.common.DesEncrypter;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.UserId;
import com.sf.biocapture.entity.audit.BfpFailureLog;

import nw.commons.StopWatch;
import nw.orm.core.query.QueryModifier;
import org.hibernate.criterion.Projections;

@Stateless
public class SyncDS extends DataService{
    
        private static final String BOOLEAN_TRUE = "true";
        private static final String BOOLEAN_FALSE = "false";
        private DesEncrypter desEncrypter = new DesEncrypter();
	
	@Inject
	private BioCache cache;
	
	private final String STATUS_KEY = "SYNC_STATS-";
        
        private boolean startsWith (String uid) {
            String keys [] = {"kycncc", "win", "droid"};
            for (String key : keys) {
                if (uid.toLowerCase().startsWith(key)) {
                    return true;
                }
            }
            return false;
        }
	
	public String checkStatus(String uid){
		logger.debug("Checking uidstatus for " + uid);
                if (uid != null && !uid.isEmpty()) {
                    if (!startsWith(uid)) {
                        logger.debug("unique id before decryption: " + uid);
                        uid = desEncrypter.decrypt(uid);
                        logger.debug("unique id after decryption: " + uid);
                    }
                }
		Long item = cache.getItem(uid.replaceAll(" ", ""), Long.class);
		if(item != null){
			logger.debug("Uidstatus is cached for " + uid);
			return BOOLEAN_TRUE + "," + item;
		}else{
			logger.debug("Uidstatus NOT cached for " + uid);
			Timestamp si = getSyncInfo(uid.replaceAll(" ", ""));
			if(si != null){
				cache.setItem(uid.replaceAll(" ", ""), si.getTime(), 24 * 60 * 60);
				logger.debug("Uidstatus timestamp for: " + uid + "; time: " + si.getTime());
				return BOOLEAN_TRUE + "," + si.getTime();
			}else{
				logger.debug("Checking bfp failure log for " + uid);
				//check bfp failure log
				Date date = getBfpFailureLogStatus(uid.replaceAll(" ", ""));
				if(date != null ){
					logger.debug("Found status in bfp failure log for " + uid);
					cache.setItem(uid.replaceAll(" ", ""), date.getTime(), 24 * 60 * 60);
					return BOOLEAN_TRUE + "," + date.getTime();
				}
			}
		}
		logger.debug(uid + " does not exist in USER_ID and BFP_FAILURE_LOG");
		return BOOLEAN_FALSE;
	}
	
	public String checkUidStatus(String uid){
                StopWatch sw = new StopWatch(true);
		logger.info("inside the checkstatus of SyncDS : the start time is : " + sw.currentElapsedTime());
		Long item = cache.getItem(uid.replaceAll(" ", ""), Long.class);
                logger.info("inside the checkstatus of SyncDS : the elapsed time after checking cache is : " + sw.elapsedTime() );
		if(item != null){
			return BOOLEAN_TRUE + "," + item;
		}else{
                    logger.info("inside the checkstatus of SyncDS : the time at checking cache again is : " + sw.elapsedTime() );
			String wait = cache.getItem(STATUS_KEY + uid, String.class);
                        logger.info("inside the checkstatus of SyncDS : the elapsed time after checking cache again is : " + sw.currentElapsedTime() );
			if("WAIT".equalsIgnoreCase(wait)){
				return BOOLEAN_FALSE;
			}else{
				Timestamp si = getSyncInfo(uid.replaceAll(" ", ""));
				if(si != null && cache.setItem(uid.replaceAll(" ", ""), si.getTime(), 24 * 60 * 60)){
					return BOOLEAN_TRUE + "," + si.getTime();
				}else{
					cache.setItem(STATUS_KEY + uid, "WAIT", 2 * 60);
				}
			}
		}
		return BOOLEAN_FALSE;
	}
	
	public String checkState(String lDate){
            StopWatch sw = new StopWatch();
            logger.info("inside the checkState of SyncDS : the before checking server time is : " + sw.currentElapsedTime() );
		logger.info("Client State check for: " + lDate);
		String response = BOOLEAN_FALSE;
		String[] sdata = lDate.split(getDelimiter());// date, pv, tag
		if(sdata.length < 3){
			return BOOLEAN_FALSE;
		}else{
			try {
				response = checkServerTime(sdata[0]);
//				updatePatchVersion(sdata[2], sdata[1]);
			} catch (IOException e) {
				logger.error("Exception ", e);
			}
		}
                logger.info("inside the checkState of SyncDS : the after checking server time is : " + sw.elapsedTime());
		return response;
	}
	
	private String checkServerTime(String lDate) throws IOException{
		String result = "true";
		Date sDate = new Date();
		long diff = Math.abs(sDate.getTime() - Long.parseLong(lDate));
		if (diff > 1 * 60 * 1000) { // Check if local date is within 15 minutes out of range
			result = String.valueOf(new Date().getTime());
		}
		return result;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	protected void updatePatchVersion(String tag, String appVersion){
		String pv = cache.getItem("PV-" +tag, String.class);
		if(pv != null && !pv.equalsIgnoreCase(appVersion)){
			String hql = "SELECT e FROM EnrollmentRef e WHERE upper(e.code)=:tag";
			EnrollmentRef node = dbService.getByHQL(EnrollmentRef.class, hql, QueryParameter.create("tag", tag));
			node.setDescription(pv);
			dbService.update(node);
			cache.setItem("PV-"+ tag, appVersion, 60 * 60 * 24 * 7);
			logger.info("Patch update for: " + tag + " ==>app version " + appVersion);
		}
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Timestamp getSyncInfo(String uid){
                QueryModifier modifier = new QueryModifier(UserId.class);
                modifier.addProjection(Projections.property("createDate"));
		return dbService.getByCriteria(Timestamp.class, modifier, Restrictions.eq("uniqueId", uid));
	}
	
	private List<BfpFailureLog> getBfpLog(String uid){
		return dbService.getListByCriteria(BfpFailureLog.class, Restrictions.eq("uniqueId", uid));
	}
        
	private Date getBfpFailureLogStatus(String uid){
                QueryModifier modifier = new QueryModifier(BfpFailureLog.class);
                modifier.addProjection(Projections.property("createDate"));
                modifier.setPaginated(0, 1);
		return dbService.getByCriteria(Date.class, modifier, Restrictions.eq("uniqueId", uid));
	}
}
