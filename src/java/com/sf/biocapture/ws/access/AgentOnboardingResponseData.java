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
 * @author Marcel
 * @since 12-Aug-2016, 11:27:21
 */
public class AgentOnboardingResponseData extends ResponseData {

    private String email;
    private String msisdn;
    private String firstName;
    private String lastName;

    public AgentOnboardingResponseData() {
        super(ResponseCodeEnum.ERROR);
    }

    public AgentOnboardingResponseData(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
