/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws;

import java.io.Serializable;

/**
 *
 * @author Marcel
 * @since 19-Aug-2016, 18:22:31
 */
public class ResponseData implements Serializable {

    private Integer code;
    private String description;

    public ResponseData() {
    }

    public ResponseData(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.getCode();
        this.description = responseCodeEnum.getDescription();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(ResponseCodeEnum responseCodeEnum) {
        this.code = responseCodeEnum.getCode();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
