package com.meiqi.dsmanager.action.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.dsmanager.util.MemcacheLejjClient;

@Service
public class MemcacheActionImpl implements IMemcacheAction {

	@Autowired
	private MemcacheLejjClient memcacheLejjClient;
	
	@Override
	public boolean putCache(String key,Object object) {
		return memcacheLejjClient.put(key, object);
	}
	
	@Override
	public boolean putCache(String key,Object object,long expire) {
		return memcacheLejjClient.put(key, object,new Date(expire));
	}

	@Override
	public Object getCache(String key) {
		return memcacheLejjClient.get(key);
	}

	@Override
	public boolean removeCache(String key) {
		return memcacheLejjClient.delete(key);
	}

	@Override
	public String createCacheKey(String reqJson) {
		return MD5Util.MD5(reqJson);
	}
}
