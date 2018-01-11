package com.sf.biocapture.ws.access;

import java.util.List;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.onboarding.AgentFingerprintPojo;

/**
 * 
 * @author Nnanna
 *
 */
public class FingerLoginResponse extends FetchPrivilegesResponse {
	private List<AgentFingerprintPojo> fingerprints;
	
	public FingerLoginResponse(ResponseCodeEnum code, String description){
		setCode(code);
		setDescription(description);
	}

	public List<AgentFingerprintPojo> getFingerprints() {
		return fingerprints;
	}

	public void setFingerprints(List<AgentFingerprintPojo> fingerprints) {
		this.fingerprints = fingerprints;
	}

}
