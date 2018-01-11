package com.sf.biocapture.ws.tags;

public class ClientRefResponse {
	
	private int status = -1; // -1 failure, -2 error with message, 0 success
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
