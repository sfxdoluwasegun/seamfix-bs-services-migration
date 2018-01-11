/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.audit.pojo;

/**
 *
 * @author Marcel
 * @since Jul 1, 2016, 11:00:23 AM
 */
public class ClientActivityLogPojo {

    private Long id;
    private String macAddress;
    private String kitTag;
    private String username;
    private String fullName;
    private String activity;
    private String enrollmentRef;
    private Long duration;
    private Long activityStartTime;
    private Long activityEndTime;
    private String activityCode;
    private String refDeviceId;
    private String realTimeDeviceId;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getKitTag() {
        return kitTag;
    }

    public void setKitTag(String kitTag) {
        this.kitTag = kitTag;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getEnrollmentRef() {
        return enrollmentRef;
    }

    public void setEnrollmentRef(String enrollmentRef) {
        this.enrollmentRef = enrollmentRef;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Long activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public Long getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(Long activityEndTime) {
        this.activityEndTime = activityEndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getRefDeviceId() {
        return refDeviceId;
    }

    public void setRefDeviceId(String refDeviceId) {
        this.refDeviceId = refDeviceId;
    }

    public String getRealTimeDeviceId() {
        return realTimeDeviceId;
    }

    public void setRealTimeDeviceId(String realTimeDeviceId) {
        this.realTimeDeviceId = realTimeDeviceId;
    }

    
    
}
