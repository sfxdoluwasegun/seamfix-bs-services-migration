package com.sf.biocapture.ws.swap;

import java.io.Serializable;
import java.util.ArrayList;

import com.sf.biocapture.ws.ResponseCodeEnum;

/**
 * 
 * @author Nnanna
 *
 */
public class SimSwapResponse implements Serializable {
	
	private static final long serialVersionUID = -2920112427264508809L;

	private String code = "0";
	
	private String message;
	
	private String msisdn;
	
	private ArrayList<String> dialedNumbers = null;
	
	public SimSwapResponse(){
		
	}
	
	public SimSwapResponse(String code, String message){
		this.code = code;
		this.message = message;
	}
	
	public SimSwapResponse(ResponseCodeEnum resp){
		this.code = String.valueOf(resp.getCode());
		this.message = resp.getDescription();
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
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

	public ArrayList<String> getDialedNumbers() {
		return dialedNumbers;
	}

	public void setDialedNumbers(ArrayList<String> dialedNumbers) {
		this.dialedNumbers = dialedNumbers;
	}
}