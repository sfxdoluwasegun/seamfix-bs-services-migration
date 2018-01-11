package com.sf.biocapture.proxy;

import java.io.Serializable;

public class SpecialData implements Serializable 
{
	private byte[] biometricData;
	private String biometricDataType;
	private String reason;

	
	public byte[] getBiometricData() {
		return this.biometricData;
	}

	public void setBiometricData(byte[] biometricData) {
		this.biometricData = biometricData;
	}

	public String getBiometricDataType() {
		return this.biometricDataType;
	}

	public void setBiometricDataType(String biometricDataType) {
		this.biometricDataType = biometricDataType;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
