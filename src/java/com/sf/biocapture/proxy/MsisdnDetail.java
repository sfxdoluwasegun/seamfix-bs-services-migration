package com.sf.biocapture.proxy;

import java.io.Serializable;
import java.sql.Timestamp;

public class MsisdnDetail implements Serializable{

	private static final long serialVersionUID = -2345671283292L;

	private String msisdn;

	private String serial;

	private String subscriberType;

	private Boolean newSubscriber;

	private Boolean zap;

	private Timestamp activationTimestamp;

	private Boolean activationStatus;

	private Timestamp msisdnPartKey;

	public String getMsisdn() {
		return this.msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getSerial() {
		return this.serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getSubscriberType() {
		return this.subscriberType;
	}

	public void setSubscriberType(String subscriberType) {
		this.subscriberType = subscriberType;
	}

	public Boolean getNewSubscriber() {
		return this.newSubscriber;
	}

	public void setNewSubscriber(Boolean newSubscriber) {
		this.newSubscriber = newSubscriber;
	}

	public Boolean getZap() {
		return this.zap;
	}

	public void setZap(Boolean zap) {
		this.zap = zap;
	}

	public Timestamp getActivationTimestamp() {
		return this.activationTimestamp;
	}

	public void setActivationTimestamp(Timestamp activationTimestamp) {
		this.activationTimestamp = activationTimestamp;
	}

	public Boolean getActivationStatus() {
		return this.activationStatus;
	}

	public void setActivationStatus(Boolean activationStatus) {
		this.activationStatus = activationStatus;
	}

	public Timestamp getMsisdnPartKey() {
		return msisdnPartKey;
	}

	public void setMsisdnPartKey(Timestamp msisdnPartKey) {
		this.msisdnPartKey = msisdnPartKey;
	}

	

	@Override
	public String toString() {
		return "MSISDN {msisdn: " + msisdn + ", serial: " + serial + "}";
	}

}
