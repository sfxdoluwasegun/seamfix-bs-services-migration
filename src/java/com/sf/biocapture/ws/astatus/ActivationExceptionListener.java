/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.astatus;

import com.sf.biocapture.app.BsClazz;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 *
 * @author PC
 * @since Jun 21, 2017 - 11:17:52 AM
 */
public class ActivationExceptionListener extends BsClazz implements ExceptionListener {
    
    @Override
    public void onException(JMSException jmse) {
        logger.error("Activation Queue encountered an issue", jmse);
    }
    
}
