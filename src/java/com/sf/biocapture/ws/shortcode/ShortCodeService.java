/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.shortcode;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.common.AesEncrypter;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.access.AccessResponse;
import com.sf.biocapture.ws.access.AccessResponseData;
import com.sf.biocapture.ws.tags.TagsDS;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Path;
import nw.orm.core.exception.NwormQueryException;

/**
 *
 * @author Marcel
 * @since 15-Nov-2016, 18:53:22
 */
@Path("/shortcode")
public class ShortCodeService extends BsClazz implements IShortCodeService {

    @Inject
    private AccessDS accessDS;

    @Inject
    private TagsDS tagsDS;
    private AesEncrypter aesEncrypter;

    @PostConstruct
    protected void doSetup() {
        aesEncrypter = new AesEncrypter();
    }

    @Override
    public String handleShortCode(String request) {
        String response = "";
        logger.debug("Request message: " + request);
        ShortCodeRequestData requestData = new ShortCodeRequestData();
        requestData.readRequestData(request);
        switch (requestData.getRequestTypeEnum()) {
            case LGN:
                response = handleLogin(requestData);
                break;
        }

        return response;
    }

    private String handleLogin(ShortCodeRequestData shortCodeRequestData) {
        AccessResponseData responseData = new AccessResponseData();
        responseData.setCode(ResponseCodeEnum.ERROR);
        AccessResponse accessResponse = new AccessResponse();
        KMUser user = null;
        try {
            user = accessDS.getUser(shortCodeRequestData.getEmail());
            Long cTime = shortCodeRequestData.getClientTime() != null ? Long.valueOf(shortCodeRequestData.getClientTime()) : 0L;
            if (cTime != 0L) {
                if (!tagsDS.clientTimeIsCorrect(shortCodeRequestData.getClientTime())) {
                    accessResponse.setStatus(-1);
                    accessResponse.setMessage("Please correct your system time. Server time is " + new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(new Date()));
                } else {
                    //the password is encrypted at this point thus should be decrypted
                    String password = shortCodeRequestData.getPassword();
                    password = aesEncrypter.decrypt(password);
                    accessResponse = accessDS.performLogin(user, password, shortCodeRequestData.getTag(), null);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Error in retrieving client time:", e);
            //TODO stop login from proceeding?
        }
        boolean otpRequired = false;
        try {
            otpRequired = Boolean.valueOf(accessDS.getSettingValue(SettingsEnum.OTP_REQUIRED));
        } catch (NumberFormatException e) {
            logger.error("", e);
        } catch (NwormQueryException e) {
            logger.error("", e);
        }
        if (accessResponse.getStatus() == 0) {
            if (otpRequired) {
                responseData = accessDS.requestLoginOtp(user, accessResponse);
            } else {
                responseData.setCode(ResponseCodeEnum.SUCCESS);
                responseData.from(accessResponse);
            }
            if (user != null) {
                responseData.setPrivileges(user.getPrivileges());
                responseData.setCached(accessDS.isUserCacheable(user));
            }
            responseData.setDescription("");//response code description is changed to empty string on successful login to reduce the length of sms.
        } else {
            responseData.setCode(ResponseCodeEnum.ERROR);
            responseData.from(accessResponse);
        }
        responseData.setOtpRequired(otpRequired);
        return responseData.concatenate();

    }
}
