package com.sf.biocapture.util.map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.SettingsEnum;

import nw.commons.NeemClazz;

/**
*
* @author Charles
*/
@Stateless
public class Geofencing extends NeemClazz {
	
	@Inject
	AccessDS accessDs;
	
	/**
	 * Mean radius.
	 */
	private static double EARTH_RADIUS = 6371;

	/**
	 * Returns the distance between two sets of latitudes and longitudes in meters.
	 * <p/>
	 * Based from the following JavaScript SO answer:
	 * http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula,
	 * which is based on https://en.wikipedia.org/wiki/Haversine_formula (error rate: ~0.55%).
	 */
	public static double getDistanceBetween(double lat1, double lon1, double lat2, double lon2) {
	    double dLat = toRadians(lat2 - lat1);
	    double dLon = toRadians(lon2 - lon1);

	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	            Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
	                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double d = EARTH_RADIUS * c * 1000; //multiply by 1000 to convert to meters

	    return d;
	}

	public static double toRadians(double degrees) {
	    return degrees * (Math.PI / 180);
	}
	
	public boolean isWithinCircleBoundary(double lat, double lng, String mac, String deviceId) {
		double radius = Double.parseDouble(accessDs.getSettingValue(SettingsEnum.GEO_FENCE_RADIUS));
		logger.debug("Geofence radius --- "+ radius);
		
		Coordinate coordinate = accessDs.getOutletCoordinate(mac, deviceId);
		if(coordinate == null) {
			return true;
		}
		
		double lat2 = coordinate.getLat();
		double lng2 = coordinate.getLng();
		if(lat2 != 0 && lng2 != 0) {
			if(getDistanceBetween(lat, lng, lat2, lng2) > radius) {
				return false;
			}
		}
		
		return true;
	}
}
