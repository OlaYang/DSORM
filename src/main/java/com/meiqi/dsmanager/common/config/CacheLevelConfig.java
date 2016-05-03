package com.meiqi.dsmanager.common.config;
/**
 * 缓存等级配置
 * @author DuanRan
 * @date 2015年6月16日
 */
public enum CacheLevelConfig {
	NO_CACHE(0),             //不进行缓存  
	MEMCACHE_CACHE(1),      //memcache缓存
	DSMANAGER_CACHE(2);         //dsmanager缓存
	
	public int level;
	private CacheLevelConfig(int level){
		this.level=level;
	}
}
