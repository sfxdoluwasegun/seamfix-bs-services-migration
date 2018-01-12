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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Clement
 */

@Stateless
public class ActivationDS  extends DataService{
    
     public ResponseData  smsActivation(String uniqueId, String phoneNumber){
            
            ResponseData  resp = new ResponseData ();
            SmsActivationRequest req = dbService.getByCriteria(SmsActivationRequest.class, Restrictions.eq("phoneNumber", phoneNumber),
                                                                                               Restrictions.eq("uniqueId", uniqueId));
            if(req != null){
                if(req.getConfirmationStatus() == null){
                    req.setConfirmationStatus(Boolean.TRUE);
                }
                req.setActivationTimestamp(new Timestamp(new Date().getTime()));
                req.setMsisdnUpdateTimestamp(new Timestamp(new Date().getTime()));
                boolean success = dbService.update(req);
                logger.debug("SmsActivationStatus update successful -" + success);
                resp.setCode(ResponseCodeEnum.SUCCESS);
                resp.setDescription("Activation was Successful");
            }
            else{
                resp.setCode(ResponseCodeEnum.ERROR);
                resp.setDescription("Activation was not successful, uniqueId and Phone number do not exist");
            }
            return resp;
     }
}
