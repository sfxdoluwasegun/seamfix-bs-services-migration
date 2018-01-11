package com.sf.biocapture.ws.swap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sf.biocapture.agl.integration.CreateCustomerOrder;
import com.sf.biocapture.agl.integration.GetSubscriberDetails;
import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ws.HeaderIdentifier;
import javax.ws.rs.core.HttpHeaders;
/**
 * 
 * @author Nnanna
 *
 */
@Path("/simswap")
public class SimSwapService extends BsClazz implements ISimSwapService {
    
    private static final String MISSING_MSISDN = "Msisdn is missing";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
    
	@Inject
	private JmsSender jmsSender;
	
	@Inject
    private BioCache cache;
	
	@Inject
	SimSwapHelper helper; 
	
	@Inject
	CreateCustomerOrder simSwapOrder;
	
	@Inject
	SimSwapDs swapDs;
	
	@Inject
	GetSubscriberDetails subDetails;
        
        @Inject 
        IntrusionAnalyzer analyzer;

	@Override
	public Response load(String msisdn) {
		if(msisdn == null){
			return Response.status(Status.OK).entity(new SimSwapResponse("1", MISSING_MSISDN)).build();
		}
		String resp = queueMsisdn(msisdn);
		return Response.status(Status.OK).entity(new SimSwapResponse("0", resp)).build();
	}
	
	@Override
	public Response fetch(String msisdn) {
		if(msisdn == null){
			return Response.status(Status.OK).entity(new SimSwapResponse("1", MISSING_MSISDN)).build();
		}
		SimSwapResponse ssr = cache.getItem("FDN-" + msisdn.trim(), SimSwapResponse.class);
		if(ssr == null){
			queueMsisdn(msisdn); //queue msisdn
			ssr = new SimSwapResponse("1", "Frequently dialed numbers retrieval in progress");
		}
		return Response.status(Status.OK).entity(ssr).build();
	}

	@Override
	public Response lastRecharge(String msisdn) {
		if(msisdn == null){
			return Response.status(Status.OK).entity(new LastRechargeResponse("-1", MISSING_MSISDN, 0)).build();
		}
		LastRechargeResponse lrc = cache.getItem("LRC-" + msisdn.trim(), LastRechargeResponse.class);
		if(lrc == null){
			//call ESF
			try {
				lrc = helper.getLastRecharge(msisdn, msisdn + "-" + sdf.format(new Date()));
			} catch (ParseException e) {
				logger.error("Response from ESF has a date format issue: ", e);
			}
			cache.setItem("LRC-" + msisdn.trim(), lrc, 1800);
		}
		return Response.status(Status.OK).entity(lrc).build();
	}
	
	private String queueMsisdn(String msisdn){
		String resp = jmsSender.queueMsisdn(msisdn);
		logger.debug("Sim swap response for " + msisdn + ": " + resp);
		return resp;
	}

	@Override
	public SimSwapResponse doSwap(HttpHeaders header,SimSwapRequest req) {
                HeaderIdentifier headerIdentifier = analyzer.getIdentifier(header);
		String transactionId = sdf.format(new Date());
		SimSwapResponse resp = simSwapOrder.doSimSwap(req.getMsisdn(), transactionId, req.getOrderNumber(), req.getPuk(), req.getSimSerial(), req.getAgentEmail());
		
		//if resp code is 0, create sim swap log
		//and prep record for owc push
		if(resp != null && resp.getCode().equalsIgnoreCase("0")){
			swapDs.createSimSwapLog(headerIdentifier.getRefDeviceId(),req);
		}
		
		return resp;
	}

}