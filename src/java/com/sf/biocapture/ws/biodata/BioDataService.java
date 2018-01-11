package com.sf.biocapture.ws.biodata;

import com.sf.biocapture.agl.integration.AgilityResponse;
import com.sf.biocapture.agl.integration.FetchBillingDetails;
import com.sf.biocapture.agl.integration.FetchSubscriptionListIntegrator;
import com.sf.biocapture.agl.integration.GetSubscriberDetails;
import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import com.sf.biocapture.app.BiodataCache;
import com.sf.biocapture.proxy.BioData;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.KycPrivilege;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.enums.UseCaseEnum;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.HeaderIdentifier;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;

import nw.commons.StopWatch;

import org.apache.commons.lang3.math.NumberUtils;

@Path("/biodata")
public class BioDataService extends BsClazz implements IBioDataService {

    private static final String INVALID_MSISDN_MESSAGE = "This is an invalid msisdn. Note msisdn must be 11 digits.";
    private static final String SUBSCRIBER_FOUND = "Retrieved subscriber bio data for the specified msisdn";
    private final String DEFAULT_BOOLEAN_VAL = "true";

    @Inject
    BioDataDS biodataDS;
    @Inject
    IntrusionAnalyzer analyzer;
    @Inject
    AccessDS accessDS;
    @Inject
    GetSubscriberDetails sDetails;
    @Inject
    FetchSubscriptionListIntegrator subcriberListService;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
    @Inject
    BiodataCache biodataCache;

    @Inject
    FetchBillingDetails billingDetails;

    @SuppressWarnings("CPD-START")
    @Override
    public BioDataResponse getBioData(String msisdn) {

        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }

        String cacheItemName = "REREG-" + msisdn + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse(SUBSCRIBER_FOUND, 1, bd);
        }

        return getAgilityRecords(msisdn, null, true, false, cacheItemName, UseCaseEnum.RE_REGISTRATION.getName(), null);
    }

    @Override
    public BioDataResponse getPlatinumBioData(String msisdn) {

        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }

        String cacheItemName = "PD-" + msisdn + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse(SUBSCRIBER_FOUND, 1, bd);
        }

        return getAgilityRecords(msisdn, null, false, false, cacheItemName, UseCaseEnum.PLATINUM.getName(), null);
    }

    @Override
    public BioDataResponse getSmeCorporate(String serialNumber) {

        if ((serialNumber == null) || serialNumber.isEmpty() || !(NumberUtils.isDigits(serialNumber))
                || (serialNumber.length() != 20)) {
            return getResponse("This is an invalid serial number. Note serial numbers must be 20 digits.", 0, null);
        }

        String cacheItemName = "SERIAL-" + serialNumber + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse("Retrieved subscriber bio data for the specified serial number", 1, bd);
        }

        return getAgilityRecords(null, serialNumber, false, false, cacheItemName, UseCaseEnum.SME_CORPORATE.getName(), null);
    }

    @Override
    public BioDataResponse getMnpSmeCorporate(String msisdn) {
        logger.debug("MSISDN: " + msisdn);
        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }

        String cacheItemName = "PD-" + msisdn + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse(SUBSCRIBER_FOUND, 1, bd);
        }

        return getAgilityRecords(msisdn, null, false, false, cacheItemName, UseCaseEnum.MNP_SME_CORPORATE.getName(), null);
    }

    private BioDataResponse getResponse(String msg, int status, BioData bio) {
        BioDataResponse bdr = new BioDataResponse();
        bdr.setMessage(msg);
        bdr.setStatus(status);
        if (bio != null) {
            bdr.setBioData(bio);
        }

        return bdr;
    }

    @Override
    public BioDataResponse addSim(String msisdn) {
        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }

        String cacheItemName = "ADDSIM-" + msisdn + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse(SUBSCRIBER_FOUND, 1, bd);
        }

        return getAgilityRecords(msisdn, null, true, true, cacheItemName, UseCaseEnum.ADD_SIM.getName(), null);
    }

    @Override
    public BioDataResponse reReg(HttpHeaders headers, String msisdn) {

        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }
        
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);
        String agentEmail = headerIdentifier == null ? "" : headerIdentifier.getEmailAddress();

        String cacheItemName = "REREG-" + msisdn + "-" + agentEmail + "-";
