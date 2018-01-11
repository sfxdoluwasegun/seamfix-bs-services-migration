package com.sf.biocapture.ws.astatus;

import java.sql.Timestamp;

public class AStatus {
	
        private String message;
	private String status;
	private String uniqueId;
	private String phoneNumber;
	private Timestamp activationTimestamp;
	private int code;
        
        public String getMessage(){
            return message;
        }
        
        public void setMessage(String message){
            this.message = message;
        }
        
        public int getCode(){
            return code;
        }
        
        public void setCode(int code){
            this.code = code;
        }
        
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Timestamp getActivationTimestamp() {
		return activationTimestamp;
	}

	public void setActivationTimestamp(Timestamp activationTimestamp) {
		this.activationTimestamp = activationTimestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}
