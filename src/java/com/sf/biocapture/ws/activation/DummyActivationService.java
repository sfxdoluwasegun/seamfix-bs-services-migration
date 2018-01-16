/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.activation;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.ResponseData;
import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 *
 * @author Clement
 */
@Path("/activation")
public class DummyActivationService extends BsClazz implements IDummyActivationService{

    @Inject
    DummyActivationDS dummyActivationDs;

    @Override
    public ResponseData activation(String usecase, String uniqueId, String subscriberInfo) {
       
           return dummyActivationDs.smsActivation(usecase, uniqueId, subscriberInfo);
    }
      
}
