package com.sf.biocapture.ws.kitapproval;

//import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.sf.biocapture.app.BsClazz;

@Path("/approval")
public class ApprovalService extends BsClazz implements IApprovalService {
	@Inject
	private ApprovalDS approvalDS;
	
	@Override
//	@RolesAllowed({"lite"})
	public ApprovalResponse approve(String tag, String macAddress) {
		logger.debug("Tag: "+ tag + "; Mac Address: " + macAddress); 
		ApprovalResponse response = approvalDS.getKitApprovalStatus(tag, macAddress);
		return response;
	}

	@Override
	public ApprovalResponse testApprove(String tag, String macAddress) {
		logger.debug("Tag: "+ tag + "; Mac Address: " + macAddress); 
		ApprovalResponse response = approvalDS.getKitApprovalStatus(tag, macAddress);
		return response;
	}

}
