package com.sf.biocapture.ws.kitapproval;

import javax.ejb.Stateless;
//import javax.ejb.TransactionAttribute;
//import javax.ejb.TransactionAttributeType;

import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.common.Base64Coder;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.License;
import nw.orm.core.exception.NwormQueryException;

@Stateless
//@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ApprovalDS extends DataService{
	
	public ApprovalResponse getKitApprovalStatus(String tag, String macAddress){
		logger.debug("Checking kit approval status for " + tag + "... MAC Address: " + macAddress);
		ApprovalResponse response = new ApprovalResponse();
		if(tag == null || macAddress == null){
			response.setMessage("Tag or MAC Address missing in request");
			return response;
		}
		//retrieve the kit status from here
		License license = null;
		try{
			license = dbService.getByCriteria(License.class, Restrictions.eq("macAddress", macAddress));
		}catch(NwormQueryException e){
			e.printStackTrace();
			response.setMessage("Kit info not retrieved.");
			return response;
		}
		String resp = null;
		if(license != null && license.getApproved() != null && license.getApproved()){
			logger.debug(tag + " has been approved");
			resp = tag + macAddress + "true";
		}else{
			logger.debug(tag + " is not found or has not been approved");
			resp = tag + macAddress + "false";
		}
		response.setStatus(0);
		response.setMessage(new Base64Coder().encodeString(resp));
		return response;
	}
}
