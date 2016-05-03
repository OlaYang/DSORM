/**   
 * @Title: MemcacheLejjClient.java 
 * @Package com.meiqi.dsmanager.passport.util 
 * @Description: 
 * @author liujie@lejj.com  
 * @date 2015年4月29日 下午5:02:52 
 * @version V1.0   
 */
package com.meiqi.dsmanager.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.danga.MemCached.MemCachedClient;
import com.schooner.MemCached.MemcachedItem;

/**
 * @ClassName: MemcacheLejjClient
 * @Description:
 * @date 2015年4月29日 下午5:02:52
 * 
 */
@Component
public class MemcacheLejjClient {

	@Autowired
	private MemCachedClient memCachedClient;

	public MemCachedClient getMemCachedClient() {
		this.memCachedClient.setDefaultEncoding("UTF-8");
		return memCachedClient;
	}

	public void setMemCachedClient(MemCachedClient memCachedClient) {
		this.memCachedClient = memCachedClient;
	}

	public boolean add(String key, Object value) {
		return memCachedClient.add(key, value);
	}

	public boolean add(String key, Object value, Integer expire) {
		return memCachedClient.add(key, value, expire);
	}

	public boolean put(String key, Object value) {
		return memCachedClient.set(key, value);
	}

	public boolean put(String key, Object value, Integer expire) {
		return memCachedClient.set(key, value, expire);
	}

	public boolean put(String key, Object value, Date expire) {
		return memCachedClient.set(key, value, expire);
	}
	
	public boolean replace(String key, Object value) {
		return memCachedClient.replace(key, value);
	}

	public boolean replace(String key, Object value, Integer expire) {
		return memCachedClient.replace(key, value, expire);
	}

	public Object get(String key) {
		return memCachedClient.get(key);
	}
	
	public MemcachedItem gets(String key){
		return memCachedClient.gets(key);
	}
	
	public boolean delete(String key) {
		return memCachedClient.delete(key);
	}

}
