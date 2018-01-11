/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;

/**
 *
 * @author Marcel
 * @since Jul 12, 2016, 2:29:16 PM
 */
public class ResponseItemPojo implements Serializable {

    private String responseType;
    private Object data;

    public ResponseItemPojo() {
    }

    public ResponseItemPojo(String responseType, Object data) {
        this.responseType = responseType;
        this.data = data;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    
}
