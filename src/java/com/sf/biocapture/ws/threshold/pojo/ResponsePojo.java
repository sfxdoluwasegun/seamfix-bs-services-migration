/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Marcel
 * @since Jul 12, 2016, 2:29:16 PM
 */
public class ResponsePojo implements Serializable {

    public enum ResponseCode {

        UP_TO_DATE("1", "Threshold is up to date"),
        SUCCESS("0", "Success"),
        ERROR("-1", "Unable to process the request"),;

        private ResponseCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        private String code;
        private String description;
    }

    private String code;
    private String description;
    private Integer thresholdVersion;
    private List<ResponseItemPojo> profileItems;

    public ResponsePojo() {
    }

    public ResponsePojo(Integer thresholdVersion, List<ResponseItemPojo> responseItemPojos, ResponseCode responseCode) {
        this.code = responseCode.code;
        this.description = responseCode.description;
        this.thresholdVersion = thresholdVersion;
        this.profileItems = responseItemPojos;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getThresholdVersion() {
        return thresholdVersion;
    }

    public void setThresholdVersion(Integer thresholdVersion) {
        this.thresholdVersion = thresholdVersion;
    }

    public List<ResponseItemPojo> getProfileItems() {
        return profileItems;
    }

    public void setProfileItems(List<ResponseItemPojo> profileItems) {
        this.profileItems = profileItems;
    }
}
