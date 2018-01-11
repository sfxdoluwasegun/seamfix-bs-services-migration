package com.sf.biocapture.ws.swap;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mtn.esf.xmlns.wsdl.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListPortType;
import com.mtn.esf.xmlns.wsdl.queryitem.QueryItemPortType;
import com.mtn.esf.xmlns.xsd.common.CommonComponentsType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.CustomerDetailsType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListRequestType;
import com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListResponseType;
import com.mtn.esf.xmlns.xsd.queryitem.AdditionalItemInfoType;
import com.mtn.esf.xmlns.xsd.queryitem.AdditionalStatusType;
import com.mtn.esf.xmlns.xsd.queryitem.IdentificationType;
import com.mtn.esf.xmlns.xsd.queryitem.InventoryInformationType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryCriteriaType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryItemRequestType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryItemResponseType;
import com.mtn.esf.xmlns.xsd.queryitem.StatusInfoType;
import com.sf.biocapture.agl.integration.GetSubscriberIntegrator;
import com.sf.biocapture.agl.integration.QueryItemIntegrator;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.DataService;
/**
 * 
 * @author Nnanna
 *
 */
@SuppressWarnings("PMD")
@Stateless
public class SimSwapHelper extends DataService {

	private static final String _234 = "234";
	@Inject
	private QueryItemIntegrator queryIntegrator;

	@Inject
	private GetSubscriberIntegrator subscriberIntegrator;

	@Inject
	private JmsSender jmsSender;

	@SuppressWarnings("PMD")
	public LastRechargeResponse getLastRecharge(String msisdn, String transactionId) throws ParseException{
		logger.debug("Getting last recharge amount for " + msisdn);

		QueryItemRequestType qType = new QueryItemRequestType();
		CommonComponentsType cType = new CommonComponentsType();
		cType.setOpCoID("NG"); //country code
		cType.setSenderID(appProps.getProperty("sender-id", "CRM"));
		cType.setProcessingNumber(transactionId);
		//
		qType.setCommonComponents(cType);
		qType.setQueryType(appProps.getProperty("query-type", "LoadedCardDetails"));

		//msisdn
		if(msisdn != null){
			cType.setMSISDNNum(_234 + msisdn.replaceFirst("0*", ""));
		}

		IdentificationType idType = null;
		if(msisdn != null){ 
			idType = new IdentificationType();
			idType.setIdType("ServiceID");
			idType.setIdValue(_234 + msisdn.replaceFirst("0*", ""));
		}

		QueryCriteriaType qct = new QueryCriteriaType();
		qct.getIdentification().add(idType);

		qType.getQueryCriteria().add(qct);

		QueryItemPortType port = null;
		try{
			port = queryIntegrator.getQueryItemPort();
		}catch(Exception ex){
			logger.error("", ex);
			return handleFailureResponse();
		}

		String requestXml = null;
		String responseXml = null;

		//TODO get request string here
		requestXml = getSimSwapMessage(QueryItemRequestType.class, qType);
		//		logger.debug("requestXml: " + requestXml);

		QueryItemResponseType resp = null;
		try{
			resp = port.queryItem(qType);
		}catch(Exception ex){
			logger.error("", ex);
			return handleFailureResponse();
		}

		StatusInfoType info = resp.getStatusInfo();
		logger.debug("Status Code: " + info.getStatusCode()); //0 = success; 1 = failure
		logger.debug("Status Description: " + info.getStatusDesc());

		responseXml = getSimSwapMessage(QueryItemResponseType.class, resp);
		//		logger.debug("responseXml: " + responseXml);

		//log request and response
		jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), null, "QUERY_LAST_RECHARGE", msisdn));
