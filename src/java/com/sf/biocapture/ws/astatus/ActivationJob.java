/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.astatus;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.ds.AccessDS;
import java.util.Hashtable;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import weblogic.jms.common.JMSConstants;
import weblogic.jms.extensions.WLConnection;

/**
 *
 * @author Marcel
 * @since Jun 2, 2017
 */
@Singleton
@Startup
@DependsOn("StartupListener")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ActivationJob extends BsClazz {

    @Inject
    private ActivationDS activationDS;

    @Inject
    private AccessDS accessDS;

    @Inject
    private JmsSender jmsSender;

    private final String lookupFactoryClassName = "weblogic.jndi.WLInitialContextFactory";
    private String connectionFactoryJndiName;
    private String queueJndiName;
    private String brokerUrl;
    private String jmsUser;
    private String jmsPassword;

    @PostConstruct
    @SuppressWarnings("PMD") //needed to catch exception because a third party api
    public void initJob() {
        try {
            logger.debug("About to start activation job");
            connectionFactoryJndiName = appProps.getProperty("jms-cf-jndi", "MTN_ConnFactoryNG");
            queueJndiName = appProps.getProperty("jms-queue-jndi", "SIMActivationStatus");
            brokerUrl = "t3://" + appProps.getProperty("jms-provider-url", "10.184.0.158:8011");
            jmsUser = appProps.getProperty("jms-user", "seamfix");
            jmsPassword = appProps.getProperty("jms-password", "sfix#2017");
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, lookupFactoryClassName);
            env.put(Context.PROVIDER_URL, brokerUrl);
            InitialContext ic = new InitialContext(env);
            QueueConnectionFactory qcf = (QueueConnectionFactory) ic.lookup(connectionFactoryJndiName);
            QueueConnection qc = qcf.createQueueConnection(jmsUser, jmsPassword);
            WLConnection wlc = (WLConnection) qc;
            wlc.setReconnectPolicy(JMSConstants.RECONNECT_POLICY_ALL);
            wlc.setReconnectBlockingMillis(appProps.getLong("jms-connection-retrial-interval", 60000L));
            wlc.setTotalReconnectPeriodMillis(-1);
            qc.setExceptionListener(new ActivationExceptionListener());
            QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) ic.lookup(queueJndiName);
            MessageConsumer messageConsumer = qs.createConsumer(queue);
            messageConsumer.setMessageListener(new ActivationMessageListener(activationDS, accessDS, jmsSender));
            qc.start();
            logger.debug("Activation job started");
        } catch (NamingException ex) {
            logger.error("", ex);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }
}
