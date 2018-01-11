package com.sf.biocapture.krm;

public class ZonalSync {
	
	private String zone;
	private Number syncs;
	
	public ZonalSync() {
		// TODO Auto-generated constructor stub
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public Number getSyncs() {
		return syncs;
	}

	public void setSyncs(Number syncs) {
		this.syncs = syncs;
	}
	
	@Override
	public String toString() {
		return "[ zone: " + zone +", syncs: " + syncs + " ]";
	}

}
