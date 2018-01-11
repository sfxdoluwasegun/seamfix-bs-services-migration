package com.sf.biocapture.sim.churn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.entity.ChurnDetail;
import com.sf.biocapture.entity.ChurnMsisdn;

@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/ChurnQueue")})
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ChurnListener extends BsClazz implements MessageListener{
	
	@Inject
	private ChurnDS cds;
	
        @SuppressWarnings("PMD")
	@Override
	public void onMessage(Message msg) {
		ObjectMessage omsg = (ObjectMessage) msg;
		try {
			ChurnMessage cm = (ChurnMessage) omsg.getObject();
			churn(cm);
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
		
	}
	
	/**
	 * Check if a number has been previously churned
	 * Check if this record has been previously processed
	 * @param msisdn
	 */
	private void churn(ChurnMessage cm){
		
		List<ChurnMsisdn> updateList = new ArrayList<ChurnMsisdn>();
		List<ChurnMsisdn> createList = new ArrayList<ChurnMsisdn>();
		
		List<String> msisdns = cm.getList();
		for(String msisdn: msisdns){
			processMsisdn(msisdn, cm.getRefNumber(), updateList, createList);
		}
		if(!updateList.isEmpty()){
			cds.updateBulk(updateList);
			logger.debug("Churn List Updated: " + updateList.size());
		}
		if(!createList.isEmpty()){
			cds.saveBulk(createList);
			logger.debug("Churn List Created: " + createList.size());
		}
	}
	
	private void processMsisdn(String msisdn, String ref, List<ChurnMsisdn> updateList, List<ChurnMsisdn> createList){
		ChurnMsisdn record = cds.getRecord(msisdn);
		if(record == null){// new churn
			record = new ChurnMsisdn();
			record.setMsisdn(msisdn);
			ChurnDetail cd = new ChurnDetail();
			cd.setRefNumber(ref);
			cd.setChurnDate(new Date());
			record.addChurnDetail(cd);
			createList.add(record);
		}else if(!hasProcessed(record, ref)){
			ChurnDetail cd = new ChurnDetail();
			cd.setRefNumber(ref);
			cd.setChurnDate(new Date());
			record.addChurnDetail(cd);
			updateList.add(record);
		}
		
	}
	
	private boolean hasProcessed(ChurnMsisdn msisdn, String ref){
		Set<ChurnDetail> churnDetails = msisdn.getChurnDetails();
		for(ChurnDetail cd: churnDetails){
			if(ref.equalsIgnoreCase(cd.getRefNumber())){
				return true;
			}
		}
		return false;
	}

}