//        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
//        if (bd != null) {
//            return getResponse(SUBSCRIBER_FOUND, 1, bd);
//        }

        return getAgilityRecords(msisdn, null, true, false, cacheItemName, UseCaseEnum.RE_REGISTRATION.getName(), agentEmail);
    }

    private int getSwapTimeFrame() {
        int timeFrame = 0;
        try {
            timeFrame = Integer.valueOf(accessDS.getSettingValue(SettingsEnum.SIMSWAP_TIME_FRAME));//in days
        } catch (NumberFormatException nfe) {
            logger.error("Invalid simswap time frame configured:", nfe);
            timeFrame = 7;
        }

        return timeFrame;
    }

    private boolean checkSwapTimeFrame(Timestamp syncDate, int timeFrameDays) {
        long serverTime = System.currentTimeMillis();
        long activationTime = syncDate.getTime();
        long timeFrameMil = TimeUnit.MILLISECONDS.convert(timeFrameDays, TimeUnit.DAYS);
        logger.debug("time since activation: " + (serverTime - activationTime) + "ms; time frame allowed: " + timeFrameMil + "ms");

        return (serverTime - activationTime) > timeFrameMil;
    }

    private boolean skipRegistrationDateEligibility(String agentEmail) {
        if (agentEmail != null && !agentEmail.isEmpty()) {
            KMUser user = accessDS.getUser(agentEmail);
            if (user != null) {
                return user.hasPrivilege(KycPrivilege.RE_REGISTRATION_RULE);
            }
        }

        return false;
    }

    @SuppressWarnings("PMD")
    private BioDataResponse getAgilityRecords(String msisdn, String serialNumber, boolean loadBiometrics, boolean mustReturnBiometrics, String key, String useCase, String agentEmail) {
        logger.debug("msisdn: " + msisdn + ", serialNumber: " + serialNumber + ", loadBiometrics: " + loadBiometrics + ", mustReturnBiometrics: " + mustReturnBiometrics + ", key: " + key + ", useCase: " + useCase + ", agentEmail: " + agentEmail);
        BioData bd = null;
        try {

            AgilityResponse ar = sDetails.getSubscriberDetails(msisdn, serialNumber, msisdn + "-" + sdf.format(new Date()));
            if (ar == null) {
                return getResponse("Could not retrieve bio data for the specified msisdn.", 0, null);
            }

            if (ar.getCode().equals("-2")) { // Checking if connection to agility failed.
                int errorStatus = ((ar.getCode() != null) && NumberUtils.isDigits(ar.getCode())) ? Integer.valueOf(ar.getCode()) : -2;
                return getResponse(ar.getDescription(), errorStatus, null);
            }

            if (ar.getBioData() == null) {
                return getResponse("Could not retrieve bio data for the specified msisdn.", 0, null);
            }

            if (useCase.equalsIgnoreCase(UseCaseEnum.RE_REGISTRATION.getName()) && appProps.getBool("enable-rrr-check", false)) {
                //validate msisdn eligibility
                logger.debug("agent email address: " + agentEmail + ", last reg date ------------------------- : " + ar.getLastSIMRegDate());
                if (agentEmail == null || agentEmail.isEmpty()) {
                    //skip this process. it is assumed that agent without email address is coming from legacy system. this assumpetion is true based on subsequenct logic before this method.
                } else {
                    if (ar.getLastSIMRegDate() != null && !skipRegistrationDateEligibility(agentEmail)) {
                        Integer dayLimit = Integer.valueOf(accessDS.getSettingValue(SettingsEnum.RE_REGISTRATION_RULE_DAYS_LIMIT));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(ar.getLastSIMRegDate());
                        cal.add(Calendar.DAY_OF_WEEK, dayLimit);
                        Timestamp ts = new Timestamp(cal.getTime().getTime());
                        logger.debug("last sim reg date: " + ar.getLastSIMRegDate() + ", expected due date: " + ts);
                        if (ts.after(new Date())) {
                            logger.debug("not due to be registered: " + msisdn);
                            return getResponse(appProps.getProperty("rrr-failure-message", "You are not eligible for re-registration"), 0, null);
                        }
                    }
                }
            }

            if (useCase.equalsIgnoreCase(UseCaseEnum.SIM_SWAP.getName())) {
                //check if msisdn is eligible for swap
                Boolean doDateValidation = Boolean.valueOf(accessDS.getSettingValue(SettingsEnum.SIMSWAP_DATE_VALIDATION));
                if (doDateValidation) {
                    logger.debug("Checking if msisdn is eligible for sim swap...");
                    boolean valid = false;
                    String message = msisdn + " is not eligible for sim swap";
                    List<Timestamp> msisdns = biodataDS.getActivationTimestampRequests(msisdn);
                    if (msisdns != null && !msisdns.isEmpty()) {
                        logger.debug("Number of registrations found for " + msisdn + ": " + msisdns.size());
                        if (msisdns.size() == 1) {
                            valid = true;
                        } else {
                            if (msisdns.size() > 1) {
                            //check that the period between when the msisdn was last activated and 
                                //the swap date exceeds the allowed time frame
                                int daysCount = getSwapTimeFrame();
                                if (!checkSwapTimeFrame(msisdns.get(0), daysCount)) {
                                    valid = false;
                                    message = "Swap submission failed due to " + daysCount + " days registration rule";
                                } else {
                                    valid = true;
                                }
                            } else {
                                valid = false;
                                message = msisdn + " is not eligible for sim swap";
                            }
                        }
                    } else {
                        valid = false;
                        message = msisdn + " is not eligible for sim swap";
                    }

                    if (!valid) {
                        return getResponse(message, 0, null);
                    }
                }
            }

            bd = ar.getBioData();

            if (appProps.getBool("check-bio-update-eligibility", true)) {
                if (useCase.equalsIgnoreCase(UseCaseEnum.PLATINUM.getName()) || useCase.equalsIgnoreCase(UseCaseEnum.MNP_SME_CORPORATE.getName())) {
                    //check if msisdn is eligible for biometric update
                    if (!bd.isBiometricUpdateEligible()) {
                        logger.debug(msisdn + " is NOT eligible for biometric update!!");
                        return getResponse(msisdn + " is not eligible for this use case", 0, null);
                    } else {
                        logger.debug(msisdn + " is eligible for biometric update!!");
                    }
                }
            }

            if (loadBiometrics && Boolean.valueOf(accessDS.getSettingValue("BIO-FETCH-" + useCase, DEFAULT_BOOLEAN_VAL))) {
                logger.debug("Loading biometrics for " + useCase);
                bd = biodataDS.getBioData(bd);
            } else {
                logger.debug("Biometrics loading disabled for " + useCase);
            }

            if (Boolean.valueOf(accessDS.getSettingValue("SUB-LIST-" + useCase, DEFAULT_BOOLEAN_VAL))) {
                logger.debug("Fetching number of child msisdn for " + useCase);
                //This calls the service to return the number of child msisdn associated with a primary msisdn.
                AgilityResponse arCount = subcriberListService.fetchStatusLevelSubscriptionList(msisdn, "SFX_" + msisdn + "_" + sdf.format(new Date()));
                bd.setMsisdnCount(arCount.getChildMsisdnCount());
            }

            if (Boolean.valueOf(accessDS.getSettingValue("FETCH-DATA-" + useCase, DEFAULT_BOOLEAN_VAL))) {
                logger.debug("Fetching incomplete data for " + useCase + " use case");
                biodataDS.loadMiscData(bd);
            }
            //fetch the invoice amount from the api
            StopWatch sw = new StopWatch(true);
            Double invoiceAmount = billingDetails.getBillingInfo(msisdn, "CBI_" + msisdn + "_" + sdf.format(new Date()));
            logger.debug("Time taken to fetch billing details: [" + sw.elapsedTime() + "ms]");
            if (invoiceAmount != null) {
                bd.setInvoiceAmount(invoiceAmount);
            }

            if (mustReturnBiometrics) {
                if (bd.getPassportData() == null) {
                    return getResponse("Could not retrieve bio data for the specified msisdn.", 0, null);
                }
            }

            biodataCache.setItem(key, bd, 60 * 5);
        } catch (Exception e) {
            logger.error("basic data: " + bd);
            logger.error("", e);
            return getResponse("An error occurred. Please try again.", 0, null);
        }
        return getResponse(SUBSCRIBER_FOUND, 1, bd);
    }

    @Override
    public BioDataResponse testBiometricsFetch(String uniqueId) {
        StopWatch sw = new StopWatch(true);
        BioDataResponse bdr = new BioDataResponse();
        if (uniqueId == null) {
            bdr.setStatus(1);
            bdr.setMessage("Missing subscriber unique ID");
        } else {
            BioData bd = new BioData();
            bd.setUniqueId(uniqueId);
            bd = biodataDS.getBioData(bd);
            bdr.setBioData(bd);
            bdr.setStatus(0);
            bdr.setMessage("Successfully retrieved subscriber biometrics");
        }
        logger.debug("Overall time taken to return subscriber biometrics: [" + sw.elapsedTime() + "ms]");
        return bdr;
    }

    @Override
    public BioDataResponse getSimSwapData(String msisdn) {
        if ((msisdn == null) || msisdn.isEmpty() || !(NumberUtils.isDigits(msisdn))
                || (msisdn.length() != 11)) {
            return getResponse(INVALID_MSISDN_MESSAGE, 0, null);
        }

        String cacheItemName = "SIMSWAP-" + msisdn + "-";
        BioData bd = biodataCache.getItem(cacheItemName, BioData.class);
        if (bd != null) {
            return getResponse(SUBSCRIBER_FOUND, 1, bd);
        }

        return getAgilityRecords(msisdn, null, true, false, cacheItemName, UseCaseEnum.SIM_SWAP.getName(), null);
    }
}
