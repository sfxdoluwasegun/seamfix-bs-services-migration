package com.sf.biocapture.postactivation.sms;

import java.util.ArrayList;
import java.util.List;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.postactivation.TabsDS;
import java.io.IOException;

public class SimNotifier extends BsClazz implements Runnable{
	
	private Long index = 0L;
	
	private KannelSMS ksms;
	private TabsDS psm;
	
	private String activationMessage;
	
	public SimNotifier(Long index, KannelSMS ksms, TabsDS psm, String activationMessage) {
		this.index = index;
		this.ksms = ksms;
		this.psm = psm;
		this.activationMessage = activationMessage;
		logger.info("Initializing sim notifier : " + index);
	}

	@Override
	public void run() {
		List<SmsReq> sars = psm.listUnnotifiedRecords(index.intValue());
		if(sars != null && !sars.isEmpty()){
			List<Long> pnss = new ArrayList<Long>();
			for (SmsReq sar : sars) {
                            try {
                                if(ksms.sendSMS(sar.getPhoneNumber(), activationMessage)){
                                    pnss.add(sar.getId());
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
			}
			if(!pnss.isEmpty()){
				int updated = psm.getDbService().executeHQLUpdate("Update PhoneNumberStatus p set p.status = 'ACTIVATED' WHERE p.id in"
						+ pnss.toString().replace("[", "(").replace("]", ")"));
				logger.info("Total records updated: " + updated + " Total expected: " + pnss.size());
			}
		}
	}

}
