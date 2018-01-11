package com.sf.biocapture.ws.onboarding;

import java.util.List;

public class AgentData {

	private String tag;
	
	private String mac;
	
	private String emailAddress;
        
        private String onboardedByEmailAddress;
        
        private Long onboardDateValue;
	
	private String portrait;
	
	private List<AgentFingerprintPojo> fingerprints;
	
	public AgentData(){}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public List<AgentFingerprintPojo> getFingerprints() {
		return fingerprints;
	}

	public void setFingerprints(List<AgentFingerprintPojo> fingerprints) {
		this.fingerprints = fingerprints;
	}

        public String getOnboardedByEmailAddress() {
            return onboardedByEmailAddress;
        }

        public void setOnboardedByEmailAddress(String onboardedByEmailAddress) {
            this.onboardedByEmailAddress = onboardedByEmailAddress;
        }

        public Long getOnboardDateValue() {
            return onboardDateValue;
        }

        public void setOnboardDateValue(Long onboardDateValue) {
            this.onboardDateValue = onboardDateValue;
        }
        
        
}