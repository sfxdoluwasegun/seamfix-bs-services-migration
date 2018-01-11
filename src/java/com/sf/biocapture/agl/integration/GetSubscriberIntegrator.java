package com.sf.biocapture.agl.integration;

import com.mtn.esf.xmlns.wsdl.WsdlConstant;
import com.mtn.esf.xmlns.wsdl.fetchcustomerbillingdetails.FetchCustomerBillingDetailsBindingQSService;
import com.mtn.esf.xmlns.wsdl.fetchcustomerbillingdetails.FetchCustomerBillingDetailsPortType;
import com.mtn.esf.xmlns.wsdl.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListBindingQSService;
import com.mtn.esf.xmlns.wsdl.fetchcustomersubscriptionlist.FetchCustomerSubscriptionListPortType;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import com.mtn.esf.xmlns.wsdl.getcustomerprofiledetails.GetCustomerProfileDetailsPortType;
import com.mtn.esf.xmlns.wsdl.getcustomerprofiledetails.GetCustomerProfileDetailsService;
import com.sf.biocapture.app.BsClazz;

/**
 * @author Nnanna
 */
@SuppressWarnings("PMD")
@Singleton
public class GetSubscriberIntegrator extends BsClazz {
	private GetCustomerProfileDetailsService service;
	private FetchCustomerBillingDetailsBindingQSService billingService;
	private FetchCustomerSubscriptionListBindingQSService fetchSubscriberService;

	private FetchCustomerBillingDetailsPortType billingPort;
	private GetCustomerProfileDetailsPortType getSubscriberDetailsPort;
	private FetchCustomerSubscriptionListPortType fetchSubscriberListPort;

	@PostConstruct
	protected void init(){
		initGetSubscriberDetailsService();
		initFetchSubscriptionListService();
		initFetchCustomerBillingService();
	}

	private void initFetchCustomerBillingService(){
		String endPoint = appProps.getProperty("agl-billing-endpoint", WsdlConstant.DEFAULT_FETCH_CUSTOMER_BILLING_URL);
		try {
			logger.debug("Initializing Customer Billing Details service...");
			URL url = new URL(endPoint);
			billingService = new FetchCustomerBillingDetailsBindingQSService(url);
		} catch (MalformedURLException e) {
			logger.error("Error in reading endpoint for fetching customer billing details: ", e);
			billingService = new FetchCustomerBillingDetailsBindingQSService();
		}

		try{
			billingPort = billingService.getFetchCustomerBillingDetailsBindingQSPort();
		}catch(Exception e){
			logger.error("Exception: ", e);
		}
	}

	private void initFetchSubscriptionListService(){
		String endPoint = appProps.getProperty("agl-fetchsubscriptionlist-enpoint", WsdlConstant.DEFAULT_FETCH_SUBSCRIPTION_LIST_URL);
		logger.debug("Customer Subscription list endPoint: " + endPoint);
		try {
			logger.debug("Initializing connection to get subscription list service...");
			URL url = new URL(endPoint);
			fetchSubscriberService = new FetchCustomerSubscriptionListBindingQSService(url);
		} catch (MalformedURLException e) {
			logger.error("Error in reading endpoint for getting customer subscription list: ", e);
			fetchSubscriberService = new FetchCustomerSubscriptionListBindingQSService();
		}
		try {
			fetchSubscriberListPort = fetchSubscriberService.getFetchCustomerSubscriptionListBindingQSPort();//.getFetchCustomerSubscriptionListPort();
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}

	private void initGetSubscriberDetailsService(){
		String endPoint = appProps.getProperty("agl-getsub-endpoint", WsdlConstant.DEFAULT_GET_SUBSCRIBER_DETAILS_URL);
		logger.debug("Customer profile endPoint: " + endPoint);
		try {
			logger.debug("Initializing connection to get subscriber service...");
			URL url = new URL(endPoint);
			service = new GetCustomerProfileDetailsService(url);
		} catch (MalformedURLException e) {
			logger.error("Error in reading endpoint for getting customer details: ", e);
			service = new GetCustomerProfileDetailsService();
		}
		try {
			getSubscriberDetailsPort = service.getGetCustomerProfileDetailsPort();
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}

	public FetchCustomerBillingDetailsPortType getCustomerBillingPort(){
		return billingPort;
	}

	public GetCustomerProfileDetailsPortType getCustomerProfilePort(){
		return getSubscriberDetailsPort;
	}

	public FetchCustomerSubscriptionListPortType getFetchSubscriberListPort() {
		return fetchSubscriberListPort;
	}
}
