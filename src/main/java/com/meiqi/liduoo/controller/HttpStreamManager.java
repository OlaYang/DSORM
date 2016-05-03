package com.meiqi.liduoo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理Thread中的HttpRequest、HttpResponse等变量
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午3:24:25
 */
public class HttpStreamManager {

	private static final ThreadLocal<HttpCallStream> client = new ThreadLocal<HttpCallStream>();
	
	public static HttpCallStream get() {
		HttpCallStream ci = client.get();
		if (ci == null) {
			ci = new HttpCallStream();
			set(ci);
		}
		return ci;
	}

	public static void set(HttpCallStream clientInfo) {
		client.set(clientInfo);
	}
	public static void setRequest(HttpServletRequest request) {
		get().setRequest(request);
	}
	public static void setResponse(HttpServletResponse response) {
		get().setResponse(response);
	}
	public static void setRootKey(String  root) {
		get().setRootKey(root);
	}
}
