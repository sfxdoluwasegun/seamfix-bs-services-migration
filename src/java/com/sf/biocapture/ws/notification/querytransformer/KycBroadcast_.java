/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.notification.querytransformer;

import java.util.Date;

/**
 *
 * @author Marcel
 * @since Jul 10, 2017 - 1:51:10 PM
 */
public class KycBroadcast_ {

    private Long pk;
    private String message;
    private boolean global;
    private boolean expired;
    private Long nodeFk;
    private Long userFk;
    private Date lastModified;

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Long getNodeFk() {
        return nodeFk;
    }

    public void setNodeFk(Long nodeFk) {
        this.nodeFk = nodeFk;
    }

    public Long getUserFk() {
        return userFk;
    }

    public void setUserFk(Long userFk) {
        this.userFk = userFk;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
}
