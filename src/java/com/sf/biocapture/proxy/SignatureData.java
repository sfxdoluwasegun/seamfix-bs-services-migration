package com.sf.biocapture.proxy;

import java.io.Serializable;

public class SignatureData implements Serializable{
	
	private byte[] signatureData;

	public byte[] getSignatureData() {
		return this.signatureData;
	}

	public void setSignatureData(byte[] signatureData) {
		this.signatureData = signatureData;
	}

}
