/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.access.queuelistener;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.audit.AccessLog;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Marcel
 * @since Jun 21, 2017 - 6:56:15 PM
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/AccessLogQueue")})
public class AccessLogListener extends BsClazz implements MessageListener {

    @Inject
    AccessDS dataService;

    @Override
    public void onMessage(Message msg) {
        ObjectMessage om = (ObjectMessage) msg;
        try {
            AccessLog accessLog = (AccessLog) om.getObject();
            dataService.getDbService().create(accessLog);
        } catch (JMSException e) {
            logger.error("Exception ", e);
        }
    }

}
