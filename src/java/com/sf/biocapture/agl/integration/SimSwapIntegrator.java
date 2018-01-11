package com.sf.biocapture.agl.integration;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import com.mtn.esf.xmlns.wsdl.WsdlConstant;
import com.mtn.esf.xmlns.wsdl.createcustomerorder.CreateCustomerOrderBindingQSService;
import com.mtn.esf.xmlns.wsdl.createcustomerorder.CreateCustomerOrderPortType;
import com.sf.biocapture.app.BsClazz;

/**
 * 
 * @author Nnanna
 * @since 27/10/2016
 */
@SuppressWarnings("PMD")
@Singleton
public class SimSwapIntegrator extends BsClazz {
	private CreateCustomerOrderBindingQSService customerOrderService;
	private CreateCustomerOrderPortType customerOrderPort;
	
	@PostConstruct
	protected void init(){
		logger.error("Initializing create customer order...");
		initCreateCustomerOrder();
	}
	
	private void initCreateCustomerOrder(){
		String endPoint = appProps.getProperty("agl-customer-order-endpoint", WsdlConstant.DEFAULT_CREATE_CUSTOMER_ORDER_URL);
		try {
			logger.debug("Initializing customer order service...endPoint: " + endPoint);
			URL url = new URL(endPoint);
			customerOrderService = new CreateCustomerOrderBindingQSService(url);
		} catch (MalformedURLException e) {
			logger.error("Error in reading endpoint for customer order:", e);
			customerOrderService = new CreateCustomerOrderBindingQSService();
		}

		try{
			customerOrderPort = customerOrderService.getCreateCustomerOrderBindingQSPort();
		}catch(Exception e){
			logger.error("Exception: ", e);
		}
	}

	public CreateCustomerOrderPortType getCustomerOrderPort() {
		return customerOrderPort;
	}

}