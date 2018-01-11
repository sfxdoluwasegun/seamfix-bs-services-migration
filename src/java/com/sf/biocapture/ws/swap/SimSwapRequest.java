package com.sf.biocapture.ws.swap;

/**
 * 
 * @author Nnanna
 * @since 24/11/2016
 *
 */
public class SimSwapRequest {
	/**
	 * msisdn to be swapped
	 */
	private String msisdn;
	
	/**
	 * IMSI. optional for now
	 */
	private String orderNumber;
	
	/**
	 * PUK of the new sim
	 */
	private String puk;
	
	/**
	 * Sim Serial of the new sim
	 */
	private String simSerial;
	
	/**
	 * kit tag
	 */
	private String tag;
	
	/**
	 * kit mac address
	 */
	private String macAddress;
	/**
	 * unique ID
	 */
	private String uniqueId;
	
	/**
	 * email address of the agent doing the swap
	 */
	private String agentEmail;
	
	/**
	 * time the swap was initiated on the client
	 */
	private Long swapTime;
	
	/**
	 * subscriber's portrait, encoded in base64
	 */
	private String subscriberPassport;
        
        /*
        * Device's unique id
        */

        private String deviceId;
        
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPuk() {
		return puk;
	}

	public void setPuk(String puk) {
		this.puk = puk;
	}

	public String getSimSerial() {
		return simSerial;
	}

	public void setSimSerial(String simSerial) {
		this.simSerial = simSerial;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getAgentEmail() {
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}

	public Long getSwapTime() {
		return swapTime;
	}

	public void setSwapTime(Long swapTime) {
		this.swapTime = swapTime;
	}

	public String getSubscriberPassport() {
		return subscriberPassport;
	}

	public void setSubscriberPassport(String subscriberPassport) {
		this.subscriberPassport = subscriberPassport;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
        
        
}
