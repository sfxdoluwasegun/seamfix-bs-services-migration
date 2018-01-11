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
 * @since Jul 7, 2016, 1:17:54 PM
 */
@XmlRootElement(name = "image_characteristics")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageCharacteristicsPojo implements Serializable {

    @XmlElement(name = "number_channels")
    private PropertyInfoPojo numberChannels;
    @XmlElement(name = "background_pad_type")
    private PropertyInfoPojo backgroundPadType;
    @XmlElement(name = "percent_background_uniformity")
    private PropertyInfoPojo percentBackgroundUniformity;
    @XmlElement(name = "degree_of_clutter")
    private PropertyInfoPojo degreeOfClutter;
    @XmlElement(name = "background_type")
    private PropertyInfoPojo backgroundType;

    public PropertyInfoPojo getNumberChannels() {
        return numberChannels;
    }

    public void setNumberChannels(PropertyInfoPojo numberChannels) {
        this.numberChannels = numberChannels;
    }

    public PropertyInfoPojo getBackgroundPadType() {
        return backgroundPadType;
    }

    public void setBackgroundPadType(PropertyInfoPojo backgroundPadType) {
        this.backgroundPadType = backgroundPadType;
    }

    public PropertyInfoPojo getPercentBackgroundUniformity() {
        return percentBackgroundUniformity;
    }

    public void setPercentBackgroundUniformity(PropertyInfoPojo percentBackgroundUniformity) {
        this.percentBackgroundUniformity = percentBackgroundUniformity;
    }

    public PropertyInfoPojo getDegreeOfClutter() {
        return degreeOfClutter;
    }

    public void setDegreeOfClutter(PropertyInfoPojo degreeOfClutter) {
        this.degreeOfClutter = degreeOfClutter;
    }

    public PropertyInfoPojo getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(PropertyInfoPojo backgroundType) {
        this.backgroundType = backgroundType;
    }
    
    
}
