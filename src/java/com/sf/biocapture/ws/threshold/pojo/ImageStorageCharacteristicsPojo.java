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
@XmlRootElement(name = "image_storage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageStorageCharacteristicsPojo implements Serializable {

    @XmlElement(name = "jpeg_quality_level")
    private PropertyInfoPojo jpegQualityLevel;
    @XmlElement(name = "j2k_compression_ratio")
    private PropertyInfoPojo j2kCompressionRatio;
    @XmlElement(name = "j2k_roi_foreground_compression_ratio")
    private PropertyInfoPojo j2kRoiForegroundCompressionRatio;
    @XmlElement(name = "j2k_roi_background_compression_ratio")
    private PropertyInfoPojo j2kRoiBackgroundCompressionRatio;
    @XmlElement(name = "image_format")
    private PropertyInfoPojo imageFormat;

    public PropertyInfoPojo getJpegQualityLevel() {
        return jpegQualityLevel;
    }

    public void setJpegQualityLevel(PropertyInfoPojo jpegQualityLevel) {
        this.jpegQualityLevel = jpegQualityLevel;
    }

    public PropertyInfoPojo getJ2kCompressionRatio() {
        return j2kCompressionRatio;
    }

    public void setJ2kCompressionRatio(PropertyInfoPojo j2kCompressionRatio) {
        this.j2kCompressionRatio = j2kCompressionRatio;
    }

    public PropertyInfoPojo getJ2kRoiForegroundCompressionRatio() {
        return j2kRoiForegroundCompressionRatio;
    }

    public void setJ2kRoiForegroundCompressionRatio(PropertyInfoPojo j2kRoiForegroundCompressionRatio) {
        this.j2kRoiForegroundCompressionRatio = j2kRoiForegroundCompressionRatio;
    }

    public PropertyInfoPojo getJ2kRoiBackgroundCompressionRatio() {
        return j2kRoiBackgroundCompressionRatio;
    }

    public void setJ2kRoiBackgroundCompressionRatio(PropertyInfoPojo j2kRoiBackgroundCompressionRatio) {
        this.j2kRoiBackgroundCompressionRatio = j2kRoiBackgroundCompressionRatio;
    }

    public PropertyInfoPojo getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(PropertyInfoPojo imageFormat) {
        this.imageFormat = imageFormat;
    }
    
    
}
