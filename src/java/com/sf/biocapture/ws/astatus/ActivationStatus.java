package com.sf.biocapture.ws.astatus;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sf.biocapture.app.BsClazz;
import nw.commons.StopWatch;


@Path("/astatus")
public class ActivationStatus extends BsClazz implements IActivationStatus{
    
        private static final String LITE = "lite";
        private static final String SMART_CLIENT = "smartclient";

	@Inject
	private ActivationDS asm;

	@Override
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response postStatus(String msisdn) {
		return getStatus(msisdn);
	}

	@Override
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response getStatus(String msisdn) {
		StopWatch sw = new StopWatch(true);
		String response = "";
		if(msisdn == null){
			response = "";
		}else{
			response = asm.checkStatusOld(msisdn);
		}
		logger.info("Processing activation status: " + msisdn + " [" + sw.elapsedTime() + " ms]");
		return Response.status(200).entity(response).build();
                
	}

	@Override
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response getAgilityStatus(String key, String uid,boolean useMsisdn) {                        
            return Response.status(200).entity( asm.checkStatus(key, uid,useMsisdn) ).build();
	}
         
        
	public void get(@Context HttpHeaders headers){
            headers.getRequestHeader("user-agent");
	}

	@Override
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response setStatus(String msisdn, Long date, String status) {
		asm.setStatus(msisdn, date, status);
		return Response.status(Status.OK).entity("Thanks").build();
	}

    @Override
    @RolesAllowed({SMART_CLIENT, LITE})
    public Response checkStatus(String msisdn) {
        return Response.status(200).entity( asm.checkStatus(msisdn) ).build();
    }

}