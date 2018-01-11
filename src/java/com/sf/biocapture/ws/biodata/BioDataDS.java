package com.sf.biocapture.ws.biodata;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.BasicData;
import com.sf.biocapture.entity.DynamicData;
import com.sf.biocapture.entity.PassportData;
import com.sf.biocapture.entity.PassportDetail;
import com.sf.biocapture.entity.SignatureData;
import com.sf.biocapture.entity.SmsActivationRequest;
import com.sf.biocapture.entity.UserId;
import com.sf.biocapture.entity.WsqImage;
import com.sf.biocapture.proxy.BioData;
import com.sf.biocapture.proxy.ProxyTranslator;
import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nw.commons.StopWatch;
import nw.orm.core.query.QueryFetchMode;
import nw.orm.core.query.QueryModifier;
import nw.orm.core.query.QueryParameter;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author @wizzyclems
 * @author Nnanna
 */
@Stateless
public class BioDataDS extends DataService {
    
    private static final String TIME_UNIT = "ms";
    private static final String BASIC_DATA_ID = "basicData.id";
    private static final String BASIC_DATA = "basicData";
    

    @Inject
    private BioCache cache;
    
    public List<SmsActivationRequest> getMsisdnActivationRequests(String msisdn){
    	QueryModifier qm = new QueryModifier(SmsActivationRequest.class);
    	qm.addOrderBy(Order.desc("receiptTimestamp"));
    	
    	return dbService.getListByCriteria(SmsActivationRequest.class, qm, Restrictions.eq("phoneNumber", msisdn));
    }
    
    public List<SmsActivationRequest> getActivationRequests(String msisdn){
    	Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("msisdn", msisdn);
		
    	String hql = "select sar from SmsActivationRequest sar where "
    			+ "sar.confirmationStatus is true "
    			+ "and sar.phoneNumber = :msisdn "
    			+ "order by sar.receiptTimestamp desc";
    	
    	return dbService.getListByHQL(hql, parameters, SmsActivationRequest.class);
    }
 
    public List<Timestamp> getActivationTimestampRequests(String msisdn) {
        QueryModifier modifier = new QueryModifier(SmsActivationRequest.class);
        
        //exclude join clause
        QueryFetchMode fetchMode = new QueryFetchMode();
        fetchMode.setAlias("phoneNumberStatus");
        fetchMode.setFetchMode(FetchMode.LAZY);
        modifier.addFetchMode(fetchMode);
        
        //order by clause
        modifier.addOrderBy(Order.desc("receiptTimestamp"));
        
        //limit returned columns
        modifier.addProjection(Projections.property("receiptTimestamp"));
        
        //where clause
        Conjunction con = Restrictions.conjunction();
        con.add(Restrictions.eq("confirmationStatus", true));
        con.add(Restrictions.eq("phoneNumber", msisdn));
        
        return dbService.getListByCriteria(Timestamp.class, modifier, con);
    }

    public BioData translateToProxy(Map<String, Object> params, BioData bd) {
        BioData b = (bd == null) ? (new BioData()) : bd;

        Set<String> keys = params.keySet();
        ProxyTranslator tran = new ProxyTranslator();
        for (String k : keys) {
            Object obj = params.get(k);

            if (obj instanceof BasicData) {
                logger.debug("found basic data");
                BasicData basicData = (BasicData) obj;
                b.setBd(tran.toBasicData(basicData));
                b.setUniqueId(basicData.getUserId().getUniqueId());
            }

            if (obj instanceof DynamicData) {
                logger.debug("found dynamic data");
                b.setDynamicData(tran.toProxyDynamicData((DynamicData) obj));
            }

            if (obj instanceof PassportData) {
                logger.debug("found passport data");
                b.setPassportData(tran.toPassport((PassportData) obj));
            }

            if (obj instanceof List) {
                logger.debug("found wsq image");
                List objList = (List) obj;
                logger.debug("**** the size of the wsq images found is : " + objList.size());
                List<WsqImage> wsqList = new ArrayList<>();
                for (Object o : objList) {
                    if (o instanceof WsqImage) {
                        WsqImage w = (WsqImage) o;
                        b.addWsqImage(tran.toWsqImage(w));
                    }
                }
            }

            if (obj instanceof Integer) {
                logger.debug("the msisdn count");
                b.setMsisdnCount((Integer) obj);
            }

            if (obj instanceof SignatureData) {
                logger.debug("found signature data");
                SignatureData o = (SignatureData) obj;
                if (b.getPassportDetail() != null) {
                    b.getPassportDetail().setSignature(tran.toSignatureData(o));
                }
            }
        }

        return b;
    }

    public BioData getBioData(BioData bd) {
        if ((bd.getUniqueId() == null) || bd.getUniqueId().isEmpty()) {
            return null;
        }

        Map<String, Object> params = getUserBiometrics(bd.getUniqueId().trim());
        if ((params == null) || params.isEmpty()) {
            return bd;
        } else {
            //add missing fields to biodata object
            bd = addMissingFields(bd, params);

            //remove dynamic data and passport detail objects
            params.remove("DYNAMICDATA");
            params.remove("PASSPORTDETAIL");
        }

        return translateToProxy(params, bd);
    }

    public boolean recordExist(String uniqueId) {
        boolean recordExist = false;
        List<UserId> userIds = getDbService().getListByCriteria(UserId.class, Restrictions.eq("uniqueId", uniqueId.trim()).ignoreCase());
        if ((userIds != null) && (userIds.size() > 0)) {
            recordExist = true;
        }

        return recordExist;
    }

