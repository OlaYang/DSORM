package com.meiqi.dsmanager.common.config;
/**
 * ds_manager缓存配置
 * @author DuanRan
 * @date 2015年6月15日
 */
public enum ConcurrentLinkedHashMapConfig {
	CACHE_NOT_STALE(-1);//缓存不过时
	
	private ConcurrentLinkedHashMapConfig(long time){
		this.time=time;
	}
	public long time;
}
