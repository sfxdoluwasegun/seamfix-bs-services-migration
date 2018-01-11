/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since 26-Aug-2016, 12:58:08
 */
@XmlRootElement(name = "fingers")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FingerprintTypeProfilePojo implements Serializable {

    @XmlElement(name = "thumb")
    private FingerprintProfilePojo thumbFingerprintProfilePojo;
    @XmlElement(name = "index")
    private FingerprintProfilePojo indexFingerprintProfilePojo;
    @XmlElement(name = "middle")
    private FingerprintProfilePojo middleFingerprintProfilePojo;
    @XmlElement(name = "ring")
    private FingerprintProfilePojo ringFingerprintProfilePojo;
    @XmlElement(name = "last")
    private FingerprintProfilePojo lastFingerprintProfilePojo;

    public FingerprintProfilePojo getThumbFingerprintProfilePojo() {
        return thumbFingerprintProfilePojo;
    }

    public void setThumbFingerprintProfilePojo(FingerprintProfilePojo thumbFingerprintProfilePojo) {
        this.thumbFingerprintProfilePojo = thumbFingerprintProfilePojo;
    }

    public FingerprintProfilePojo getIndexFingerprintProfilePojo() {
        return indexFingerprintProfilePojo;
    }

    public void setIndexFingerprintProfilePojo(FingerprintProfilePojo indexFingerprintProfilePojo) {
        this.indexFingerprintProfilePojo = indexFingerprintProfilePojo;
    }

    public FingerprintProfilePojo getMiddleFingerprintProfilePojo() {
        return middleFingerprintProfilePojo;
    }

    public void setMiddleFingerprintProfilePojo(FingerprintProfilePojo middleFingerprintProfilePojo) {
        this.middleFingerprintProfilePojo = middleFingerprintProfilePojo;
    }

    public FingerprintProfilePojo getRingFingerprintProfilePojo() {
        return ringFingerprintProfilePojo;
    }

    public void setRingFingerprintProfilePojo(FingerprintProfilePojo ringFingerprintProfilePojo) {
        this.ringFingerprintProfilePojo = ringFingerprintProfilePojo;
    }

    public FingerprintProfilePojo getLastFingerprintProfilePojo() {
        return lastFingerprintProfilePojo;
    }

    public void setLastFingerprintProfilePojo(FingerprintProfilePojo lastFingerprintProfilePojo) {
        this.lastFingerprintProfilePojo = lastFingerprintProfilePojo;
    }
    
    
    
}