    public void loadMiscData(BioData bd) {
        String nationality = getNationalityProps().getProperty(bd.getDynamicData().getDda19().trim(), "");
        bd.getDynamicData().setDda19(nationality);

        if (bd.getDynamicData().getDa8() != null) {
            String stateOfOrigin = getStateProps().getProperty(bd.getDynamicData().getDa8().trim(), "");
            bd.getDynamicData().setDa8(stateOfOrigin);
        }

        if (bd.getDynamicData().getDda5() != null) {
            String stateOfResidence = getStateProps().getProperty(bd.getDynamicData().getDda5().trim(), "");
            bd.getDynamicData().setDda5(stateOfResidence);
        }

        if (bd.getDynamicData().getDda16() != null) {
            String companyState = getStateProps().getProperty(bd.getDynamicData().getDda16().trim(), "");
            bd.getDynamicData().setDda16(companyState);
        }
    }

    public Map<String, Object> getUserBiometrics(String uniqueId) {
        Map<String, Object> out = new HashMap<>();
        Session session = null;
        StopWatch sw = new StopWatch(true);
        try {
            session = getDbService().getSessionService().getManagedSession();
            logger.debug("Registration unique ID: " + uniqueId);
            //basic data
            QueryParameter basicParameter = new QueryParameter();
            basicParameter.setName("uniqueId");
            basicParameter.setValue(uniqueId);
            String query = "select b.id from basic_data b, user_id u where b.user_id_fk = u.id and u.unique_id = :uniqueId";
            List<BigDecimal> ids = getDbService().getBySQL(BigDecimal.class, query, null, basicParameter);
            if ((ids == null) || ids.isEmpty()) {
                logger.debug("*** The returned basic data is null or wrong type");
                return null;
            }
            long bid = ids.get(0).longValue();
            logger.debug("Basic data id retrieved for " + uniqueId + ": " + bid);
            logger.debug("Basic Data: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
            //passport
            Criteria passportCriteria = session.createCriteria(PassportData.class);
            passportCriteria.setProjection(Projections.projectionList()
                    .add(Projections.property("passportData").as("passportData"))
                    .add(Projections.property("faceCount").as("faceCount")));
            passportCriteria.setFetchMode(BASIC_DATA, FetchMode.LAZY);
            passportCriteria.add(Restrictions.eq(BASIC_DATA_ID, bid));
            PassportData passportData = (PassportData) passportCriteria.setResultTransformer(Transformers.aliasToBean(PassportData.class)).uniqueResult();
            if (passportData != null) {
                logger.debug("passportData: " + passportData.getPassportData());
                out.put("PASSPORTDATA", passportData);
            }
            
            logger.debug("Passport Data: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
            //fingerprints
            Criteria wsqCrit = session.createCriteria(WsqImage.class);
            wsqCrit.setFetchMode(BASIC_DATA, FetchMode.LAZY);
            wsqCrit.add(Restrictions.eq(BASIC_DATA_ID, bid));
            out.put("WSQ", wsqCrit.list());
            logger.debug("Fingerprint Data: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
            //foreigners passport
            Criteria signCrit = session.createCriteria(SignatureData.class);
            signCrit.add(Restrictions.eq(BASIC_DATA_ID, bid));
            signCrit.setFetchMode(BASIC_DATA, FetchMode.LAZY);
            Object sigObj = signCrit.uniqueResult();
            if (sigObj != null) {
                SignatureData sd = (SignatureData) sigObj;
                out.put("SIGNATUREDATA", sd);
                logger.debug("Signature Data: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
                //fetch passport detail
                Criteria pdlCrit = session.createCriteria(PassportDetail.class);
                pdlCrit.add(Restrictions.eq("signature.id", sd.getId()));
                pdlCrit.setFetchMode("signature", FetchMode.LAZY);
                Object pdlObj = pdlCrit.uniqueResult();
                if (pdlObj != null) {
                    out.put("PASSPORTDETAIL", (PassportDetail) pdlObj);
                }
                logger.debug("Passport Detail: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
            }
            //fields not returned by agility - configurable
            Criteria ddCrit = session.createCriteria(DynamicData.class);
            ddCrit.setFetchMode(BASIC_DATA, FetchMode.LAZY);
            ddCrit.add(Restrictions.eq(BASIC_DATA_ID, bid));
            Object ddObj = ddCrit.uniqueResult();
            if (ddObj != null) {
                out.put("DYNAMICDATA", (DynamicData) ddObj);
            }
            logger.debug("Dynamic Data: [" + sw.currentElapsedTime() + " " + TIME_UNIT + "]");
            sw.stop();
        } catch (HibernateException ex) {
            logger.error("Exception in retrieving subscriber biometrics from kyc db: ", ex);
        } finally {
            if (session != null) {
                getDbService().getSessionService().closeSession(session);
            }
        }
        return out;
    }

    private BioData addMissingFields(BioData bd, Map<String, Object> out) {
        DynamicData dd = (DynamicData) out.get("DYNAMICDATA");
        if (dd != null && bd.getDynamicData() != null) {
            bd.getDynamicData().setDda2(String.valueOf(dd.getDda2()));
            if ((bd.getDynamicData().getDda11() != null) && (!bd.getDynamicData().getDda11().isEmpty())
                    && !(bd.getDynamicData().getDda11().equalsIgnoreCase("Individual"))) {
                bd.getDynamicData().setDda16(dd.getDda16()); //company address state
                bd.getDynamicData().setDda17(dd.getDda17()); // company address lga
                bd.getDynamicData().setDda18(dd.getDda18()); // company address postal code
            }
        }

        PassportDetail pd = (PassportDetail) out.get("PASSPORTDETAIL");
        if (pd != null) {
            bd.getPassportDetail().setResidencyStatus(pd.getResidencyStatus());
        }

        return bd;
    }
}