package com.sf.biocapture.proxy;

import java.io.Serializable;

public class PassportData implements Serializable{

	private static final long serialVersionUID = -164764090279822128L;

	private byte[] passportData;

	private Integer faceCount = Integer.valueOf(0);

	public byte[] getPassportData() {
		return passportData;
	}

	public void setPassportData(byte[] passportData) {
		this.passportData = passportData;
	}

	public Integer getFaceCount() {
		return faceCount;
	}

	public void setFaceCount(Integer faceCount) {
		this.faceCount = faceCount;
	}

}
