package com.sf.biocapture.agl.integration;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import com.mtn.esf.xmlns.wsdl.queryitem.QueryItemPortType;
import com.mtn.esf.xmlns.wsdl.queryitem.QueryItemService;
import com.sf.biocapture.app.BsClazz;

/**
 * @author Nnanna
 */

@Singleton
public class QueryItemIntegrator extends BsClazz  {
	private QueryItemPortType queryItemPort;
	private QueryItemService service;
	
	@PostConstruct
	protected void init(){
		initQueryItemService();
	}
	
        @SuppressWarnings("PMD")
	private void initQueryItemService(){
                // production url : http://10.184.19.80:8011/MTNN-OSB-Project/RequesterABCS/QueryItem/QueryItemESFReqABCSPSv1.1.0?wsdl
                String productionUrl = "http://10.184.19.80:8011/MTNN-OSB-Project/RequesterABCS/QueryItem/QueryItemESFReqABCSPSv1.1.0?wsdl";

                // POC url : http://10.184.0.103:8011/MTNN-OSB-Project/RequesterABCS/QueryItem/QueryItemESFReqABCSPSv1.1.0?wsdl
                String testUrl = "http://10.184.0.103:8011/MTNN-OSB-Project/RequesterABCS/QueryItem/QueryItemESFReqABCSPSv1.1.0?wsdl";
                
		String endPoint = appProps.getProperty("agl-query-endpoint", productionUrl);
		logger.debug("Query item endPoint: " + endPoint);
		try {
			logger.debug("Initializing connection to query item service...");
			URL url = new URL(endPoint);
			service = new QueryItemService(url);
		} catch (Exception e) {
			logger.error("Error in reading endpoint for querying agl for sim serial: ", e);
			service = new QueryItemService();
		}
		try{
			queryItemPort = service.getQueryItemPort();
		} catch (Exception e) {
			logger.error("Exception:", e);
		}
	}
	
	public QueryItemPortType getQueryItemPort(){
		return queryItemPort;
	}
	
	public QueryItemService getService(){
		return service;
	}
}