package com.meiqi.liduoo.base.utils;

import java.util.Map;

/**
 * Map相关的实用方法
 * 
 * @author FrankGui
 *
 */
@SuppressWarnings("rawtypes")
public class MapUtil {

	public static boolean isNullOrEmpty(Map map) {
		return map == null || map.size() <= 0;
	}

	public static int size(Map map) {
		if (isNullOrEmpty(map)) {
			return 0;
		} else {
			return map.size();
		}
	}

	public static boolean notEmpty(Map map) {
		if (null != map) {
			return !map.isEmpty();
		}
		return false;
	}

}
