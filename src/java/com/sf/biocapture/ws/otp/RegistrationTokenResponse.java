/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.otp;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 *
 * @author Marcel
 * @since 11-Oct-2016, 20:24:32
 */
public class RegistrationTokenResponse extends ResponseData {

    private String token;

    public RegistrationTokenResponse() {
    }

    public RegistrationTokenResponse(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
