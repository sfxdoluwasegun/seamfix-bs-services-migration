package com.sf.biocapture.ds;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.entity.security.BannedSource;

@Stateless
public class BioDS extends DataService {

	@Inject
	private BioCache cache;

	public void unBanExpiredBannedItems(){
		List<BannedSource> list = getDbService().getListByCriteria(BannedSource.class, Restrictions.le("expiryDate", new Date()));
		for (BannedSource ban : list) {
			ban.setDeleted(true);
			cache.removeItem(ban.getRemoteAddress());
			update(ban);

		}
	}

}
