package com.sf.biocapture.agl.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.mtn.esf.xmlns.wsdl.getcustomerprofiledetails.GetCustomerProfileDetailsPortType;
import com.mtn.esf.xmlns.xsd.common.CommonComponentsType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.AdditionalInfoNameValType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.AddressCommunicationType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.CustomerPartyResponseType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.EmailCommunicationType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.EmploymentDetailsType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.GetCustomerProfileDetailsRequestType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.GetCustomerProfileDetailsResponseType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.IdentificationType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.PartyContactType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.PersonDetailsType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.PhoneCommunicationType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.StatusInfoType;
import com.mtn.esf.xmlns.xsd.getcustomerprofiledetails.SubscriberSpecificationType;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.proxy.BasicData;
import com.sf.biocapture.proxy.BioData;
import com.sf.biocapture.proxy.DynamicData;
import com.sf.biocapture.proxy.PassportDetail;

import java.text.ParseException;

import javax.ejb.Stateless;

@Stateless
public class GetSubscriberDetails extends DataService {

    private static final String _234 = "234";

    @Inject
    private GetSubscriberIntegrator subscriberIntegrator;
    
    @Inject
    AccessDS accessDS;
    
    @Inject
	private JmsSender jmsSender;

    @SuppressWarnings("PMD")
    public AgilityResponse getSubscriberDetails(String msisdn, String simSerial, String transactionId) {
        GetCustomerProfileDetailsPortType port = null;

        try {
            port = subscriberIntegrator.getCustomerProfilePort();

        GetCustomerProfileDetailsRequestType req = new GetCustomerProfileDetailsRequestType();
        CommonComponentsType cType = new CommonComponentsType();
        if (msisdn != null) {
            //msisdn.startsWith("0") ? msisdn.substring(1) : msisdn
            cType.setMSISDNNum(_234 + msisdn.replaceFirst("0*", "")); //strip the leading zero
        }
        cType.setOpCoID("NG"); //country code
        cType.setSenderID(appProps.getProperty("sender-id", "ESF"));
        cType.setProcessingNumber(transactionId); //transaction id
        //

        req.setCommonComponents(cType);
        //
        req.setQueryType(appProps.getProperty("query-type-getsub", "RegistrationDetails"));//QueryCustomerProfile")); 
        req.setLevelCode("ServiceLevel");
        req.setLOB("GSM");
        req.setExtUser(appProps.getProperty("sender-id", "ESF"));

        if (msisdn != null) {
            IdentificationType it = new IdentificationType();
            it.setIdType("ServiceID");
            it.setIdValue(_234 + msisdn.replaceFirst("0*", ""));
            req.setIdentification(it);
        }

        String requestXml = getSubscriberDetailsMessage(GetCustomerProfileDetailsRequestType.class, req);

        GetCustomerProfileDetailsResponseType resp = null;
        try {
            resp = port.getCustomerProfileDetailsOperation(req);
        } catch (Exception ex) {
            logger.error("Exception: ", ex);
            return getFailureResponse();
        }

        StatusInfoType info = resp.getStatusInfo();
        logger.debug("Code: " + info.getStatusCode() + "; Description: " + info.getStatusDesc()); //0 = success; 1 = failure

        String responseXml = getSubscriberDetailsMessage(GetCustomerProfileDetailsResponseType.class, resp);

        //log request and response
        jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), simSerial, "GET_SUBSCRIBER_DETAILS", msisdn));
