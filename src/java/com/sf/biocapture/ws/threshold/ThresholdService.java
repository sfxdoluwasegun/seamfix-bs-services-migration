/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.threshold.pojo.DemographicsPojo;
import com.sf.biocapture.ws.threshold.pojo.FingerprintProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.FingerprintTypeProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.PassportProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.RequestPojo;
import com.sf.biocapture.ws.threshold.pojo.RequestTypeEnum;
import com.sf.biocapture.ws.threshold.pojo.ResponseItemPojo;
import com.sf.biocapture.ws.threshold.pojo.ResponsePojo;
import com.sf.biocapture.ws.threshold.pojo.ResponsePojo.ResponseCode;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Marcel
 * @since Jul 4, 2016, 6:04:43 PM
 */
@Path("/threshold")
public class ThresholdService extends BsClazz implements IThresholdService {

    private final ThresholdUtil thresholdUtil = new ThresholdUtil();
    private static final String PASSPORT_PROFILE_THRESHOLD_NAME = "passport-profile-threshold.xml";
    private static final String FINGERPRINT_PROFILE_THRESHOLD_NAME = "fingerprint-profile-threshold.xml";

    /**
     * Replaced with getThresholdPojoWithFingerType(requestPojo)
     *
     * @param requestPojo
     * @return
     * @deprecated
     */
    @SuppressWarnings("CPD-START")
    @Deprecated
    @Override
    public Response getThresholdPojo(RequestPojo requestPojo) {
        ResponsePojo responsePojo = new ResponsePojo(null, null, ResponseCode.ERROR);
        if (requestPojo != null) {
            List<ResponseItemPojo> responseItemPojos = new ArrayList<>();

            int thresholdVersion = requestPojo.getThresholdVersion();
            try {
                int currentVersion = appProps.getInt("threshold-version-phase-1", 0);
                if (thresholdVersion == currentVersion) {
                    responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.UP_TO_DATE);
                } else {
                    //fingerprint
                    if (thresholdUtil.getFingerprintProfilePojo() == null) {
                        FingerprintProfilePojo fingerprintProfilePojo = thresholdUtil.unmarshal(FingerprintProfilePojo.class, FINGERPRINT_PROFILE_THRESHOLD_NAME);
                        thresholdUtil.setFingerprintProfilePojo(fingerprintProfilePojo);
                    }

                    ResponseItemPojo fingerprintItemPojo = new ResponseItemPojo(RequestTypeEnum.FINGERPRINT.name(), thresholdUtil.getFingerprintProfilePojo());
                    responseItemPojos.add(fingerprintItemPojo);

                    //passport
                    if (thresholdUtil.getPassportProfilePojo() == null) {
                        PassportProfilePojo passportProfilePojo = thresholdUtil.unmarshal(PassportProfilePojo.class, PASSPORT_PROFILE_THRESHOLD_NAME);
                        thresholdUtil.setPassportProfilePojo(passportProfilePojo);
                    }
                    JAXBContext jc = JAXBContext.newInstance(PassportProfilePojo.class);
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
                    StringWriter sw = new StringWriter();
                    marshaller.marshal(thresholdUtil.getPassportProfilePojo(), sw);
                    String passportXml = sw.toString();
                    ResponseItemPojo passportItemPojo = new ResponseItemPojo(RequestTypeEnum.PASSPORT.name(), passportXml);
                    responseItemPojos.add(passportItemPojo);

                    responsePojo = new ResponsePojo(currentVersion, responseItemPojos, ResponseCode.SUCCESS);
                }
            } catch (NumberFormatException | JAXBException e) {
                responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.ERROR);
                logger.error("", e);
            }
        }
        return Response.status(200).entity(responsePojo).build();
    }

    @Override
    public Response getThreshold(RequestPojo requestPojo, int targetEndpoint) {
        switch (targetEndpoint) {
            case 0:
                return getThresholdPojoWithFingerType(requestPojo);
            case 3:
                return getThreshold(requestPojo);
        }

        return Response.status(200).entity(new ResponsePojo(null, null, ResponseCode.ERROR)).build();
    }

    @Override
    public Response getThresholdPojoWithFingerType(RequestPojo requestPojo) {
        ResponsePojo responsePojo = new ResponsePojo(null, null, ResponseCode.ERROR);
        if (requestPojo != null) {
            List<ResponseItemPojo> responseItemPojos = new ArrayList<>();

            int thresholdVersion = requestPojo.getThresholdVersion();
            try {
                int currentVersion = appProps.getInt("threshold-version", 0);
                if (thresholdVersion == currentVersion) {
                    responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.UP_TO_DATE);
                } else {
                    //fingerprint
                    if (thresholdUtil.getFingerprintTypeProfilePojo() == null) {
                        FingerprintTypeProfilePojo fingerprintTypeProfilePojo = thresholdUtil.unmarshal(FingerprintTypeProfilePojo.class, "fingerprint-type-profile-threshold.xml");
                        thresholdUtil.setFingerprintTypeProfilePojo(fingerprintTypeProfilePojo);
                    }

                    ResponseItemPojo fingerprintItemPojo = new ResponseItemPojo(RequestTypeEnum.FINGERPRINT.name(), thresholdUtil.getFingerprintTypeProfilePojo());
                    responseItemPojos.add(fingerprintItemPojo);

                    //passport
                    if (thresholdUtil.getPassportProfilePojo() == null) {
                        PassportProfilePojo passportProfilePojo = thresholdUtil.unmarshal(PassportProfilePojo.class, PASSPORT_PROFILE_THRESHOLD_NAME);
                        thresholdUtil.setPassportProfilePojo(passportProfilePojo);
                    }
                    JAXBContext jc = JAXBContext.newInstance(PassportProfilePojo.class);
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
                    StringWriter sw = new StringWriter();
                    marshaller.marshal(thresholdUtil.getPassportProfilePojo(), sw);
                    String passportXml = sw.toString();
                    ResponseItemPojo passportItemPojo = new ResponseItemPojo(RequestTypeEnum.PASSPORT.name(), passportXml);
                    responseItemPojos.add(passportItemPojo);

                    //demographics
                    if (thresholdUtil.getDemographicsPojo() == null) {
                        DemographicsPojo demographicsPojo = thresholdUtil.unmarshal(DemographicsPojo.class, "demographic-threshold.xml");
                        thresholdUtil.setDemographicsPojo(demographicsPojo);
                    }
                    ResponseItemPojo demographicItemPojo = new ResponseItemPojo(RequestTypeEnum.DEMOGRAPHICS.name(), thresholdUtil.getDemographicsPojo());
                    responseItemPojos.add(demographicItemPojo);

                    responsePojo = new ResponsePojo(currentVersion, responseItemPojos, ResponseCode.SUCCESS);
                }
            } catch (NumberFormatException | JAXBException e) {
                responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.ERROR);
                logger.error("", e);
            }
        }
        return Response.status(200).entity(responsePojo).build();
    }

    public Response getThreshold(RequestPojo requestPojo) {
        ResponsePojo responsePojo = new ResponsePojo(null, null, ResponseCode.ERROR);
        if (requestPojo != null) {
            List<ResponseItemPojo> responseItemPojos = new ArrayList<>();

            int thresholdVersion = requestPojo.getThresholdVersion();
            try {
                int currentVersion = appProps.getInt("threshold-version-phase-3", 0);
                if (thresholdVersion == currentVersion) {
                    responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.UP_TO_DATE);
                } else {
                    //fingerprint
                    if (thresholdUtil.getFingerprintTypeProfilePojo() == null) {
                        FingerprintTypeProfilePojo fingerprintTypeProfilePojo = thresholdUtil.unmarshal(FingerprintTypeProfilePojo.class, "fingerprint-type-profile-threshold.xml");
                        thresholdUtil.setFingerprintTypeProfilePojo(fingerprintTypeProfilePojo);
                    }

                    ResponseItemPojo fingerprintItemPojo = new ResponseItemPojo(RequestTypeEnum.FINGERPRINT.name(), thresholdUtil.getFingerprintTypeProfilePojo());
                    responseItemPojos.add(fingerprintItemPojo);

                    //passport
                    if (thresholdUtil.getPassportProfilePojo() == null) {
                        PassportProfilePojo passportProfilePojo = thresholdUtil.unmarshal(PassportProfilePojo.class, PASSPORT_PROFILE_THRESHOLD_NAME);
                        thresholdUtil.setPassportProfilePojo(passportProfilePojo);
                    }
                    JAXBContext jc = JAXBContext.newInstance(PassportProfilePojo.class);
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
                    StringWriter sw = new StringWriter();
                    marshaller.marshal(thresholdUtil.getPassportProfilePojo(), sw);
                    String passportXml = sw.toString();
                    ResponseItemPojo passportItemPojo = new ResponseItemPojo(RequestTypeEnum.PASSPORT.name(), passportXml);
                    responseItemPojos.add(passportItemPojo);

                    //demographics
                    if (thresholdUtil.getDemographicsPojo() == null) {
                        DemographicsPojo demographicsPojo = thresholdUtil.unmarshal(DemographicsPojo.class, "demographic-threshold-3.xml");
                        thresholdUtil.setDemographicsPojo(demographicsPojo);
                    }
                    ResponseItemPojo demographicItemPojo = new ResponseItemPojo(RequestTypeEnum.DEMOGRAPHICS.name(), thresholdUtil.getDemographicsPojo());
                    responseItemPojos.add(demographicItemPojo);

                    responsePojo = new ResponsePojo(currentVersion, responseItemPojos, ResponseCode.SUCCESS);
                }
            } catch (NumberFormatException | JAXBException e) {
                responsePojo = new ResponsePojo(thresholdVersion, responseItemPojos, ResponseCode.ERROR);
                logger.error("", e);
            }
        }
        return Response.status(200).entity(responsePojo).build();
    }

    @Override
    public Response getRequestPojo() {
        ThresholdUtil thresholdUtil = new ThresholdUtil();
        thresholdUtil.marshal(PassportProfilePojo.class, thresholdUtil.newPassportProfilePojo(), PASSPORT_PROFILE_THRESHOLD_NAME);
        thresholdUtil.marshal(FingerprintProfilePojo.class, thresholdUtil.newFingerprintProfilePojo(), FINGERPRINT_PROFILE_THRESHOLD_NAME);
        thresholdUtil.marshal(DemographicsPojo.class, thresholdUtil.newDemographicsPojo(), "demographic-threshold.xml");
        return Response.status(200).entity(new RequestPojo(2, "2398HHH", "MK1234567890", "mugwu@seamfix.com")).build();
    }

    @Override
    public Response getRequestPojoWithFingerType() {
        ThresholdUtil thresholdUtil = new ThresholdUtil();
        thresholdUtil.marshal(PassportProfilePojo.class, thresholdUtil.newPassportProfilePojo(), PASSPORT_PROFILE_THRESHOLD_NAME);
        thresholdUtil.marshal(FingerprintTypeProfilePojo.class, thresholdUtil.newFingerprintTypeProfilePojo(), "fingerprint-type-profile-threshold.xml");
        thresholdUtil.marshal(DemographicsPojo.class, thresholdUtil.newDemographicsPojo(), "demographic-threshold.xml");
        return Response.status(200).entity(new RequestPojo(2, "2398HHH", "MK1234567890", "mugwu@seamfix.com")).build();
    }

}
