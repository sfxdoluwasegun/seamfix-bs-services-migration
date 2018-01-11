package com.sf.biocapture.ws.vtu;

import java.util.Date;

import com.google.gson.Gson;

/**
 * 
 * @author Nnanna
 * @since 10 Nov 2017, 13:08:11
 */
public class VTUVendingRequest {
	private String otp;
	
	private String vtuNumber;
	
	private String subscriberNumber;
	
	private double amount;
	
	private String clientTxnID;
	
	private Date txnDate;
	
	private String txnType;
	
	private String agentEmail;
	
	private String kitTag;
	
	private String deviceId;
	
	private String bundleId;

	public String getVtuNumber() {
		return vtuNumber;
	}

	public void setVtuNumber(String vtuNumber) {
		this.vtuNumber = vtuNumber;
	}

	public String getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(String subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getClientTxnID() {
		return clientTxnID;
	}

	public void setClientTxnID(String clientTxnID) {
		this.clientTxnID = clientTxnID;
	}

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getAgentEmail() {
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}

	public String getKitTag() {
		return kitTag;
	}

	public void setKitTag(String kitTag) {
		this.kitTag = kitTag;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public static void main(String[] args) {
		VTUVendingRequest vtu = new VTUVendingRequest();
		vtu.setAgentEmail("zen@zendesk.com");
		vtu.setAmount(100.0);
		vtu.setBundleId("1");
		vtu.setClientTxnID("123456789");
		vtu.setDeviceId("12345");
		vtu.setOtp("47254");
		vtu.setKitTag("DROID-JJC-CODE-AKW-EAS-087426");
		vtu.setSubscriberNumber("08149573029");
		vtu.setTxnDate(new Date());
		vtu.setTxnType("AIRTIME");
		vtu.setVtuNumber("08149573029");
		
		System.out.println(new Gson().toJson(vtu));
	}
}
