
package com.sf.biocapture.ws.tradepartners;

import com.sf.biocapture.proxy.TradePartner;
import com.sf.biocapture.app.BsClazz;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("/trade-partners")
public class TradePartnersService  extends BsClazz implements ITradePartnersService
{
    @Inject
    TradePartnersDS tpDS;

    @Override
    public TPStatusResponse getAll() {
        logger.debug("retrieving all trade partners details...");
                               
        TPStatusResponse response = new TPStatusResponse();
        
        List<TradePartner> tps = tpDS.getAllTradePartners();
        
        if( (tps == null) || tps.isEmpty() ){
            response.setMessage("No trade partners found.");
            response.setStatus(0);
            return response;
        }
        
        response.setMessage("All Trade partners successfully retrieved.");       
        response.setStatus(1);
        response.setTradePartners( tps );
        
        return response;
    }

    @Override
    public TPStatusResponse getTradePartnersByState(String stateName) {
        TPStatusResponse response = new TPStatusResponse();
        
        if( (stateName == null) || (stateName.isEmpty())){
            response.setStatus(0);
            response.setMessage("The name of the state is required.");
            return response;
        }
        
        List<TradePartner> tps = tpDS.getTradePartnersByState(stateName.trim());
        
        if( (tps == null) || tps.isEmpty() ){
            response.setMessage("No trade partners found for specified state.");
            response.setStatus(0);
            response.setTradePartners( tps );
            
            return response;
        }
        
        response.setMessage("All Trade partners from the specified state successfully retrieved.");       
        response.setStatus(1);
        response.setTradePartners( tps );
        
        return response;
    }
        
}
