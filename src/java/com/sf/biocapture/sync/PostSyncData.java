package com.sf.biocapture.sync;

import java.util.Date;

public class PostSyncData {
	
	private String uniqueId;
	private String msisdn;
	private Date syncTime;
	
	public PostSyncData() {
		// TODO Auto-generated constructor stub
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Date getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}

}
