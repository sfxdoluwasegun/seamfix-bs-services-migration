package com.sf.biocapture.ws.onboarding;

public class AgentFingerprintPojo {
	private String fingerType;
	
	private String fingerData; //base64 encoding of fingerprint bytes
	
	public AgentFingerprintPojo(){}
	
	public AgentFingerprintPojo(String fingerType, String fingerData){
		this.fingerType = fingerType;
		this.fingerData = fingerData;
	}

	public String getFingerType() {
		return fingerType;
	}

	public void setFingerType(String fingerType) {
		this.fingerType = fingerType;
	}

	public String getFingerData() {
		return fingerData;
	}

	public void setFingerData(String fingerData) {
		this.fingerData = fingerData;
	}
}
