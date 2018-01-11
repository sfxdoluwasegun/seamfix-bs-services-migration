
package com.sf.biocapture.ws.tradepartners;

import com.sf.biocapture.StatusResponse;
import com.sf.biocapture.proxy.TradePartner;
import java.util.ArrayList;
import java.util.List;

public class TPStatusResponse extends StatusResponse{
    
	private List<TradePartner> tradePartners = new ArrayList<>();
        
        public TPStatusResponse(){            
        }
        
        public void setTradePartners(List<TradePartner> tradePartners) {
            this.tradePartners = tradePartners;
        }
        
        public List<TradePartner> getTradePartners() {
            return tradePartners;
        }
}