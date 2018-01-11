
package com.sf.biocapture.ws.biodata;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author @wizzyclems
 */
public interface IBioDataService {
    
    public static final String MSISDN = "msisdn";
    
    @POST
    @Path("/details")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse getBioData(@FormParam(MSISDN) String msisdn);
    
    @POST
    @Path("/add-sim")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse addSim(@FormParam(MSISDN) String msisdn);
    
    @POST
    @Path("/re-reg")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse reReg(@Context HttpHeaders headers, @FormParam(MSISDN) String msisdn);
        
    @POST
    @Path("/platinum-number")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse getPlatinumBioData(@FormParam(MSISDN) String msisdn);
    
    @POST
    @Path("/sc")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse getSmeCorporate(@FormParam("serialNumber") String serialNumber);
    
    @POST
    @Path("/msc")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse getMnpSmeCorporate(@FormParam(MSISDN) String msisdn);
    
    @POST
    @Path("/test")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse testBiometricsFetch(@FormParam("uniqueId") String uniqueId);
    
    @POST
    @Path("/swap-details")
    @Produces({MediaType.APPLICATION_JSON})
    public BioDataResponse getSimSwapData(@FormParam(MSISDN) String msisdn);
    
}
