package com.sf.biocapture.proxy;

import java.io.Serializable;

public class WsqImage implements Serializable {

	private static final long serialVersionUID = 234554321111L;

	private byte[] wsqData;

	private int nfiq;

	private float compressionRatio;

	private String finger;

	private String reasonCode;

	public byte[] getWsqData() {
		return this.wsqData;
	}

	public void setWsqData(byte[] wsqData) {
		this.wsqData = wsqData;
	}

	public int getNfiq() {
		return this.nfiq;
	}

	public void setNfiq(int nfiq) {
		this.nfiq = nfiq;
	}

	public float getCompressionRatio() {
		return this.compressionRatio;
	}

	public void setCompressionRatio(float compressionRatio) {
		this.compressionRatio = compressionRatio;
	}

	public String getFinger() {
		return this.finger;
	}

	public void setFinger(String finger) {
		this.finger = finger;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	@Override
	public String toString() {
		return "FingerPrint{finger: " + finger + ", nfiq: " + nfiq + ", reason: " + reasonCode + "}";
	}

}
