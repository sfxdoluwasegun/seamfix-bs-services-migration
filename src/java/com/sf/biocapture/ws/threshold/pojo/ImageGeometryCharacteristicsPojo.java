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
@XmlRootElement(name = "image_geometry")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageGeometryCharacteristicsPojo implements Serializable {

    @XmlElement(name = "image_width")
    private PropertyInfoPojo imageWidth;
    @XmlElement(name = "image_height")
    private PropertyInfoPojo imageHeight;
    @XmlElement(name = "eye_separation")
    private PropertyInfoPojo eyeSeparation;
    @XmlElement(name = "eye_axis_location_ratio")
    private PropertyInfoPojo eyeAxisLocationRatio;
    @XmlElement(name = "centerline_location_ratio")
    private PropertyInfoPojo centerlineLocationRatio;
    @XmlElement(name = "image_width_to_head_width_ratio")
    private PropertyInfoPojo imageWidthToHeadWidthRatio;
    @XmlElement(name = "head_height_to_image_height_ratio")
    private PropertyInfoPojo headHeightToImageHeightRatio;
    @XmlElement(name = "eye_axis_angle")
    private PropertyInfoPojo eyeAxisAngle;
    @XmlElement(name = "eye_axis_location_ratio_child")
    private PropertyInfoPojo eyeAxisLocationRatioChild;

    public PropertyInfoPojo getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(PropertyInfoPojo imageWidth) {
        this.imageWidth = imageWidth;
    }

    public PropertyInfoPojo getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(PropertyInfoPojo imageHeight) {
        this.imageHeight = imageHeight;
    }

    public PropertyInfoPojo getEyeSeparation() {
        return eyeSeparation;
    }

    public void setEyeSeparation(PropertyInfoPojo eyeSeparation) {
        this.eyeSeparation = eyeSeparation;
    }

    public PropertyInfoPojo getEyeAxisLocationRatio() {
        return eyeAxisLocationRatio;
    }

    public void setEyeAxisLocationRatio(PropertyInfoPojo eyeAxisLocationRatio) {
        this.eyeAxisLocationRatio = eyeAxisLocationRatio;
    }

    public PropertyInfoPojo getCenterlineLocationRatio() {
        return centerlineLocationRatio;
    }

    public void setCenterlineLocationRatio(PropertyInfoPojo centerlineLocationRatio) {
        this.centerlineLocationRatio = centerlineLocationRatio;
    }

    public PropertyInfoPojo getImageWidthToHeadWidthRatio() {
        return imageWidthToHeadWidthRatio;
    }

    public void setImageWidthToHeadWidthRatio(PropertyInfoPojo imageWidthToHeadWidthRatio) {
        this.imageWidthToHeadWidthRatio = imageWidthToHeadWidthRatio;
    }

    public PropertyInfoPojo getHeadHeightToImageHeightRatio() {
        return headHeightToImageHeightRatio;
    }

    public void setHeadHeightToImageHeightRatio(PropertyInfoPojo headHeightToImageHeightRatio) {
        this.headHeightToImageHeightRatio = headHeightToImageHeightRatio;
    }

    public PropertyInfoPojo getEyeAxisAngle() {
        return eyeAxisAngle;
    }

    public void setEyeAxisAngle(PropertyInfoPojo eyeAxisAngle) {
        this.eyeAxisAngle = eyeAxisAngle;
    }

    public PropertyInfoPojo getEyeAxisLocationRatioChild() {
        return eyeAxisLocationRatioChild;
    }

    public void setEyeAxisLocationRatioChild(PropertyInfoPojo eyeAxisLocationRatioChild) {
        this.eyeAxisLocationRatioChild = eyeAxisLocationRatioChild;
    }
    
    
}
