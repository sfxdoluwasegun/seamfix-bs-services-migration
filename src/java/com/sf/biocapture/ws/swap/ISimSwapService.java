package com.sf.biocapture.ws.swap;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Nnanna
 *
 */
public interface ISimSwapService {
    
        public static final String MSISDN = "msisdn";
	
	@POST
	@Path("/load")
	@Produces({MediaType.APPLICATION_JSON})
	public Response load(@FormParam(MSISDN) String msisdn);
	
	@POST
	@Path("/fetch")
	@Produces({MediaType.APPLICATION_JSON})
	public Response fetch(@FormParam(MSISDN) String msisdn);
	
	@POST
	@Path("/lrc")
	@Produces({MediaType.APPLICATION_JSON})
	public Response lastRecharge(@FormParam(MSISDN) String msisdn);
	
	@POST
    @Path("/simchange")
    @Produces({MediaType.APPLICATION_JSON})
    public SimSwapResponse doSwap(@Context HttpHeaders headers, SimSwapRequest req);
}
