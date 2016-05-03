package com.meiqi.dsmanager.util;

import com.meiqi.dsmanager.cache.CachePool;

public class CacheUtil {

	/**
	 * 设置缓存
	 * @param key 缓存key
	 * @param object 缓存数据
	 * @param expires 缓存时间  毫秒  如果为-1那么永久生效
	 */
	public static void  putCache(String key,Object object,long expires){
		CachePool.getInstance().putCacheItem(key, object, expires);
	}
	/**
	 * 获取缓存
	 * @param key
	 */
    public static Object getCache(String key){
    	return CachePool.getInstance().getCacheItem(key);
	}
    /**
     * 删除缓存
     * @param key
     */
    public static void removeCache(String key){
    	CachePool.getInstance().removeCacheItem(key);
	}
    /**
     * 获取当前的缓存的大小
     */
    public static int getCacheSize(){
    	return CachePool.getInstance().getSize();
	}
    /**
     * 清空所有缓存
     */
    public static void clearAllCache(){
    	CachePool.getInstance().clearAllItems();
    }
}
