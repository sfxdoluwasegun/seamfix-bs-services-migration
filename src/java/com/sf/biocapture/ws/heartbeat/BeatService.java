 package com.sf.biocapture.ws.heartbeat;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.HeaderIdentifier;
import javax.ws.rs.core.HttpHeaders;

@Path("/heartbeat")
public class BeatService extends BsClazz implements IBeatService{
    
        private static final String LITE = "lite";
        private static final String SMART_CLIENT = "smartclient";

	@Inject
	private JmsSender jmsSender;

	@Inject
	private BeatDS bds;
        
        @Inject
        private IntrusionAnalyzer analyzer;

	@RolesAllowed({SMART_CLIENT, LITE})
        @Override
	public Response getBeat(String beat) {
		Integer rate = jmsSender.queueBeat(beat);
		logger.debug("HeartBeat: " + beat);
		return Response.status(200).entity(rate).build();
	}

	@RolesAllowed({SMART_CLIENT, LITE})
        @Override
	public Response postBeat(String beat) {
		return getBeat(beat);
	}

	@Override
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response postBeat(HttpHeaders headers, ClientBeat beat) {
		Integer rate = 10;
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);
        
		if(beat.getTag() != null) {
            beat.setRefDeviceId(headerIdentifier.getRefDeviceId());
            beat.setRealTimeDeviceId(headerIdentifier.getRealTimeDeviceId());
			rate = jmsSender.queueBeat(beat);
			logger.debug("HeartBeat from: " + beat.getTag());
		}

		return Response.status(200).entity(rate.toString()).build();
	}

	@GET
	@Path("/ls")
	@Produces(MediaType.APPLICATION_XML)
	@RolesAllowed({SMART_CLIENT, LITE})
	public Response listBeats(){
		return Response.status(javax.ws.rs.core.Response.Status.OK).entity(bds.listBeats()).build();
	}

}
