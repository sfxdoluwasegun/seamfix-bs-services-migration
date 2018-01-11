package com.sf.biocapture.ds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.agl.integration.AgilityResponse;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.audit.AgilityIntegrationLog;
import com.sf.biocapture.entity.device.DeviceTagRequest;
import com.sf.biocapture.entity.security.KMRole;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ilog.ILog;
import com.sf.biocapture.ilog.agl.AgilityLog;
import com.sf.biocapture.krm.ZonalSync;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;

import nw.orm.core.query.QueryModifier;
import nw.orm.core.query.QueryParameter;
import nw.orm.core.query.SQLModifier;
import nw.orm.core.service.Nworm;


@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class DataService extends BsClazz {

	private String delimiter = "#s#x#";
	protected SimpleDateFormat defaultSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	protected Nworm dbService;

	@PostConstruct
	public void init(){
		dbService = Nworm.getInstance();
	}

	public Nworm getDbService(){
		return this.dbService;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Long countOf(Class<?> clazz, Criterion ...criterions){// jimmy solanke
		QueryModifier qm = new QueryModifier(clazz);
		qm.addProjection(Projections.rowCount());
		return dbService.getByCriteria(Long.class, qm, criterions);
	}

	public Long countRefs() {
		QueryModifier qm = new QueryModifier(EnrollmentRef.class);
		qm.addProjection(Projections.rowCount());

		return dbService.getByCriteria(Long.class, qm);
	}

	public List<EnrollmentRef> listEnrollmentRefs(Integer index) {
		QueryModifier qm = new QueryModifier(EnrollmentRef.class);
		qm.setPaginated(index, 300);
		qm.transformResult(false);

		return dbService.getListByCriteria(EnrollmentRef.class, qm);
	}

	public EnrollmentRef getEnrollmentRef(String ref) {
		return dbService.getByCriteria(EnrollmentRef.class, Restrictions.eq("code", ref));
	}

	public EnrollmentRef getEnrollmentRefByDeviceId (String deviceId) {
		if (deviceId == null || deviceId.isEmpty()) {
			logger.debug("Provided Device Id is null");
			return null;
		}
		return dbService.getByCriteria(EnrollmentRef.class, Restrictions.eq("deviceId", deviceId.toUpperCase()));
	}
	
	public String getDeploymentStateName (Long enrollmentRefId) {
		Session session = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			String query = "select s.name from node_assignment na"
					+ " join node n on n.id = na.node_fk "
					+ " join lga l on l.id = na.lga_fk "
					+ " join state s on s.id = l.state_fk "
					+ " where n.enrollment_ref = :enrollmentRefId";
			SQLQuery sqlq = session.createSQLQuery(query);
			sqlq.setMaxResults(1);
			sqlq.setParameter("enrollmentRefId", enrollmentRefId);
			return (String) sqlq.uniqueResult();
		} catch (HibernateException he) {
			logger.error("Unable to retrieve kit state of deployment", he);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return null;
	}

	public EnrollmentRef getEnrollmentRefByMacAddressOrDeviceId(String macAddress, String deviceId) {
		Conjunction conjunction = Restrictions.conjunction();
		if (deviceId != null && !deviceId.isEmpty()) {
			conjunction.add(Restrictions.eq("deviceId", deviceId.toUpperCase()));
		} else if (macAddress != null && !macAddress.isEmpty()) {
			conjunction.add(Restrictions.eq("macAddress", macAddress).ignoreCase());
		} else {
			return null;
		}
		return dbService.getByCriteria(EnrollmentRef.class, conjunction);
	}

	public EnrollmentRef getEnrollmentRefByMac(String macAddress) {
		if (macAddress != null) {
			macAddress = macAddress.toUpperCase();
		}
		return dbService.getByCriteria(EnrollmentRef.class, Restrictions.eq("macAddress", macAddress));
	}

	public Node getNodeByMac(String macAddress) {
		return dbService.getByCriteria(Node.class, Restrictions.eq("macAddress", macAddress).ignoreCase());
	}

	public Date getDateTime(int hourOfDay){
		Calendar cale = Calendar.getInstance();
		cale.set(cale.get(Calendar.YEAR), cale.get(Calendar.MONTH), cale.get(Calendar.DAY_OF_MONTH), hourOfDay, 0, 0);
		System.out.println(cale.getTime());
		return cale.getTime();
	}

	public Date getDay(int dayOfMonth){
		Calendar cale = Calendar.getInstance();
		cale.set(cale.get(Calendar.YEAR), cale.get(Calendar.MONTH), dayOfMonth, 0, 0, 0);
		System.out.println(cale.getTime());
		return cale.getTime();
	}

	public Date getMonth(int month){
		Calendar cale = Calendar.getInstance();
		cale.set(cale.get(Calendar.YEAR), month, 1, 0, 0, 0);
		System.out.println(cale.getTime());
		return cale.getTime();
	}

	public boolean update(Object obj){
		return dbService.update(obj);
	}

	public KMUser getUser(String email){
		if(StringUtils.isBlank(email)) {
			return null;
		}
		return dbService.getByCriteria(KMUser.class, Restrictions.eq("emailAddress", email.trim().toLowerCase()));
	}
	public Boolean isActiveUser(String email){
		if (email == null || email.isEmpty()) {
			return null;
		}
		QueryModifier modifier = new QueryModifier(KMUser.class);
		modifier.addProjection(Projections.property("active"));
		Boolean result = dbService.getByCriteria(Boolean.class, modifier, Restrictions.eq("emailAddress", email.trim().toLowerCase()));
		return result;
	}

	public KMRole getKmUserRole(String role){
		return dbService.getByCriteria(KMRole.class, Restrictions.eq("role", role));
	}

	public boolean save(Object item){
		return dbService.create(item) != null;
	}

	protected List<ZonalSync> getSyncCount(){

		String sql = "SELECT count(*) as \"syncs\", z.name as \"zone\" FROM SMS_ACTIVATION_REQUEST s, ENROLLMENT_REF e, NODE n, STATE st, ZONE z WHERE s.enrollment_ref = e.code "
				+ "AND e.mac_address = n.mac_address AND n.state_fk = st.id AND st.zone_fk = z.id AND s.receipt_timestamp BETWEEN :start AND :end GROUP BY z.name ";

		//		String hql = "SELECT n.state.zone as zone, count(s) as syncs FROM SmsActivationRequest s, EnrollmentRef e, Node n WHERE s.receiptTimestamp BETWEEN :start AND :end and s.enrollmentRef = e.code and e.macAddress = n.macAddress";
		Date startDate = new Date(new Date().getTime() - 500000);
		Date endDate = new Date();
		logger.debug("Start Date: " + startDate + " End Date: " + endDate);
		List<ZonalSync> lsz = dbService.getBySQL(ZonalSync.class, sql, new SQLModifier(), QueryParameter.create("start", startDate), QueryParameter.create("end", endDate));
		logger.debug("Zonal Info returned: " + lsz.size());
		return lsz;
	}

	protected boolean isEmpty(String fieldVal){
		return fieldVal == null || (fieldVal != null && fieldVal.trim().isEmpty());
	}

	protected ILog logActivity(String requestXml, String responseXml, String code,
			String description, String simSerial, String requestType, String msisdn) {
		AgilityLog agilityLog = new AgilityLog();
		agilityLog.setRequestType(requestType);
		AgilityIntegrationLog log = new AgilityIntegrationLog();
		log.setResponseCode(code);
		log.setResponseDescription(description);
		log.setRequestType(requestType);
		log.setMsisdn(msisdn);
		log.setSimSerial(simSerial);
		agilityLog.setAgilityIntegrationLog(log);
		agilityLog.setRequestPayload(requestXml);
		agilityLog.setResponsePayload(responseXml);
		return agilityLog;
	}

	protected String translateActivationStatus( String activationStatus ){
		//AC- Active, SP-Suspended, IA-Inactive, WP-Work Order Progress, PP- Pending Payments, PA=Pre-Active
		switch(activationStatus.toUpperCase()){
		case "AC" :
			return "ACTIVE";
		case "SP" :
			return "SUSPENDED";
		case "IA" :
			return "INACTIVE";
		case "WP" :
			return "WORK ORDER PROGRESS";
		case "PP" :
			return "PENDING PAYMENTS";
		case "PA" :
			return "PREACTIVE";
		default : 
			return "";                 
		}

	}

	protected AgilityResponse getFailureResponse(){
		AgilityResponse ar = new AgilityResponse();
		ar.setCode("-2");
		ar.setChildMsisdnCount(null);
		ar.setDescription("Unable to connect to remote service");
		ar.setValid(null);                
		return ar;
	}

	public boolean hasDeviceTagRequest (String deviceId) {
		boolean hasRequest = false;
		Session session = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			Criteria criteria = session.createCriteria(DeviceTagRequest.class);
			criteria.add(Restrictions.eq("requestedDeviceId", deviceId.toUpperCase()));
			criteria.setProjection(Projections.rowCount());
			Long count = (Long) criteria.uniqueResult();
			if (count != null && count > 0) {
				hasRequest = true;
			}
		} catch (HibernateException e) {
			logger.error("Exception in checking if device has tag request:", e);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return hasRequest;
	}
	
	public boolean kitExists(String tag, String deviceId){
		boolean exists = false;
		Session session = null;
		try {
			session = dbService.getSessionService().getManagedSession();
			Criteria criteria = session.createCriteria(EnrollmentRef.class);
			Disjunction or = Restrictions.disjunction();
			if(tag != null){
				or.add(Restrictions.eq("code", tag.trim()));
			}
			if(deviceId != null){
				or.add(Restrictions.eq("deviceId", deviceId.trim()));
			}
			criteria.add(or);
			criteria.setProjection(Projections.rowCount());
			Long count = (Long) criteria.uniqueResult();
			if(count != null && count > 0) {
				exists = true;
			}
		} catch (HibernateException e) {
			logger.error("Exception thrown in checking kit existence:", e);
		} finally {
			dbService.getSessionService().closeSession(session);
		}
		return exists;
	}
}