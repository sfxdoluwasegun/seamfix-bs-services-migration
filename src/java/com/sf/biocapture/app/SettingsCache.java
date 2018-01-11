package com.sf.biocapture.app;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * Dedicated memcache client implementation
 * for client settings service
 * @author Nnanna
 * @since 26/01/2017
 *
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SettingsCache extends BsClazz {
	protected String addressList;

	protected MemcachedClient client;

	@PostConstruct
	public void init(){
		logger.debug("Initializing Cache for Settings");
		addressList = appProps.getProperty("settings-cache-server-list", "localhost:11211");
		connect();
	}

	@PreDestroy
	public void end(){

	}

	protected void connect(){
		try {
			client = new XMemcachedClientBuilder(AddrUtil.getAddresses(addressList)).build();
			logger.debug("Connected settings cache to memcached servers: " + addressList);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private boolean validateSettingCacheConnection(){
		if(client == null){
			logger.error("Settings memcached server connection appears unsuccessful, retrying: " + addressList);
			connect();
		}
		return client != null;
	}

	/**
	 * Get an item from memcached
	 * @param key
	 * @return
	 */
	public Object getItem(String key){
		if(!validateSettingCacheConnection()){
			return null;
		}
		Object obj = null;
		try {
			obj = client.get(key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.error("", e);
		}
		return obj;
	}

	/**
	 * Gets an item from memecached
	 * @param key
	 * @param returnClazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getItem(String key, Class<T> returnClazz){
		T obj = (T) getItem(key);
		return obj;
	}

	/**
	 * Adds/updates an item on Memcached
	 * @param key
	 * @param item
	 * @param age seconds to keep in cache
	 * @return
	 */
	public boolean setItem(String key, Object item, Integer age){
		if(!validateSettingCacheConnection()){
			return false;
		}
		boolean success = false;
		try {
			key = key == null ? key : key.replace(" ", "");//ensure whitespace is not included
			success = client.set(key, age, item);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.error("", e);
		}
		return success;
	}

	/**
	 * Removes an item from memcached
	 * @param key
	 * @return
	 */
	public boolean removeItem(String key){
		if(validateSettingCacheConnection()){
			return false;
		}
		boolean success = false;
		try {
			success = client.delete(key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.error("", e);
		}
		return success;
	}
}
