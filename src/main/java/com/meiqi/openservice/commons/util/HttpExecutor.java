package com.meiqi.openservice.commons.util;

import java.io.IOException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class HttpExecutor {
	public static final Integer MAX_IDLE_TIME_OUT = Integer.valueOf(60000);

	public static HttpClient httpClient;

	static {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.closeIdleConnections(MAX_IDLE_TIME_OUT.intValue());
		httpClient = new HttpClient(connectionManager);
	}

	public static String get(String url) throws HttpException, IOException {
		GetMethod get = new GetMethod(url);
		try {
			httpClient.executeMethod(get);
			return IOUtil.streamToString(get.getResponseBodyAsStream());
		} finally {
			get.releaseConnection();
		}
	}

	public static String post(String url, String content) throws IOException {
		return post(url, content, "text/json", "utf-8");
	}

	public static String post(String url, String content, String contentType, String charset) throws IOException {
		PostMethod post = new PostMethod(url);
		post.setRequestEntity(new StringRequestEntity(content, contentType, charset));
		try {
			httpClient.executeMethod(post);
			return IOUtil.streamToString(post.getResponseBodyAsStream());
		} finally {
			post.releaseConnection();
		}
	}
}
