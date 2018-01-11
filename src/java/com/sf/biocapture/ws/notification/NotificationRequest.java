package com.sf.biocapture.ws.notification;

import java.util.Date;

import com.google.gson.Gson;

/**
 * @author Nnanna
 * @since 15/11/2016
 */
public class NotificationRequest {
	private String email;
	private String macAddress;
	private Date lastMessageDate;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public Date getLastMessageDate() {
		return lastMessageDate;
	}
	public void setLastMessageDate(Date lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}
	
	public static void main(String[] args) {
		NotificationRequest req = new NotificationRequest();
		req.setEmail("sakinmukomi@seamfix.com");
		req.setMacAddress("LASH");
		req.setLastMessageDate(null);
		
		System.out.println(new Gson().toJson(req));
	}
}
