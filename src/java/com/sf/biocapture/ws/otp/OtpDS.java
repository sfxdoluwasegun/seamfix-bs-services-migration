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

import nw.commons.BCrypt;

import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author @wizzyclems
 * @author Marcel Ugwu
 */
@Stateless
public class OtpDS extends TokenGenerator {
    
    @Inject
    AccessDS accessDS;

    public String generateOTP(OtpStatusRecordTypeEnum otpStatusRecordTypeEnum, String msisdn) {
        int otpLength = 10;
        try {
            otpLength = Integer.valueOf(accessDS.getSettingValue("OTP-SCHEMA-LENGTH", "10"));
        } catch (NumberFormatException e) {
            logger.error("", e);
        }
        String otpSchema = accessDS.getSettingValue("OTP-SCHEMA-TYPE", "DIGITS");
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
        
        long otpExpirationTime = 5; //This time is in minutes
        String settingsValue = accessDS.getSettingValue("OTP-EXPIRATION-TIME-IN-MINUTES", "5");
        if ((settingsValue != null) && NumberUtils.isDigits(settingsValue)) {
            otpExpirationTime = Long.valueOf(settingsValue);
        }

        OTPStatusRecord otpStatusRecord = new OTPStatusRecord();
        otpStatusRecord.setMsisdn(msisdn);
        if(otpStatusRecordTypeEnum.equals(OtpStatusRecordTypeEnum.VTU_AGENT_VALIDATION)){
        	String hashOTP = accessDS.getSettingValue("VTU-VALIDATION-HASH-OTP", "true");
        	if(Boolean.valueOf(hashOTP)){
        		//hash OTP
        		String otpHash = BCrypt.hashpw(otpToken, BCrypt.gensalt());
        		logger.debug("OTP hash: {}", otpHash);
        		otpStatusRecord.setOtp(otpHash);
        	}else{
        		//save OTP as plain text
        		otpStatusRecord.setOtp(otpToken);
        	}
        }else{
        	otpStatusRecord.setOtp(otpToken);
        }
        otpStatusRecord.setOtpUsed(Boolean.FALSE);
        otpStatusRecord.setOtpStatusRecordTypeEnum(otpStatusRecordTypeEnum);
        Timestamp timeGenerated = new Timestamp(new Date().getTime());
        long otpExpirationInMilliseconds = otpExpirationTime * 60 * 1000;
        Timestamp expirationTime = new Timestamp(timeGenerated.getTime() + otpExpirationInMilliseconds);
        otpStatusRecord.setTimeGenerated(timeGenerated);
        otpStatusRecord.setExpirationTime(expirationTime);

        Serializable s = getDbService().create(otpStatusRecord);

        String resp = s + ":" + otpToken + ":" + otpExpirationTime;
        return resp;
    }
}
