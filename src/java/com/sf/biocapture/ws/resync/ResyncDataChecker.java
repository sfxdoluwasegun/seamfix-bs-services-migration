package com.sf.biocapture.ws.resync;

import java.util.List;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.entity.EnrollmentRef;

public class ResyncDataChecker extends BsClazz implements Runnable{
	
	private Integer index;
	private ResyncDS rsyncDS;
	
	private BioCache cache;
	
	public ResyncDataChecker(Integer index, ResyncDS rsyncDS) {
		this.index = index;
		this.rsyncDS = rsyncDS;
		this.cache = rsyncDS.getCache();
	}

	@Override
	public void run() {
		List<EnrollmentRef> refs = rsyncDS.listEnrollmentRefs(index);
		for (EnrollmentRef ref : refs) {
			logger.debug("Checking resync for: " + ref.getCode());
			List<String> records = rsyncDS.listRecordsWithoutBiometrics(ref.getCode());
			logger.debug("Resync List found for : " + ref.getCode() + " " + records.size());
			ResyncResponse rr = new ResyncResponse();
			rr.setStatus(0);
			rr.setResyncList(records);
			logger.debug("Resync Items cached for: " + ref.getCode() + " " + cache.setItem("RESYNC-" + ref.getCode(), rr, 24 * 60 * 60));
		}
		if(appProps.getInt("resync_ff_test", 0) == 1){
			resyncTestRecords();
		}
	}
	
	private void resyncTestRecords(){
		String list = "FF_TEST_BANANA-1403726090847,FF_TEST_BANANA-1403110917683,FF_TEST_BANANA-1403781486614,FF_TEST_BANANA-1405591695107,FF_TEST_BANANA-1406899114101,FF_TEST_BANANA-1403255117042,FF_TEST_BANANA-1398296673900,FF_TEST_BANANA-1397488386171";
		cache.setItem("RESYNC-FF_TEST_BANANA", list, 30 * 60);
	}

}
