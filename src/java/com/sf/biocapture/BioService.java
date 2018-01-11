package com.sf.biocapture;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.ds.BioDS;
import com.sf.biocapture.entity.enums.KycManagerRole;
import com.sf.biocapture.entity.security.KMRole;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.postactivation.PostActivationService;
import com.sf.biocapture.ws.vtu.VtuDS;

import nw.orm.core.exception.NwormQueryException;

@Singleton
@Startup
@DependsOn("StartupListener")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BioService extends BsClazz{

	@Inject
	protected KannelSMS ksms;

	@Inject
	protected PostActivationService pas;

	@Inject
	protected BioDS ds;
	
	@Inject
	protected VtuDS vtuDs;

	@Inject
	protected BioCache cache;

	@Resource
	protected ManagedScheduledExecutorService mses;

	@PostConstruct
	protected void init(){
		logger.info("Initializing Bio Services");
		//		ksms.test();
		defaultSupport();
		vtuDs.loadBundleTypes();
		//		banUtility();
	}

	@PreDestroy
	protected void cleanUp(){
		mses.shutdown();
	}

	private void defaultSupport(){
		// support@seamfix.com
		try {
			KMUser user = ds.getUser("support@seamfix.com");
			if(user == null){
				KMRole role = ds.getKmUserRole(KycManagerRole.SUPPORT.name());
				if(role == null){
					role = new KMRole();
					role.setRole(KycManagerRole.SUPPORT.name());
					ds.save(role);
				}
				user = new KMUser();
				user.setEmailAddress("support@seamfix.com");
				user.setPassword("*peters#");
				user.setMobile("08149573029");
				user.setOrbitaId(-1L);
				user.addRole(role);
				ds.save(user);
			}
		} catch (NwormQueryException e) {
			logger.error("", e);
		}
	}

	//	@Asynchronous
	//	protected void banUtility(){
	//		BanRemover br = new BanRemover(ds);
	//		mses.scheduleAtFixedRate(br, 0, 10, TimeUnit.MINUTES);
	//	}
}
