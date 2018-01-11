package com.sf.biocapture.postactivation.sms;

import java.io.Serializable;

public class SmsReq implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1447733929354834283L;
	
	private Long id;
	private String uniqueId;
	private String phoneNumber;
	
	private Long phoneNumberStatus;
	
	public SmsReq() {
		// TODO Auto-generated constructor stub
	}
	
	public SmsReq(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public Long getPhoneNumberStatus() {
		return phoneNumberStatus;
	}

	public void setPhoneNumberStatus(Long phoneNumberStatus) {
		this.phoneNumberStatus = phoneNumberStatus;
	}
	
	@Override
	public String toString() {
		return "SmsReq[id: " + this.id + ", uniqueId: " + this.uniqueId + ", msisdn: " + this.phoneNumber + ", " + " pns: " + this.phoneNumberStatus + "]";
	}

}
