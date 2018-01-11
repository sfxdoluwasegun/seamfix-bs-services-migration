package com.sf.biocapture.ws.tags;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.device.DeviceTagRequest;
import com.sf.biocapture.entity.device.enums.TagApprovalStatusEnum;
import com.sf.biocapture.entity.device.enums.TagRequestTypeEnum;
import com.sf.biocapture.entity.enums.DeviceTypeEnum;
import com.sf.biocapture.entity.enums.KycPrivilege;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.HeaderIdentifier;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import com.sf.biocapture.ws.tags.pojo.EnrollmentRefPojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.HttpHeaders;
import nw.orm.core.exception.NwormQueryException;
import org.hibernate.HibernateException;

@Stateless
public class TagsAction extends BsClazz {

    @Inject
    protected TagsDS ds;

    @Inject
    private AccessDS accessDS;

    @Inject
    private IntrusionAnalyzer analyzer;

    private boolean kitIsBlacklisted(String tag, String mac, String deviceId) {
        return accessDS.checkBlacklistStatus(tag, mac, deviceId).equalsIgnoreCase("Y");
    }

    /**
     * This method is used to update existing enrollment ref with their
     * corresponding device id which the client generates on first launch of the
     * application after the installation of the build with the device id
     * implementation.
     *
     * @param headers
     * @return
     */
    public ResponseData updateDeviceId(HttpHeaders headers, TagRequestPojo tagRequestPojo) {
        logger.warn("Device ID Update: " + tagRequestPojo);
        ResponseData responseData = new ResponseData(ResponseCodeEnum.ERROR);
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);

