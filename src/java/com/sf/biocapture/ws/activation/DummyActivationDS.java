/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.activation;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.SmsActivationRequest;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import java.sql.Timestamp;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Clement
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DummyActivationDS  extends DataService{
    
     public ResponseData  smsActivation(String usecase, String uniqueId, String subscriberInfo){
            
            ResponseData  resp = new ResponseData ();
            SmsActivationRequest req = null;
            if(StringUtils.isEmpty(usecase) || !useCaseExists(usecase)){
                       resp.setCode(ResponseCodeEnum.ERROR);
                       resp.setDescription("The usecase specified is not correct");
                    return resp ;
            }
            
            try{
                    if(usecase.equals(UsecaseEnum.NM.name())){
                          req = dbService.getByCriteria(SmsActivationRequest.class, Restrictions.eq("phoneNumber", subscriberInfo),
                                                         Restrictions.eq("uniqueId", uniqueId));
                    }
                    if(usecase.equals(UsecaseEnum.NS.name())){
                          req = dbService.getByCriteria(SmsActivationRequest.class, Restrictions.eq("serialNumber", subscriberInfo),
                                                         Restrictions.eq("uniqueId", uniqueId));
                    }
                 
                 
                    if(req != null){
                           if(req.getConfirmationStatus() == null){
                               req.setConfirmationStatus(Boolean.TRUE);
                               req.setActivationTimestamp(new Timestamp(new Date().getTime()));
                               req.setMsisdnUpdateTimestamp(new Timestamp(new Date().getTime()));
                               boolean success = dbService.update(req);
                               logger.debug("SmsActivationStatus update successful - " ,success);
                               resp.setCode(ResponseCodeEnum.SUCCESS);
                               resp.setDescription("Activation was Successful");

                           }
                           else{
                               resp.setCode(ResponseCodeEnum.SUCCESS);
                               resp.setDescription("Msisdn has been activated previously");
                           }

                    }
                 
            }catch(HibernateException e){
                logger.error("SmsActivationRequest was not successful",e);
                resp.setCode(ResponseCodeEnum.ERROR);
                resp.setDescription("SmsActivationRequest was not successful");
                return resp;
            }
            
            
            return resp;
     }
     
     public boolean useCaseExists(String usecase){    
        for (UsecaseEnum u : UsecaseEnum.values()) {
            if (u.name().equals(usecase)) {
                return true;
            }
        }
        return false;
    }
}
