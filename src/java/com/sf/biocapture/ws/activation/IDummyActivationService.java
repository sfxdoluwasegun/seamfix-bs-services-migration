package com.sf.biocapture.ws.activation;

import com.sf.biocapture.ws.ResponseData;
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
public interface IDummyActivationService {
	
	@POST
	@Path("/{usecase}/{uniqueId}/{subscriberInfo}")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseData activation(@PathParam ("usecase")String usecase,@PathParam("uniqueId") String uniqueId, @PathParam("subscriberInfo") String subscriberInfo);
	
}
