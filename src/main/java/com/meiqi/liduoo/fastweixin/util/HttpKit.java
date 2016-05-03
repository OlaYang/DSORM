package com.meiqi.liduoo.fastweixin.util;

import java.util.Map;

/**
 * JFinal-weixin Http请求工具类
 * 
 * @author L.cm
 */
public final class HttpKit {

	private HttpKit() {
	}

	public static String get(String url) {
		return delegate.get(url);
	}

	public static String get(String url, Map<String, String> queryParas) {
		return delegate.get(url, queryParas);
	}

	public static String post(String url, String data) {
		return delegate.post(url, data);
	}

	public static String postSSL(String url, String data, String certPath, String certPass) {
		return delegate.postSSL(url, data, certPath, certPass);
	}

	/**
	 * http请求工具 委托 优先使用OkHttp 最后使用JFinal HttpKit
	 */
	private interface HttpDelegate {
		String get(String url);

		String get(String url, Map<String, String> queryParas);

		String post(String url, String data);

		String postSSL(String url, String data, String certPath, String certPass);
	}

	// http请求工具代理对象
	private static final HttpDelegate delegate;

	static {
		delegate = new HttpKitDelegate();
	}

	/**
	 * HttpKit代理
	 */
	private static class HttpKitDelegate implements HttpDelegate {

		@Override
		public String get(String url) {
			return HttpBaseKit.get(url);
		}

		@Override
		public String get(String url, Map<String, String> queryParas) {
			return HttpBaseKit.get(url, queryParas);
		}

		@Override
		public String post(String url, String data) {
			return HttpBaseKit.post(url, data);
		}

		@Override
		public String postSSL(String url, String data, String certPath, String certPass) {
			return HttpBaseKit.postSSL(url, data, certPath, certPass);
		}
	}
}
