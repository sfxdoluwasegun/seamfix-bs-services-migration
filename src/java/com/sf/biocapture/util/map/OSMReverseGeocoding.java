package com.sf.biocapture.util.map;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.enums.SettingsEnum;

import eu.bitm.NominatimReverseGeocoding.Address;
import eu.bitm.NominatimReverseGeocoding.NominatimReverseGeocodingJAPI;
import nw.commons.NeemClazz;


/**
 * This is a custom class. A replica of NominatimReverseGeocodingJAPI class
 * except that it gets the address with a proxy as seen in the getJSON method.
 * 
 * @author Charles
 */
@Stateless
public class OSMReverseGeocoding extends NeemClazz {
	
	@Inject
	AccessDS accessDS;
	
	private final String NominatimInstance = "http://nominatim.openstreetmap.org";

	private int zoomLevel = 18;
	

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("use -help for instructions");
		} else if (args.length < 2) {
			if (args[0].equals("-help")) {
				System.out.println("Mandatory parameters:");
				System.out.println("   -lat [latitude]");
				System.out.println("   -lon [longitude]");
				System.out.println("\nOptional parameters:");
				System.out.println("   -zoom [0-18] | from 0 (country) to 18 (street address), default 18");
				System.out.println("   -osmid       | show also osm id and osm type of the address");
				System.out.println("\nThis page:");
				System.out.println("   -help");
			} else {
				System.err.println("invalid parameters, use -help for instructions");
			}
		} else {
			boolean latSet = false;
			boolean lonSet = false;
			boolean osm = false;

			double lat = -200.0D;
			double lon = -200.0D;
			int zoom = 18;

			for (int i = 0; i < args.length; ++i) {
				if (args[i].equals("-lat")) {
					try {
						lat = Double.parseDouble(args[(i + 1)]);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid latitude");
						return;
					}

					latSet = true;
					++i;
				} else if (args[i].equals("-lon")) {
					try {
						lon = Double.parseDouble(args[(i + 1)]);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid longitude");
						return;
					}

					lonSet = true;
					++i;
				} else if (args[i].equals("-zoom")) {
					try {
						zoom = Integer.parseInt(args[(i + 1)]);
					} catch (NumberFormatException nfe) {
						System.out.println("Invalid zoom");
						return;
					}

					++i;
				} else if (args[i].equals("-osm")) {
					osm = true;
				} else {
					System.err.println("invalid parameters, use -help for instructions");
					return;
				}
			}

			if ((latSet) && (lonSet)) {
				NominatimReverseGeocodingJAPI nominatim = new NominatimReverseGeocodingJAPI(zoom);
				Address address = nominatim.getAdress(lat, lon);
				System.out.println(address);
				if (osm) {
					System.out.print("OSM type: " + address.getOsmType() + ", OSM id: " + address.getOsmId());
				}
			} else {
				System.err.println("please specifiy -lat and -lon, use -help for instructions");
			}
		}
	}

	public OSMReverseGeocoding() {
	}

	public OSMReverseGeocoding(int zoomLevel) {
		if ((zoomLevel < 0) || (zoomLevel > 18)) {
			logger.error("invalid zoom level, using default value");
			zoomLevel = 18;
		}

		this.zoomLevel = zoomLevel;
	}

	public Address getAdress(double lat, double lon) {
		Address result = null;
		String urlString = accessDS.getSettingValue(SettingsEnum.OSM_REVERSE_GEOCODING_URL) + lat + "&lon=" + lon + "&zoom=" + this.zoomLevel;
		try {
			result = new Address(getJSON(urlString), this.zoomLevel);
		} catch (IOException e) {
			logger.error("Can't connect to server.", e);
		}
		return result;
	}

	private String getJSON(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection(new MapUtil().getProxy());
		InputStream is = conn.getInputStream();
		String json = IOUtils.toString(is, "UTF-8");
		is.close();
		return json;
	}
}