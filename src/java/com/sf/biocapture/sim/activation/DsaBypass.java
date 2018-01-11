package com.sf.biocapture.sim.activation;

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.app.NetworkClient;
import com.sf.biocapture.entity.PhoneNumberStatus;
import com.sf.biocapture.postactivation.TabsDS;
import com.sf.biocapture.postactivation.sms.SmsReq;
import java.io.IOException;

@Stateless
public class DsaBypass extends BsClazz {

    @Inject
    private TabsDS tabsDs;

    @Inject
    private KannelSMS ksms;

    private String urlPath = "unbar/?msisdn=";

    private String bypassCodes = appProps.getProperty("bypass-codes", "848");

    public long shouldBypass(String result) {
        String[] codes = bypassCodes.split("#sfx#");
        for (String code : codes) {
            if (result.contains(">" + code + "</")) {
                return Long.valueOf(code);
            }
        }

        if ("Exception".equals(result)) {
            return 1010101L; // Time outs and other exceptions
        } else if ("NO_EIM".equalsIgnoreCase(result)) {
            return 1010102L; // NO EIM entry
        } else if ("MANUAL".equalsIgnoreCase(result)) {
            return 1010103L; // Manual entry
        }
        return 0L;
    }

    public void bypass(SmsReq req, String resultString, String stubEndpoint, String processId) {
		// bypass candidate found
        // push msisdn to bypass service
        long code = shouldBypass(resultString);
        String path = urlPath + req.getPhoneNumber();
        PhoneNumberStatus pns = new PhoneNumberStatus();
        pns.setInitTimestamp(new Timestamp(new Date().getTime()));
        NetworkClient ns = new NetworkClient();
        String data = ns.getData(path);
        Gson gson = new Gson();
        ByPassResponse resp = gson.fromJson(data, ByPassResponse.class);
        if (resp != null) {
            pns.setTabsFileNumber(code);
            pns.setRemarks(resultString);
            if ("SUCCESS".equalsIgnoreCase(resp.getResponse())) {
                try {
                    if (ksms.sendSMS(req.getPhoneNumber(), "")) { //TODO
                        pns.setStatus("ACTIVATED");
                    } else {
                        pns.setStatus("ACTIVATED_WITH_SMS_UNSENT");
                    }
                    tabsDs.createTabsLog(req.getPhoneNumber(), req.getId(), "ACTIVATED", "Processed via ByPass", stubEndpoint, resultString, processId);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                pns.setStatus("NOT_ACTIVATED");
                tabsDs.createTabsLog(req.getPhoneNumber(), req.getId(), "NOT_ACTIVATED", "Processed via ByPass", stubEndpoint, resultString, processId);
            }
            pns.setFinalTimestamp(new Timestamp(new Date().getTime()));
//			pns.setActivationTimestamp(new Timestamp(resp.getActivationTime().getTime()));
//			mainService.updatePhoneNumberStatus(pns);
//			req.setStatus("TABS");
//		    mainService.updateSmsActivationRequest(req);
        }
    }

}
