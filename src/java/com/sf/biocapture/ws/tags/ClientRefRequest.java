package com.sf.biocapture.ws.tags;

import java.sql.Timestamp;
import java.util.Date;

public class ClientRefRequest {
	
	private String ref;
	private String mac;
	private String netCardName;
	private String adminEmail;
	
	private String fingerLicense;
	private String wsqLicesne;
	
	private String adminMobile;
	private String kitType;
	
	private Float patchVersion;
	private Date installDate;
	
	private String machineModel;
	private String machineSerial;
	private String machineManufacturer;
	private String machineOS;
                
	private Timestamp clientTime;
	
	public String getNetCardName() {
		return netCardName;
	}

	public void setNetCardName(String netCardName) {
		this.netCardName = netCardName;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getFingerLicense() {
		return fingerLicense;
	}

	public void setFingerLicense(String fingerLicense) {
		this.fingerLicense = fingerLicense;
	}

	public String getWsqLicesne() {
		return wsqLicesne;
	}

	public void setWsqLicesne(String wsqLicesne) {
		this.wsqLicesne = wsqLicesne;
	}

	public String getAdminMobile() {
		return adminMobile;
	}

	public void setAdminMobile(String adminMobile) {
		this.adminMobile = adminMobile;
	}

	public String getKitType() {
		return kitType;
	}

	public void setKitType(String kitType) {
		this.kitType = kitType;
	}

	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public String getMac() {
		return mac;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}

	public Date getInstallDate() {
		return installDate;
	}

	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}

	public Float getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(Float patchVersion) {
		this.patchVersion = patchVersion;
	}

	public String getMachineOS() {
		return machineOS;
	}

	public void setMachineOS(String machineOS) {
		this.machineOS = machineOS;
	}

	public String getMachineManufacturer() {
		return machineManufacturer;
	}

	public void setMachineManufacturer(String machineManufacturer) {
		this.machineManufacturer = machineManufacturer;
	}

	public String getMachineSerial() {
		return machineSerial;
	}

	public void setMachineSerial(String machineSerial) {
		this.machineSerial = machineSerial;
	}

	public String getMachineModel() {
		return machineModel;
	}

	public void setMachineModel(String machineModel) {
		this.machineModel = machineModel;
	}

	public Timestamp getClientTime() {
		return clientTime;
	}

	public void setClientTime(Timestamp clientTime) {
		this.clientTime = clientTime;
	}

}
