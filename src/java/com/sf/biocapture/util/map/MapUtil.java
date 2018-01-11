/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.util.map;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.SettingsEnum;

import eu.bitm.NominatimReverseGeocoding.Address;
import nw.commons.NeemClazz;

/**
 *
 * @author best
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MapUtil extends NeemClazz {

	@Inject
    AccessDS accessDs;

	@SuppressWarnings("PMD")
    public Address getAddressFromOpenStreetMap(Double lat, Double lng) {
    	try {
    		OSMReverseGeocoding reverseGeoCode = new OSMReverseGeocoding();
	        return reverseGeoCode.getAdress(lat, lng);
    	} catch(Exception e) {
    		logger.error("Failed to retrieve address from OSM", e);
    	}
    	return null;
    }

    public GeocodingResult getAddressFromGoogleMap(Double lat, Double lng) {
        LatLng latLng = new LatLng(lat, lng);
        String googleMapKey = accessDs.getSettingValue(SettingsEnum.BS_GOOGLE_MAP_API_KEY);

        try {
        	GeoApiContext context = new GeoApiContext.Builder().proxy(getProxy()).apiKey(googleMapKey).build();
            GeocodingResult[] results = null;
            results = GeocodingApi.reverseGeocode(context, latLng).await();
            if (results != null) {
               return results[0];
            }
        } catch (ApiException | InterruptedException | IOException ex) {
            logger.error("Failed to retrieved address from google map api", ex);
        }
        return null;
    }
    
    public Proxy getProxy() {
    	try {
	    	String proxyIP = accessDs.getSettingValue(SettingsEnum.PROXY_IP_FOR_GEOCODING);
	    	int proxyPort = Integer.parseInt(accessDs.getSettingValue(SettingsEnum.PROXY_PORT_FOR_GEOCODING));
	    	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIP, proxyPort));
	    	return proxy;
    	} catch(IllegalArgumentException | SecurityException e) {
    		logger.error("Error while getting proxy: ", e);
    	}
    	return null;
    }
}
