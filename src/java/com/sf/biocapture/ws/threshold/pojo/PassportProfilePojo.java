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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since Jul 7, 2016, 1:11:14 PM
 */
@XmlRootElement(name = "profile")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PassportProfilePojo implements Serializable {

    @XmlElement(name = "face_characteristics")
    private FaceCharacteristicsPojo faceCharacteristicsPojo;
    @XmlElement(name = "eye_characteristics")
    private EyeCharacteristicsPojo eyeCharacteristicsPojo;
    @XmlElement(name = "image_characteristics")
    private ImageCharacteristicsPojo imageCharacteristicsPojo;
    @XmlElement(name = "anomalies")
    private AnomaliesCharacteristicsPojo anomaliesCharacteristicsPojo;
    @XmlElement(name = "image_geometry")
    private ImageGeometryCharacteristicsPojo imageGeometryCharacteristicsPojo;
    @XmlElement(name = "image_storage")
    private ImageStorageCharacteristicsPojo imageStorageCharacteristicsPojo;
    @XmlAttribute(name = "version")
    private String version;

    public FaceCharacteristicsPojo getFaceCharacteristicsPojo() {
        return faceCharacteristicsPojo;
    }

    public void setFaceCharacteristicsPojo(FaceCharacteristicsPojo faceCharacteristicsPojo) {
        this.faceCharacteristicsPojo = faceCharacteristicsPojo;
    }

    public EyeCharacteristicsPojo getEyeCharacteristicsPojo() {
        return eyeCharacteristicsPojo;
    }

    public void setEyeCharacteristicsPojo(EyeCharacteristicsPojo eyeCharacteristicsPojo) {
        this.eyeCharacteristicsPojo = eyeCharacteristicsPojo;
    }

    public ImageCharacteristicsPojo getImageCharacteristicsPojo() {
        return imageCharacteristicsPojo;
    }

    public void setImageCharacteristicsPojo(ImageCharacteristicsPojo imageCharacteristicsPojo) {
        this.imageCharacteristicsPojo = imageCharacteristicsPojo;
    }

    public AnomaliesCharacteristicsPojo getAnomaliesCharacteristicsPojo() {
        return anomaliesCharacteristicsPojo;
    }

    public void setAnomaliesCharacteristicsPojo(AnomaliesCharacteristicsPojo anomaliesCharacteristicsPojo) {
        this.anomaliesCharacteristicsPojo = anomaliesCharacteristicsPojo;
    }

    public ImageGeometryCharacteristicsPojo getImageGeometryCharacteristicsPojo() {
        return imageGeometryCharacteristicsPojo;
    }

    public void setImageGeometryCharacteristicsPojo(ImageGeometryCharacteristicsPojo imageGeometryCharacteristicsPojo) {
        this.imageGeometryCharacteristicsPojo = imageGeometryCharacteristicsPojo;
    }

    public ImageStorageCharacteristicsPojo getImageStorageCharacteristicsPojo() {
        return imageStorageCharacteristicsPojo;
    }

    public void setImageStorageCharacteristicsPojo(ImageStorageCharacteristicsPojo imageStorageCharacteristicsPojo) {
        this.imageStorageCharacteristicsPojo = imageStorageCharacteristicsPojo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
