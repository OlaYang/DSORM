/**
 * 
 */
package com.meiqi.liduoo.fastweixin.util;

/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

/**
 * HttpKit
 */
public class HttpBaseKit {
	private static String DEFAULT_USER_AGENT = "UTF-8";

	private HttpBaseKit() {
	}

	/**
	 * https 域名校验
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * https 证书管理
	 */
	private class TrustAnyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String CHARSET = "UTF-8";

	private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
	private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpBaseKit().new TrustAnyHostnameVerifier();
	private static final String DEFAULT_CHARSET = CHARSET;

	private static SSLSocketFactory initSSLSocketFactory() {
		try {
			TrustManager[] tm = { new HttpBaseKit().new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpURLConnection getHttpConnection(String url, String method, Map<String, String> headers)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
		}

		conn.setRequestMethod(method);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		conn.setConnectTimeout(19000);
		conn.setReadTimeout(19000);

		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

		if (headers != null && !headers.isEmpty())
			for (Entry<String, String> entry : headers.entrySet())
				conn.setRequestProperty(entry.getKey(), entry.getValue());

		return conn;
	}

	/**
	 * Send GET request
	 */
	public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), GET, headers);
			conn.connect();
			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String get(String url, Map<String, String> queryParas) {
		return get(url, queryParas, null);
	}

	public static String get(String url) {
		return get(url, null, null);
	}

	/**
	 * Send POST request
	 */
	public static String post(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), POST, headers);
			conn.connect();

			OutputStream out = conn.getOutputStream();
			out.write(data.getBytes(CHARSET));
			out.flush();
			out.close();

			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String post(String url, Map<String, String> queryParas, String data) {
		return post(url, queryParas, data, null);
	}

	public static String post(String url, String data, Map<String, String> headers) {
		return post(url, null, data, headers);
	}

	public static String post(String url, String data) {
		return post(url, null, data, null);
	}

	private static String readResponseString(HttpURLConnection conn) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		try {
			inputStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Build queryString of the url
	 */
	public static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
		if (queryParas == null || queryParas.isEmpty())
			return url;

		StringBuilder sb = new StringBuilder(url);
		if (url.indexOf("?") == -1) {
			sb.append("?");
		}

		sb.append(buildQueryString(queryParas));
		return sb.toString();
	}

	public static String buildQueryString(Map<String, String> queryParas) {
		if (queryParas == null || queryParas.isEmpty()) {
			return "";
		}
		boolean isFirst = true;
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : queryParas.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append("&");

			String key = entry.getKey();
			String value = entry.getValue();
			if (StrUtil.isNotBlank(value))
				try {
					value = URLEncoder.encode(value, CHARSET);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	public static String readIncommingRequestData(HttpServletRequest request) {
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = request.getReader();
			for (String line = null; (line = br.readLine()) != null;) {
				result.append(line).append("\n");
			}

			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 涉及资金回滚的接口会使用到商户证书，包括退款、撤销接口的请求
	 * 
	 * @param url
	 *            请求的地址
	 * @param data
	 *            xml数据
	 * @param certPath
	 *            证书文件目录
	 * @param certPass
	 *            证书密码
	 * @return String 回调的xml信息
	 */
	protected static String postSSL(String url, String data, String certPath, String certPass) {
		HttpsURLConnection conn = null;
		OutputStream out = null;
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(new FileInputStream(certPath), certPass.toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientStore, certPass.toCharArray());
			KeyManager[] kms = kmf.getKeyManagers();
			SSLContext sslContext = SSLContext.getInstance("TLSv1");

			sslContext.init(kms, null, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			URL _url = new URL(url);
			conn = (HttpsURLConnection) _url.openConnection();

			conn.setConnectTimeout(25000);
			conn.setReadTimeout(25000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
			conn.connect();

			out = conn.getOutputStream();
			out.write(data.getBytes(DEFAULT_CHARSET));
			out.flush();

			inputStream = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARSET));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOKit.closeQuietly(out);
			IOKit.closeQuietly(reader);
			IOKit.closeQuietly(inputStream);
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String readData(HttpServletRequest request) {
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = request.getReader();
			for (String line=null; (line=br.readLine())!=null;) {
				result.append(line).append("\n");
			}
			
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (br != null)
				try {br.close();} catch (IOException e) {}
		}
	}
}
