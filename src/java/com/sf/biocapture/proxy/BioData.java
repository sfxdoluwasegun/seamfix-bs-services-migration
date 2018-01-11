/**
 * The purpose of this class is to serve as a carrier of the user bio data 
 * transmitted to the client that requested for it.
 * 
 */
package com.sf.biocapture.proxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author @wizzyclems
 */
public class BioData implements Serializable{
    
    private static final long serialVersionUID = 3985733685784940014L;
    BasicData bd;
    
    Integer msisdnCount;
    String uniqueId;
    DynamicData dynamicData;
    PassportData passportData;
    PassportDetail passportDetail;
    List<WsqImage> wsqImages = new ArrayList<>();
    
    private String accountId;
    private double invoiceAmount;
    private boolean biometricUpdateEligible;
       
    public BioData(){
    }
        
    public BasicData getBd() {
        return bd;
    }

    public void setBd(BasicData bd) {
        this.bd = bd;
    }

    public DynamicData getDynamicData() {
        return dynamicData;
    }

    public void setDynamicData(DynamicData dynamicData) {
        this.dynamicData = dynamicData;
    }

    public PassportData getPassportData() {
        return passportData;
    }

    public void setPassportData(PassportData passportData) {
        this.passportData = passportData;
    }

    public void addWsqImage(WsqImage w){
        wsqImages.add(w);
    }
    
    public List<WsqImage> getWsqImages() {
        return wsqImages;
    }

    public void setWsqImages(List<WsqImage> wsqImages) {
        this.wsqImages = wsqImages;
    }

    public PassportDetail getPassportDetail() {
        return passportDetail;
    }

    public void setPassportDetail(PassportDetail passportDetail) {
        this.passportDetail = passportDetail;
    }
    
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getMsisdnCount() {
        return msisdnCount;
    }

    public void setMsisdnCount(Integer msisdnCount) {
        this.msisdnCount = msisdnCount;
    }

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public boolean isBiometricUpdateEligible() {
		return biometricUpdateEligible;
	}

	public void setBiometricUpdateEligible(boolean biometricUpdateEligible) {
		this.biometricUpdateEligible = biometricUpdateEligible;
	}

    @Override
    public String toString() {
        return "BioData{" + "bd=" + bd + ", msisdnCount=" + msisdnCount + ", uniqueId=" + uniqueId + ", dynamicData=" + dynamicData + ", passportData=" + passportData + ", passportDetail=" + passportDetail + ", wsqImages=" + wsqImages + ", accountId=" + accountId + ", invoiceAmount=" + invoiceAmount + ", biometricUpdateEligible=" + biometricUpdateEligible + '}';
    }
   
}
