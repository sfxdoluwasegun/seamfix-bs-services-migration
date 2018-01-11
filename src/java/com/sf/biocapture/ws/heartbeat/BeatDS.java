package com.sf.biocapture.ws.heartbeat;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nw.orm.core.query.QueryModifier;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.HeartBeat;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeatDS extends DataService{
	
	public BeatList listBeats(){
		QueryModifier qm = new QueryModifier(HeartBeat.class);
		qm.setPaginated(0, 100);
		List<HeartBeat> ls = getDbService().getListByCriteria(HeartBeat.class, qm);
		BeatList bl = new BeatList();
		bl.setBeats(ls);
		
		return bl;
	}

}
