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
@XmlRootElement(name = "face_characteristics")
@XmlAccessorType(XmlAccessType.FIELD)
public class FaceCharacteristicsPojo implements Serializable {

    @XmlElement(name = "pose_angle_yaw")
    private PropertyInfoPojo poseAngleYaw;
    @XmlElement(name = "pose_angle_pitch")
    private PropertyInfoPojo poseAnglePitch;
    @XmlElement(name = "smile_likelihood")
    private PropertyInfoPojo smileLikelihood;
    @XmlElement(name = "facial_dynamic_range")
    private PropertyInfoPojo facialDynamicRange;
    @XmlElement(name = "percent_facial_brightness")
    private PropertyInfoPojo percentFacialBrightness;
    @XmlElement(name = "percent_facial_saturation")
    private PropertyInfoPojo percentFacialSaturation;
    @XmlElement(name = "brightness_score")
    private PropertyInfoPojo brightnessScore;

    public PropertyInfoPojo getPoseAngleYaw() {
        return poseAngleYaw;
    }

    public void setPoseAngleYaw(PropertyInfoPojo poseAngleYaw) {
        this.poseAngleYaw = poseAngleYaw;
    }

    public PropertyInfoPojo getPoseAnglePitch() {
        return poseAnglePitch;
    }

    public void setPoseAnglePitch(PropertyInfoPojo poseAnglePitch) {
        this.poseAnglePitch = poseAnglePitch;
    }

    public PropertyInfoPojo getSmileLikelihood() {
        return smileLikelihood;
    }

    public void setSmileLikelihood(PropertyInfoPojo smileLikelihood) {
        this.smileLikelihood = smileLikelihood;
    }

    public PropertyInfoPojo getFacialDynamicRange() {
        return facialDynamicRange;
    }

    public void setFacialDynamicRange(PropertyInfoPojo facialDynamicRange) {
        this.facialDynamicRange = facialDynamicRange;
    }

    public PropertyInfoPojo getPercentFacialBrightness() {
        return percentFacialBrightness;
    }

    public void setPercentFacialBrightness(PropertyInfoPojo percentFacialBrightness) {
        this.percentFacialBrightness = percentFacialBrightness;
    }

    public PropertyInfoPojo getPercentFacialSaturation() {
        return percentFacialSaturation;
    }

    public void setPercentFacialSaturation(PropertyInfoPojo percentFacialSaturation) {
        this.percentFacialSaturation = percentFacialSaturation;
    }

    public PropertyInfoPojo getBrightnessScore() {
        return brightnessScore;
    }

    public void setBrightnessScore(PropertyInfoPojo brightnessScore) {
        this.brightnessScore = brightnessScore;
    }
    
    
}