//        logger.info("==Queuing agility integration log: " + msg);
        
        AgilityResponse ar = new AgilityResponse();
        ar.setCode(info.getStatusCode());
        ar.setDescription(info.getStatusDesc());

        processAgilityResponse(info, resp, ar);

        return ar;
        } catch (Exception ex) {
            logger.error("", ex);
            return getFailureResponse();
        }
    }
    
    
    /**
     * Processes the response (xml) object from agility
     * to extract required parameters
     * @param info
     * @param resp
     * @param ar
     */
    private void processAgilityResponse(StatusInfoType info, GetCustomerProfileDetailsResponseType resp, AgilityResponse ar){
    	if (info.getStatusCode().equalsIgnoreCase("0")) {
            BioData bioData = new BioData();
            List<CustomerPartyResponseType> cprTypes = resp.getCustomerPartyResponse();

            for (CustomerPartyResponseType cpr : cprTypes) {
                bioData.setBd(getBasicData(cpr.getPersonInformation()));
                bioData.setDynamicData(getDynamicData(cpr));
                bioData.setPassportDetail(getPassportDetail(cpr)); //TODO only check this if subscriber is a foreigner
                bioData.getDynamicData().setDda11(cpr.getLevelCode());
                bioData.getDynamicData().setDa19(cpr.getTypeCode());

                //registration details
                getActivationDate(cpr.getAdditionalInfo(), ar);
                getLastSIMRegDate(cpr.getAdditionalInfo(), ar);
                if(appProps.getBool("getsub-use-parent-status", true)){
                	getParentStatus(cpr.getAdditionalInfo(), ar);
                }else{
                	getRegistrationStatus(cpr.getSubscriberSpecification(), ar);
                }

                List<EmploymentDetailsType> eDetails = cpr.getPersonInformation().getEmploymentDetails();
                for (EmploymentDetailsType eTypes : eDetails) {
                    if ((eTypes.getJobTitle() != null) && !eTypes.getJobTitle().isEmpty()) {
                        bioData.getDynamicData().setDa2(eTypes.getJobTitle());
                        break;
                    }
                }

                // retrieving the middle name ;                        
                List<AdditionalInfoNameValType> infoTypes = cpr.getPersonInformation().getAdditionalInfo();
                for (AdditionalInfoNameValType infoType : infoTypes) {
                    //get middle name
                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("MiddleName")) {
                        bioData.getBd().setOthername((infoType.getValue() == null ? "" : infoType.getValue()));
                        break;
                    }
                }

                //line below is trying to set the registration state based on the registration lga.
                if ((bioData.getDynamicData().getDda9() != null) && !bioData.getDynamicData().getDda9().isEmpty()) {
                    bioData.getBd().setStateOfRegistration(""); // retrieve matching state from kyc db and set same        
                }

                List<PhoneCommunicationType> pcTypes = cpr.getPartyContact().getPhoneCommunication();
                if ((pcTypes != null) && !pcTypes.isEmpty()) {
                    int cnt = 0;
                    for (PhoneCommunicationType pc : pcTypes) {

                        if ((pc.getTypeCode() != null) && pc.getTypeCode().equalsIgnoreCase("TelephoneNumber")) {
                            bioData.getDynamicData().setDa4(pc.getCompleteNumber());
                        }

                        if ((pc.getTypeCode() != null) && pc.getTypeCode().equalsIgnoreCase("AlternateMobileNumber")) {
                            bioData.getDynamicData().setDa10(pc.getCompleteNumber());
                        }

                        if ((pc.getCompleteNumber() != null) && !pc.getCompleteNumber().isEmpty()) {
                            ++cnt;
                        }
                    }
                    bioData.setMsisdnCount(cnt);
                }
                bioData.setUniqueId(getSubscriberSpec(cpr.getSubscriberSpecification(), "BiometricDetails", "TransactionID"));
                logger.debug("==============Registration UNIQUE ID: " + bioData.getUniqueId());

                if ((bioData.getDynamicData().getDda11() != null) && (!bioData.getDynamicData().getDda11().isEmpty())
                        && (bioData.getDynamicData().getDda11().equalsIgnoreCase("Individual"))) {
                	initializeCompanyFields(bioData);
                }

                //retrieve account ID
                bioData.setAccountId(getAccountId(cpr.getAdditionalInfo()));
                
                //check if subscriber is eligible for biometric update
                bioData.setBiometricUpdateEligible(checkBiometricUpdateStatus(cpr.getSubscriberSpecification()));
            }
            ar.setBioData(bioData);
        } else {
            ar.setValid(false);
        }
    }
    
    private void initializeCompanyFields(BioData bioData){
    	bioData.getDynamicData().setDda8("");
        bioData.getDynamicData().setDa15("");
        bioData.getDynamicData().setDa16("");
        bioData.getDynamicData().setDa17("");
        bioData.getDynamicData().setDda14("");
        bioData.getDynamicData().setDda15("");
        bioData.getDynamicData().setDda16("");
        bioData.getDynamicData().setDda17("");
        bioData.getDynamicData().setDda18("");
    }
    
    private boolean checkBiometricUpdateStatus(List<SubscriberSpecificationType> types){
    	String platinumCode = getSubscriberSpec(types, "MobileSIMDetails", "MobileCategoryCode");
    	String mnpCode = getSubscriberSpec(types, "MobileSIMDetails", "PortInFlag");
    	String postpaidCode = getSubscriberSpec(types, "TelecomDetails", "TMDocFlagStatus");
    	
    	logger.debug("Platinum code: " + platinumCode + "; MNP: " + mnpCode + "; Postpaid: " + postpaidCode); 
    	
    	boolean status = isEligibleForBiometricUpdate(platinumCode, SettingsEnum.AGL_BIO_UPDATE_PLATINUM_CODES) || 
    			isEligibleForBiometricUpdate(mnpCode, SettingsEnum.AGL_BIO_UPDATE_MNP_CODES) ||
    			isEligibleForBiometricUpdate(postpaidCode, SettingsEnum.AGL_BIO_UPDATE_POSTPAID_CODES);
        logger.debug("status: " + status);
        return status;
    }
    
    private boolean isEligibleForBiometricUpdate(String categoryCode, SettingsEnum category) {
        if(categoryCode != null && !categoryCode.isEmpty()) {
            String validKeys = accessDS.getSettingValue(category);
            String keys[] = validKeys.split(",");
            for (String key : keys) {
                if(key.equalsIgnoreCase(categoryCode)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private String getSubscriberSpec(List<SubscriberSpecificationType> types, String specCategory, String specName) {
        String fieldValue = "";
        for (SubscriberSpecificationType sType : types) {
        	if(sType.getName().trim().equalsIgnoreCase(specCategory)){
        		List<AdditionalInfoNameValType> infoTypes = sType.getAdditionalInfo();
        		for (AdditionalInfoNameValType infoType : infoTypes) {
        			if ((infoType.getName() != null) && (infoType.getName().equalsIgnoreCase(specName))) {
        				fieldValue = infoType.getValue();
        				break;
        			}
        		}
        	}
        }
        return fieldValue;
    }
    
    private String getAdditionalInfo(List<AdditionalInfoNameValType> infoTypes, String infoName, AgilityResponse ar){
    	String infoValue = null;
    	for (AdditionalInfoNameValType infoType : infoTypes) {
            //get registration date
            if (infoType.getName() != null && infoType.getName().equalsIgnoreCase(infoName)) {
            	infoValue = infoType.getValue();
            	break;
            }
    	}
    	return infoValue;
    }

    private void getActivationDate(List<AdditionalInfoNameValType> infoTypes, AgilityResponse ar) {
    	String activationDate = getAdditionalInfo(infoTypes, "ActivationDate", ar);
    	logger.debug("Activation date from response: " + activationDate);
    	try {
            Date aDate = new SimpleDateFormat("dd/MM/yyyy").parse(activationDate);
            ar.setActivationDate(new Timestamp(aDate.getTime()));
        } catch (ParseException e) {
            logger.error("Exception on formatting activation date from agility : " + e.getMessage());
        }
    }
    
    
    private void getLastSIMRegDate(List<AdditionalInfoNameValType> infoTypes, AgilityResponse ar) {
    	String lastSIMRegDate = getAdditionalInfo(infoTypes, "LastSIMRegDate", ar);
    	logger.debug("last SIM reg date from response: " + lastSIMRegDate);
    	try {
            if (lastSIMRegDate != null) {
                Date aDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(lastSIMRegDate);
                ar.setLastSIMRegDate(new Timestamp(aDate.getTime()));
            }
        } catch (ParseException e) {
            logger.error("Exception on formatting activation date from agility : " + e.getMessage());
        }
    }
    
    private void getParentStatus(List<AdditionalInfoNameValType> infoTypes, AgilityResponse ar){
    	String activationStatus = getAdditionalInfo(infoTypes, "ParentStatus", ar);
    	logger.debug("Activation status from response: " + activationStatus);
    	ar.setActivationStatus(translateActivationStatus(activationStatus));
    }

    private void getRegistrationStatus(List<SubscriberSpecificationType> specs, AgilityResponse ar) {
        for (SubscriberSpecificationType type : specs) {
            if (type.getName() != null && type.getName().equalsIgnoreCase("RegistrationDetails") && type.getStatus() != null) {
                logger.debug("============Registration subscription status: " + type.getStatus());
                ar.setActivationStatus(translateActivationStatus(type.getStatus()));
                break;
            }
        }
    }

    private String getAccountId(List<AdditionalInfoNameValType> additionalInfo) {
        String accountCode = null;
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            for (AdditionalInfoNameValType info : additionalInfo) {
                if (info.getName().equalsIgnoreCase("AccountCode")) {
                    accountCode = info.getValue().trim();
                    break;
                }
            }
        }
        return accountCode;
    }

    private PassportDetail getPassportDetail(CustomerPartyResponseType cpr) {
        PassportDetail pd = new PassportDetail();

        //Retrieving international passport details of a foreigner
        List<AdditionalInfoNameValType> infoTypes = cpr.getPersonInformation().getAdditionalInfo();
        for (AdditionalInfoNameValType infoType : infoTypes) {
            if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("PassportNumber")) {
                pd.setPassportNumber((infoType.getValue() == null ? "" : infoType.getValue().trim()));
            }

            if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("PassportIssuedByDesc")) {
                pd.setIssueCountry((infoType.getValue() == null ? "" : infoType.getValue().trim()));
            }

            if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("PassportExpiryDate")) {
                String strExpiryDate = (infoType.getValue() == null ? "" : infoType.getValue().trim());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date d = null;
                try {
                    d = sdf.parse(strExpiryDate);
                } catch (ParseException e) {
//                    logger.error("Passport Expiry Date failed to parse. format used - yyyyMMdd");
                    logger.error("ParseException on PassportExpiryDate from ESF - " + e.getMessage());
                }
                pd.setExpiryDate(d);
            }
        }

        return pd;
    }

    private BasicData getBasicData(PersonDetailsType pd) {
        BasicData bd = new BasicData();
        bd.setFirstname(pd.getName().getFirstName());
        bd.setSurname(pd.getName().getFamilyName());

        if ((pd.getGender() != null) && !(pd.getGender().isEmpty())) {
            if (pd.getGender().equalsIgnoreCase("M")) {
                bd.setGender("Male");
            } else if (pd.getGender().equalsIgnoreCase("F")) {
                bd.setGender("Female");
            }

        }

        bd.setIsProcessed(true);
        if (pd.getDateOfBirth() != null) {
            String bday = pd.getDateOfBirth();
            try {
                bd.setBirthday(bday == null ? null : new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(bday).getTime()));
            } catch (ParseException e) {
                logger.error("Error in getting subscriber's dob: ", e);
                bd.setBirthday(null);
            }
        }
        return bd;
    }

    private DynamicData getDynamicData(CustomerPartyResponseType cpr) {
        DynamicData dd = new DynamicData();
        dd.setDda19(cpr.getPersonInformation().getNationality());
        dd.setDa1(cpr.getPersonInformation().getDateOfBirth());

        PartyContactType pct = cpr.getPartyContact();
        if (pct != null) {
            List<EmailCommunicationType> communicationTypes = pct.getEmailCommunication();
            if (communicationTypes != null && !communicationTypes.isEmpty()) {
                EmailCommunicationType communicationType = communicationTypes.get(0);
                dd.setDa6(communicationType.getCompleteEmail());
            }
        }            
//            dd.setDa6(cpr.getPartyContact().getEmailCommunication().get(0).getCompleteEmail());
        
        //residential address
        List<AddressCommunicationType> acTypes = cpr.getPartyContact().getAddressCommunication();
        for (AddressCommunicationType acType : acTypes) {
            if ((acType.getUseCode() != null) && acType.getUseCode().equalsIgnoreCase("ResidentialAddress")) {
                StringBuilder sb = new StringBuilder();
                String space = " ";
                if ((acType.getLineOne() != null) && !acType.getLineOne().isEmpty()) {
                    sb.append(acType.getLineOne().trim());
                    dd.setDa11(acType.getLineOne().trim());
                }
                if ((acType.getLineTwo() != null) && !acType.getLineTwo().isEmpty()) {
                    sb.append(space);
                    sb.append(acType.getLineTwo().trim());
                    dd.setDa12(acType.getLineTwo().trim());
                }
                if ((acType.getLineThree() != null)) {
                    sb.append(space).append(String.valueOf(acType.getLineThree()));
                    dd.setDa13(String.valueOf(acType.getLineThree()));
                }

                dd.setDa3(sb.toString()); //residential address

            } else if ((acType.getUseCode() != null) && acType.getUseCode().equalsIgnoreCase("PostalAddress")) {

                if ((acType.getLineOne() != null) && (!acType.getLineOne().isEmpty())) {
                    dd.setDda7(acType.getLineOne().trim()); // postal address
                }
                if (acType.getLineTwo() != null) {
                    dd.setDda18(acType.getLineTwo().trim()); // company postal address
                }

            } else if ((acType.getUseCode() != null) && acType.getUseCode().equalsIgnoreCase("BusinessAddress")) {

                StringBuilder sb = new StringBuilder();
                String space = " ";
                if ((acType.getLineOne() != null) && !acType.getLineOne().isEmpty()) {
                    sb.append(acType.getLineOne().trim());
                    dd.setDa15(acType.getLineOne().trim());
                }

                if ((acType.getLineTwo() != null) && !acType.getLineTwo().isEmpty()) {
                    sb.append(space).append(acType.getLineTwo().trim());
                    dd.setDa16(acType.getLineTwo().trim());
                }

                if ((acType.getLineThree() != null)) {
                    sb.append(space).append(String.valueOf(acType.getLineThree()));
                    dd.setDa17(String.valueOf(acType.getLineThree()));
                }

                dd.setDda15(sb.toString()); //concatenated company address

                dd.setDda17(acType.getCityName()); //company lga

                List<AdditionalInfoNameValType> infoTypes = acType.getAdditionalInfo();
                for (AdditionalInfoNameValType infoType : infoTypes) {
                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("DistrictDescription")) { //Company State
                        dd.setDda16((infoType.getValue() == null ? "" : " " + infoType.getValue().trim()));
                        break;
                    }
                }
                //There's no dynamic data field for company country which esf returns as countrycode			
            } else {
                List<AdditionalInfoNameValType> infoTypes = acType.getAdditionalInfo();
                for (AdditionalInfoNameValType infoType : infoTypes) {
                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("StateDesc")) {
                        dd.setDda5((infoType.getValue() == null) ? "" : infoType.getValue().trim());
//                                    break;
                    } else if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("LGADesc")) {
                        dd.setDda6((infoType.getValue() == null) ? "" : infoType.getValue().trim());//lga of residence
//                                    break;
                    }

                }
            }
        }

        // This retrieves MothersMaidenName,lga of registration and Type of Identification
        List<SubscriberSpecificationType> subType = cpr.getSubscriberSpecification();
        for (SubscriberSpecificationType sType : subType) {
            if ((sType.getName() != null) && sType.getName().equalsIgnoreCase("ServiceAdditionalDetails")) {
                List<AdditionalInfoNameValType> infoTypes = sType.getAdditionalInfo();
                for (AdditionalInfoNameValType infoType : infoTypes) {

                                //State of origin and lga of origin
                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("StateOfOriginDesc")) {
                        dd.setDa8((infoType.getValue() == null ? "" : infoType.getValue().trim()));
                    }

                    //lga of origin
                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("LGAOfOriginDesc")) {
                        dd.setDa9((infoType.getValue() == null ? "" : infoType.getValue().trim()));
                    }

                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("MotherMaidenName")) {
                        dd.setDda12((infoType.getValue() == null ? "" : infoType.getValue().trim()));
                    }

                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("DocumentTypeDesc")) {
                        dd.setDa5((infoType.getValue() == null ? "" : infoType.getValue().trim()));
                    }

                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("RegistrationCityDesc")) {
                        dd.setDda9((infoType.getValue() == null ? "" : infoType.getValue().trim()));
                    }

                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("SFCompanyName")) {
                        dd.setDda8((infoType.getValue() == null ? "" : infoType.getValue()));
                    }

                    if ((infoType.getName() != null) && infoType.getName().equalsIgnoreCase("SFCompRCNum")) {
                        dd.setDda14((infoType.getValue() == null ? "" : infoType.getValue()));
                    }
                }
            }
        }

        dd.setDa19(cpr.getTypeCode()); //subscriber type - prepaid/postpaid

        return dd;
    }
    
    @SuppressWarnings("rawtypes")
    public <T> String getSubscriberDetailsMessage(Class clazz, T obj){
    	try {
    		JAXBContext context = JAXBContext.newInstance(clazz);
    		Marshaller m = context.createMarshaller();
    		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    		//write response to output stream
    		OutputStream os = new ByteArrayOutputStream();
    		m.marshal(obj, os);                        
    		return os.toString();
    	} catch (JAXBException e) {
    		logger.error("Exception thrown in umarshalling ESF response:", e);
    	}
    	return null;
    }
}
