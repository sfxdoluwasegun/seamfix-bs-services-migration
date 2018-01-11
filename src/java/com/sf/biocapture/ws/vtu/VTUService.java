package com.sf.biocapture.ws.vtu;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.sf.biocapture.app.BsClazz;

/**
 * 
 * @author Nnanna
 * @since 8 Nov 2017, 13:58:20
 */
@Path("/vtu")
public class VTUService extends BsClazz implements IVTUService {
	
	@Inject
	VtuDS vtuDS;

	@Override
	public Response validateVTUNumber(String vtuNumber, String agentEmail) {
		VTUResponse resp = vtuDS.validateVTUNumber(vtuNumber, agentEmail);
		return Response.ok(resp).build();
	}

	@Override
	public Response doVending(VTUVendingRequest request) {
		VTUResponse resp = vtuDS.doVending(request);
		return Response.ok(resp).build();
	}
        
        @Override
	public Response doRequery(VTUVendingRequest request) {
		VTUResponse resp = vtuDS.getVendingStatus(request);
		return Response.ok(resp).build();
	}
}
