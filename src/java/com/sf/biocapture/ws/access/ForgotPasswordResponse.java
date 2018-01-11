/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.access;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;


/**
 *
 * @author best
 */

public class ForgotPasswordResponse extends ResponseData {
    
   private String name;
   private String msisdn;
   private String otp;
   private String otpExpirationTime;
  

    public ForgotPasswordResponse(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public ForgotPasswordResponse() {
      
    }
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpExpirationTime() {
        return otpExpirationTime;
    }

    public void setOtpExpirationTime(String otpExpirationTime) {
        this.otpExpirationTime = otpExpirationTime;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   
   
    
}
