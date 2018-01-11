package com.sf.biocapture.ws.access;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import com.sf.biocapture.ws.vtu.BundleTypeDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SettingsResponse extends ResponseData {

    private boolean corporateKit;
    private int maxMsisdn;
    private long heartbeatRate;

    private boolean updateAvailable;
    private Float updateVersion;
    private Date releaseDate;

    private String serverMessage;
    private String regexOne;
    private String regexTwo;
    private boolean spoofData;

    private int clientRecordsLifespan;
    private String localIdTypes;
    private String foreignIdTypes;

    //sim swap settings
    /**
     * fingerprint and/or questionnaire validation
     */
    private String modeOfValidation;

    /**
     * no of failed fingerprint validation allowed on the client
     */
    private String allowableFpFailures;

    /**
     * no of frequently dialed numbers that must be matched for sim swap to
     * continue
     */
    private int matchedMsisdns = 3;

    private HashMap<String, String> questionnaireValidation = null;

    private int maxChildMsisdn;

    /**
     * no of minutes a client is allowed to stay idle before user is forced to
     * login again
     */
    private int clientlockoutPeriod;

    private HashMap<String, String> clientFieldSettings = null;

    private boolean signRegistration;
    private boolean otpRequired;
    private List<String> loginMode;
    private boolean loginOffline;
    private String offlineValidationType;
    private boolean airtimeSalesMandatory;
    private String airtimeSalesURL;
    private List<String> availableUseCases;
    private int clientActivityLogBatchSize = 20;
    private int maximumMsisdnAllowedPerRegistration = 5;
    private boolean enableVasModule = true;
    private int minimumAcceptableCharacter;

    /**
     * *SERVICE INTERVALS**
     */
    /**
     * Check for notification
     */
    private int notificationsChecker;

    /**
     * Onboarding
     */
    private int agentBioSynchronizer;

    /**
     * Xml
     */
    private int auditXmlSynchronizer;
    private int thresholdUpdater;

    private int clientAuditSyncInterval;
    private int activationChecker;
    private int synchronizer;
    private int harmonizer;
    private int settingsService;
    private int blackLister;
    private int requestShortcode;
    private int responseShortcode;
    /**
     * controls the interval at which device location status is checked
     */
    private int clientLocationCheckPopupInterval;
    /**
     * the interval at which the thread that updates device id at the backend is
     * called. this call is made iff device id has not been updated before.
     */
    private int updateDeviceIdInterval;
    /*
     change password parameters
     */
    private String passwordPolicy;
    private String passwordRegex;

    //CLIENT DYA
    private List<String> diamondYellowAcctUseCase;
    private String dyaAccountType;

    private boolean locationMandatory;

    //VTU
    private boolean vtuVendingMandatory;
    private List<String> transactionTypes;
    private List<BundleTypeDTO> bundleTypes;

    public SettingsResponse() {
        setCode(ResponseCodeEnum.ERROR);
        setDescription(ResponseCodeEnum.ERROR.getDescription());
    }

    public boolean isCorporateKit() {
        return corporateKit;
    }

    public void setCorporateKit(boolean corporateKit) {
        this.corporateKit = corporateKit;
    }

    public int getMaxMsisdn() {
        return maxMsisdn;
    }

    public void setMaxMsisdn(int maxMsisdn) {
        this.maxMsisdn = maxMsisdn;
    }

    public int getMaxChildMsisdn() {
        return maxChildMsisdn;
    }

    public void setMaxChildMsisdn(int maxChildMsisdn) {
        this.maxChildMsisdn = maxChildMsisdn;
    }

    public Long getHeartbeatRate() {
        return heartbeatRate;
    }

    public void setHeartbeatRate(Long heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    public Float getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(Float updateVersion) {
        this.updateVersion = updateVersion;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRegexOne() {
        return regexOne;
    }

    public void setRegexOne(String regexOne) {
        this.regexOne = regexOne;
    }

    public String getRegexTwo() {
        return regexTwo;
    }

    public void setRegexTwo(String regexTwo) {
        this.regexTwo = regexTwo;
    }

    public boolean isSpoofData() {
        return spoofData;
    }

    public void setSpoofData(boolean spoofData) {
        this.spoofData = spoofData;
    }

    public int getClientRecordsLifespan() {
        return clientRecordsLifespan;
    }

    public void setClientRecordsLifespan(int clientRecordsLifespan) {
        this.clientRecordsLifespan = clientRecordsLifespan;
    }

    public String getLocalIdTypes() {
        return localIdTypes;
    }

    public void setLocalIdTypes(String localIdTypes) {
        this.localIdTypes = localIdTypes;
    }

    public String getForeignIdTypes() {
        return foreignIdTypes;
    }

    public void setForeignIdTypes(String foreignIdTypes) {
        this.foreignIdTypes = foreignIdTypes;
    }

    public String getModeOfValidation() {
        return modeOfValidation;
    }

    public void setModeOfValidation(String modeOfValidation) {
        this.modeOfValidation = modeOfValidation;
    }

    public String getAllowableFpFailures() {
        return allowableFpFailures;
    }

    public void setAllowableFpFailures(String allowableFpFailures) {
        this.allowableFpFailures = allowableFpFailures;
    }

    public HashMap<String, String> getQuestionnaireValidation() {
        return questionnaireValidation;
    }

    public void setQuestionnaireValidation(HashMap<String, String> questionnaireValidation) {
        this.questionnaireValidation = questionnaireValidation;
    }

    public int getMatchedMsisdns() {
        return matchedMsisdns;
    }

    public void setMatchedMsisdns(int matchedMsisdns) {
        this.matchedMsisdns = matchedMsisdns;
    }

    public int getClientlockoutPeriod() {
        return clientlockoutPeriod;
    }

    public void setClientlockoutPeriod(int clientlockoutPeriod) {
        this.clientlockoutPeriod = clientlockoutPeriod;
    }

    public HashMap<String, String> getClientFieldSettings() {
        return clientFieldSettings;
    }

    public void setClientFieldSettings(HashMap<String, String> clientFieldSettings) {
        this.clientFieldSettings = clientFieldSettings;
    }

    public boolean isSignRegistration() {
        return signRegistration;
    }

    public void setSignRegistration(boolean signRegistration) {
        this.signRegistration = signRegistration;
    }

    public int getClientAuditSyncInterval() {
        return clientAuditSyncInterval;
    }

    public void setClientAuditSyncInterval(int clientAuditSyncInterval) {
        this.clientAuditSyncInterval = clientAuditSyncInterval;
    }

    public boolean isOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public List<String> getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(List<String> loginMode) {
        this.loginMode = loginMode;
    }

    public boolean isLoginOffline() {
        return loginOffline;
    }

    public void setLoginOffline(boolean loginOffline) {
        this.loginOffline = loginOffline;
    }

    public String getOfflineValidationType() {
        return offlineValidationType;
    }

    public void setOfflineValidationType(String offlineValidationType) {
        this.offlineValidationType = offlineValidationType;
    }

    public boolean getAirtimeSalesMandatory() {
        return airtimeSalesMandatory;
    }

    public void setAirtimeSalesMandatory(boolean airtimeSalesMandatory) {
        this.airtimeSalesMandatory = airtimeSalesMandatory;
    }

    public String getAirtimeSalesURL() {
        return airtimeSalesURL;
    }

    public void setAirtimeSalesURL(String airtimeSalesURL) {
        this.airtimeSalesURL = airtimeSalesURL;
    }

    public List<String> getAvailableUseCases() {
        return availableUseCases;
    }

    public void setAvailableUseCases(List<String> availableUseCases) {
        this.availableUseCases = availableUseCases;
    }

    public void setClientActivityLogBatchSize(int clientActivityLogBatchSize) {
        this.clientActivityLogBatchSize = clientActivityLogBatchSize;
    }

    public int getClientActivityLogBatchSize() {
        return clientActivityLogBatchSize;
    }

    public int getMaximumMsisdnAllowedPerRegistration() {
        return maximumMsisdnAllowedPerRegistration;
    }

    public void setMaximumMsisdnAllowedPerRegistration(int maximumMsisdnAllowedPerRegistration) {
        this.maximumMsisdnAllowedPerRegistration = maximumMsisdnAllowedPerRegistration;
    }

    public boolean isEnableVasModule() {
        return enableVasModule;
    }

    public void setEnableVasModule(boolean enableVasModule) {
        this.enableVasModule = enableVasModule;
    }

    public int getMinimumAcceptableCharacter() {
        return minimumAcceptableCharacter;
    }

    public void setMinimumAcceptableCharacter(int minimumAcceptableCharacter) {
        this.minimumAcceptableCharacter = minimumAcceptableCharacter;
    }

    public int getNotificationsChecker() {
        return notificationsChecker;
    }

    public void setNotificationsChecker(int notificationsChecker) {
        this.notificationsChecker = notificationsChecker;
    }

    public int getAgentBioSynchronizer() {
        return agentBioSynchronizer;
    }

    public void setAgentBioSynchronizer(int agentBioSynchronizer) {
        this.agentBioSynchronizer = agentBioSynchronizer;
    }

    public int getAuditXmlSynchronizer() {
        return auditXmlSynchronizer;
    }

    public void setAuditXmlSynchronizer(int auditXmlSynchronizer) {
        this.auditXmlSynchronizer = auditXmlSynchronizer;
    }

    public int getThresholdUpdater() {
        return thresholdUpdater;
    }

    public void setThresholdUpdater(int thresholdUpdater) {
        this.thresholdUpdater = thresholdUpdater;
    }

    public int getActivationChecker() {
        return activationChecker;
    }

    public void setActivationChecker(int activationChecker) {
        this.activationChecker = activationChecker;
    }

    public int getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(int synchronizer) {
        this.synchronizer = synchronizer;
    }

    public int getHarmonizer() {
        return harmonizer;
    }

    public void setHarmonizer(int harmonizer) {
        this.harmonizer = harmonizer;
    }

    public int getSettingsService() {
        return settingsService;
    }

    public void setSettingsService(int settingsService) {
        this.settingsService = settingsService;
    }

    public int getBlackLister() {
        return blackLister;
    }

    public void setBlackLister(int blackLister) {
        this.blackLister = blackLister;
    }

    public void setHeartbeatRate(long heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public int getRequestShortcode() {
        return requestShortcode;
    }

    public void setRequestShortcode(int requestShortcode) {
        this.requestShortcode = requestShortcode;
    }

    public int getResponseShortcode() {
        return responseShortcode;
    }

    public void setResponseShortcode(int responseShortcode) {
        this.responseShortcode = responseShortcode;
    }

    public String getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public String getPasswordRegex() {
        return passwordRegex;
    }

    public void setPasswordRegex(String passwordRegex) {
        this.passwordRegex = passwordRegex;
    }

    public List<String> getDiamondYellowAcctUseCase() {
        return diamondYellowAcctUseCase;
    }

    public void setDiamondYellowAcctUseCase(List<String> diamondYellowAcctUseCase) {
        this.diamondYellowAcctUseCase = diamondYellowAcctUseCase;
    }

    public String getDyaAccountType() {
        return dyaAccountType;
    }

    public void setDyaAccountType(String dyaAccountType) {
        this.dyaAccountType = dyaAccountType;
    }

    public int getClientLocationCheckPopupInterval() {
        return clientLocationCheckPopupInterval;
    }

    public void setClientLocationCheckPopupInterval(int clientLocationCheckPopupInterval) {
        this.clientLocationCheckPopupInterval = clientLocationCheckPopupInterval;
    }

    public int getUpdateDeviceIdInterval() {
        return updateDeviceIdInterval;
    }

    public void setUpdateDeviceIdInterval(int updateDeviceIdInterval) {
        this.updateDeviceIdInterval = updateDeviceIdInterval;
    }

    public boolean isLocationMandatory() {
        return locationMandatory;
    }

    public void setLocationMandatory(boolean locationMandatory) {
        this.locationMandatory = locationMandatory;
    }

    public List<String> getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(List<String> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public List<BundleTypeDTO> getBundleTypes() {
        return bundleTypes;
    }

    public void setBundleTypes(List<BundleTypeDTO> bundleTypes) {
        this.bundleTypes = bundleTypes;
    }

    public boolean isVtuVendingMandatory() {
        return vtuVendingMandatory;
    }

    public void setVtuVendingMandatory(boolean vtuVendingMandatory) {
        this.vtuVendingMandatory = vtuVendingMandatory;
    }
}
