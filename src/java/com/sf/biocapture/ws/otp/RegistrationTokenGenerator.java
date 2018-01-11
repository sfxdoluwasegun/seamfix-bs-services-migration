/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.otp;

import com.sf.biocapture.ds.TokenGenerator;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.OTPStatusRecord;
import com.sf.biocapture.entity.enums.OtpStatusRecordTypeEnum;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author Marcel
 * @since 11-Oct-2016, 09:07:23
 */
@Stateless
public class RegistrationTokenGenerator extends TokenGenerator {
    
    @Inject
    AccessDS accessDS;

    @Override
    public String generateOTP(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String msisdn) {
        int otpLength = 10;
        try {
            otpLength = Integer.valueOf(accessDS.getSettingValue("REG-TOKEN-SCHEMA-LENGTH", "10"));
        } catch (NumberFormatException e) {
            logger.error("", e);
        }
        String otpSchema = accessDS.getSettingValue("REG-TOKEN-SCHEMA-TYPE", "DIGITS");
        String otpToken = "";
        switch (otpSchema) {
            case "ALPHABETS":
                otpToken = generateAlphabets(otpLength);
                break;
            case "DIGITS":
                otpToken = generateDigits(otpLength);
                break;
            case "ALPHANUMERIC":
                otpToken = generateToken(otpLength);
                break;
            default:
                //defaults to number otp
                otpToken = generateDigits(otpLength);
        }

        long otpExpiration = 5; //This time is in minutes
        String val = accessDS.getSettingValue("REG-TOKEN-EXPIRATION-TIME-IN-MINUTES", "5");
        try {
            if ((val != null)) {
                otpExpiration = Long.valueOf(val);
            }
        } catch (NumberFormatException e) {
            logger.error("", e);
        }

        OTPStatusRecord otp = new OTPStatusRecord();
        otp.setMsisdn(msisdn);
        otp.setOtp(otpToken);
        otp.setOtpUsed(Boolean.FALSE);
        otp.setOtpStatusRecordTypeEnum(otpStatusRecordTypeEnum);
        Timestamp timeGenerated = new Timestamp(new Date().getTime());
        long otpExpirationInMilliseconds = otpExpiration * 60 * 1000;
        Timestamp expirationTime = new Timestamp(timeGenerated.getTime() + otpExpirationInMilliseconds);
        otp.setTimeGenerated(timeGenerated);
        otp.setExpirationTime(expirationTime);

        Serializable s = getDbService().create(otp);

        String resp = s + ":" + otpToken + ":" + otpExpiration;
        return resp;
    }
}
