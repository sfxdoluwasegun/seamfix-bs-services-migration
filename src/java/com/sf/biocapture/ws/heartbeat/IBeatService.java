package com.sf.biocapture.ws.heartbeat;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface IBeatService {

	@GET
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response getBeat(@QueryParam("beat") String beat);

	@POST
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response postBeat(@FormParam("beat") String beat);

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.TEXT_HTML})
	public Response postBeat(@Context HttpHeaders headers, ClientBeat beat);

}
