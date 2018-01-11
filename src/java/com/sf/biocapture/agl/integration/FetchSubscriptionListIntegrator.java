package com.sf.biocapture.agl.integration;

import com.mtn.esf.xmlns.wsdl.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListPortType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mtn.esf.xmlns.xsd.common.CommonComponentsType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.AdditionalInfoType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListRequestType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListResponseType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.IdentificationType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.StatusInfoType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.SubscriptionListType;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.DataService;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import javax.ejb.Stateless;

@SuppressWarnings({"PMD", "CPD-START"}) //this was added to bypass Exception catch in cases where web service calls are made. 
@Stateless
public class FetchSubscriptionListIntegrator extends DataService {

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private static final String _234 = "234";
    private static final String ESF = "ESF";

    @Inject
    private GetSubscriberIntegrator subscriberIntegrator;
    
    @Inject
	private JmsSender jmsSender;

    public AgilityResponse fetchStatusLevelSubscriptionList(String msisdn, String transactionId) {
        logger.debug("MSISDN: " + msisdn + " ; Transaction Id : " + transactionId);

        FetchCustomerSubscriptionListPortType port = null;

        try {
            logger.debug("retrieving fetch port");
            port = subscriberIntegrator.getFetchSubscriberListPort();
        } catch (Exception ex) {
            logger.error("", ex);
            return getFailureResponse();
        }

        logger.debug("instantiation request type");
        FetchCustomerSubscriptionListRequestType req = new FetchCustomerSubscriptionListRequestType();
        logger.debug("instantiation common type");
        CommonComponentsType cType = new CommonComponentsType();
        if (msisdn != null) {
            // msisdn.startsWith("0") ? _234 + msisdn.substring(1) : msisdn);
            cType.setMSISDNNum(_234 + msisdn.replaceFirst("0*", "")); //strip the leading zero
        }
        cType.setOpCoID("NG"); //country code
        cType.setSenderID(appProps.getProperty("sender-id", ESF));
        cType.setProcessingNumber(transactionId); //transaction id
        //
        req.setCommonComponents(cType);
        //
        req.setQueryType(appProps.getProperty("query-type-getsubscription-list", "GetServiceStatus"));
        req.setPrefLang("en");

        if (msisdn != null) {
            IdentificationType serviceIdType = new IdentificationType();
            serviceIdType.setIdType("ServiceID");
            serviceIdType.setIdValue(_234 + msisdn.replaceFirst("0*", ""));
            req.getIdentification().add(serviceIdType);

            IdentificationType userIdType = new IdentificationType();
            userIdType.setIdType("UserID");
            userIdType.setIdValue(ESF);
            req.getIdentification().add(userIdType);
        }

        QueryCriteriaType qc = new QueryCriteriaType();
        qc.setName("InfoLevel");
        qc.setValue("StatusLevel");
        req.getQueryCriteria().add(qc);

        String requestXml = getFetchSubscriptionListMessage(FetchCustomerSubscriptionListRequestType.class, req);
//        logger.debug("requestXml: " + requestXml);

        FetchCustomerSubscriptionListResponseType resp = null;
        try {
            logger.debug("sending activation status check request.");
            resp = port.fetchCustomerSubscriptionList(req);
            logger.debug("retrieving activation status check response.");

        } catch (Exception ex) {
            logger.error("", ex);
            ex.printStackTrace();
            return getFailureResponse();
        }

        StatusInfoType info = resp.getStatusInfo();
        logger.debug("Code: " + info.getStatusCode()); //0 = success; 1 = failure
        logger.debug("Description: " + info.getStatusDesc());

        String responseXml = getFetchSubscriptionListMessage(FetchCustomerSubscriptionListResponseType.class, resp);
//        logger.debug("responseXml: " + responseXml);

        //log request and response
        jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), null, "FETCH_SUBSCRIPTION_LIST", msisdn));

        AgilityResponse ar = new AgilityResponse();
        ar.setCode(info.getStatusCode());
        ar.setDescription(info.getStatusDesc());

        if (info.getStatusCode().equalsIgnoreCase("0")) {
            ar.setValid(Boolean.TRUE);
            List<SubscriptionListType> subListType = resp.getSubscriptionList();
            for (SubscriptionListType sType : subListType) {

                ar.setUniqueId(transactionId);

                //Retrieving the activation date.
                List<AdditionalInfoType> aTypes = sType.getAdditionalInfo();
                for (AdditionalInfoType aT : aTypes) {
                    if ((aT.getID() != null) && (aT.getID().equalsIgnoreCase("BiometricAvailableFor"))) {
                        try {
                            ar.setChildMsisdnCount(Integer.valueOf(aT.getValue()));
                        } catch (Exception e) {
                            logger.error("GetsubscriberActivationStatus : Exception on the returned BiometricAvailableFor : " + e.getMessage());
                            ar.setChildMsisdnCount(null);
                        }
                    }

                    if ((aT.getID() != null) && (aT.getID().equalsIgnoreCase("AssetStatus"))) {
                        try {
                            ar.setAssetStatus(translateAssetStatus(aT.getValue()));
                        } catch (Exception e) {
                            logger.error("FetchSubscriptionListIntegrator : Exception on the returned AssetStatus : " + e.getMessage());
                            ar.setAssetStatus(null);
                        }
                    }
                }
            }

        } else {
            ar.setValid(false);
        }

        return ar;
    }

    public String translateAssetStatus(String value) {
        if ((value == null) || value.isEmpty()) {
            return null;
        }

        switch (value) {
            case "A":
                value = "Active";
                break;
            case "Q":
                value = "Quarantine";//Q status numbers are used for recycled";
                break;
            case "F":
                value = "Free";
                break;
            case "G":
                value = "Blocked";//Blocked for DSA";
                break;
            case "R":
                value = "Reserved";
                break;
            case "P":
                value = "Pending";
                break;
            case "E":
                value = "Paired";
                break;
            case "O":
                value = "Port out";
                break;
        }
        return value;
    }

    public AgilityResponse fetchServiceLevelSubscriptionList(String msisdn, String transactionId) {
        logger.debug("MSISDN: " + msisdn + " ; Transaction Id : " + transactionId);

        AgilityResponse ar = null;
        FetchCustomerSubscriptionListPortType port = null;

        try {
            logger.debug("retrieving fetch port");
            port = subscriberIntegrator.getFetchSubscriberListPort();
        } catch (Exception ex) {
            logger.error("", ex);
            return getFailureResponse();
        }

        logger.debug("instantiation request type");
        FetchCustomerSubscriptionListRequestType req = new FetchCustomerSubscriptionListRequestType();
        logger.debug("instantiation common type");
        CommonComponentsType cType = new CommonComponentsType();
        if (msisdn != null) {
            // msisdn.startsWith("0") ? _234 + msisdn.substring(1) : msisdn);
            cType.setMSISDNNum(_234 + msisdn.replaceFirst("0*", "")); //strip the leading zero
        }
        cType.setOpCoID("NG"); //country code
        cType.setSenderID(appProps.getProperty("sender-id", ESF));
        cType.setProcessingNumber(transactionId); //transaction id
        //
        Date d = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss.SSS");
        XMLGregorianCalendar xmlDate;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(sdf1.format(d) + "T" + sdf2.format(d) + "Z");
            //			cType.setOrderDateTime(xmlDate);
        } catch (Exception e1) {
            logger.error("", e1);
        }
        req.setCommonComponents(cType);
        //
        req.setQueryType(appProps.getProperty("query-type-getsubscription-list", "GetServiceStatus"));
        req.setPrefLang("en");

        if (msisdn != null) {
            IdentificationType serviceIdType = new IdentificationType();
            serviceIdType.setIdType("ServiceID");
            serviceIdType.setIdValue(_234 + msisdn.replaceFirst("0*", ""));
            req.getIdentification().add(serviceIdType);

            IdentificationType userIdType = new IdentificationType();
            userIdType.setIdType("UserID");
            userIdType.setIdValue(ESF);
            req.getIdentification().add(userIdType);
        }

        QueryCriteriaType qc = new QueryCriteriaType();
        qc.setName("InfoLevel");
        qc.setValue("ServiceLevel");
        req.getQueryCriteria().add(qc);

        String requestXml = null;
        String responseXml = null;

        FetchCustomerSubscriptionListResponseType resp = null;
        try {
            logger.debug("sending activation status check request.");
            resp = port.fetchCustomerSubscriptionList(req);
            logger.debug("retrieving activation status check response.");

        } catch (Exception ex) {
            logger.error("", ex);
            return getFailureResponse();
        }

        StatusInfoType info = resp.getStatusInfo();
        logger.debug("Code: " + info.getStatusCode()); //0 = success; 1 = failure
        logger.debug("Description: " + info.getStatusDesc());

        //log request and response
        jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), null, "FETCH_SUBSCRIPTION_LIST", msisdn));
