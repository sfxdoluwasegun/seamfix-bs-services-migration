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
@XmlRootElement(name = "anomalies")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnomaliesCharacteristicsPojo implements Serializable {

    @XmlElement(name = "focus_likelihood")
    private PropertyInfoPojo focusLikelihood;
    @XmlElement(name = "sharpness_likelihood")
    private PropertyInfoPojo sharpnessLikelihood;
    @XmlElement(name = "glasses_likelihood")
    private PropertyInfoPojo glassesLikelihood;
    @XmlElement(name = "dark_glasses_likelihood")
    private PropertyInfoPojo darkGlassesLikelihood;
    @XmlElement(name = "glare_likelihood")
    private PropertyInfoPojo glareLikelihood;
    @XmlElement(name = "forehead_covering_likelihood")
    private PropertyInfoPojo foreheadCoveringLikelihood;

    public PropertyInfoPojo getFocusLikelihood() {
        return focusLikelihood;
    }

    public void setFocusLikelihood(PropertyInfoPojo focusLikelihood) {
        this.focusLikelihood = focusLikelihood;
    }

    public PropertyInfoPojo getSharpnessLikelihood() {
        return sharpnessLikelihood;
    }

    public void setSharpnessLikelihood(PropertyInfoPojo sharpnessLikelihood) {
        this.sharpnessLikelihood = sharpnessLikelihood;
    }

    public PropertyInfoPojo getGlassesLikelihood() {
        return glassesLikelihood;
    }

    public void setGlassesLikelihood(PropertyInfoPojo glassesLikelihood) {
        this.glassesLikelihood = glassesLikelihood;
    }

    public PropertyInfoPojo getDarkGlassesLikelihood() {
        return darkGlassesLikelihood;
    }

    public void setDarkGlassesLikelihood(PropertyInfoPojo darkGlassesLikelihood) {
        this.darkGlassesLikelihood = darkGlassesLikelihood;
    }

    public PropertyInfoPojo getGlareLikelihood() {
        return glareLikelihood;
    }

    public void setGlareLikelihood(PropertyInfoPojo glareLikelihood) {
        this.glareLikelihood = glareLikelihood;
    }

    public PropertyInfoPojo getForeheadCoveringLikelihood() {
        return foreheadCoveringLikelihood;
    }

    public void setForeheadCoveringLikelihood(PropertyInfoPojo foreheadCoveringLikelihood) {
        this.foreheadCoveringLikelihood = foreheadCoveringLikelihood;
    }
    
    
}
