package com.sf.biocapture.postactivation;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nw.orm.core.query.QueryAlias;
import nw.orm.core.query.QueryModifier;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.SmsActivationRequest;
import com.sf.biocapture.postactivation.sms.SmsReq;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TabsDS extends DataService {
	
	private final String unsendStatus = "ACTIVATED_WITH_SMS_UNSENT";
	
	/**
	 * Retrieves the list of records (a batch of 500) yet to receive sms alerts after activation
	 * @return
	 */
	public List<SmsReq> listUnnotifiedRecords(int pageIndex){
		QueryModifier qm = new QueryModifier(SmsActivationRequest.class);
		qm.addAlias(new QueryAlias("phoneNumberStatus", "pns"));
		qm.addProjection(Projections.property("pns.id").as("id"));
		qm.addProjection(Projections.property("phoneNumber").as("phoneNumber"));
		qm.setPaginated(pageIndex, appProps.getInt("unactivated.page", 500));
		qm.transformResult(true);
		return dbService.getListByCriteria(SmsReq.class, qm, Restrictions.eq("pns.status", unsendStatus), Restrictions.eq("isInitiator", true));
	}
	
	public Long getUnnotifiedCount(){
		QueryModifier qm = new QueryModifier(SmsActivationRequest.class);
		qm.addAlias(new QueryAlias("phoneNumberStatus", "pns"));
		qm.addProjection(Projections.rowCount());
		qm.transformResult(false);
		return dbService.getByCriteria(Long.class, qm, Restrictions.eq("pns.status", unsendStatus), Restrictions.eq("isInitiator", true));
	}
	
	public void updatePhoneNumberStatus(){
		
	}
	
	public void updateSmsActivationRequest(){
		
	}

	public void createTabsLog(String phoneNumber, Long id, String string,
			String string2, String stubEndpoint, String resultString,
			String processId) {
		// TODO Auto-generated method stub
		
	}

}
