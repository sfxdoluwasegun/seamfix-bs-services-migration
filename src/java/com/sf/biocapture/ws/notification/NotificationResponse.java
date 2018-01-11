package com.sf.biocapture.ws.notification;

import java.util.List;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 * 
 * @author Nnanna
 * @since 10/11/2016
 */
public class NotificationResponse extends ResponseData {
	String agentEmail;
	List<NotificationData> notifications;
	
	public NotificationResponse(){}
	
	public NotificationResponse(ResponseCodeEnum code, String description){
		setCode(code);
		setDescription(description);
	}

	public List<NotificationData> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationData> notifications) {
		this.notifications = notifications;
	}

	public String getAgentEmail() {
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}
}