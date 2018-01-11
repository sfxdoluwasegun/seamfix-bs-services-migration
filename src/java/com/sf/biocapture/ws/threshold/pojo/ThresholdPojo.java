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
 * @since Jul 4, 2016, 6:11:18 PM
 */
public class ThresholdPojo implements Serializable {

    private String passport;
    private FingerprintProfilePojo fingerprint;

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public FingerprintProfilePojo getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(FingerprintProfilePojo fingerprint) {
        this.fingerprint = fingerprint;
    }

}
