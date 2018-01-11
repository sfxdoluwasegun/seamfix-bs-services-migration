/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.biocapture.ws.threshold.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since Jul 8, 2016, 8:24:14 AM
 */
@XmlRootElement(name = "eye_characteristics")
@XmlAccessorType(XmlAccessType.FIELD)
public class EyeCharacteristicsPojo {

    @XmlElement(name = "eye_contrast")
    private PropertyInfoPojo eyeContrast;
    @XmlElement(name = "left_eye_closed_likelihood")
    private PropertyInfoPojo leftEyeClosedLikelihood;
    @XmlElement(name = "right_eye_closed_likelihood")
    private PropertyInfoPojo rightEyeClosedLikelihood;
    @XmlElement(name = "redeye_likelihood")
    private PropertyInfoPojo redeyeLikelihood;
    @XmlElement(name = "off_angle_gaze_likelihood")
    private PropertyInfoPojo offAngleGazeLikelihood;
    @XmlElement(name = "left_eye_valid_likelihood")
    private PropertyInfoPojo leftEyeValidLikelihood;
    @XmlElement(name = "right_eye_valid_likelihood")
    private PropertyInfoPojo rightEyeValidLikelihood;
    @XmlElement(name = "frame_covered_right_eye_likelihood")
    private PropertyInfoPojo frameCoveredRightEyeLikelihood;
    @XmlElement(name = "hair_covered_right_eye_likelihood")
    private PropertyInfoPojo hairCoveredRightEyeLikelihood;
    @XmlElement(name = "frame_covered_left_eye_likelihood")
    private PropertyInfoPojo frameCoveredLeftEyeLikelihood;
    @XmlElement(name = "hair_covered_left_eye_likelihood")
    private PropertyInfoPojo hairCoveredLeftEyeLikelihood;

    public PropertyInfoPojo getEyeContrast() {
        return eyeContrast;
    }

    public void setEyeContrast(PropertyInfoPojo eyeContrast) {
        this.eyeContrast = eyeContrast;
    }

    public PropertyInfoPojo getLeftEyeClosedLikelihood() {
        return leftEyeClosedLikelihood;
    }

    public void setLeftEyeClosedLikelihood(PropertyInfoPojo leftEyeClosedLikelihood) {
        this.leftEyeClosedLikelihood = leftEyeClosedLikelihood;
    }

    public PropertyInfoPojo getRightEyeClosedLikelihood() {
        return rightEyeClosedLikelihood;
    }

    public void setRightEyeClosedLikelihood(PropertyInfoPojo rightEyeClosedLikelihood) {
        this.rightEyeClosedLikelihood = rightEyeClosedLikelihood;
    }

    public PropertyInfoPojo getRedeyeLikelihood() {
        return redeyeLikelihood;
    }

    public void setRedeyeLikelihood(PropertyInfoPojo redeyeLikelihood) {
        this.redeyeLikelihood = redeyeLikelihood;
    }

    public PropertyInfoPojo getOffAngleGazeLikelihood() {
        return offAngleGazeLikelihood;
    }

    public void setOffAngleGazeLikelihood(PropertyInfoPojo offAngleGazeLikelihood) {
        this.offAngleGazeLikelihood = offAngleGazeLikelihood;
    }

    public PropertyInfoPojo getLeftEyeValidLikelihood() {
        return leftEyeValidLikelihood;
    }

    public void setLeftEyeValidLikelihood(PropertyInfoPojo leftEyeValidLikelihood) {
        this.leftEyeValidLikelihood = leftEyeValidLikelihood;
    }

    public PropertyInfoPojo getRightEyeValidLikelihood() {
        return rightEyeValidLikelihood;
    }

    public void setRightEyeValidLikelihood(PropertyInfoPojo rightEyeValidLikelihood) {
        this.rightEyeValidLikelihood = rightEyeValidLikelihood;
    }

    public PropertyInfoPojo getFrameCoveredRightEyeLikelihood() {
        return frameCoveredRightEyeLikelihood;
    }

    public void setFrameCoveredRightEyeLikelihood(PropertyInfoPojo frameCoveredRightEyeLikelihood) {
        this.frameCoveredRightEyeLikelihood = frameCoveredRightEyeLikelihood;
    }

    public PropertyInfoPojo getHairCoveredRightEyeLikelihood() {
        return hairCoveredRightEyeLikelihood;
    }

    public void setHairCoveredRightEyeLikelihood(PropertyInfoPojo hairCoveredRightEyeLikelihood) {
        this.hairCoveredRightEyeLikelihood = hairCoveredRightEyeLikelihood;
    }

    public PropertyInfoPojo getFrameCoveredLeftEyeLikelihood() {
        return frameCoveredLeftEyeLikelihood;
    }

    public void setFrameCoveredLeftEyeLikelihood(PropertyInfoPojo frameCoveredLeftEyeLikelihood) {
        this.frameCoveredLeftEyeLikelihood = frameCoveredLeftEyeLikelihood;
    }

    public PropertyInfoPojo getHairCoveredLeftEyeLikelihood() {
        return hairCoveredLeftEyeLikelihood;
    }

    public void setHairCoveredLeftEyeLikelihood(PropertyInfoPojo hairCoveredLeftEyeLikelihood) {
        this.hairCoveredLeftEyeLikelihood = hairCoveredLeftEyeLikelihood;
    }
    
}