        EnrollmentRef enrollmentRef = null;
        try {
            enrollmentRef = accessDS.getEnrollmentRefByDeviceId(headerIdentifier.getRefDeviceId());
            if (enrollmentRef == null) {
                //not found try by mac address
                enrollmentRef = accessDS.getEnrollmentRefByMac(headerIdentifier.getMac());
                if (enrollmentRef == null) {
                    //kit does not exist
                    logger.debug("Enrollment Ref not found for the provided Mac Address: " + headerIdentifier.getMac());
                    return responseData;
                }
            }
            if (enrollmentRef.getDeviceId() == null || enrollmentRef.getDeviceId().isEmpty()) {
                //update enrollment ref iff device id is null or empty
                enrollmentRef.setDeviceId(headerIdentifier.getRefDeviceId());
                accessDS.getDbService().update(enrollmentRef);
                logger.debug("Updated enrollment ref device id successfully.");

                Node node = accessDS.getNodeByMac(headerIdentifier.getMac());
                if (node != null) {
                    node.setEnrollmentRef(enrollmentRef);
                    accessDS.getDbService().update(node);
                    logger.debug("Updated Node successfully.");
                }
                //create and approve device tag request if none exists. this is needed to ensure that existing systems have at least one device tag request.                
                if (!accessDS.hasDeviceTagRequest(headerIdentifier.getRefDeviceId())) {
                    DeviceTagRequest deviceTagRequest = new DeviceTagRequest();
                    deviceTagRequest.setAppVersion(tagRequestPojo.getAppVersion());
                    deviceTagRequest.setRequestedDeviceId(headerIdentifier.getRefDeviceId());
                    deviceTagRequest.setTagRequestTypeEnum(TagRequestTypeEnum.TAG);
                    deviceTagRequest.setTagApprovalStatusEnum(TagApprovalStatusEnum.APPROVED);
                    deviceTagRequest.setEnrollmentRef(enrollmentRef);
                    deviceTagRequest.setKitTag(enrollmentRef.getCode());
                    try {
                        if (tagRequestPojo.getDeviceType() != null) {
                            deviceTagRequest.setDeviceTypeEnum(DeviceTypeEnum.valueOf(tagRequestPojo.getDeviceType().trim()));
                        }
                    } catch (IllegalArgumentException e) {
                        logger.error("Unable to convert Device Type Enum: " + tagRequestPojo.getDeviceType(), e);
                    }
                    try {
                        accessDS.save(deviceTagRequest);
                    } catch (NwormQueryException e) {
                        logger.error("Unable to create Device Tag Request", e);
                    }
                }
            } else {
                logger.debug("Device id is up to date");
            }
            responseData.setCode(ResponseCodeEnum.SUCCESS);
        } catch (NwormQueryException e) {
            logger.error("", e);
        }
        return responseData;
    }

    public ResponseData confirmDeviceTag(TagRequestPojo tagRequestPojo) {
        logger.warn("Self Tag Request: " + tagRequestPojo);
        TagResponsePojo responseData = new TagResponsePojo(ResponseCodeEnum.ERROR);
        responseData.setDescription("Device is not yet tagged");
        if (tagRequestPojo != null && tagRequestPojo.getDeviceId() != null && !tagRequestPojo.getDeviceId().trim().isEmpty()) {
            EnrollmentRef enrollmentRef = null;
            try {
                enrollmentRef = accessDS.getEnrollmentRefByDeviceId(tagRequestPojo.getDeviceId().trim());
            } catch (NwormQueryException e) {
                logger.error("", e);
            }
            if (enrollmentRef == null) {
                initiateDeviceTagRequest(tagRequestPojo);
                responseData.setCode(ResponseCodeEnum.ERROR);
                responseData.setDescription("Device is not yet tagged");
                return responseData;
            }
            /**
             * this initialization was done just to ensure that the enrollment
             * ref id is not part of what was sent to the client
             */

            String deploymentStateName = accessDS.getDeploymentStateName(enrollmentRef.getId());
            EnrollmentRefPojo er = new EnrollmentRefPojo();
            er.setCode(enrollmentRef.getCode());
            er.setCorporate(enrollmentRef.getCorporate());
            er.setCustom1(enrollmentRef.getCustom1());
            er.setCustom2(enrollmentRef.getCustom2());
            er.setCustom3(enrollmentRef.getCustom3());
            er.setDateInstalled(enrollmentRef.getDateInstalled());
            er.setDescription(enrollmentRef.getDescription());
            er.setDeviceId(enrollmentRef.getDeviceId());
            er.setInstalledBy(enrollmentRef.getInstalledBy());
            er.setMacAddress(enrollmentRef.getMacAddress());
            er.setName(enrollmentRef.getName());
            er.setStateName(deploymentStateName);
            er.setNetworkCardName(enrollmentRef.getNetworkCardName());
            responseData.setCode(ResponseCodeEnum.SUCCESS);
            responseData.setDescription("Device is tagged");
            responseData.setEnrollmentRef(er);
        } else {
            logger.debug("Tag Request Pojo: " + tagRequestPojo);
        }

        return responseData;
    }

    private void initiateDeviceTagRequest(TagRequestPojo tagRequestPojo) {
        if (tagRequestPojo == null) {
            logger.error("Request payload is null.");
            return;
        }
        String deviceId = tagRequestPojo.getDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            logger.debug("Device ID is null or empty: " + deviceId);
            return;
        }
        if (accessDS.hasDeviceTagRequest(deviceId)) {
            logger.debug("Device Tag Request was previously initiated: " + deviceId);
            return;
        }

        DeviceTagRequest deviceTagRequest = new DeviceTagRequest();
        deviceTagRequest.setRequestedDeviceId(deviceId);
        deviceTagRequest.setAppVersion(tagRequestPojo.getAppVersion());
        deviceTagRequest.setTagRequestTypeEnum(TagRequestTypeEnum.TAG);
        try {
            if (tagRequestPojo.getDeviceType() != null) {
                deviceTagRequest.setDeviceTypeEnum(DeviceTypeEnum.valueOf(tagRequestPojo.getDeviceType().trim()));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Unable to convert Device Type Enum: " + tagRequestPojo.getDeviceType(), e);
        }
        try {
            accessDS.save(deviceTagRequest);
            logger.debug("Device Tag Request was  initiated successfully: " + deviceId);
        } catch (NwormQueryException e) {
            logger.error("", e);
        }
    }

    public ClientRefResponse retag(HttpHeaders headers, ClientRefRequest cr) {
        ClientRefResponse cresp = new ClientRefResponse();
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);

        logger.debug("MAC ADDRESS: " + cr.getMac() + "; Tag: " + cr.getRef() + " ; admin email : [" + cr.getAdminEmail() + "]");
        try {
            String clientTaggingAllowed = accessDS.getSettingValue(SettingsEnum.ALLOW_CLIENT_TAGGING);
            if (!Boolean.parseBoolean(clientTaggingAllowed)) {
                cresp.setStatus(-2);
                cresp.setMessage("Re-Tag not allowed. Upgrade to the latest version.");
                return cresp;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Unable able to convert setting value to a boolean.", e);
        }

        try {
            KMUser kUser = ds.getUser(cr.getAdminEmail());
            if ((cr.getAdminEmail() == null) || (cr.getAdminEmail().isEmpty()) || (kUser == null) || !kUser.isActive() || !kUser.hasPrivilege(KycPrivilege.TAGGING)) {
                cresp.setStatus(-2);
                cresp.setMessage("A valid user email is required for re-tagging a kit. Please provide one and try again.");
                return cresp;
            }

            if (isEmpty(cr.getMac()) || isEmpty(cr.getRef())) {
                // invalid request received
                cresp.setStatus(-2);
                cresp.setMessage("Unspecified Machine ID. MAC Address not valid");
            } else {
                //check if kit is blacklisted
                if (kitIsBlacklisted(cr.getRef(), cr.getMac(), headerIdentifier.getRefDeviceId())) {
                    cresp.setStatus(-2);
                    cresp.setMessage("Kit is blacklisted. Please contact support");
                    return cresp;
                }

                //check if client time is incorrect
                if (cr.getClientTime() != null) {
                    if (!ds.clientTimeIsCorrect(String.valueOf(cr.getClientTime().getTime()))) {
                        cresp.setStatus(-2);
                        cresp.setMessage("Please correct your system time. Server time is " + new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(new Date()));
                        return cresp;
                    }
                }

                List<EnrollmentRef> refs = ds.getKitByTagOrMac(cr.getMac(), cr.getRef()); // check for ref existence
                logger.debug("No of records found: " + refs.size());
                if (refs.isEmpty()) {
                    // ref does not exist
                    cresp.setStatus(-2);
                    cresp.setMessage("This Kit has not been tagged. Retag request has been rejected");
                } else if (refs.size() == 1) {
                    // ref exists
                    logger.debug("-------->>>>> ABOUT TO RETAG KIT REF: " + cr.getRef());
                    EnrollmentRef ref = refs.get(0);
                    cresp = ds.retagKit(cr, ref, false);

                } else if (refs.size() == 2) {
                    logger.debug("Double kit entry detected...");
                    cresp = ds.handleDoubleKitEntry(cr, refs);
                } else {
                    cresp.setStatus(-2);
                    cresp.setMessage("Multiple Entries found, please contact support");
                }
            }
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Error while re-tagging kit.", e);
        }
        return cresp;
    }

    public ClientRefResponse testKit(String refz) {
        ClientRefResponse cr = new ClientRefResponse();
        logger.debug("Test request received " + refz);
        EnrollmentRef ref = ds.getEnrollmentRef(refz);
        if (ref != null) {
            ref.setCustom1("TESTED");
            if (ds.update(ref)) {
                cr.setStatus(0);
                cr.setMessage("Test result updated successfully");
                return cr;
            }
        }
        cr.setStatus(-2);
        cr.setMessage("Failed to update test result, please retry");

        return cr;
    }

    /**
     * Returns true if text is empty
     *
     * @param text
     * @return
     */
    private boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

}
