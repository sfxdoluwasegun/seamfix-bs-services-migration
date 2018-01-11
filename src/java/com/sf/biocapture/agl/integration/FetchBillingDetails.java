package com.sf.biocapture.agl.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.mtn.esf.xmlns.wsdl.fetchcustomerbillingdetails.FetchCustomerBillingDetailsPortType;
import com.mtn.esf.xmlns.xsd.common.CommonComponentsType;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.CustomerPartyBillingProfileType;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.FetchCustomerBillingDetailsRequestType;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.FetchCustomerBillingDetailsResponseType;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.IdentificationType;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.IdentificationTypeBilling;
import com.mtn.esf.xmlns.xsd.fetchcustomerbillingdetails.StatusInfoType;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.DataService;

/**
 * Calls ESF to retrieve the billing info 
 * (like invoice amount) of a subscriber
 * @author Nnanna
 *
 */
@SuppressWarnings("PMD")
@Stateless
public class FetchBillingDetails extends DataService {
	@Inject
	private GetSubscriberIntegrator subscriberIntegrator;
	
	@Inject
	private JmsSender jmsSender;
	
	public Double getBillingInfo(String msisdn, String transactionId){
		FetchCustomerBillingDetailsPortType  port = null;
		
		try{
			port = subscriberIntegrator.getCustomerBillingPort();
		}catch(Exception e){
			logger.error("Error in getting customer billing port:", e);
			return null;
		}
		
		FetchCustomerBillingDetailsRequestType req = new FetchCustomerBillingDetailsRequestType();
		CommonComponentsType cType = new CommonComponentsType();
		if(msisdn != null){
			cType.setMSISDNNum("234" + msisdn.replaceFirst("0*", "")); //strip the leading zero
		}
		cType.setOpCoID("NG");
		cType.setSenderID(appProps.getProperty("cbi-sender-id", "ESF"));
		cType.setProcessingNumber(transactionId);
		//
		req.setCommonComponents(cType);
		//
		req.setLevelCode(appProps.getProperty("cbi-level-code", "ServiceLevel"));
		req.setExtUser(appProps.getProperty("cbi-sender-id", "esfuser"));
		req.setPrefLang("en");
		
		IdentificationType it = new IdentificationType();
		it.setIdType("TypeCode");
		it.setIdValue(appProps.getProperty("cbi-type-code", "getinvoicedtls"));
		
		IdentificationType it2 = new IdentificationType();
		it2.setIdType("ActionType");
		it2.setIdValue(appProps.getProperty("cbi-action-type", "view"));
		
		req.getIdentification().add(it);
		req.getIdentification().add(it2);
		
		String requestXml = getFetchBillingDetailsMessage(FetchCustomerBillingDetailsRequestType.class, req);
//		logger.debug("requestXml: " + requestXml);
		
		FetchCustomerBillingDetailsResponseType resp = null;
		
		try{
			resp = port.fetchCustomerBillingDetailsOperation(req);
		}catch(Exception ex){
			logger.error("Exception: ", ex);
			return null;
		}
		
		StatusInfoType info = resp.getStatusInfo();
		logger.debug("Code: " + info.getStatusCode() + "; Description: " + info.getStatusDesc()); //0 = success; 1 = failure
		
		String responseXml = getFetchBillingDetailsMessage(FetchCustomerBillingDetailsResponseType.class, resp);
//		logger.debug("responseXml: " + responseXml);
		
		//log request and response
		jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), null, "FETCH_BILLING_DETAILS", msisdn));
//		logger.info("==Queuing agility integration log: " + msg);
		
		if(info.getStatusCode().equalsIgnoreCase("0")){
			for(CustomerPartyBillingProfileType bill : resp.getCustomerPartyBillingProfile()){
				for(IdentificationTypeBilling idBill : bill.getIdentification()){
					if(idBill.getIdType().equalsIgnoreCase(appProps.getProperty("cbi-invoiceid-tag", "TOT_OUTSTANDING_AMT"))){
						logger.debug("Invoice amount retrieved from response: " + idBill.getIdValue());
						try{
							return Double.valueOf(idBill.getIdValue());
						}catch(Exception e){
							logger.error("Error in retrieving invoice amount: ", e);
							return null;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public <T> String getFetchBillingDetailsMessage(Class clazz, T obj){
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