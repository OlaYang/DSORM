package com.meiqi.app.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.meilele.datalayer.common.utils.IOUtil;

public class HttpUtil {
    public static final Integer MAX_IDLE_TIME_OUT = Integer.valueOf(60000);
    private static final String URL_ENCODE        = "utf-8";
    public static HttpClient    httpClient;

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



    public static Map<String, String> packParamsFromRequest(HttpServletRequest request, String charSet)
            throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        Map<?, ?> requestParams = request.getParameterMap();
        for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), charSet);
            params.put(name, valueStr);
        }
        return params;
    }



    /**
     * 
     * Http 请求获取html页面源码
     *
     * @param urlStr
     * @return
     */
    public static String getHtmlContent(String urlStr) {
        StringBuffer contentBuffer = new StringBuffer();
        int responseCode = -1;
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            // IE代理进行下载
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            // 获得网页返回信息码
            responseCode = con.getResponseCode();
            if (responseCode == -1) {
                System.out.println(url.toString() + " : connection is failure...");
                con.disconnect();
                return null;
            }
            // 请求失败
            if (responseCode >= 400) {
                System.out.println("请求失败:get response code: " + responseCode);
                con.disconnect();
                return null;
            }

            InputStream inStr = con.getInputStream();
            InputStreamReader istreamReader = new InputStreamReader(inStr, URL_ENCODE);
            BufferedReader buffStr = new BufferedReader(istreamReader);
            String str = null;
            while ((str = buffStr.readLine()) != null)
                contentBuffer.append(str);
            inStr.close();
        } catch (Exception e) {
            contentBuffer = new StringBuffer();
            contentBuffer.append("请求失败,error:" + e.getMessage());
        } finally {
            con.disconnect();
        }
        return contentBuffer.toString();
    }
}
