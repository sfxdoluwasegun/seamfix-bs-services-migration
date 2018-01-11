package com.sf.biocapture.ws.onboarding;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
/**
 * 
 * @author Nnanna
 *
 */
public interface IOnboardingService {
	
	@POST
	@Path("/sync")
	@Produces(MediaType.APPLICATION_JSON)
	public OnboardingResponse onboardAgent(@Context HttpHeaders headers, AgentData data);
	
	@POST
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public OnboardingResponse onboardStatus(@FormParam("email") String agentEmail);

}
