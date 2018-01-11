package com.sf.biocapture.ws.swap;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
/**
 * 
 * @author Nnanna
 *
 */
@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/SimSwap")})
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MsisdnListener extends BsClazz implements MessageListener {
	@Inject
    private BioCache cache;
	
	@Inject
	SimSwapHelper helper;

	@Override
	public void onMessage(Message message) {
		try{
			ObjectMessage om = (ObjectMessage) message;
			if(om.getObject() instanceof String){
				String msisdn = (String) om.getObject();
				loadFrequentlyDialled(msisdn);
				logger.info("MSISDN " + msisdn + " loaded successfully");
			}
		}catch(JMSException e){
			logger.error("Error in adding msisdn to sim swap queue: ", e);
		}
	}

	private void loadFrequentlyDialled(String msisdn) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		SimSwapResponse ssr = helper.getFrequentlyDialedNumbers(msisdn, msisdn + "-" + sdf.format(new Date()));
		cache.setItem("FDN-" + msisdn.trim(), ssr, 60);
	}

}
