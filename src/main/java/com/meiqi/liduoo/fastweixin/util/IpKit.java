/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.meiqi.liduoo.fastweixin.util;

import javax.servlet.http.HttpServletRequest;

public class IpKit {
	
	public static String getRealIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static String getRealIpV2(HttpServletRequest request) {
		String accessIP = request.getHeader("x-forwarded-for");
        if (null == accessIP)
            return request.getRemoteAddr();
        return accessIP;
	}

	/**
	 * 判断是否腾讯系域名
	 * 
	 * @param host
	 * @return
	 */
	public static boolean isTencentDomain(String host) {
		if (host == null) {
			return false;
		}
		host = host.toLowerCase();
		final String[] tencentDomains = { ".qq.com", "mmbiz.qpic.cn","mmbiz.qlogo.cn" };
		for (String tencent : tencentDomains) {
			if (host.indexOf(tencent) >= 0) {
				return true;
			}
		}
	
		return false;
	}
}
