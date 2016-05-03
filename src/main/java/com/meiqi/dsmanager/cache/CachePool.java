package com.meiqi.dsmanager.cache;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.dsmanager.entity.DataSources;

public class CachePool {
	// 设置初始化容量
	private static final int CAPACITY = 512;
	// 设置最大缓存个数
	private static final int WEIGHT = 10000;
	private static CachePool instance;// 缓存池唯一实例
	//private Map<String, Object> cacheMap;// 缓存Map
	//这里用到了ConcurrentLinkedHashMap，可以设置最大缓存个数，当超过这个限制时，会按照先进先出的方式删除掉最老的缓存
	private static Map<String, Object> cacheMap = new ConcurrentLinkedHashMap.Builder<String, Object>()
			.initialCapacity(CAPACITY).maximumWeightedCapacity(WEIGHT).build();
	// 用户请求授权cache
	private static Map<String, Object>  authCacheMap = new ConcurrentLinkedHashMap.Builder<String, Object>()
            .initialCapacity(CAPACITY).maximumWeightedCapacity(WEIGHT).build();
	
	// dataSource缓存
    private static Map<String, DataSources>  dataSourceCacheMap = new ConcurrentLinkedHashMap.Builder<String, DataSources>()
                .initialCapacity(CAPACITY).maximumWeightedCapacity(WEIGHT).build();
    
	// 用于用户点击图片多次后加心的数据缓存
    private static Map<String, Map<String,Object>>  likeCacheMap = new HashMap<String, Map<String,Object>>(CAPACITY);
	
	public static Map<String, DataSources> getDataSourceCacheMap() {
        return dataSourceCacheMap;
    }

    public static void setDataSourceCacheMap(Map<String, DataSources> dataSourceCacheMap) {
        CachePool.dataSourceCacheMap = dataSourceCacheMap;
    }


    // app  数据是否已是脏数据的标示缓存cache
	private static Map<String, String>  appExpireObjectCacheMap = new ConcurrentLinkedHashMap.Builder<String, String>()
	            .initialCapacity(CAPACITY).maximumWeightedCapacity(WEIGHT).build();

	/**
	 * 得到唯一实例
	 * 
	 * @return
	 */
	public synchronized static CachePool getInstance() {
		if (instance == null) {
			instance = new CachePool();
		}
		return instance;
	}

	/**
	 * 清除所有Item缓存
	 */
	public void clearAllItems() {
		cacheMap.clear();
	}
	
	/**
     * 清除所有用户请求授权Item缓存
     */
    public void clearAllAuthItems() {
        authCacheMap.clear();
    }
    public void clearCacheItem(String name) {
		if (!cacheMap.containsKey(name)) {
			return ;
		}
		cacheMap.remove(name);
	}
	/**
	 * 获取缓存实体
	 * 
	 * @param name
	 * @return
	 */
	public Object getCacheItem(String name) {
		if (!cacheMap.containsKey(name)) {
			return null;
		}
		CacheItem cacheItem = (CacheItem) cacheMap.get(name);
		if(cacheItem.isExpired()){
			cacheMap.remove(name);
			return null;
		}
		return cacheItem.getEntity();
	}
	
	
	/**
     * 获取用户请求授权缓存实体
     * 
     * @param name
     * @return
     */
    public Object getAuthCacheItem(String name) {
        if (!authCacheMap.containsKey(name)) {
            return null;
        }
        CacheItem cacheItem = (CacheItem) authCacheMap.get(name);
        if(cacheItem.isExpired()){
            authCacheMap.remove(name);
            return null;
        }
        return cacheItem.getEntity();
    }

	/**
	 * 存放缓存信息
	 * 
	 * @param name
	 * @param obj
	 * @param expires
	 */
	public  void putCacheItem(String name, Object obj, long expires) {
		if (!cacheMap.containsKey(name)) {
			cacheMap.put(name, new CacheItem(obj, expires));
		}
		CacheItem cacheItem = (CacheItem) cacheMap.get(name);
		cacheItem.setCreateOrUpdateTime(new Date());
		cacheItem.setEntity(obj);
		cacheItem.setExpireTime(expires);
	}
	
	
	
	
	
	/**
     * 存放用户请求授权缓存信息
     * 
     * @param name
     * @param obj
     * @param expires
     */
    public  void putAuthCacheItem(String name, Object obj, long expires) {
        if (!authCacheMap.containsKey(name)) {
            authCacheMap.put(name, new CacheItem(obj, expires));
        }
        CacheItem cacheItem = (CacheItem) authCacheMap.get(name);
        cacheItem.setCreateOrUpdateTime(new Date());
        cacheItem.setEntity(obj);
        cacheItem.setExpireTime(expires);
    }
	

	public void putCacheItem(String name, Object obj) {
		putCacheItem(name, obj, -1);
	}
	
	public void putAuthCacheItem(String name, Object obj) {
        putAuthCacheItem(name, obj, -1);
    }
	

	/**
	 * 移除缓存数据
	 * 
	 * @param name
	 */
	public void removeCacheItem(String name) {
		if (!cacheMap.containsKey(name)) {
			return;
		}
		cacheMap.remove(name);
	}

	/**
     * 移除用户请求授权缓存数据
     * 
     * @param name
     */
    public void removeAuthCacheItem(String name) {
        if (!authCacheMap.containsKey(name)) {
            return;
        }
        authCacheMap.remove(name);
    }
	/**
	 * 根据sourceName移除缓存
	 */
	public void removeDataSourceCache(String dataSourceName){
		if(!dataSourceCacheMap.containsKey(dataSourceName)){
			return;
		}
		dataSourceCacheMap.remove(dataSourceName);
	}
	
	/**
	 * 获取缓存数据的数量
	 * 
	 * @return
	 */
	public int getSize() {
		return cacheMap.size();
	}
	
	
	/**
     * 获取用户请求授权缓存数据的数量
     * 
     * @return
     */
    public int getAuthCacheSize() {
        return authCacheMap.size();
    }
    
    /**
     * 清除用户点击商品次数缓存
     * 
     * @param name
     * @return
     */
    public void removeLikeCache(String key) {
        likeCacheMap.remove(key);
    }

    /**
     * 用户点击商品次数+1，时时记录最后一次非空uid、time
     * @param uid 
     * @param key
     * @return 更新后的值
     */
    public Map<String, Object> incLikeCache(String key, String uid) {
        Map<String, Object> m = likeCacheMap.get(key);
        if(m == null){
            m = new HashMap<String, Object>();
            likeCacheMap.put(key, m);
        }
        
        if (!(uid == null || uid.equals(""))) {
            m.put("uid", uid);
            
            int eventTime = DateUtils.getSecond();
            m.put("eventTime", eventTime);
        }
        
        Integer count = 1;
        if(m.containsKey("count")){
            count += Integer.valueOf(String.valueOf(m.get("count")));
        }
        m.put("count", count);
        
        return m;
    }
}
