
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.entity.enums;

/**
 *
 * @author Marcel
 * @since 09-Nov-2016, 09:15:07
 */
@SuppressWarnings("PMD")
public enum SettingsEnum {

    OTP_REQUIRED("OTP-REQUIRED", "false", "Used to specify whether otp is required during client login"),
    LOGIN_OFFLINE("LOGIN-OFFLINE", "false", "Determines whether an agent can login offline"),
    LOGIN_OFFLINE_VALIDATION_TYPE("LOGIN-OFFLINE-VALIDATION-TYPE", "CACHED", "Can either be CACHED OR SHORTCODE"),
    AIRTIME_SALES_MANDATORY("AIRTIME-SALES-MANDATORY", "false", "Informs client on the availability of airtime sales"),
    AIRTIME_SALES_URL("AIRTIME-SALES-URL", "http://seamfix.com/", "URL to airtime sales"),
    AVAILABLE_USE_CASE("AVAILABLE-USE-CASE", "NS,NM,SS,RR,AR,BU", "This is used to specify available use case exposed to client"),
    CLIENT_ACTIVITY_LOG_BATCH_SIZE("CLIENT-ACTIVITY-LOG-BATCH-SIZE", "20", "Determines the batch size of client activity log sent to the server from the client"),
    MAXIMUM_MSISDN_ALLOWED_PER_REGISTRATION("MAXIMUM-MSISDN-ALLOWED-PER-REGISTRATION", "5", "Specifies the maximum number of msisdn that can be registered per registration process"),
    AGILITY_SIM_SERIAL_VALID_STATUS_KEYS("AGILITY-SIM-SERIAL-VALID-STATUS-KEYS", "F,E", "Comma separated keys used to specify valid sim serial keys retrieved from agility"),
    AGL_BIO_UPDATE_PLATINUM_CODES("AGL-BIO-UPDATE-PLATINUM-CODES", "PLATM", "Comma separated codes used to specify the category codes valid for registering platinum customers"),
    AGL_BIO_UPDATE_MNP_CODES("AGL-BIO-UPDATE-MNP-CODES", "Port-In", "Comma separated codes used to specify the category codes valid for registering MNP customers"),
    AGL_BIO_UPDATE_POSTPAID_CODES("AGL-BIO-UPDATE-POSTPAID-CODES", "Y", "Comma separated codes used to specify the category codes valid for registering Postpaid customers"),
    SIMSWAP_DATE_VALIDATION("SIMSWAP-DATE-VALIDATION", "true", "Indicates if sim swap date will be compared against activation date"),
    SIMSWAP_TIME_FRAME("SIMSWAP-TIME-FRAME", "7", "Minimum no of days allowed between sim activation and sim swap"),
    ENABLE_VAS_MODULE("ENABLE-VAS-MODULE", "true", "Determines whether or not the VAS module will be enabled on the clients"),
    MINIMUM_ACCEPTABLE_CHARACTER("MINIMUM-ACCEPTABLE-CHARACTER", "2", "This is the minimum number of allowed characters for name field during foreigner's registration"),
    
    NOTIFICATION_CHECKER_INTERVAL("NOTIFICATION-CHECKER-INTERVAL", "60", "Time interval, in seconds, between which client checks for notifications from server"),
    AGENT_BIOSYNC_INTERVAL("AGENT-BIOSYNC-INTERVAL", "60", "Time interval, in seconds, between which client sends available agents' biometrics to server"),
    AUDIT_XML_SYNC_INTERVAL("AUDIT-XML-SYNC-INTERVAL", "1800", "Time interval, in seconds, between which client sends available audit xml files to SFTP server"),
    THRESHOLD_CHECKER_INTERVAL("THRESHOLD-CHECKER-INTERVAL", "3600", "Time interval, in seconds, between which client checks server for threshold updates"),
    ACTIVATION_CHECKER_INTERVAL("ACTIVATION-CHECKER-INTERVAL", "1200", "Time interval, in seconds, between which client checks server for activation status of msisdns"),
    SYNCHRONIZER_INTERVAL("SYNCHRONIZER-INTERVAL", "2", "Time interval, in seconds, between which client sends available sync files to SFTP server for processing"),
    HARMONIZER_INTERVAL("HARMONIZER-INTERVAL", "120", "Time interval, in seconds, between which client checks for status of each registration using unique ID"),
    SETTINGS_INTERVAL("SETTINGS_INTERVAL", "300", "Time interval, in seconds, between which client checks server for settings"),
    CLIENT_LOCATION_CHECK_POPUP_INTERVAL("CLIENT-LOCATION-CHECK-POPUP-INTERVAL", "3600", "controls the interval at which device location status is checked"),
    UPDATE_DEVICE_ID_INTERVAL("UPDATE-DEVICE-ID-INTERVAL", "600", "the interval at which the thread that updates device id at the backend is called. this call is made iff device id has not been updated before"),
    BLACKLIST_CHECKER_INTERVAL("BLACKLIST-CHECKER-INTERVAL", "600", "Time interval, in seconds, between which client checks server for kit and agent blacklist status"),
    OFFLINE_RESPONSE_SHORTCODE("OFFLINE-RESPONSE-SHORTCODE", "5034", "The response shortcode used during offline login"),
    OFFLINE_REQUEST_SHORTCODE("OFFLINE-REQUEST-SHORTCODE", "799", "The shortcode called during offline login"),
    RE_REGISTRATION_RULE_DAYS_LIMIT("RE-REGISTRATION-RULE-DAYS-LIMIT", "90", "The number of days required before a number can be re-registered"),
    EMAIL_NOTIFICATION_AUTHENTICATOR_FROM_EMAIL("BS-EMAIL-NOTIFICATION-AUTHENTICATOR-FROM-EMAIL", "noreply@kyc.mtnnigeria.net", "Sender's email address"),
    EMAIL_NOTIFICATION_AUTHENTICATOR_USER_NAME("BS-EMAIL-NOTIFICATION-AUTHENTICATOR-USERNAME", "bm@kyc.mtnnigeria.net", "Sender's user name"),
    EMAIL_NOTIFICATION_AUTHENTICATOR_PASSWORD("BS-EMAIL-NOTIFICATION-AUTHENTICATOR-PASSWORD", "openminds", "Sender's password"),
    EMAIL_NOTIFICATION_AUTHENTICATOR_HOST_NAME("BS-EMAIL-NOTIFICATION-AUTHENTICATOR-HOST-NAME", "10.1.224.7", "The hostname of the outgoing mail server."),
    EMAIL_NOTIFICATION_AUTHENTICATOR_HOST_PORT("BS-EMAIL-NOTIFICATION-AUTHENTICATOR-HOST-PORT", "25", "The port number of the outgoing mail server"),
    CAC_ROLE("CAC-ROLE", "CAC", "The role for CAC users. This should be comma separated should there be need to send email to multiple roles"),
    SIMROP_URL("SIMROP-URL", "https://seamfix.com", "SIMROP url"),
    
