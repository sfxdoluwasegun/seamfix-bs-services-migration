package com.sf.biocapture.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ejb.Singleton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;


@Singleton
public class NetworkClient extends BsClazz{
	
	private String httpSite = "192.168.2.28";
	private int httpPort = 8990;
	private boolean httpsProtocol;
	
	private String serverURL(){
		String url = httpsProtocol ? "https://": "http://";
		url = url + httpSite + ":" + httpPort;
		return url;
	}
	
	public String getData(String path){
		HttpClient httpclient = HttpClientBuilder.create().build(); //new DefaultHttpClient();
		HttpGet hg = new HttpGet(serverURL() + "/" + path);
		hg.setHeader("User-Agent", "Airtel KYC");
		
		try {
			HttpResponse response = httpclient.execute(hg);
			int responseCode = response.getStatusLine().getStatusCode();
			logger.debug("Status Code for Request: " + path + " CODE: " + responseCode);
			
			if(responseCode == 200){
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				 
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				return result.toString();
			}
			
		} catch (ClientProtocolException e) {
			logger.error("Exception ", e);
		} catch (IOException e) {
			logger.error("Exception ", e);
		}
		
		return "";
	}

}
