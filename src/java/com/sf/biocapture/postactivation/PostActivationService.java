package com.sf.biocapture.postactivation;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.postactivation.sms.SimNotifier;

@Stateless
public class PostActivationService extends BsClazz{
	
	@Inject
	private TabsDS psm;
	
	@Resource
	private ManagedExecutorService mes;
	
	@Inject
	private KannelSMS ksms;
	
	private String defaultActivationMessage;
	private String activationMessage;
	
	private Integer simNotifyCount = 5;
	private Integer step = 0;
	
	@PostConstruct
	protected void init(){
		defaultActivationMessage = "Dear valued customer, you have been activated on Airtel network, you may now take full advantage of our excellent services. Welcome to Airtel";
		activationMessage = appProps.getProperty("activation-message", defaultActivationMessage);
		simNotifyCount = appProps.getInt("sim-notifier-app-count", 5);
		simNotifyCount = appProps.getInt("sim-notifier-app-count", 5);
	}
	
	public void notifyActivatedSubscribers(){
		Long max = psm.getUnnotifiedCount();
		if(max == null){
			max = 0L;
		}
		logger.info("Bulk collection size for unsent sms: " + max);
		Long key = 0L;
		for(Long z = 0L; z <= max; z = z + appProps.getInt("unactivated.page", 500)){
			if(step == 0) {
				key = 0L;
			}
			key = key + appProps.getInt("unactivated.page", 500);
			SimNotifier sn = new SimNotifier(key, ksms, psm, activationMessage);
			mes.submit(sn);
			step = step + 1;
			if(step == simNotifyCount) {
				step = 0; // reset step
			}
		}
	}

}
