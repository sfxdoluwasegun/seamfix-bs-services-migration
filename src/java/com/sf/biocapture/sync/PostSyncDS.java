package com.sf.biocapture.sync;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.sf.biocapture.ds.DataService;

import nw.orm.core.query.QueryParameter;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PostSyncDS extends DataService {
	
	public List<PostSyncData> getSyncsWithNullActivationTimestamp(){
		Calendar i = Calendar.getInstance();
		int month = i.get(Calendar.MONTH);
		String hql = "SELECT s.uniqueId as uniqueId, s.phoneNumber as msisdn, u.createDate as syncTime FROM SmsActivationRequest s, UserId u WHERE u.uniqueId = s.uniqueId AND s.activationTimestamp is null AND s.receiptTimestamp > :startMonth";
		return dbService.getListByHQL(PostSyncData.class, hql, QueryParameter.create("startMonth", getMonth(month)));
		
	}

}
