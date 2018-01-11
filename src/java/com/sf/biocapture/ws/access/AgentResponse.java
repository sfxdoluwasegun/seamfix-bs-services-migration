package com.sf.biocapture.ws.access;

public class AgentResponse {
	private String tag;
	private String kitStatus;
	private String agentStatus;
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getKitStatus() {
		return kitStatus;
	}
	public void setKitStatus(String kitStatus) {
		this.kitStatus = kitStatus;
	}
	public String getAgentStatus() {
		return agentStatus;
	}
	public void setAgentStatus(String agentStatus) {
		this.agentStatus = agentStatus;
	}
}
