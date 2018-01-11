/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.threshold.pojo.AnomaliesCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.DemographicPropertyInfo;
import com.sf.biocapture.ws.threshold.pojo.DemographicsPojo;
import com.sf.biocapture.ws.threshold.pojo.EyeCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.FaceCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.FingerprintProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.FingerprintTypeProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.ImageCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.ImageGeometryCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.ImageStorageCharacteristicsPojo;
import com.sf.biocapture.ws.threshold.pojo.PassportProfilePojo;
import com.sf.biocapture.ws.threshold.pojo.PropertyInfoPojo;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Marcel
 * @since Jul 13, 2016, 9:08:23 AM
 */
public class ThresholdUtil extends BsClazz {

    private PassportProfilePojo passportProfilePojo;
    private FingerprintProfilePojo fingerprintProfilePojo;
    private FingerprintTypeProfilePojo fingerprintTypeProfilePojo;
    private DemographicsPojo demographicsPojo;

    public ThresholdUtil() {
    }

    public FingerprintTypeProfilePojo getFingerprintTypeProfilePojo() {
        return fingerprintTypeProfilePojo;
    }

    public void setFingerprintTypeProfilePojo(FingerprintTypeProfilePojo fingerprintTypeProfilePojo) {
        this.fingerprintTypeProfilePojo = fingerprintTypeProfilePojo;
    }

    public PassportProfilePojo getPassportProfilePojo() {
        return passportProfilePojo;
    }

    public void setPassportProfilePojo(PassportProfilePojo passportProfilePojo) {
        this.passportProfilePojo = passportProfilePojo;
    }

    public FingerprintProfilePojo getFingerprintProfilePojo() {
        return fingerprintProfilePojo;
    }

    public void setFingerprintProfilePojo(FingerprintProfilePojo fingerprintProfilePojo) {
        this.fingerprintProfilePojo = fingerprintProfilePojo;
    }

    public DemographicsPojo getDemographicsPojo() {
        return demographicsPojo;
    }

    public void setDemographicsPojo(DemographicsPojo demographicsPojo) {
        this.demographicsPojo = demographicsPojo;
    }

