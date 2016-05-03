package com.meiqi.liduoo.base.utils;

import org.springframework.context.ApplicationContext;

import com.meiqi.dsmanager.action.impl.MemcacheActionImpl;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * Cache相关的通用静态方法
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午1:13:31
 */
public class CacheUtils {
	public static Long EXP_1_HOUR = 60 * 60 * 1000L; // 1小时
	public static Long EXP_1_DAY = 24 * EXP_1_HOUR; // 1天
	public static Long EXP_1_MONTH = 30 * EXP_1_DAY; // 1月

	static MemcacheActionImpl cache = null;

	static {
		ApplicationContext applicationContext = MyApplicationContextUtil.getContext();
		cache = (MemcacheActionImpl) applicationContext.getBean("memcacheActionImpl");
	}

	public static boolean putCache(String key, Object object) {
		return cache.putCache(key, object);
	}

	public static boolean putCache(String key, Object object, long expire) {
		return cache.putCache(key, object, expire);
	}

	// public static boolean putCache(String key, Object object, Date expire) {
	// return cache.putCache(key, object, expire);
	// }

	public static Object getCache(String key) {
		return cache.getCache(key);
	}

	public static boolean removeCache(String key) {
		return cache.removeCache(key);
	}

	public static String createCacheKey(String reqJson) {
		return MD5Util.MD5(reqJson);
	}
}
