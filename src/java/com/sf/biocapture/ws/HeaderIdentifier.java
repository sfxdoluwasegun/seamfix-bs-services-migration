/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws;

import java.io.Serializable;

/**
 *
 * @author Marcel
 * @since 25-Apr-2017
 */
public class HeaderIdentifier implements Serializable {

    private String tag;
    private String mac;
    private String emailAddress;
    private String userUUID;
    private String refDeviceId;
    private String realTimeDeviceId;
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }
    
    public String getRefDeviceId(){
        return refDeviceId;
    }
    
    public void setRefDeviceId(String refDeviceId){
        this.refDeviceId = refDeviceId;
    }

    public String getRealTimeDeviceId(){
        return realTimeDeviceId;
    }
    
    public void setRealTimeDeviceId(String realTimeDeviceId){
        this.realTimeDeviceId = realTimeDeviceId;
    }
    
    @Override
    public String toString() {
        return "HeaderIdentifier{" + "tag=" + tag + ", mac=" + mac + ", emailAddress=" + emailAddress + ", userUUID=" + userUUID +", refDeviceId="+ refDeviceId+ ", realTimeDeviceId="+realTimeDeviceId+ '}';
    }
    
}
