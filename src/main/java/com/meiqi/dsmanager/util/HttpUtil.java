package com.meiqi.dsmanager.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;

public final class HttpUtil {

	private static final Logger LOG = Logger.getLogger(HttpUtil.class);

	private HttpUtil() {
	}

	/**
	 * 发送简单postdata请求
	 * 
	 * @param url
	 * @param propsMap
	 * @param connectTimeout
	 * @return
	 */
	public static String httpPostData(String url, String body,
			int connectTimeout) {
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try {
			httpClient = new HttpClient();
			// 设置超时
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(connectTimeout);
			// httpClient.getHttpConnectionManager().getParams().set
			postMethod = new PostMethod(url);
			RequestEntity requestEntity = new StringRequestEntity(body,
					"text/xml", "UTF-8");
			postMethod.setRequestEntity(requestEntity);
			return execute(httpClient, postMethod, url);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Send request to " + url + " error", e);
			return null;
		} finally {
			// 关闭连接
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			if (httpClient != null) {
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			}
		}
	}

	
	/**
	 * 发送简单post请求
	 * 
	 * @param url
	 * @param propsMap
	 * @param connectTimeout
	 * @return
	 */
	public static String httpPost(String url, Map<String, Object> propsMap,
			int connectTimeout) {
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try {
			httpClient = new HttpClient();
			// 设置超时
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(connectTimeout);
			postMethod = new PostMethod(url);

			if (propsMap != null && propsMap.size() > 0) {
				packParams(postMethod, propsMap);
			}

			return execute(httpClient, postMethod, url);
		} catch (Exception e) {
			LOG.error("Send request to " + url + " error", e);
			return null;
		} finally {
			// 关闭连接
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			if (httpClient != null) {
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			}
		}
	}

	/**
	 * 用HTTP post方式上传文件
	 * 
	 * @param url
	 *            路径
	 * @param inputStream
	 *            上传文件流
	 * @param requestHeaderMap
	 *            header信息
	 * @param connectTimeout
	 *            超时时间
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String postFile(String url, InputStream inputStream,
			Map<String, String> requestHeaderMap, int connectTimeout)
			throws FileNotFoundException {
		if (inputStream == null) {
			return null;
		}
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try {
			httpClient = new HttpClient();
			postMethod = new PostMethod(url);
			// 设置超时
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(connectTimeout);
			postMethod.setRequestEntity(new InputStreamRequestEntity(
					inputStream));
			if (requestHeaderMap != null && requestHeaderMap.size() > 0) {
				Set<Map.Entry<String, String>> entrySet = requestHeaderMap
						.entrySet();
				for (Map.Entry<String, String> entry : entrySet) {
					postMethod.setRequestHeader(entry.getKey(),
							entry.getValue());
				}
			}
			return execute(httpClient, postMethod, url);
		} catch (Exception e) {
			LOG.error("postFiles to " + url + " error", e);
			return null;
		} finally {
			// 关闭连接
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			if (httpClient != null) {
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			}
		}
	}

	/**
	 * 发送简单get请求
	 * 
	 * @param url
	 * @param propsMap
	 * @param connectTimeout
	 * @return
	 */
	public static String httpGet(String url, Map<String, Object> propsMap,
			int connectTimeout) {
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		try {
			httpClient = new HttpClient();
			// 设置超时
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(connectTimeout);
			getMethod = new GetMethod(url);
			if (propsMap != null && propsMap.size() > 0) {
				packParams(getMethod, propsMap);
			}
			return execute(httpClient, getMethod, url);
		} catch (Exception e) {
			LOG.error("Send request to " + url + " error", e);
			return null;
		} finally {
			// 关闭连接
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
			if (httpClient != null) {
				httpClient.getHttpConnectionManager().closeIdleConnections(0);
			}
		}
	}

	/**
	 * 获得带ContextPath的完整Url
	 * **/
	public static String getContextPathUrl(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append(request.getScheme()).append("://");
		sb.append(request.getServerName()).append(":");
		if (80 != request.getServerPort()) {
			sb.append(request.getServerPort());
		}
		sb.append(request.getContextPath());
		return sb.toString();
	}

	private static String execute(HttpClient httpClient, HttpMethod httpMethod,
			String url) throws HttpException, IOException {
		// 发送请求
		int statuCode = httpClient.executeMethod(httpMethod);
		if (statuCode == HttpServletResponse.SC_OK) {
			return httpMethod.getResponseBodyAsString();
		}
		LOG.warn("Bad Request " + url + ", Response status is " + statuCode);
		return null;
	}

	private static void packParams(PostMethod postMethod,
			Map<String, Object> propsMap) {
		// 参数设置
		NameValuePair[] postData = new NameValuePair[propsMap.size()];
		Set<String> keySet = propsMap.keySet();
		int index = 0;
		for (String key : keySet) {
			postData[index++] = new NameValuePair(key, propsMap.get(key)
					.toString());
		}
		postMethod.addParameters(postData);
		postMethod.getParams().setContentCharset("UTF-8");
	}

	private static void packParams(GetMethod getMethod,
			Map<String, Object> propsMap) {
		// 参数设置
		NameValuePair[] getData = new NameValuePair[propsMap.size()];
		Set<String> keySet = propsMap.keySet();
		int index = 0;
		for (String key : keySet) {
			getData[index++] = new NameValuePair(key, propsMap.get(key)
					.toString());
		}
		getMethod.setQueryString(getData);
		getMethod.getParams().setContentCharset("UTF-8");
	}

}
