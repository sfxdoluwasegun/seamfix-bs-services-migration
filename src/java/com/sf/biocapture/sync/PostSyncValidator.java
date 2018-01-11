package com.sf.biocapture.sync;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.app.BsClazz;

import nw.orm.core.query.QueryParameter;

@Stateless
public class PostSyncValidator extends BsClazz{
	
	@Inject
	private PostSyncDS syncDS;
	
	public void updateNullBiometrics(){
		logger.debug("Retrieving records with null activation timestamp");
		List<PostSyncData> ps = syncDS.getSyncsWithNullActivationTimestamp();
		logger.debug("Null records found: " + ps.size());
		for(PostSyncData p: ps){
			String uql = "UPDATE SmsActivationRequest s SET s.activationTimestamp = :activatedTimestamp WHERE s.uniqueId = :uid AND s.phoneNumber = :msisdn";
			syncDS.getDbService().executeHQLUpdate(uql, QueryParameter.create("uid", p.getUniqueId()), QueryParameter.create("activatedTimestamp", p.getSyncTime()), QueryParameter.create("msisdn", p.getMsisdn()));
		}
		logger.debug("Entries updated: " + ps.size());
	}

}
