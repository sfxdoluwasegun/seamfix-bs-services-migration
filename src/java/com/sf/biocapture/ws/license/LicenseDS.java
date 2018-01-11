
package com.sf.biocapture.ws.license;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.FMLicenseRequest;
import com.sf.biocapture.entity.enums.KycManagerRole;
import com.sf.biocapture.entity.security.KMUser;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import nw.orm.core.exception.NwormQueryException;

import nw.orm.core.query.QueryAlias;
import nw.orm.core.query.QueryFetchMode;
import nw.orm.core.query.QueryModifier;
import org.hibernate.Criteria;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author @wizzyclems
 */
@Stateless
public class LicenseDS extends DataService {
    
    public boolean createLicenseRequest(String macAddress, String tagId, String deviceId, String agentName, String agentEmail){
        if (agentEmail == null) {
            logger.debug("agent email address was not provided.");
            return false;
        }
        QueryModifier qm = new QueryModifier(KMUser.class);
        
        QueryFetchMode assignedZone = new QueryFetchMode();
        assignedZone.setAlias("assignedZone");
        assignedZone.setFetchMode(FetchMode.LAZY);
        qm.addFetchMode(assignedZone);
        
        QueryFetchMode roles = new QueryFetchMode();
        roles.setAlias("roles");
        roles.setFetchMode(FetchMode.LAZY);
        qm.addFetchMode(roles);
        
        QueryFetchMode assignedDealer = new QueryFetchMode();
        assignedDealer.setAlias("assignedDealer");
        assignedDealer.setFetchMode(FetchMode.LAZY);
        qm.addFetchMode(assignedDealer);
                
    	KMUser user = dbService.getByCriteria(KMUser.class, qm, Restrictions.eq("emailAddress", agentEmail.toLowerCase()));
        
//        Session session = null;
//        KMUser user = null;
//        try {
//            session = dbService.getSessionService().getManagedSession();
//            String query = "select u.pk as pk, u.surname as surname, u.first_name as firstName from km_user u where u.email_address = :emailAddress";
//            SQLQuery sqlq = session.createSQLQuery(query);
//            sqlq.setParameter("emailAddress", agentEmail.toLowerCase());
//            sqlq.addScalar("pk", new LongType());
//            sqlq.addScalar("surname", new StringType());
//            sqlq.addScalar("firstName", new StringType());
//            user = (KMUser) sqlq.setResultTransformer(Transformers.aliasToBean(KMUser.class)).uniqueResult();
//        } catch (HibernateException | NwormQueryException e) {
//            logger.error("", e);
//        } finally {
//            dbService.getSessionService().closeSession(session);
//        }
//        logger.debug(user.getPk() + " " + user.getFirstName());
    	if(user == null){
    		logger.debug("Unknown user requesting for license...");
    		return false;
    	}
        
        EnrollmentRef ref = null;
        if (deviceId != null && !deviceId.isEmpty()){
            ref = dbService.getByCriteria(EnrollmentRef.class, Restrictions.eq("deviceId", deviceId.toUpperCase()));
            
        }
        FMLicenseRequest fmr = new FMLicenseRequest();
        fmr.setAgentName(user.getSurname() + " " + user.getFirstName());
        fmr.setEmailAddress(agentEmail);
        fmr.setKitTag(tagId);
        fmr.setMacAddress(macAddress);
        fmr.setRequestDate(new Timestamp(new Date().getTime()));
        fmr.setRequestedBy(user);
        fmr.setEnrollmentRef(ref);
        
        Serializable s = getDbService().create(fmr);
        
        return (s != null);
    }
    
    public String[] getAllAdminEmails(){
        QueryModifier m = new QueryModifier(KMUser.class);
        m.addAlias(new QueryAlias("roles","r"));
        QueryFetchMode qf = new QueryFetchMode();
        qf.setAlias("r");
        qf.setFetchMode(FetchMode.JOIN);
        m.addFetchMode( qf );
        m.addProjection(Projections.property("emailAddress"));
        
        String[] roles = new String[]{KycManagerRole.ADMIN.name()} ;
        List<String> emails = getDbService().getListByCriteria(String.class, m, Restrictions.in("r.role", roles));
        if( (emails != null) && !emails.isEmpty() ){
            logger.debug("*** The admin email count is : " + emails.size());
            logger.debug("The list of admin emails returned is : " + emails.toArray( new String[emails.size()] ) );
            return emails.toArray( new String[emails.size()] );
        }
        
        return null;
    }
    
    public String getKitLicenseStatus(String macAddress, String deviceId){
	logger.debug("Checking for the validity status of the specified mac address.");
        // this is a combination of the approvalstatus and the license code
        String response = "";
        
        if( macAddress == null){
            return response;
        }
        
        Session session = null;
        FMLicenseRequest licenceRequestTransformer = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            Criteria criteria = session.createCriteria(FMLicenseRequest.class, "fm");
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.eq("fm.macAddress", macAddress).ignoreCase());
            criteria.createAlias("fm.enrollmentRef", "ref");
            if (deviceId != null && !deviceId.isEmpty()) {
                disjunction.add(Restrictions.eq("ref.deviceId", deviceId.toUpperCase()));
            }
            criteria.add(disjunction);                
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property("fm.approved").as("approved"))
                    .add(Projections.property("fm.licenseHash").as("licenseHash")));
            criteria.addOrder(Order.desc("fm.requestDate"));
            criteria.setFetchMode("fm.approvedBy", FetchMode.LAZY);
            criteria.setFetchMode("fm.requestedBy", FetchMode.LAZY);
            criteria.setMaxResults(1);
            licenceRequestTransformer = (FMLicenseRequest) criteria.setResultTransformer(Transformers.aliasToBean(FMLicenseRequest.class)).uniqueResult();
        } catch (NwormQueryException | HibernateException e) {
            logger.error("", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
 
        if (licenceRequestTransformer == null) {
            return "";
        }

        if( (licenceRequestTransformer.getApproved() == null) ){
                return null;
        }
        
        
       response = licenceRequestTransformer.getApproved() + ":" + licenceRequestTransformer.getLicenseHash();
        
       return response;
    }

    public boolean allowCreateLicense(String macAddress, String deviceId) {
        String msg = getKitLicenseStatus(macAddress, deviceId);
        if( msg == null ){
//          "The license approval for this kit is pending."
            return false;
        }
        
        if( msg.isEmpty() ){
//          "There is no license for this kit. Please request for license."
            return true;
        }
        
        String[] resp = msg.split(":");
        return resp[0].equalsIgnoreCase("false");
    }
}
