/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;

/**
 *
 * @author Marcel
 * @since Jul 12, 2016, 2:29:16 PM
 */
public class RequestPojo implements Serializable {

    private Integer thresholdVersion;
    private String tagName;
    private String macAddress;
    private String username;

    public RequestPojo() {
    }

    public RequestPojo(Integer thresholdVersion, String tagName, String macAddress, String username) {
        this.thresholdVersion = thresholdVersion;
        this.tagName = tagName;
        this.macAddress = macAddress;
        this.username = username;
    }

    public Integer getThresholdVersion() {
        return thresholdVersion;
    }

    public void setThresholdVersion(Integer thresholdVersion) {
        this.thresholdVersion = thresholdVersion;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
