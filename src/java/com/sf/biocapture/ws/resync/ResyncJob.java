package com.sf.biocapture.ws.resync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.app.BsClazz;

@Stateless
public class ResyncJob extends BsClazz{
	
	@Inject
	private ResyncDS rsyncDS;

	public void listResyncRecords() {
		Long totalRefs = rsyncDS.countRefs();
		logger.debug("Total Tags to be listed: " + totalRefs);
		ExecutorService service = Executors.newFixedThreadPool(5);
		for(Long t = 0L; t <= totalRefs; t = t + 300) {
			ResyncDataChecker rdc = new ResyncDataChecker(t.intValue(), rsyncDS);
			service.submit(rdc);
		}
		
	}
}
