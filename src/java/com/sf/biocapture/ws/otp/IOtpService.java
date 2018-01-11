package com.sf.biocapture.ws.otp;

import com.sf.biocapture.ws.ResponseData;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author @wizzyclems
 */
public interface IOtpService {
    
    public static String MSISDN = "msisdn";
    public static String OTP = "otp";

    @POST
    @Path("/status")
    @Produces({MediaType.APPLICATION_JSON})
    public OTPStatusResponse checkOTPStatus(@FormParam(OTP) String otp, @FormParam(MSISDN) String msisdn);

    @POST
    @Path("/request")
    @Produces({MediaType.APPLICATION_JSON})
    public OTPStatusResponse requestOTP(@FormParam(MSISDN) String msisdn);

    @POST
    @Path("/oreg/request")
    @Produces({MediaType.APPLICATION_JSON})
    public ResponseData requestOnlineRegistrationOTP(@FormParam(MSISDN) String msisdn);

    @POST
    @Path("/oreg/verify")
    @Produces({MediaType.APPLICATION_JSON})
    public ResponseData verifyOnlineRegistrationOTP(@FormParam(OTP) String otp, @FormParam(MSISDN) String msisdn);
}