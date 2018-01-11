package com.sf.biocapture.ds;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.criterion.Restrictions;

import nw.orm.core.query.QueryParameter;
import nw.orm.core.query.SQLModifier;

import com.sf.biocapture.entity.Zone;
import com.sf.biocapture.entity.krm.ZonalHourlySync;
import com.sf.biocapture.krm.ZonalSync;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class KrmDS extends DataService {

	public List<Zone> listZones(){
		return dbService.getAll(Zone.class);
	}

	/**
	 * Retrieves syncs for selected zone
	 * @param id <b> zone id </b>
	 * @return count of the syncs for target zone
	 */
	public Map<String, Long> getHourlySync(){
		logger.debug("Getting Hourly Syncs");
		Calendar i = Calendar.getInstance();
		int hour = i.get(Calendar.HOUR_OF_DAY);
		return mapZonalSyc(getSyncCount(hour - 1, hour));
	}
	
	public Map<String, Long> getCommulativeSync(){
		logger.debug("Getting Commulative Day Syncs");
		Calendar i = Calendar.getInstance();
		int hour = i.get(Calendar.HOUR_OF_DAY);
		return mapZonalSyc(getSyncCount(0, hour));
	}
	
	private List<ZonalSync> getSyncCount(int start, int end){
		
		String sql = "SELECT count(*) as \"syncs\", z.name as \"zone\" FROM SMS_ACTIVATION_REQUEST s, ENROLLMENT_REF e, NODE n, STATE st, ZONE z WHERE s.enrollment_ref = e.code "
				+ "AND e.mac_address = n.mac_address AND n.state_fk = st.id AND st.zone_fk = z.id AND s.receipt_timestamp BETWEEN :start AND :end GROUP BY z.name ";
		
//		String hql = "SELECT n.state.zone as zone, count(s) as syncs FROM SmsActivationRequest s, EnrollmentRef e, Node n WHERE s.receiptTimestamp BETWEEN :start AND :end and s.enrollmentRef = e.code and e.macAddress = n.macAddress";
		Date startDate = getDateTime(start);
		Date endDate = getDateTime(end);
		logger.debug("Start Date: " + startDate + " End Date: " + endDate);
		List<ZonalSync> lsz = dbService.getBySQL(ZonalSync.class, sql, new SQLModifier(), QueryParameter.create("start", startDate), QueryParameter.create("end", endDate));
		logger.debug("Zonal Info returned: " + lsz.size());
		return lsz;
	}
	
	private Map<String, Long> mapZonalSyc(List<ZonalSync> zss){
		Map<String , Long> ks = new HashMap<String, Long>();
		for(ZonalSync zs: zss){
			ks.put(zs.getZone(), zs.getSyncs().longValue());
		}
		
		return ks;
	}
	
	public ZonalHourlySync getZonalHourlySync(String zone, int syncHour, int syncDay, int syncMonth, int syncYear){
		return dbService.getByCriteria(ZonalHourlySync.class, Restrictions.eq("zone", zone), Restrictions.eq("syncTime", syncHour), Restrictions.eq("syncDay", syncDay), Restrictions.eq("syncMonth", syncMonth), Restrictions.eq("syncYear", syncYear));
	}
	
	public static void main(String[] args) {
//		System.out.println(getDateTime(0));
	}
}
