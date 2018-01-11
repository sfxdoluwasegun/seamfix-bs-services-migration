package com.sf.biocapture.ws.notification;

import java.io.Serializable;
import java.util.Date;

import com.sf.biocapture.entity.KycBroadcast;
import com.sf.biocapture.ws.notification.querytransformer.KycBroadcast_;

/**
 * 
 * @author Nnanna
 * @since 10/11/2016
 *
 */
public class NotificationData implements Serializable {
	private String message;
	private Date created;
	private int messageId;
	private boolean globalMessage = false;
	private boolean kitMessage = false;
	private boolean userMessage = false;
	
	public NotificationData(){}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public static NotificationData from(KycBroadcast broadcast){
		NotificationData datum = new NotificationData();
		datum.setMessage(broadcast.getMessage());
		datum.setGlobalMessage(broadcast.isGlobal());
		datum.setCreated(broadcast.getLastModified());
		datum.setKitMessage(broadcast.getTargetNode() != null);
		datum.setUserMessage(broadcast.getTargetUser() != null);
		datum.setMessageId(new String(broadcast.getMessage() + broadcast.getPk()).hashCode());
		
		return datum;
	}
        
	public static NotificationData from(KycBroadcast_ broadcast){
		NotificationData datum = new NotificationData();
		datum.setMessage(broadcast.getMessage());
		datum.setGlobalMessage(broadcast.isGlobal());
		datum.setCreated(broadcast.getLastModified());
		datum.setKitMessage(broadcast.getNodeFk()!= null);
		datum.setUserMessage(broadcast.getUserFk()!= null);
		datum.setMessageId(new String(broadcast.getMessage() + broadcast.getPk()).hashCode());
		
		return datum;
	}

	public boolean isKitMessage() {
		return kitMessage;
	}

	public void setKitMessage(boolean kitMessage) {
		this.kitMessage = kitMessage;
	}

	public boolean isUserMessage() {
		return userMessage;
	}

	public void setUserMessage(boolean userMessage) {
		this.userMessage = userMessage;
	}

	public boolean isGlobalMessage() {
		return globalMessage;
	}

	public void setGlobalMessage(boolean globalMessage) {
		this.globalMessage = globalMessage;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
}