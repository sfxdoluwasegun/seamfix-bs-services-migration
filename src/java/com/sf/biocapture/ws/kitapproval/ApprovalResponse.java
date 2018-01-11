package com.sf.biocapture.ws.kitapproval;

public class ApprovalResponse {
	private int status = -1;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
