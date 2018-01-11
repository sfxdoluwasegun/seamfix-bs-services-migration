package com.sf.biocapture.ws.resync;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nw.orm.core.query.QueryModifier;
import nw.orm.core.query.QueryParameter;

import org.hibernate.criterion.Projections;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.SmsActivationRequest;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ResyncDS extends DataService {
	
	@Inject
	private BioCache cache;

	public ResyncResponse getResyncList(String ref) {
		ResyncResponse item = getCache().getItem("RESYNC-" + ref.trim().replaceAll(" ", ""), ResyncResponse.class);
		return item == null ? new ResyncResponse(0) : item;
	}
	
	/**
	 * 
	 * @param ref enrollment ref
	 * @return list of records without bio-metrics for this tag
	 */
	public List<String> listRecordsWithoutBiometrics(String ref) {
		QueryModifier qm = new QueryModifier(SmsActivationRequest.class);
		qm.addProjection(Projections.property("uniqueId"));
		qm.transformResult(false);
		String hql = "SELECT s.uniqueId as uniqueId FROM SmsActivationRequest s WHERE  s.enrollmentRef = :ref and s.activationTimestamp IS NULL AND NOT EXISTS (SELECT u.uniqueId FROM UserId u WHERE u.uniqueId = s.uniqueId)";
		return dbService.getListByHQL(String.class, hql, QueryParameter.create("ref", ref));
	}
	
	public List<SmsActivationRequest> listSynchedSMSRecords(String ref) {
		QueryModifier qm = new QueryModifier(SmsActivationRequest.class);
		qm.addProjection(Projections.property("uniqueId"));
		qm.transformResult(true);
		String hql = "SELECT s FROM SmsActivationRequest s WHERE  s.enrollmentRef = :ref and s.activationTimestamp IS NULL AND EXISTS (SELECT u.uniqueId FROM UserId u WHERE u.uniqueId = s.uniqueId)";
		return dbService.getListByHQL(SmsActivationRequest.class, hql, QueryParameter.create("ref", ref));
	}

	public BioCache getCache() {
		return cache;
	}

}
