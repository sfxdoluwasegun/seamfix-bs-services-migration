package com.sf.biocapture.ws.access;

import java.io.Serializable;

public class NodeData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8507949746187352598L;

	private String macId;
	
	private String ref;
	
	private boolean corporateKit;
	
	private Float patchVersion;
	
	private String serverMessage;
        
        private String deviceId;
        
	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	public Float getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(Float patchVersion) {
		this.patchVersion = patchVersion;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public boolean isCorporateKit() {
		return corporateKit;
	}

	public void setCorporateKit(boolean corporateKit) {
		this.corporateKit = corporateKit;
	}
        
        public String getDeviceId(){
                return deviceId;
        }
        
        public void setDeviceId(String deviceId){
                this.deviceId = deviceId;
        }

}
