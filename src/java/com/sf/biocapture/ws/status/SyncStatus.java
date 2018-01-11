package com.sf.biocapture.ws.status;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

import nw.commons.StopWatch;

@Path("/uidstatus")
public class SyncStatus extends BsClazz implements ISyncStatus {

	@Inject
	private SyncDS ssm;

	@Override
	@RolesAllowed({"smartclient", "lite"})
	public Response getStatus(String uid, String ldate) {
		String response = checkUidOrDate(uid, ldate);
		return Response.status(200).entity(response).build();
	}

	@Override
	public Response postStatus(String uid, String ldate) {
		return getStatus(uid, ldate);
	}

	@Override
	public Response checkStatus(String uid, String ldate) {
		String response = checkUidOrDate(uid, ldate);
		ResponseData rd = new ResponseData();
		rd.setCode(ResponseCodeEnum.SUCCESS);
		rd.setDescription(response);
		return Response.status(200).entity(rd).build();
	}
	
	private String checkUidOrDate(String uid, String ldate){
		logger.debug("suid: " + uid + "; ldate: " + ldate);
		StopWatch sw = new StopWatch(true);
		logger.debug("Inside getStatus of uidstatus : The sart time for uidstatus is : " + sw.currentElapsedTime());
		uid = uid == null || "".equals(uid) ? "x": uid;
		String response = "false";
		if(ldate == null){
			response = ssm.checkStatus(uid);
		}else{
			logger.debug("Inside getStatus of uidstatus : The time before checking uistatus is : " + sw.elapsedTime());
			response = ssm.checkState(ldate);                        
			logger.debug("Request Processing time: " + ldate + " [" +sw.elapsedTime() + " ms]");
		}
		logger.debug("Uid status for " + uid + ": " + response);
		return response;
	}
}