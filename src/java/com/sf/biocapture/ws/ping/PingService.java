package com.sf.biocapture.ws.ping;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sf.biocapture.app.BsClazz;

@Path("/ping")
public class PingService extends BsClazz{
	
	@GET
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response ping(@Context HttpServletRequest req){
		logger.info("PING from: " + req.getRemoteAddr());
		return Response.status(200).build();
	}
	
	@POST
	@Path("/")
	@Produces({MediaType.TEXT_PLAIN})
	public Response postPing(@Context HttpServletRequest req){
		logger.info("PING from: " + req.getRemoteAddr());
		return Response.status(200).build();
	}
	
}
