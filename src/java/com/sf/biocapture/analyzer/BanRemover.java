package com.sf.biocapture.analyzer;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.BioDS;

/**
 * This class is in charge of unbanning ip addresses since it can be reused
 * @author Ogwara O. Rowland
 *
 */
@SuppressWarnings("PMD")
public class BanRemover extends BsClazz implements Runnable{

	private BioDS bioDs;

	public BanRemover(BioDS bioDs) {
		this.bioDs = bioDs;

		logger.debug("Initialized Ban Remover");
	}

	@Override
	public void run() {
		try {
			bioDs.unBanExpiredBannedItems();
		} catch (Exception e) {
			logger.error("Exception ", e);
		}
	}

}
