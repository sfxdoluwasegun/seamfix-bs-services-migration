package com.sf.biocapture.ws.status;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ISyncStatus {
	
	@GET
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getStatus(@QueryParam("suid") String uid, @QueryParam("lDate") String ldate);
	
	@POST
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response postStatus( @FormParam("suid") String uid, @FormParam("lDate") String ldate);
	
	/**
	 * convenience end point for rest-handler
	 * @param uid
	 * @param ldate
	 * @return
	 */
	@POST
	@Path("/check")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkStatus( @FormParam("suid") String uid, @FormParam("lDate") String ldate);

}
