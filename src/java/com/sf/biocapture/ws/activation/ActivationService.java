/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.activation;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.ResponseData;
import javax.inject.Inject;

/**
 *
 * @author PC
 */
public class ActivationService extends BsClazz implements IActivationService{

    @Inject
    ActivationDS activationDs;

    @Override
    public ResponseData activation(String uniqueId, String phoneNumber) {
       
           return activationDs.smsActivation(uniqueId, phoneNumber);
    }
      
}
