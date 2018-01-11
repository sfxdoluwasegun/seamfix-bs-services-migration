package com.sf.biocapture.ws.activation;

import com.sf.biocapture.ws.ResponseData;
import com.sf.biocapture.ws.vtu.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Clemet
 * @since 11 Jan 2018, 14:15:13
 */
public interface IActivationService {
	
	@POST
	@Path("/activation/{uniqueId}/{phoneNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseData activation(@PathParam("uniqueId") String uniqueId, @PathParam("phoneNumber") String phoneNumber);
	
}
