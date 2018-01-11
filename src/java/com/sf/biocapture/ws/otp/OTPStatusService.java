package com.sf.biocapture.ws.otp;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.entity.enums.OtpStatusRecordTypeEnum;
import java.io.IOException;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import javax.inject.Inject;
import javax.ws.rs.Path;
import nw.orm.core.exception.NwormQueryException;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author Marcel Ugwu
 */
@Path("/otp")
public class OTPStatusService extends BsClazz implements IOtpService {

    @Inject
    private BioCache cache;

    @Inject
    OtpDS otpDS;
    @Inject
    RegistrationTokenGenerator registrationTokenGenerator;

    @Inject
    KannelSMS kSms;

    String cacheKey = "OTP-";

    /**
     * * Status 0 - invalid, 1 - valid, 2 - expired, 3 - no otp entry,4 -
     * invalid input parameters, non-positive-value - error
     *
     * @param otp
     * @param msisdn
     * @return
     */
    @Override
    public OTPStatusResponse checkOTPStatus(String otp, String msisdn) {
        logger.debug("OTP is : " + otp + " ,Msisdn : " + msisdn);

        OTPStatusResponse response = new OTPStatusResponse();

        //Status  0 - false, 1 - true, non-positive-value - error
        if ((otp == null) || otp.isEmpty() || (msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            response.setMessage("Both the msisdn and the OTP are required inputs. Note that msisdn must be 11 digits.");
            response.setStatus(4);
            return response;
        }

        int code = 0;
        try {
            code = otpDS.verifyOtpStatus(OtpStatusRecordTypeEnum.LOGIN, otp, msisdn);
        }catch(NwormQueryException e){
            response.setMessage("An error occurred while verifying your otp. Please try again later.");
            response.setStatus(-1);
            return response;
        }

        String msg = "";
        switch (code) {
            case 1:
                msg = "The specified otp and msisdn combination is valid.";
                break;
            case 2:
                msg = "This otp has expired. Please request for a new one.";
                break;
            case 3:
                msg = "There is no record with the otp, msisdn combination.";
                break;
            case 4:
                msg = "Both the msisdn and the OTP are required inputs. Note that msisdn must be 11 digits.";
                break;
        }
        response.setMessage(msg);
        response.setStatus(code);
        return response;
    }

    /**
     * * * Status 0 - failed to generate/send otp, 1 - success, 4 - invalid
     * input parameters, non-positive-value - error
     *
     * @param msisdn
     * @return
     */
    @Override
    public OTPStatusResponse requestOTP(String msisdn) {
        logger.debug("now sending otp to user with msisdn : " + msisdn);
        OTPStatusResponse response = new OTPStatusResponse();
        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            response.setMessage("The specified msisdn is invalid. Note that msisdn must be 11 digits.");
            response.setStatus(4);
            return response;
        }

        Object obj = cache.getItem(cacheKey + msisdn);
        if (obj == null) {
            if (otpDS.phoneNumberExist(msisdn)) {
                //stores the msisdn for 1hr in the cache
                cache.setItem(cacheKey + msisdn, msisdn, 60 * 60);
            } else {
                response.setMessage("This is not a valid msisdn. Cannot be found in our records.");
                response.setStatus(4);
                return response;
            }
        }

        try {   //s + ":" + otpToken + ":" + otpExpiration;
            String resp = otpDS.generateOTP(OtpStatusRecordTypeEnum.LOGIN, msisdn);
            String msg = "";
            int code = 0;

            String[] respSplit = resp.split(":");
            if (respSplit[0] != null) {

//                boolean b = kSms.sendSMS(msisdn, "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes." );
                String message = "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes.";
                boolean sendSMS = kSms.sendSMS(msisdn, message);
                if (sendSMS) {
                    msg = "The OTP successfully sent to subscriber.";
                    code = 1;
                } else {
                    msg = "Failed to generate/send OTP. Try again later.";
                    code = 0;
                }
            } else {
                msg = "An error occurred while generating OTP. Please try again later.";
                code = -1;
            }

            response.setMessage(msg);
            response.setStatus(code);
            return response;
        }catch(NwormQueryException | IOException e){
            logger.error("Exception thrown in generating and sending OTP", e);
            response.setMessage("An error occurred while generating your otp. Try again later.");
            response.setStatus(-1);
            return response;
        }

    }

    @SuppressWarnings("PMD")
    @Override
    public ResponseData requestOnlineRegistrationOTP(String msisdn) {
        ResponseData responseData = new ResponseData(ResponseCodeEnum.ERROR);
        try {
            if (!otpDS.phoneNumberExist(msisdn)) {
                responseData.setCode(ResponseCodeEnum.INACTIVE_ACCOUNT);
                responseData.setDescription("Msisdn was not found");
                return responseData;
            }
            String resp = otpDS.generateOTP(OtpStatusRecordTypeEnum.ONLINE_REGISTRATION, msisdn);

            logger.debug("otp response=" + resp);
            String[] respSplit = resp.split(":");
            if (respSplit[0] != null) {

                String message = "Your one-time password is " + respSplit[1] + ". It expires in " + respSplit[2] + " minutes.";
                boolean sendSMS = kSms.sendSMS(msisdn, message);
                if (sendSMS) {
                    responseData.setCode(ResponseCodeEnum.SUCCESS);
                    responseData.setDescription("OTP was generated successfully.");
                } else {
                    responseData.setCode(ResponseCodeEnum.ERROR);
                    responseData.setDescription("Failed to generate/send OTP. Try again later.");
                }
            } else {
                responseData.setCode(ResponseCodeEnum.ERROR);
                responseData.setDescription("An error occurred while generating OTP. Please try again later.");
            }

            return responseData;

        } catch (Exception e) {
            logger.error("", e);
            responseData.setCode(ResponseCodeEnum.ERROR);
            responseData.setDescription("An error occurred while generating OTP. Please try again later.");
            return responseData;
        }
    }

    @Override
    public ResponseData verifyOnlineRegistrationOTP(String otp, String msisdn) {
        ResponseData responseData = new ResponseData(ResponseCodeEnum.ERROR);
        try {
            int code = otpDS.verifyOtpStatus(OtpStatusRecordTypeEnum.ONLINE_REGISTRATION, otp, msisdn);
            switch (code) {
                case 1:
                    responseData.setCode(ResponseCodeEnum.SUCCESS);
                    responseData.setDescription("The specified OTP is valid.");
                    break;
                case 2:
                    responseData.setCode(ResponseCodeEnum.ERROR);
                    responseData.setDescription("This otp has expired.");
                    break;
                case 3:
                    responseData.setCode(ResponseCodeEnum.ERROR);
                    responseData.setDescription("No match was found for the specified OTP.");
                    break;
                case 4:
                    responseData.setCode(ResponseCodeEnum.INVALID_INPUT);
                    responseData.setDescription("Both the msisdn and the OTP are required inputs. Note that msisdn must be 11 digits.");
                    break;
            }
        } catch (NwormQueryException e) {
            responseData.setCode(ResponseCodeEnum.ERROR);
            responseData.setDescription("An error occurred while verifying your otp. Please try again later.");
            return responseData;
        }
        return responseData;
    }
}