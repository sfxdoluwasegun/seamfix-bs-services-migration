package com.sf.biocapture.agl.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.mtn.esf.xmlns.wsdl.queryitem.QueryItemPortType;
import com.mtn.esf.xmlns.xsd.common.CommonComponentsType;
import com.mtn.esf.xmlns.xsd.queryitem.AdditionalStatusType;
import com.mtn.esf.xmlns.xsd.queryitem.IdentificationType;
import com.mtn.esf.xmlns.xsd.queryitem.IdentificationType2;
import com.mtn.esf.xmlns.xsd.queryitem.InventoryInformationType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryCriteriaType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryItemRequestType;
import com.mtn.esf.xmlns.xsd.queryitem.QueryItemResponseType;
import com.mtn.esf.xmlns.xsd.queryitem.StatusInfoType;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.enums.SettingsEnum;

@Stateless
public class QuerySimSerial extends DataService {

	@Inject
	private QueryItemIntegrator queryIntegrator;

	@Inject
	AccessDS accessDS;

	@Inject
	private JmsSender jmsSender;

	/**
	 * checks the validity of a sim serial
	 * @param simSerial, sim serial to be validated
	 * @param transactionId, unique reference to the call
	 * @return
	 */
	@SuppressWarnings("PMD")
	public AgilityResponse validateSIMSerial(String simSerial, String transactionId, String puk){
		logger.info("SIM SERIAL: " + simSerial + "; TXN ID: " + transactionId + "; PUK: " + puk);

		AgilityResponse ar = null;
		QueryItemRequestType qType = new QueryItemRequestType();
		CommonComponentsType cType = new CommonComponentsType();
		cType.setOpCoID("NG"); //country code
		cType.setSenderID(appProps.getProperty("sender-id", "ESF")); //ESF
		cType.setProcessingNumber(transactionId); //transaction id
		//
		qType.setCommonComponents(cType);
		qType.setQueryType(appProps.getProperty("query-type", "SimDetails"));

		//user
		IdentificationType idType = new IdentificationType();
		idType.setIdType("ExtUser");
		idType.setIdValue(appProps.getProperty("ext-user", "ESF"));

		//sim serial
		IdentificationType idType2 = new IdentificationType();
		idType2.setIdType("SIMNum");
		idType2.setIdValue(simSerial);

		QueryCriteriaType qct = new QueryCriteriaType();
		qct.getIdentification().add(idType);
		qct.getIdentification().add(idType2);

		qType.getQueryCriteria().add(qct);
		logger.debug("=================INITIALIZING PORT==================");
		QueryItemPortType port = null;
		port = queryIntegrator.getQueryItemPort();

		String requestXml = null;
		String responseXml = null;

		//TODO get request string here
		requestXml = getQuerySimSerialMessage(QueryItemRequestType.class, qType);
		//		logger.debug("requestXml: " + requestXml);

		QueryItemResponseType resp = null;
		try{
			resp = port.queryItem(qType);
		}catch(Exception ex){
			logger.error("Exception: ", ex);
			return getFailureResponse();
		}

		StatusInfoType info = resp.getStatusInfo();
		logger.debug("Status Code: " + info.getStatusCode()); //0 = success; 1 = failure
		logger.debug("Status Description: " + info.getStatusDesc());

		responseXml = getQuerySimSerialMessage(QueryItemResponseType.class, resp);
		//		logger.debug("responseXml: " + responseXml);

		//log request and response
		jmsSender.queueIntegrationLog(logActivity(requestXml, responseXml, info.getStatusCode(), info.getStatusDesc(), simSerial, "QUERY_SIM_SERIAL", null));
//		logger.info("==Queuing agility integration log: " + msg);

		ar = new AgilityResponse();
		ar.setBioData(null);
		if(info.getStatusCode().equalsIgnoreCase("0")){
			//additional status info
			List<AdditionalStatusType> aTypes = resp.getStatusInfo().getAdditionalStatus();
			for(AdditionalStatusType ast : aTypes){
				logger.debug("Additional Code: " + ast.getStatusCode());
				logger.debug("Additional Description: " + ast.getStatusDesc());
				ar.setCode(ast.getStatusCode());
				ar.setDescription(ast.getStatusDesc());
				break;
			}
			List<InventoryInformationType> iTypes = resp.getInventoryInformation();
			for(InventoryInformationType iit : iTypes){
				ar.setValid(validateSimSerialKey(iit.getStatus())); //Free | Active | Blocked | Reserved
				List<IdentificationType2> idts = iit.getIdentification();
				for(IdentificationType2 it : idts){
					if(it.getIdType().equalsIgnoreCase("PUKNumber")){
						ar.setPuk(it.getIdValue());
						if(ar.getPuk() != null && puk != null && !puk.trim().isEmpty() && !ar.getPuk().trim().equalsIgnoreCase(puk.trim())){
							logger.debug("PUK does not match!");
							ar.setCode("1");
							ar.setDescription("PUK [" + puk + "] is invalid");
							ar.setValid(false);
						}
					}
				}
			}
		}else{
			ar.setCode(info.getStatusCode());
			ar.setDescription(info.getStatusDesc());
			ar.setValid(false);
		}

		return ar;
	}

	private boolean validateSimSerialKey (String status) {
		if (status != null) {
			String validKeys = accessDS.getSettingValue(SettingsEnum.AGILITY_SIM_SERIAL_VALID_STATUS_KEYS);
			String keys [] = validKeys.split(",");
			for (String key : keys) {
				if(key.equalsIgnoreCase(status)) {
					return true;
				}
			}
		}

		return false;
	}

	//TODO discard this method after confirming how the client handles the response code
	@Override
	public AgilityResponse getFailureResponse(){
		AgilityResponse ar = new AgilityResponse();
		ar.setCode("1");
		ar.setDescription("Unable to connect to remote service");
		ar.setValid(null);
		return ar;
	}

	@SuppressWarnings("rawtypes")
	public <T> String getQuerySimSerialMessage(Class clazz, T obj){
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