//        logger.info("==Queuing agility integration log: " + msg);

        ar = new AgilityResponse();
        ar.setCode(info.getStatusCode());
        ar.setDescription(info.getStatusDesc());

        if (info.getStatusCode().equalsIgnoreCase("0")) {
            ar.setValid(Boolean.TRUE);
            List<SubscriptionListType> subListType = resp.getSubscriptionList();
            for (SubscriptionListType sType : subListType) {
                String subscriptionStatus = sType.getSubscriptionStatus();
                logger.debug("*** subscription status is  : " + subscriptionStatus);
                ar.setActivationStatus(translateActivationStatus(subscriptionStatus));
                ar.setUniqueId(transactionId);

                //Retrieving the activation date.
                List<AdditionalInfoType> aTypes = sType.getAdditionalInfo();
                for (AdditionalInfoType aT : aTypes) {
                    if ((aT.getID() != null) && (aT.getID().equalsIgnoreCase("ActivationDate"))) {
                        try {
                            String s = aT.getValue();
                            Date aDate = sdf.parse(s);
                            ar.setActivationDate(new Timestamp(aDate.getTime()));
                            break;
                        } catch (Exception e) {
                            logger.error("GetsubscriberActivationStatus : Exception on formatting activation date from agility : " + e.getMessage());
                        }
                    }
                }
            }

        } else {
            ar.setValid(false);
        }

        return ar;
    }

    @SuppressWarnings("rawtypes")
    public <T> String getFetchSubscriptionListMessage(Class clazz, T obj){
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
