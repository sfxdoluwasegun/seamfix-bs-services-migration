package com.sf.biocapture.app;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import javax.ejb.Singleton;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * High level abstraction for low level general memcached activities
 * Used for all service calls with the exception of: sim swap, settings and biodata
 * @author nuke
 *
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class BioCache extends BsClazz{

	protected String addressList;

	protected MemcachedClient client;

	@PostConstruct
	public void init(){
		logger.debug("Initializing Cache Mechanism");
		addressList = appProps.getProperty("cache-server-list", "localhost:11211");
		connect();
//		resyncTestRecords();
	}

	@PreDestroy
	public void end(){
		
	}

	protected void connect(){
		try {
			client = new XMemcachedClientBuilder(AddrUtil.getAddresses(addressList)).build();
			logger.debug("Connected to memcached servers: " + addressList);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private boolean validateConnection(){
		if(client == null){
			logger.debug("Memcached server connection was unsuccessful, retrying: " + addressList);
			connect();
		}
		return client != null;
	}

	/**
	 * Get an item from memcached
	 * @param key
	 * @return
	 */
        @Lock(READ)
	public Object getItem(String key){
		if(!validateConnection()){
			return null;
		}

		Object obj = null;
		try {
                        key = key == null ? key : key.replace(" ", "");//ensure whitespace is not included
			obj = client.get(key);
		} catch (TimeoutException | InterruptedException | MemcachedException | IllegalArgumentException e) {
                        logger.error("key: " + key);
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
        @Lock(READ)
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
        @Lock(READ)
	public boolean setItem(String key, Object item, Integer age){
		if(!validateConnection()){
			return false;
		}
		boolean success = false;
		try {
                        key = key == null ? key : key.replace(" ", "");//ensure whitespace is not included
			success = client.set(key, age, item);
		} catch (TimeoutException | InterruptedException | MemcachedException | IllegalArgumentException e) {
                        logger.error("key: " + key);
			logger.error("", e);
		}
		return success;
	}

	/**
	 * Removes an item from memcached
	 * @param key
	 * @return
	 */
        @Lock(READ)
	public boolean removeItem(String key){
		if(!validateConnection()){
			return false;
		}
		boolean success = false;
		try {
			success = client.delete(key);
		} catch (TimeoutException | InterruptedException | MemcachedException | IllegalArgumentException e) {
                        logger.error("key: " + key);
			logger.error("", e);
		}
		return success;
	}
	
	//TODO
//	private void resyncTestRecords(){
//		String list = "FF_TEST_BANANA-1403726090847,FF_TEST_BANANA-1403110917683,FF_TEST_BANANA-1403781486614,FF_TEST_BANANA-1405591695107,FF_TEST_BANANA-1406899114101,FF_TEST_BANANA-1403255117042,FF_TEST_BANANA-1398296673900,FF_TEST_BANANA-1397488386171";
//		setItem("RESYNC-FF_TEST_BANANA", list, 30 * 60);
//	}

}
