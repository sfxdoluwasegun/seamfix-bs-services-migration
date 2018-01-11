package com.sf.biocapture.ws.astatus;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface IActivationStatus {
    
        public static final String MSISDN = "msisdn";

	@GET
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getStatus(@QueryParam(MSISDN) String msisdn);

	@GET
	@Path("/agl-status")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAgilityStatus(@QueryParam("key") String msisdn, @QueryParam("uid") String uid,@QueryParam("usemsisdn") boolean useMsisdn);

        @POST
	@Path("/check")
	@Produces({MediaType.APPLICATION_JSON})
	public Response checkStatus(@FormParam(MSISDN) String msisdn);

        
	@POST
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response postStatus(@FormParam(MSISDN) String msisdn);

	@GET
	@Path("/set")
	public Response setStatus(@QueryParam(MSISDN) String msisdn, @QueryParam("date") Long date, @QueryParam("status") String status);

}