package com.sf.biocapture.ws.resync;

import java.util.ArrayList;
import java.util.List;

public class ResyncResponse {
	
	private int status;
	
	private List<String> resyncList;
	
	public ResyncResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public ResyncResponse(int status) {
		this.status = status;
		this.resyncList = new ArrayList<String>();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<String> getResyncList() {
		return resyncList;
	}

	public void setResyncList(List<String> resyncList) {
		this.resyncList = resyncList;
	}

}
