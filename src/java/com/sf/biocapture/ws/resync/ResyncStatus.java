package com.sf.biocapture.ws.resync;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.sf.biocapture.app.BsClazz;

import nw.commons.StopWatch;

@Path("/resync")
public class ResyncStatus extends BsClazz implements IResyncStatus{

	@Inject
	private ResyncDS ssm;

	@Override
	@RolesAllowed({"smartclient", "lite"})
	public Response getStatus(String ref) {
		StopWatch sw = new StopWatch(true);
		ResyncResponse response = ssm.getResyncList(ref);
		logger.info("Request Processing time: " +  ref + " response : " + response + " [" +sw.elapsedTime() + " ms]");
		return Response.status(200).entity(response).build();
	}

}
