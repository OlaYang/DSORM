package com.meiqi.dsmanager.common.config;

public enum MemcacheConfig {

	
	CACHE_TIME_TEN_MINUTE(60 * 60 * 1000);
	
	private MemcacheConfig(long time){
		this.time=time;
	}
	public long time;
}
