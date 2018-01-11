/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.access;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Marcel
 * @since 19-Aug-2016, 18:48:46
 */
public class AccessResponseData extends ResponseData {

    private final String KEY = ">";
    private String name;
    private String msisdn;
    private boolean firstLogin = false;
    private String passwordSyntaxRegex;
    private String passwordSyntaxGuide;

    private Set<String> privileges = new HashSet<String>();
    private String otp;
    private String otpExpirationTime;
    private boolean cached = false;
    private boolean otpRequired;

    public AccessResponseData() {
        super(ResponseCodeEnum.ERROR);
    }

    public AccessResponseData(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getPasswordSyntaxRegex() {
        return passwordSyntaxRegex;
    }

    public void setPasswordSyntaxRegex(String passwordSyntaxRegex) {
        this.passwordSyntaxRegex = passwordSyntaxRegex;
    }

    public String getPasswordSyntaxGuide() {
        return passwordSyntaxGuide;
    }

    public void setPasswordSyntaxGuide(String passwordSyntaxGuide) {
        this.passwordSyntaxGuide = passwordSyntaxGuide;
    }

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public Set<String> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<String> privileges) {
		this.privileges = privileges;
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

    public boolean isOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public void from(AccessResponse accessResponse) {
        this.setDescription(accessResponse.getMessage());
        this.setFirstLogin(accessResponse.isFirstLogin());
        this.setName(accessResponse.getName());
        this.setPasswordSyntaxGuide(accessResponse.getPasswordSyntaxGuide());
        this.setPasswordSyntaxRegex(accessResponse.getPasswordSyntaxRegex());
    }

    public String concatenate() {
        StringBuilder response = new StringBuilder();
        response.append(getCode()).append(KEY).append(getDescription()).append(KEY).append(getMsisdn())
                .append(KEY).append(getName()).append(KEY).append(isOtpRequired()).append(KEY).append(getOtp()).append(KEY).append(getOtpExpirationTime()).append(KEY).append(reducePrivileges());
        return response.toString();
    }
    
    private String reducePrivileges () {
        String reduced = "";
        if (privileges != null) {
            for (String p : privileges) {
                reduced += p;
            }
        }
        return reduced;
    }
}