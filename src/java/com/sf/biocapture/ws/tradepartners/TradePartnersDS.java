
package com.sf.biocapture.ws.tradepartners;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.KycDealer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

/**
 *
 * @author @wizzyclems
 */
@Stateless
public class TradePartnersDS extends DataService {
    
    public List<com.sf.biocapture.proxy.TradePartner> getTradePartnersByState(String stateName){
        if( (stateName == null) || stateName.isEmpty() ){
            return new ArrayList<>();
        }
             
        String hql = "Select t from KycDealer t, in(t.states) s where t.active is true and upper(s.name) = upper(:stateName)";
        
        Map<String,Object> params = new HashMap<>();
        params.put("stateName", stateName.trim());
        
        List<KycDealer> tps = getDbService().getListByHQL(hql, params, KycDealer.class);
        
        return process(tps);
    }
    
    private List<com.sf.biocapture.proxy.TradePartner> process(List<KycDealer> tps){
        if( (tps == null) || tps.isEmpty() ){
            return new ArrayList<>();
        }
        
        List<com.sf.biocapture.proxy.TradePartner> tPartners = new ArrayList<>();
        tps.stream().forEach((tp) -> {
            com.sf.biocapture.proxy.TradePartner t = new com.sf.biocapture.proxy.TradePartner();
            t.setTpCode(tp.getDealCode());
            t.setTpName(tp.getName());
            
            tPartners.add(t);
        });
    
        return tPartners;
    }
    
    public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
	}
    
    public List<com.sf.biocapture.proxy.TradePartner> getAllTradePartners(){
        String hql = "Select k from KycDealer k where k.active is true";        
        List<KycDealer> tps = getDbService().getListByHQL(KycDealer.class, hql);
        
        return process(tps);
    } 
    
}