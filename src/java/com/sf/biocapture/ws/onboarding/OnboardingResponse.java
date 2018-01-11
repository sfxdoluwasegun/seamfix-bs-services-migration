package com.sf.biocapture.ws.onboarding;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 * 
 * @author Nnanna
 *
 */
public class OnboardingResponse extends ResponseData {
	
	public OnboardingResponse(){}
	
	public OnboardingResponse(ResponseCodeEnum code, String description){
		setCode(code);
		setDescription(description);
	}

}