    public <T extends Object> T unmarshal(Class<T> clazz, String fileName) {
        try {
            File file = new File(fileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return clazz.cast(jaxbUnmarshaller.unmarshal(file));
        } catch (JAXBException e) {
            logger.error("", e);
        }

        return null;
    }

    public void marshal(Class clazz, Object clazzInstance, String fileName) {
        try {

            File file = new File(fileName);
            if (file.exists()) {
                logger.debug("File already exists. Delete file in order to generate new one: " + file.getAbsolutePath());
                return;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(clazzInstance, file);
            jaxbMarshaller.marshal(clazzInstance, System.out);

//            OutputStream os = new ByteArrayOutputStream();
//            jaxbMarshaller.marshal(clazzInstance, os);
        } catch (JAXBException e) {
            logger.error("", e);
        }

    }

    public PassportProfilePojo newPassportProfilePojo() {
        PropertyInfoPojo propertyInfoPojo = new PropertyInfoPojo();
        propertyInfoPojo.setMax("8");
        propertyInfoPojo.setMin("7");
        propertyInfoPojo.setPref("8");
        propertyInfoPojo.setUnits("bits");
        propertyInfoPojo.setcMax("8.0");
        propertyInfoPojo.setcMin("6.8");
        propertyInfoPojo.setqWeight("3.0");

        AnomaliesCharacteristicsPojo anomaliesCharacteristicsPojo = new AnomaliesCharacteristicsPojo();
        anomaliesCharacteristicsPojo.setDarkGlassesLikelihood(propertyInfoPojo);
        anomaliesCharacteristicsPojo.setFocusLikelihood(propertyInfoPojo);
        anomaliesCharacteristicsPojo.setForeheadCoveringLikelihood(propertyInfoPojo);
        anomaliesCharacteristicsPojo.setGlareLikelihood(propertyInfoPojo);
        anomaliesCharacteristicsPojo.setGlassesLikelihood(propertyInfoPojo);
        anomaliesCharacteristicsPojo.setSharpnessLikelihood(propertyInfoPojo);

        EyeCharacteristicsPojo eyeCharacteristicsPojo = new EyeCharacteristicsPojo();
//      eyeCharacteristicsPojo.setBrightnessScore(propertyInfoPojo);
//      eyeCharacteristicsPojo.setFacialDynamicRange(propertyInfoPojo);
//      eyeCharacteristicsPojo.setPercentFacialBrightness(propertyInfoPojo);
//      eyeCharacteristicsPojo.setPercentFacialSaturation(propertyInfoPojo);
//      eyeCharacteristicsPojo.setPoseAnglePitch(propertyInfoPojo);
//      eyeCharacteristicsPojo.setPoseAngleYaw(propertyInfoPojo);
//      eyeCharacteristicsPojo.setSmileLikelihood(propertyInfoPojo);

        FaceCharacteristicsPojo faceCharacteristicsPojo = new FaceCharacteristicsPojo();
        faceCharacteristicsPojo.setBrightnessScore(propertyInfoPojo);
        faceCharacteristicsPojo.setFacialDynamicRange(propertyInfoPojo);
        faceCharacteristicsPojo.setPercentFacialBrightness(propertyInfoPojo);
        faceCharacteristicsPojo.setPercentFacialSaturation(propertyInfoPojo);
        faceCharacteristicsPojo.setPoseAnglePitch(propertyInfoPojo);
        faceCharacteristicsPojo.setPoseAngleYaw(propertyInfoPojo);
        faceCharacteristicsPojo.setSmileLikelihood(propertyInfoPojo);

        ImageCharacteristicsPojo imageCharacteristicsPojo = new ImageCharacteristicsPojo();
        imageCharacteristicsPojo.setBackgroundPadType(propertyInfoPojo);
        imageCharacteristicsPojo.setBackgroundType(propertyInfoPojo);
        imageCharacteristicsPojo.setDegreeOfClutter(propertyInfoPojo);
        imageCharacteristicsPojo.setNumberChannels(propertyInfoPojo);
        imageCharacteristicsPojo.setPercentBackgroundUniformity(propertyInfoPojo);
//
//        ImageFlashCharacteristicsPojo imageFlashCharacteristicsPojo = new ImageFlashCharacteristicsPojo();
//        imageFlashCharacteristicsPojo.setImageFormat(propertyInfoPojo);
//        imageFlashCharacteristicsPojo.setJ2kCompressionRatio(propertyInfoPojo);
//        imageFlashCharacteristicsPojo.setJ2kRoiBackgroundCompressionRatio(propertyInfoPojo);
//        imageFlashCharacteristicsPojo.setJ2kRoiForegroundCompressionRatio(propertyInfoPojo);
//        imageFlashCharacteristicsPojo.setJpegQualityLevel(propertyInfoPojo);

        ImageGeometryCharacteristicsPojo imageGeometryCharacteristicsPojo = new ImageGeometryCharacteristicsPojo();
        imageGeometryCharacteristicsPojo.setCenterlineLocationRatio(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setEyeAxisAngle(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setEyeAxisLocationRatio(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setEyeAxisLocationRatioChild(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setEyeSeparation(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setHeadHeightToImageHeightRatio(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setImageHeight(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setImageWidth(propertyInfoPojo);
        imageGeometryCharacteristicsPojo.setImageWidthToHeadWidthRatio(propertyInfoPojo);

        ImageStorageCharacteristicsPojo imageStorageCharacteristicsPojo = new ImageStorageCharacteristicsPojo();
        imageStorageCharacteristicsPojo.setImageFormat(propertyInfoPojo);
        imageStorageCharacteristicsPojo.setJ2kCompressionRatio(propertyInfoPojo);
        imageStorageCharacteristicsPojo.setJ2kRoiBackgroundCompressionRatio(propertyInfoPojo);
        imageStorageCharacteristicsPojo.setJ2kRoiForegroundCompressionRatio(propertyInfoPojo);
        imageStorageCharacteristicsPojo.setJpegQualityLevel(propertyInfoPojo);

        PassportProfilePojo passportProfilePojo = new PassportProfilePojo();
        passportProfilePojo.setAnomaliesCharacteristicsPojo(anomaliesCharacteristicsPojo);
        passportProfilePojo.setEyeCharacteristicsPojo(eyeCharacteristicsPojo);
        passportProfilePojo.setFaceCharacteristicsPojo(faceCharacteristicsPojo);
        passportProfilePojo.setImageCharacteristicsPojo(imageCharacteristicsPojo);
        passportProfilePojo.setImageGeometryCharacteristicsPojo(imageGeometryCharacteristicsPojo);
        passportProfilePojo.setImageStorageCharacteristicsPojo(imageStorageCharacteristicsPojo);
        passportProfilePojo.setVersion("6000000");

        return passportProfilePojo;
    }

    public FingerprintProfilePojo newFingerprintProfilePojo() {
        FingerprintProfilePojo fingerprintProfilePojo = new FingerprintProfilePojo();
        fingerprintProfilePojo.setMaxNotPartOfPrint(1);
        fingerprintProfilePojo.setMaxNumberOfDarkBlocks(1);
        fingerprintProfilePojo.setMaxNumberOfLightBlocks(1);
        fingerprintProfilePojo.setMaxNumberOfPoorRidgeFlow(1);
        fingerprintProfilePojo.setMinAFIQ(1);
        fingerprintProfilePojo.setMinNFIQ(1);
        fingerprintProfilePojo.setMinNumberOfBlocks(1);
        fingerprintProfilePojo.setMinNumberOfGoodBlocks(1);
        return fingerprintProfilePojo;
    }

    public FingerprintTypeProfilePojo newFingerprintTypeProfilePojo() {
        FingerprintTypeProfilePojo fingerprintTypeProfilePojo = new FingerprintTypeProfilePojo();
        FingerprintProfilePojo fingerprintProfilePojo = newFingerprintProfilePojo();
        fingerprintTypeProfilePojo.setIndexFingerprintProfilePojo(fingerprintProfilePojo);
        fingerprintTypeProfilePojo.setLastFingerprintProfilePojo(fingerprintProfilePojo);
        fingerprintTypeProfilePojo.setMiddleFingerprintProfilePojo(fingerprintProfilePojo);
        fingerprintTypeProfilePojo.setRingFingerprintProfilePojo(fingerprintProfilePojo);
        fingerprintTypeProfilePojo.setThumbFingerprintProfilePojo(fingerprintProfilePojo);
        return fingerprintTypeProfilePojo;
    }

    public DemographicsPojo newDemographicsPojo() {
        DemographicPropertyInfo demographicPropertyInfo = new DemographicPropertyInfo();
        demographicPropertyInfo.setMaxAllowableContinousCharacters(1);
        demographicPropertyInfo.setMaxAllowableDigits(1);
        demographicPropertyInfo.setMaxAllowableNonDigits(1);
        demographicPropertyInfo.setMaxAllowableSequentialCharacters(1);
        demographicPropertyInfo.setMaxAllowableSequentialKeyboardCharacters(1);
        demographicPropertyInfo.setMaxConsecutiveConsonants(1);
        demographicPropertyInfo.setMaxLength(1);
        demographicPropertyInfo.setMaxSpecialCharacters(1);
        demographicPropertyInfo.setMinLength(1);
        demographicPropertyInfo.setRegexExpression("");
        demographicPropertyInfo.setRegexDescription("");

        DemographicsPojo demographicsPojo = new DemographicsPojo();
        demographicsPojo.setAddress(demographicPropertyInfo);
        demographicsPojo.setCompanyAddress(demographicPropertyInfo);
        demographicsPojo.setCompanyName(demographicPropertyInfo);
        demographicsPojo.setDateOfBirth(demographicPropertyInfo);
        demographicsPojo.setEmail(demographicPropertyInfo);
        demographicsPojo.setFirstName(demographicPropertyInfo);
        demographicsPojo.setMiddleName(demographicPropertyInfo);
        demographicsPojo.setMotherMaidenName(demographicPropertyInfo);
        demographicsPojo.setSurname(demographicPropertyInfo);
        demographicsPojo.setHouseAddress(demographicPropertyInfo);
        demographicsPojo.setStreetAddress(demographicPropertyInfo);
        demographicsPojo.setCityAddress(demographicPropertyInfo);
        demographicsPojo.setCompanyHouseAddress(demographicPropertyInfo);
        demographicsPojo.setCompanyStreetAddress(demographicPropertyInfo);
        demographicsPojo.setCompanyCityAddress(demographicPropertyInfo);
        return demographicsPojo;
    }
}
