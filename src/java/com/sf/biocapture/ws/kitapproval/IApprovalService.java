package com.sf.biocapture.ws.kitapproval;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public interface IApprovalService {
	@POST
	@Path("/check")
	@Produces(MediaType.APPLICATION_JSON)
	public ApprovalResponse approve(@FormParam("tag") String tag, @FormParam("macAddress") String macAddress);
	
	@GET
	@Path("/testcheck")
	@Produces(MediaType.APPLICATION_JSON)
	public ApprovalResponse testApprove(@QueryParam("tag") String tag, @QueryParam("macAddress") String macAddress);
}
