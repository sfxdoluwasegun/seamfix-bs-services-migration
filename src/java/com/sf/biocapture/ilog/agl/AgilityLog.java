/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ilog.agl;

import com.sf.biocapture.ilog.ILog;
import com.sf.biocapture.entity.audit.AgilityIntegrationLog;
import nw.orm.core.IEntity;

/**
 *
 * @author Marcel
 * @since Aug 13, 2017 - 4:38:20 PM
 */
public class AgilityLog implements ILog {

    private String requestType;
    private String transactionId;
    private AgilityIntegrationLog agilityIntegrationLog;
    private String requestPayload;
    private String responsePayload;

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public IEntity getEntity() {
        return agilityIntegrationLog;
    }

    @Override
    public String getRequestPayload() {
        return requestPayload;
    }

    @Override
    public String getResponsePayload() {
        return responsePayload;
    }

    @Override
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAgilityIntegrationLog(AgilityIntegrationLog agilityIntegrationLog) {
        this.agilityIntegrationLog = agilityIntegrationLog;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

}
