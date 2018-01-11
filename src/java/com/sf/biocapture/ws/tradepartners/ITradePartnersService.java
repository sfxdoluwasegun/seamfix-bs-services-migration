
package com.sf.biocapture.ws.tradepartners;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author @wizzyclems
 */
public interface ITradePartnersService {
    @POST
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    public TPStatusResponse getAll();
    
    @POST
    @Path("/by-state")
    @Produces({MediaType.APPLICATION_JSON})
    public TPStatusResponse getTradePartnersByState(@FormParam("stateName") String stateName);
    
}
