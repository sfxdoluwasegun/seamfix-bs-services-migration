package com.sf.biocapture.ws.access.queuelistener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.security.KMUser;
import nw.orm.core.exception.NwormQueryException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author Nnanna
 * @since 17 Jun 2017, 16:34:29
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/LoginQueue")})
public class AccessListener extends BsClazz implements MessageListener {

    @Inject
    AccessDS dataService;

    @Override
    public void onMessage(Message msg) {
        ObjectMessage om = (ObjectMessage) msg;
        try {
            KMUser user = (KMUser) om.getObject();
            //update user
            update(user);
        } catch (JMSException e) {
            logger.error("Exception ", e);
        }
    }

    public boolean update(Object obj) {
        boolean outcome = false;
        Session session = dataService.getDbService().getSessionService().getManagedSession();
        try {
            session.merge(obj);
            dataService.getDbService().getSessionService().commit(session);
            outcome = true;
        } catch (HibernateException e) {
            dataService.getDbService().getSessionService().rollback(session);
            dataService.getDbService().getSessionService().closeSession(session);
            throw new NwormQueryException("", e);
        }
        dataService.getDbService().getSessionService().closeSession(session);
        return outcome;
    }

}
