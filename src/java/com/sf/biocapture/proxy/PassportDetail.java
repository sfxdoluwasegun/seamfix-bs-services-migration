package com.sf.biocapture.proxy;

import java.util.Date;

import java.io.Serializable;

public class PassportDetail implements Serializable{

	private String issueCountry;
	private String passportNumber;
	private Boolean residencyStatus;
	private Date expiryDate;
	private SignatureData signature;

	public String getIssueCountry() {
		return this.issueCountry;
	}

	public void setIssueCountry(String issueCountry) {
		this.issueCountry = issueCountry;
	}

	public String getPassportNumber() {
		return this.passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public Boolean getResidencyStatus() {
		return this.residencyStatus;
	}

	public void setResidencyStatus(Boolean residencyStatus) {
		this.residencyStatus = residencyStatus;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public SignatureData getSignature() {
		return this.signature;
	}

	public void setSignature(SignatureData signature) {
		this.signature = signature;
	}


}
