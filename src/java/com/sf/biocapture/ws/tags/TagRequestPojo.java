/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.tags;

/**
 *
 * @author Marcel
 * @since Sep 21, 2017 - 10:49:29 AM
 */
public class TagRequestPojo {

    private String deviceId;
    private String appVersion;
    private String deviceType;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "TagRequestPojo{" + "deviceId=" + deviceId + ", appVersion=" + appVersion + ", deviceType=" + deviceType + '}';
    }

}