//		logger.info("==Queuing agility integration log: " + msg);

		LastRechargeResponse rcResp = new LastRechargeResponse();
		if(info.getStatusCode().equalsIgnoreCase("0")){
			//additional status info
			for(AdditionalStatusType ast : resp.getStatusInfo().getAdditionalStatus()){
				logger.debug("Additional Code: " + ast.getStatusCode());
				logger.debug("Additional Description: " + ast.getStatusDesc());
				rcResp.setCode(ast.getStatusCode());
				rcResp.setMessage(ast.getStatusDesc());
				break;
			}

			List<InventoryInformationType> iTypes = resp.getInventoryInformation();
			Date lrcDate = null;
			String cost = "0";
			String df = appProps.getProperty("lrc-date-format", "yyyy-MM-dd'T'HH:mm:ss.SSS");
			for(InventoryInformationType iit : iTypes){
				//set recharge amount
				for(AdditionalItemInfoType addInfo : iit.getAdditionalInfo()){
					if(addInfo.getName().equalsIgnoreCase("DateRecharge")){
						if(addInfo.getValue() != null){
							if(lrcDate == null){
								lrcDate = new SimpleDateFormat(df).parse(addInfo.getValue());
								cost = String.valueOf(iit.getCost());
							}else{
								//compare the date with other dates to get the latest
								if(new SimpleDateFormat(df).parse(addInfo.getValue()).after(lrcDate)){
									//a more recent recharge
									lrcDate = new SimpleDateFormat(df).parse(addInfo.getValue());
									cost = String.valueOf(iit.getCost());
								}
							}
						}
					}
				}
			}

			rcResp.setAmount(Double.valueOf(cost));
			rcResp.setLastRechargeDate(new SimpleDateFormat("EEE, d MMM yyyy HH:mm").format(lrcDate));
		}else{
			//additional status info
			List<AdditionalStatusType> aTypes = resp.getStatusInfo().getAdditionalStatus();
			for(AdditionalStatusType ast : aTypes){
				rcResp.setCode(ast.getStatusCode());
				rcResp.setMessage(ast.getStatusDesc());
				break;
			}
		}

		return rcResp;
	}

	public static void main(String[] args) {
		String val = "2016-03-14T17:25:32.000+01:00";
		try {
			System.out.println(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(val));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private LastRechargeResponse handleFailureResponse(){
		LastRechargeResponse resp = new LastRechargeResponse();
		resp.setCode("-2");
		resp.setMessage("Unable to connect to remote service");
		return resp;
	}

	private SimSwapResponse getFreqDialedFailureResponse(){
		return new SimSwapResponse("-2", "Unable to connect to remote service");
	}

	public SimSwapResponse getFrequentlyDialedNumbers(String msisdn, String transactionId){
		logger.debug("Getting frequently dialed numbers for " + msisdn);

		FetchCustomerSubscriptionListPortType port = null;

		port = subscriberIntegrator.getFetchSubscriberListPort();

		FetchCustomerSubscriptionListRequestType req = new FetchCustomerSubscriptionListRequestType();
		CommonComponentsType cType = new CommonComponentsType();
		if(msisdn != null){
			cType.setMSISDNNum( _234 + msisdn.replaceFirst("0*", "") ); //strip the leading zero
		}
		cType.setOpCoID("NG");
		cType.setSenderID(appProps.getProperty("fdn-sender-id", "AGL"));
		cType.setProcessingNumber(transactionId);
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
		req.setQueryType(appProps.getProperty("query-type-freq-dialed", "GetCallEventUsage")); 
		req.setPrefLang("en");

		if(msisdn != null){
			com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.IdentificationType serviceIdType = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.IdentificationType();
			serviceIdType.setIdType("ServiceID");
			serviceIdType.setIdValue( _234 + msisdn.replaceFirst("0*", "") );
			req.getIdentification().add(serviceIdType);
		}

		//to date
		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType dateQc = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType();
		dateQc.setName("ToDate");
		dateQc.setValue(new SimpleDateFormat(appProps.getProperty("fdn-date-format", "dd-MM-yyyy")).format(new Date()));
		req.getQueryCriteria().add(dateQc); 

		//frequency of dialed numbers
		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType freqQc = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType();
		freqQc.setName("FrequencyofRecentlyDialedNumbers");
		freqQc.setValue(appProps.getProperty("fdn-frequency", "3"));
		req.getQueryCriteria().add(freqQc);

		//page number
		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType pageNoQc = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType();
		pageNoQc.setName("PageNumber");
		pageNoQc.setValue(appProps.getProperty("fdn-page-number", "1"));
		req.getQueryCriteria().add(pageNoQc);

		//page size
		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType pageSizeQc = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType();
		pageSizeQc.setName("PageSize");
		pageSizeQc.setValue(appProps.getProperty("fdn-page-size", "100"));
		req.getQueryCriteria().add(pageSizeQc);

		//call event usage type
		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType usageQc = new com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.QueryCriteriaType();
		usageQc.setName("CallEventUsageType");
		usageQc.setValue(appProps.getProperty("query-crit-usage-type", "ALL"));
		req.getQueryCriteria().add(usageQc);

		//
		String requestXml = getSimSwapMessage(FetchCustomerSubscriptionListRequestType.class, req);
		//		logger.debug("requestXml: " + requestXml);

		FetchCustomerSubscriptionListResponseType resp = null;
		try{
			resp = port.fetchCustomerSubscriptionList(req);
		}catch(Exception ex){
			logger.error("", ex);
			return getFreqDialedFailureResponse();
		}

		com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.StatusInfoType info = resp.getStatusInfo();
//		logger.debug("Code: " + info.getStatusCode());
//		logger.debug("Description: " + info.getStatusDesc());

		String responseXml = getSimSwapMessage(FetchCustomerSubscriptionListResponseType.class, resp);
		//		logger.debug("responseXml: " + responseXml);

		//log request and response
		jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), null, "FETCH_SUBSCRIPTION_LIST - FREQUENTLY_DIALED_NUMBERS", msisdn));
//		logger.info("==Queuing agility integration log: " + msg);
		
		//construct response
		SimSwapResponse ssr = new SimSwapResponse(info.getStatusCode(), info.getStatusDesc());
		ssr.setMsisdn(msisdn);

		//retrieve frequently dialed numbers
		if(info.getStatusCode() != null && info.getStatusCode().equalsIgnoreCase("0")){
			List<CustomerDetailsType> details = resp.getCustomerDetails();
			if(details != null && !details.isEmpty()){
				ArrayList<String> dialedNumbers = new ArrayList<String>();
				for(CustomerDetailsType cdt : details){
					for(com.mtn.esf.xmlns.xsd.fetchcustomersubscriptionlist.IdentificationType id : cdt.getIdentification()){
						if(id.getIdType().equalsIgnoreCase("TerminatingNumber")){
							//							logger.info("Frequently dialed number found: " + id.getIdValue());
							dialedNumbers.add("0" + id.getIdValue().replaceFirst(_234, ""));
						}
					}
				}

				//add the dialed numbers to the response
				ssr.setDialedNumbers(dialedNumbers);
			}
		}

		return ssr;
	}

	@SuppressWarnings("rawtypes")
	public <T> String getSimSwapMessage(Class clazz, T obj){
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
