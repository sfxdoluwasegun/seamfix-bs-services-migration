package com.sf.biocapture.ws.onboarding;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import javax.inject.Inject;
import javax.ws.rs.Path;

import nw.commons.StopWatch;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.ws.HeaderIdentifier;
import com.sf.biocapture.ws.ResponseCodeEnum;
import javax.ws.rs.core.HttpHeaders;
/**
 * 
 * @author Nnanna
 *
 */
@Path("/onboard")
public class OnboardingService extends BsClazz implements IOnboardingService {
	
	@Inject
	private AccessDS accessDS;
	
	@Inject
	private OnboardingDS onboardDS;
	
	@Inject
	private BioCache cache;
        
        @Inject
        private IntrusionAnalyzer analyzer;

	@Override
	public OnboardingResponse onboardAgent(HttpHeaders headers,AgentData data) {
		if(data == null || (data != null && (data.getTag() == null || data.getEmailAddress() == null || data.getMac() == null))){
			logger.debug("Onboarding an agent with incomplete data: " + data.getEmailAddress());
			return new OnboardingResponse(ResponseCodeEnum.INVALID_INPUT, "Request contains missing fields");
		}
                
		HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);
		String resp = accessDS.checkBlacklistStatus(data.getTag(), data.getMac(),headerIdentifier.getRefDeviceId());
		if(resp != null && resp.equalsIgnoreCase("Y")){
			logger.debug("Attempting to onboard a blacklisted tag: " + data.getTag());
			return new OnboardingResponse(ResponseCodeEnum.BLACKLISTED_KIT, "Kit is blacklisted");
		}
		
		StopWatch sw = new StopWatch(true);
		OnboardingResponse or = onboardDS.saveAgentData(data);
		logger.debug("Time taken to process agent onboarding info: " + sw.elapsedTime());
		sw.stop();
		
		return or;
	}

	@Override
	public OnboardingResponse onboardStatus(String agentEmail) {
		OnboardingResponse or = null;
		if(agentEmail == null || agentEmail.isEmpty()){
			return new OnboardingResponse(ResponseCodeEnum.INVALID_INPUT, "Agent's email is missing from request");
		}
		
		logger.debug("Checking onboarding status for: " + agentEmail);
		String status = cache.getItem(OnboardingDS.ONBOARDING_KEY_PREFIX + agentEmail.replaceAll(" ", ""), String.class);
		if(status != null){
			or = new OnboardingResponse(ResponseCodeEnum.SUCCESS, "Agent has been onboarded");
		}else{
			or = onboardDS.getAgentOnboardingStatus(agentEmail);
		}
		
		return or;
	}

}
