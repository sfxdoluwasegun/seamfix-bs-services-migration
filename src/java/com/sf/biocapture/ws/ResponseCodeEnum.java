/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws;

/**
 *
 * @author Marcel
 * @since 19-Aug-2016, 18:24:57
 */
public enum ResponseCodeEnum {

    SUCCESS(0, "Success"),
    ERROR(-1, "Error"),
    FAILED_AUTHENTICATION(-2, "Failed authentication"),
    INVALID_INPUT(-3, "Invalid input was provided"),
    INACTIVE_ACCOUNT(-4, "Inactive Account"),
    BLACKLISTED_KIT(-5, "Kit is blacklisted"),
    ONBOARDING_PENDING(-6, "Agent is not onboarded yet"),
    INCOMPLETE_BIOMETRICS(-7, "Incomplete biometrics"),
    ALREADY_ONBOARDED(-8, "Agent is already onboarded"),
    OFFLINE_LOGIN_NOT_ALLOWED(-9, "Offline Login is not allowed"),
    LOCKED_ACCOUNT(-10, "Locked Account"),
    FAILED_OTP_GENERATION(-10,"Failed to generate otp"), //TODO change duplicated response code
    USER_NOT_FOUND(-11, "User not found"),
    INVALID_VTU_NUMBER(-12, "Agent's VTU number is invalid or inactive"),
    VTU_TRANSACTION_FAILED(-17, "VTU transaction failed"),
    KIT_NOT_FOUND(-13, "Kit not found"),
    UNSUCCESSFUL_TRANSACTION(-14, "Vending failed. Message: "),
    RECORD_NOT_FOUND(-15,"Record was not found");

    private ResponseCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
