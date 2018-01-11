package com.sf.biocapture.krm;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.KrmDS;
import com.sf.biocapture.entity.Zone;
import com.sf.biocapture.entity.krm.ZonalHourlySync;

@Stateless
public class ZonalKrm extends BsClazz{
	
	@Inject
	private KrmDS krmDs;
	
	public void loadZonalSyncData(){
		logger.debug("Loading Zonal Summary Info");
		Calendar i = Calendar.getInstance();
		int syncHour = i.get(Calendar.HOUR_OF_DAY);
		int syncDay = i.get(Calendar.DAY_OF_MONTH);
		int syncWeek = i.get(Calendar.WEEK_OF_YEAR);
		int syncMonth = i.get(Calendar.MONTH);
		int syncYear = i.get(Calendar.YEAR);
		
		List<Zone> zones = krmDs.listZones();
		logger.debug("Size of items in zone: " + zones.size());
		if(zones == null || (zones != null && zones.isEmpty())){
			logger.debug("Listing zones failed. No zone entry found");
			
			return;
		}
		
		Map<String, Long> hs = krmDs.getHourlySync();
		Map<String, Long> cs = krmDs.getCommulativeSync();
		
		for(Zone zone: zones){
			logger.debug("Processing Zonal Summary Info for " + zone.getName());
			ZonalHourlySync zhs = krmDs.getZonalHourlySync(zone.getName(), syncHour, syncDay, syncMonth, syncYear);
			if(zhs == null){
				zhs = new ZonalHourlySync();
				zhs.setZone(zone.getName());
				zhs.setSyncTime(syncHour);
				zhs.setSyncDay(syncDay);
				zhs.setSyncWeek(syncWeek);
				zhs.setSyncMonth(syncMonth);
				zhs.setSyncYear(syncYear);
				zhs.setSyncCount(hs.get(zone.getName()) == null ? 0L : hs.get(zone.getName()));
				zhs.setDaySyncCount(cs.get(zone.getName()) == null ? 0L : cs.get(zone.getName()));
				
				ZonalHourlySync pzs = getPrevious(zone.getName(), syncHour, syncDay - 1, syncMonth, syncYear);
				process(zhs, pzs);
				krmDs.getDbService().create(zhs);
				logger.debug("Done Processing Zonal Summary Info for " + zone.getName());
			}
		}
	}
	
	private ZonalHourlySync getPrevious(String zone, int syncHour, int syncDay, int syncMonth, int syncYear){
		logger.debug("Loading Previous day Zonal Sumaary Info for " + zone);
		if(syncDay < 1){
			// start of month detected
			Calendar cs = Calendar.getInstance();
			
			syncMonth = syncMonth - 1; // last month
			if(syncMonth < 0){ // end of year
				syncYear = syncYear - 1;
				cs.set(Calendar.YEAR, syncYear);
				syncMonth = cs.getActualMaximum(Calendar.MONTH);
			}
			
			cs.set(Calendar.MONTH, syncMonth);
			syncDay = cs.getActualMaximum(Calendar.DAY_OF_MONTH);// get max day of month
			
		}
		return krmDs.getZonalHourlySync(zone, syncHour, syncDay, syncMonth, syncYear);
	}
	
	private void process(ZonalHourlySync zhs, ZonalHourlySync preZhs){
		if(preZhs == null){
			preZhs = new ZonalHourlySync();
			preZhs.setDaySyncCount(0L);
			preZhs.setSyncCount(0L);
		}
		zhs.setPrevDaySyncCount(preZhs.getDaySyncCount());
		zhs.setPrevSyncCount(preZhs.getSyncCount());
		zhs.setHourlyDifference(zhs.getSyncCount() - preZhs.getSyncCount());
		zhs.setDaySyncDifference(zhs.getDaySyncCount() - preZhs.getDaySyncCount());
	}
	
	public static void main(String[] args) {
		Calendar cs = Calendar.getInstance();
		cs.set(Calendar.MONTH, 1);
		System.out.println(cs.getActualMaximum(Calendar.DAY_OF_MONTH));
	}

}
