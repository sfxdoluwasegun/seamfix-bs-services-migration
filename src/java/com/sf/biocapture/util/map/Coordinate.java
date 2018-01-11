/**
 * 
 */
package com.sf.biocapture.util.map;

import java.io.Serializable;

/**
 * @author Charles
 *
 */
public class Coordinate implements Serializable {
	
	private static final long serialVersionUID = 1787580207315461607L;
	private double lat;
	private double lng;
	
	public Coordinate() {
	}
	
	public Coordinate(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
		
}