    SAME_LOCATION_RANGE("SAME-LOCATION-RANGE", "30", "Distance in meters that is considered as the same street/location"),
    GEO_FENCE_RADIUS("GEO-FENCE-RADIUS", "50", "Geofence radius in meters, outside which a notification will be sent."),
    GET_GEO_FENCE_LOG_BY_KIT("GET-GEO-FENCE-LOG-BY-KIT", "true", "Pull geofence log by a kit, when getting address"),

    BS_GOOGLE_MAP_API_KEY("BS-GOOGLE-MAP-API-KEY", "AIzaSyCURcOfGBLOEPpV5tCXqwZALoZUcFv_Lks", "This is the google map api key for reverse geocode to retrieve address"),
    OSM_REVERSE_GEOCODING_URL("OSM-REVERSE-GEOCODING-URL", "http://nominatim.openstreetmap.org/reverse?format=json&addressdetails=1&lat=", "OSM url for reverse geocoding"),
    PROXY_IP_FOR_GEOCODING("PROXY-IP-FOR-GEOCODING", "10.1.224.41", "The proxy IP used for reverse geocoding."),
    PROXY_PORT_FOR_GEOCODING("PROXY-PORT-FOR-GEOCODING", "8080", "The proxy port used for reverse geocoding."),
    
    ACTIVATION_STATUS_MAPPER("ACTIVATION-STATUS-MAPPER", "ACTIVATED:ACTIVATED,FAILED_ACTIVATION:FAILEDACTIVATION,FAILED_VALIDATION:FAILEDVALIDATION", "This is used to map seamfix enum against agility activation status code. Agility code may change but seamfix should not because seamfix code represents an enum in code. Entry format is; SEAMFIX_CODE:AGILITY_CODE"),
    DYA_AVAILABLE_USE_CASE("DYA-USE-CASE", "NS,NM,RR,AR,BU", "This is used to specify available use case to perform request of DYA in client"),
    DYA_ACCOUNT_TYPE("DYA-ACCOUNT-TYPE","[Select Yellow Account Type],DYA","This is the different yellow account types"),
    ALLOW_CLIENT_TAGGING("ALLOW-CLIENT-TAGGING", "true", "Determine whether tagging and re-tagging activities can be performed on the clients"),
    LOCATION_MANDATORY("LOCATION-MANDATORY","true","Determines whether location should be sent for every registration done on the clients"),
    
    AVAILABLE_VTU_TRANSACTION_TYPE("AVAILABLE-VTU-TRANSACTION-TYPE", "AIRTIME,DATA", "This is a comma separated enum values (AIRTIME,DATA) indicating the vtu vending transaction types that will be made available to the client"),
    VTU_VENDING_MANDATORY("VTU-VENDING-MANDATORY", "true", "flag to determine whether vtu vending is mandatory after registration on th client"),
    VENDING_SEQUENCE("VENDING-SEQUENCE", "1", "MTN vending sequence for making calls to vending service"),
    VTU_VEND_USERNAME("VTU-VEND-USERNAME", "", "MTN VTU vending username"),
    VTU_VEND_PASSWORD("VTU-VEND-PASSWORD", "", "MTN VTU vending password"),
    VTU_SERVICE_URL("VTU-SERVICE-URL", "http://localhost:7080/mock-vas-services/mock-host-if-service", "MTN VTU service URL"),
    MTN_DATA_TARIFF_TYPE_ID("MTN-DATA-TYPE-ID", "9", "Tariff type id parameter for MTN data"),
    MTN_AIRTIME_TARIFF_TYPE_ID("MTN-AIRTIME-TYPE-ID", "1", "Tariff type id parameter for MTN airtime")
    ;



    private SettingsEnum(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    private String name;
    private String value;
    private String description;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }


}