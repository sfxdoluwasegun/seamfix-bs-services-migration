package com.sf.biocapture.ws.swap;

import java.io.Serializable;

/**
 * 
 * @author Nnanna
 *
 */
public class LastRechargeResponse implements Serializable {
	private static final long serialVersionUID = 1154129151923163748L;
	
	private String code = "-1";
	private String message;
	private double amount = 0.0;
	private String lastRechargeDate;
	
	public LastRechargeResponse(){
		
	}
	
	public LastRechargeResponse(String code, String message, double amount){
		this.code = code;
		this.message = message;
		this.amount = amount;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getLastRechargeDate() {
		return lastRechargeDate;
	}

	public void setLastRechargeDate(String lastRechargeDate) {
		this.lastRechargeDate = lastRechargeDate;
	}
}
