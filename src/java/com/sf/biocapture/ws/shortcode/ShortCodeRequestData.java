/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.shortcode;

/**
 *
 * @author Marcel
 * @since 22-Sep-2016, 11:37:36
 */
public class ShortCodeRequestData {

    private final String KEY = ">"; //changing this will affect client applications

    private String email;
    private String password;
    private String tag;
    private String clientTime;
    private String msisdn;
    private String otp;
    private RequestTypeEnum requestTypeEnum;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getClientTime() {
        return clientTime;
    }

    public void setClientTime(String clientTime) {
        this.clientTime = clientTime;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public RequestTypeEnum getRequestTypeEnum() {
        return requestTypeEnum;
    }

    public void setRequestTypeEnum(RequestTypeEnum requestTypeEnum) {
        this.requestTypeEnum = requestTypeEnum;
    }

    public void readRequestData(String request) {
        if (request != null) {
            String data[] = request.split(KEY);
            requestTypeEnum = RequestTypeEnum.valueOf(data[0]);
            switch (requestTypeEnum) {
                case LGN:
                    email = data[1];
                    password = data[2];
                    tag = data[3];
                    clientTime = data[4];
                    break;
            }
        }
    }

}
