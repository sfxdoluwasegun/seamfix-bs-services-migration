package com.sf.biocapture.ws.resync;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface IResyncStatus {
	
	@GET
	@Path("/{ref}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getStatus(@PathParam("ref") String ref);

}
