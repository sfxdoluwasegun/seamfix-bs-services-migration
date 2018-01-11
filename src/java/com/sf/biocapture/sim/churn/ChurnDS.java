package com.sf.biocapture.sim.churn;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nw.orm.core.session.HibernateSessionService;

import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.ChurnDetail;
import com.sf.biocapture.entity.ChurnMsisdn;
import org.hibernate.HibernateException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ChurnDS extends DataService {
	
	public ChurnMsisdn getRecord(String msisdn){
		return dbService.getByCriteria(ChurnMsisdn.class, Restrictions.eq("msisdn", msisdn));
	}
	
	public void saveBulk(List<ChurnMsisdn> bulkMsisdns){
                if (bulkMsisdns == null) {
                    return;
                }
		HibernateSessionService sxf = dbService.getSessionService();
		StatelessSession sxn = sxf.getStatelessSession();
		try {
			for(ChurnMsisdn msisdn: bulkMsisdns){
				Set<ChurnDetail> details = msisdn.getChurnDetails();
				msisdn.setPk((Long)sxn.insert(msisdn));
				for(ChurnDetail detail: details){
					if(detail.getPk() == null){
						detail.setMsisdn(msisdn);
						sxn.insert(detail);
					}
				}
			}
			if(sxf.useTransactions()){
				sxn.getTransaction().commit();
			}
		} catch (HibernateException e) {
			logger.error("Exception ", e);
		}
		sxn.close();
	}
	
	public void updateBulk(List<ChurnMsisdn> bulkMsisdns){
                if (bulkMsisdns == null) {
                    return;
                }
		HibernateSessionService sxf = dbService.getSessionService();
		StatelessSession sxn = sxf.getStatelessSession();
		try {
			for(ChurnMsisdn msisdn: bulkMsisdns){
				Set<ChurnDetail> details = msisdn.getChurnDetails();
				for(ChurnDetail detail: details){
					if(detail.getPk() == null){
						detail.setMsisdn(msisdn);
						sxn.insert(detail);
					}
				}
			}
			if(sxf.useTransactions()){
				sxn.getTransaction().commit();
			}
			
		} catch (HibernateException e) {
			logger.error("Exception ", e);
		} 
                sxn.close();
	}

}
