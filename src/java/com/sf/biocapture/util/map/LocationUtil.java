package com.sf.biocapture.util.map;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;

import com.google.maps.model.GeocodingResult;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.emailapi.EmailApiAuthenticator;
import com.sf.biocapture.emailapi.EmailBean;
import com.sf.biocapture.emailapi.EmailBeanFactory;
import com.sf.biocapture.emailapi.api.impl.MtnEmailApiImp;
import com.sf.biocapture.entity.HeartbeatStatus;
import com.sf.biocapture.entity.KycDealer;
import com.sf.biocapture.entity.NodeAssignment;
import com.sf.biocapture.entity.Outlet;
import com.sf.biocapture.entity.audit.GeoFenceLog;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.heartbeatstatus.GeoFenceDS;
import com.sf.biocapture.ws.heartbeatstatus.UserEmailPojo;

import eu.bitm.NominatimReverseGeocoding.Address;
import nw.commons.NeemClazz;
import nw.orm.core.exception.NwormQueryException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LocationUtil extends NeemClazz {
	
	@Inject
	AccessDS accessDs;
	
	@Inject
	GeoFenceDS geoFenceDS;
	
	@Inject
	MapUtil mapUtil;
	
	@Inject
	Geofencing geofencing;
	
	
	public void checkForLocationDeviation(Double lat, Double lng, String address, String mac, String deviceId) {
		if(lat == null || lat == 0 || lng == null || lng == 0) {
			return;
		}
		
		//check the geofence boundary
		if(!geofencing.isWithinCircleBoundary(lat, lng, mac, deviceId)) {
			try {
				NodeAssignment nA = geoFenceDS.getNodeAssignment(mac);
				
				if(nA == null || nA.getAssignedDealer() == null || nA.getFieldSupportAgent() == null || nA.getOutlet() == null) {
		    		return;
		    	}
				
				EmailApiAuthenticator auth = accessDs.getEmailApiAuthenticator();
				String tag = nA.getTargetNode() == null ? "" : (nA.getTargetNode().getEnrollmentRef() == null ? "" : nA.getTargetNode().getEnrollmentRef().getCode());
				String simropUrl = accessDs.getSettingValue(SettingsEnum.SIMROP_URL);
				sendLocationDeviationMailtoDealer(auth, nA.getAssignedDealer(), nA.getFieldSupportAgent(), nA.getOutlet(), tag, address, simropUrl);
				sendLocationDeviationMailtoCAC(auth, nA.getAssignedDealer(), nA.getFieldSupportAgent(), nA.getOutlet(), tag, address, simropUrl);
			} catch(IllegalArgumentException | HibernateException  e) {
				logger.error("", e);
			} 
		}
	}
	
	public void sendLocationDeviationMailtoDealer(EmailApiAuthenticator auth, KycDealer dealer, KMUser agent, Outlet outlet, String tag, String currAddress, String simropUrl) {	
		String agentName = agent.getFirstName() + " " + agent.getSurname();
		String[] dealerEmail = { dealer.getEmailAddress() };
		
		EmailBean dealerBean = EmailBeanFactory.newLocationDeviationToDealer(auth, dealerEmail, dealer.getName(), tag, outlet.getCoordinateAddress(), 
				currAddress, agent.getEmailAddress(), agentName, agent.getMobile(), outlet.getName(), outlet.getAddress(), simropUrl);
		new MtnEmailApiImp().sendMail(dealerBean);
	}
		
	public void sendLocationDeviationMailtoCAC(EmailApiAuthenticator auth,  KycDealer dealer, KMUser agent, Outlet outlet, String tag, String currAddress, String simropUrl) {
		try {
			String agentName = agent.getFirstName() + " " + agent.getSurname();
			String role = accessDs.getSettingValue(SettingsEnum.CAC_ROLE);
                        String roles [] = role == null || role.isEmpty() ? null : role.split(",");
                        if (roles == null) {
                            logger.debug("No cac email was found to forward deviation alert to.");
                            return;
                        }
			List<UserEmailPojo> emails = geoFenceDS.getUsersByRole(roles);
						
			if(emails != null && emails.size() > 0) {
				String[] cacEmails = getUserEmails(emails);
				String region = dealer.getAssignedZone() == null ? "" : (dealer.getAssignedZone().getRegion() == null ? "" : dealer.getAssignedZone().getRegion().getName());
				
				EmailBean cacBean = EmailBeanFactory.newLocationDeviationToCAC(auth, cacEmails, dealer.getName(), dealer.getDealCode(), region, tag, 
						outlet.getCoordinateAddress(), currAddress, agent.getEmailAddress(), agentName, agent.getMobile(), outlet.getName(), outlet.getAddress(), simropUrl);
				new MtnEmailApiImp().sendMail(cacBean);
			}
		} catch(NwormQueryException e) {
			logger.error("Error sending location deviation to CAC: ", e);
		}
	}
	
	public String getAddress(double lat, double lng, HeartbeatStatus status) {
		String address = "";
		if(lat == 0 && lng == 0) {
			return address;
		}
		
		address = getFromNearestCoordinates(lat, lng, status);
		if(StringUtils.isNotBlank(address)) {
			return address;
		}
		
		address = getAddressFromGoogleMap(lat, lng);
		if(StringUtils.isBlank(address)) {
			address = getAddressFromOSM(lat, lng);
		}
		
		if(StringUtils.isNotBlank(address)) {
			createGeoFenceLog(lat, lng, address, status);
		}		
		return address;
	}
	
	//Get address from GeoFenceLog
	private String getFromNearestCoordinates(double lat, double lng, HeartbeatStatus status) {
		try {
			double range = Double.parseDouble(accessDs.getSettingValue(SettingsEnum.SAME_LOCATION_RANGE));			
			double coordinateRange = range * 0.00001; //1.1m is approximately equal to 0.00001 in coordinates
			
			boolean getByKit = Boolean.parseBoolean(accessDs.getSettingValue(SettingsEnum.GET_GEO_FENCE_LOG_BY_KIT));
			GeoFenceLog log = null;
			if(getByKit) {
				if(status != null) {
					log = getGeoFenceLogByKit(lat, lng, coordinateRange, status.getMacAddress(), status.getRefDeviceId());
				}
			} else {
				log = getGeoFenceLogByRadius(lat, lng, range, coordinateRange);
			}
			
			if(log != null) {
				return log.getCoordinateAddress();
			}
		} catch(IllegalArgumentException e) {
			logger.debug("Error while getting coordinate from geo fence log: ", e);
		}
		return null;
	}
	
	private GeoFenceLog getGeoFenceLogByRadius(double lat, double lng, double range, double coordinateRange) {
		List<GeoFenceLog> logs = geoFenceDS.getGeoFenceLogsByRange(lat, lng, coordinateRange);
		if(logs != null && !logs.isEmpty()) {
			for(GeoFenceLog log : logs) {
				if(Geofencing.getDistanceBetween(lat, lng, log.getLatitude(), log.getLongitude()) <= range) {
					return log;
				}
			}
		}
		return null;
	}
	
	private GeoFenceLog getGeoFenceLogByKit(double lat, double lng, double coordinateRange, String mac, String deviceId) {
		List<GeoFenceLog> logs = geoFenceDS.getGeoFenceLogsByKitRange(lat, lng, coordinateRange, mac, deviceId);
		if(logs != null && !logs.isEmpty()) {
			return logs.get(0);
		}
		return null;
	}
	
	//Get address from Google Map
	private String getAddressFromGoogleMap(double lat, double lng) {
		GeocodingResult result = mapUtil.getAddressFromGoogleMap(lat, lng);
		if(result != null) {
			return result.formattedAddress;
		}
		return null;
	}
	
	//Get address from OpenStreetMap
	private String getAddressFromOSM(double lat, double lng) {
		Address address = mapUtil.getAddressFromOpenStreetMap(lat, lng);
		if(address != null) {
			return address.getDisplayName();
		}
		return null;
	}
	
	private void createGeoFenceLog(double lat, double lng, String address, HeartbeatStatus status) {
		if (StringUtils.isNotBlank(address) && status != null) {
			try {
				GeoFenceLog log = new GeoFenceLog();
				log.setActive(true);
				log.setCoordinateAddress(address);
				log.setLatitude(lat);
				log.setLongitude(lng);
				log.setHeartbeatStatus(status);
				log.setDeleted(false);
				accessDs.getDbService().create(log);
			} catch(NwormQueryException | HibernateException e) {
				logger.error("", e);
			}
		}
	}
	
	private String[] getUserEmails(List<UserEmailPojo> users) {
		if(users == null || users.size() == 0) {
			logger.debug("----- no user to send mail to --");
			return null;
		}
		
		int size = users.size();
		String[] emails = new String[size];
		for(int i = 0; i < size; i++) {
			emails[i] = users.get(i).getEmail();
		}
		return emails;
	}
}
