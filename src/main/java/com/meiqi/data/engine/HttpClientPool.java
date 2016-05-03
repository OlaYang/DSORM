package com.meiqi.data.engine;

import com.meiqi.data.util.ConfigUtil;



/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-22
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientPool {
//    private static PoolingClientConnectionManager cm;
//    static {
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(
//                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
//        schemeRegistry.register(
//                new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
//
//        cm = new PoolingClientConnectionManager(schemeRegistry);
//        cm.setMaxTotal(200);
//        cm.setDefaultMaxPerRoute(80);
//        HttpParams params = new BasicHttpParams();
//        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ConfigUtil.getConnect_timeout());
//        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, ConfigUtil.getRead_timeout());
//    }
//
//    public static HttpClient offer() {
//        return new DefaultHttpClient(cm);
//    }

}
