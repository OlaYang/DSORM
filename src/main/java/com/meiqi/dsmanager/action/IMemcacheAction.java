package com.meiqi.dsmanager.action;





public interface IMemcacheAction {

	/**
	 * 设置缓存
	 * @param serviceName
	 * @param reqJson
	 * @param object
	 * @return
	 */
	public boolean putCache(String key,Object object);
	/**
	 * 设置缓存
	 * @param serviceName
	 * @param reqJson
	 * @param object
	 * @param expire 缓存时间 毫秒
	 * @return
	 */
	public boolean putCache(String key,Object object,long expire);
	/**
	 * 获取缓存
	 * @param serviceName
	 * @param reqJson
	 * @return
	 */
	public Object  getCache(String key);
	/**
	 * 删除缓存
	 * @param serviceName
	 * @param reqJson
	 * @return
	 */
	public boolean removeCache( String key);
	/**
	 * 生成缓存的key
	 * @param serviceName
	 * @param reqJson
	 * @return
	 */
	public String createCacheKey(String reqJson);
	
	
}
