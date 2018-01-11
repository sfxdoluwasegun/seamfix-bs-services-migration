package com.sf.biocapture.ws.vtu;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Nnanna
 * @since 8 Nov 2017, 13:58:13
 */
public interface IVTUService {
	
	@POST
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateVTUNumber(@FormParam("vtu") String vtuNumber, @FormParam("email") String agentEmail);
	
	@POST
	@Path("/vend")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doVending(VTUVendingRequest request);
        
        @POST
	@Path("/vtuRequery")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doRequery(VTUVendingRequest request);
        
        
}
