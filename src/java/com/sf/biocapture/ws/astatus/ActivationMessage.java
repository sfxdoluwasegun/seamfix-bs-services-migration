/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.astatus;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since May 31, 2017
 */
@XmlRootElement(name = "Notification")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ActivationMessage implements Serializable {
//<?xml version="1.0" encoding="UTF-8"?>
//<Notification xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:thir="http://www.example.org/ThirdParty/">
//              <ActivityType>SIM_ACTIVATION_NOTIFICATION</ActivityType>
//              <ActivityMSISDN>place msisdn here</ActivityMSISDN>
//              <ActivationDate>timestamp format YYYYMMDD HH24:MM:SS</ActivationDate>
//              <SimSerial>Put Sim Serial</SimSerial>
//              <TransactionID>Put Transaction ID here</TransactionID>
//              <ActivationStatus>Put Activation Status here</ActivationStatus>
//           </Notification>

    @XmlElement(name = "ActivityType")
    private String activityType;
    @XmlElement(name = "ActivityMSISDN")
    private String msisdn;
    @XmlElement(name = "ActivationDate")
    private String activationDate;
    @XmlElement(name = "TransactionID")
    private String transactionID;
    @XmlElement(name = "SimSerial")
    private String simSerial;
    @XmlElement(name = "ActivationStatus")
    private String activationStatus;

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getSimSerial() {
        return simSerial;
    }

    public void setSimSerial(String simSerial) {
        this.simSerial = simSerial;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    @Override
    public String toString() {
        return "ActivationMessage{" + "activityType=" + activityType + ", msisdn=" + msisdn + ", activationDate=" + activationDate + ", transactionID=" + transactionID + ", simSerial=" + simSerial + ", activationStatus=" + activationStatus + '}';
    }

}
