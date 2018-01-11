/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since Jul 8, 2016, 8:09:14 AM
 */
@XmlRootElement(name = "property")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PropertyInfoPojo implements Serializable {

    @XmlAttribute(name = "units")
    private String units;
    @XmlAttribute(name = "min")
    private String min;
    @XmlAttribute(name = "max")
    private String max;
    @XmlAttribute(name = "qWeight")
    private String qWeight;
    @XmlAttribute(name = "pref")
    private String pref;
    @XmlAttribute(name = "cMin")
    private String cMin;
    @XmlAttribute(name = "cMax")
    private String cMax;

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getqWeight() {
        return qWeight;
    }

    public void setqWeight(String qWeight) {
        this.qWeight = qWeight;
    }

    public String getPref() {
        return pref;
    }

    public void setPref(String pref) {
        this.pref = pref;
    }

    public String getcMin() {
        return cMin;
    }

    public void setcMin(String cMin) {
        this.cMin = cMin;
    }

    public String getcMax() {
        return cMax;
    }

    public void setcMax(String cMax) {
        this.cMax = cMax;
    }

}
