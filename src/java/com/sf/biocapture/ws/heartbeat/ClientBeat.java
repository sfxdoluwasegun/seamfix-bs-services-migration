package com.sf.biocapture.ws.heartbeat;

import java.io.Serializable;

public class ClientBeat implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8867821982507370629L;

    private String tag;
    private String agent;
    private String agentMobile;
    private String macAddress;
    private String deployState;
    private String modemSerial;
    private String modemModel;
    private int modemSignalLevel;

    private boolean camConnected;
    private boolean scannerConnected;
    private boolean localTimeCorrect;

    private Long clientUptime;
    private Long loginUptime;

    private Double longitude = 0.0d;
    private Double latitude = 0.0d;

    private Long totalRegistration;
    private Long totalSynchronized;
    private Long totalConfirmed;

    private Long dailyRegistration;
    private Long dailySynchronized;
    private Long dailyConfirmed;

    private String appVersion;
    private String thresholdVersion;

    private String refDeviceId;
    private String realTimeDeviceId;

    private String networkStrength;
    private String networkConnectionType;
    private Boolean rooted;
    private String osName;
    private String osVersion;
    private String locationInformationSource;
    private Double locationAccuracy;
    private String processorSpeed;
    private String usedStorage;
    private String availableStorage;
    private String totalStorage;
    private String ramSize;
    private String networkType;
    private Boolean mockedCoordinate;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeployState() {
        return deployState;
    }

    public void setDeployState(String deployState) {
        this.deployState = deployState;
    }

    public String getModemSerial() {
        return modemSerial;
    }

    public void setModemSerial(String modemSerial) {
        this.modemSerial = modemSerial;
    }

    public String getModemModel() {
        return modemModel;
    }

    public void setModemModel(String modemModel) {
        this.modemModel = modemModel;
    }

    public int getModemSignalLevel() {
        return modemSignalLevel;
    }

    public void setModemSignalLevel(int modemSignalLevel) {
        this.modemSignalLevel = modemSignalLevel;
    }

    public boolean isCamConnected() {
        return camConnected;
    }

    public void setCamConnected(boolean camConnected) {
        this.camConnected = camConnected;
    }

    public boolean isScannerConnected() {
        return scannerConnected;
    }

    public void setScannerConnected(boolean scannerConnected) {
        this.scannerConnected = scannerConnected;
    }

    public boolean isLocalTimeCorrect() {
        return localTimeCorrect;
    }

    public void setLocalTimeCorrect(boolean localTimeCorrect) {
        this.localTimeCorrect = localTimeCorrect;
    }

    public Long getClientUptime() {
        return clientUptime;
    }

    public void setClientUptime(Long clientUptime) {
        this.clientUptime = clientUptime;
    }

    public Long getLoginUptime() {
        return loginUptime;
    }

    public void setLoginUptime(Long loginUptime) {
        this.loginUptime = loginUptime;
    }

    public Long getTotalRegistration() {
        return totalRegistration;
    }

    public void setTotalRegistration(Long totalRegistration) {
        this.totalRegistration = totalRegistration;
    }

    public Long getTotalSynchronized() {
        return totalSynchronized;
    }

    public void setTotalSynchronized(Long totalSynchronized) {
        this.totalSynchronized = totalSynchronized;
    }

    public Long getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(Long totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public Long getDailyRegistration() {
        return dailyRegistration;
    }

    public void setDailyRegistration(Long dailyRegistration) {
        this.dailyRegistration = dailyRegistration;
    }

    public Long getDailySynchronized() {
        return dailySynchronized;
    }

    public void setDailySynchronized(Long dailySynchronized) {
        this.dailySynchronized = dailySynchronized;
    }

    public Long getDailyConfirmed() {
        return dailyConfirmed;
    }

    public void setDailyConfirmed(Long dailyConfirmed) {
        this.dailyConfirmed = dailyConfirmed;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getThresholdVersion() {
        return thresholdVersion;
    }

    public void setThresholdVersion(String thresholdVersion) {
        this.thresholdVersion = thresholdVersion;
    }

    public String getRefDeviceId() {
        return refDeviceId;
    }

    public void setRefDeviceId(String refDeviceId) {
        this.refDeviceId = refDeviceId;
    }

    public String getRealTimeDeviceId() {
        return realTimeDeviceId;
    }

    public void setRealTimeDeviceId(String realTimeDeviceId) {
        this.realTimeDeviceId = realTimeDeviceId;
    }

    public String getNetworkStrength() {
        return networkStrength;
    }

    public void setNetworkStrength(String networkStrength) {
        this.networkStrength = networkStrength;
    }

    public String getNetworkConnectionType() {
        return networkConnectionType;
    }

    public void setNetworkConnectionType(String networkConnectionType) {
        this.networkConnectionType = networkConnectionType;
    }

    public Boolean getRooted() {
        return rooted;
    }

    public void setRooted(Boolean rooted) {
        this.rooted = rooted;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getLocationInformationSource() {
        return locationInformationSource;
    }

    public void setLocationInformationSource(String locationInformationSource) {
        this.locationInformationSource = locationInformationSource;
    }

    public Double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(Double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public String getProcessorSpeed() {
        return processorSpeed;
    }

    public void setProcessorSpeed(String processorSpeed) {
        this.processorSpeed = processorSpeed;
    }

    public String getUsedStorage() {
        return usedStorage;
    }

    public void setUsedStorage(String usedStorage) {
        this.usedStorage = usedStorage;
    }

    public String getAvailableStorage() {
        return availableStorage;
    }

    public void setAvailableStorage(String availableStorage) {
        this.availableStorage = availableStorage;
    }

    public String getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(String totalStorage) {
        this.totalStorage = totalStorage;
    }

    public String getRamSize() {
        return ramSize;
    }

    public void setRamSize(String ramSize) {
        this.ramSize = ramSize;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public Boolean getMockedCoordinate() {
        return mockedCoordinate;
    }

    public void setMockedCoordinate(Boolean mockedCoordinate) {
        this.mockedCoordinate = mockedCoordinate;
    }

}
