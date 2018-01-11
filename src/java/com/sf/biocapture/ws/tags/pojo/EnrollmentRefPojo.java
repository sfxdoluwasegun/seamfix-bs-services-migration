/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.tags.pojo;

import java.sql.Timestamp;

/**
 *
 * @author Marcel
 * @since Sep 29, 2017 - 6:47:30 PM
 */
public class EnrollmentRefPojo {

    private String name;
    private String code;
    private String description;
    private String macAddress;
    private String networkCardName;
    private String installedBy;
    private Timestamp dateInstalled;
    private Boolean corporate;
    private String custom1;
    private String custom2;
    private String custom3;
    private String deviceId;
    private String stateName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getNetworkCardName() {
        return networkCardName;
    }

    public void setNetworkCardName(String networkCardName) {
        this.networkCardName = networkCardName;
    }

    public String getInstalledBy() {
        return installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public Timestamp getDateInstalled() {
        return dateInstalled;
    }

    public void setDateInstalled(Timestamp dateInstalled) {
        this.dateInstalled = dateInstalled;
    }

    public Boolean getCorporate() {
        return corporate;
    }

    public void setCorporate(Boolean corporate) {
        this.corporate = corporate;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
