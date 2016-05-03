package com.meiqi.data.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-8-22
 * Time: 下午5:36
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientUtil {

//    private static final HttpClient client = HttpClientPool.offer();
//
//    public static String get(String url) throws Exception {
//        HttpGet get = new HttpGet(url);
//        HttpResponse response = client.execute(get);
//        int statusCode = response.getStatusLine().getStatusCode();
//        switch (statusCode) {
//            case HttpStatus.SC_OK:
//                HttpEntity entity = response.getEntity();
//                String result = DataUtil.inputStream2String(entity.getContent());
//                return result;
//            default:
//                LogUtil.error("第三方调用错误:" + statusCode);
//        }
//        return null;
//    }
//
//    public static String post(String url, String uri, String json) throws Exception {
//        HttpPost post = new HttpPost(url + uri);
//        StringEntity stringEntity = new StringEntity(json, "UTF-8");
//        post.setEntity(stringEntity);
//        HttpResponse response = client.execute(post);
//        int statusCode = response.getStatusLine().getStatusCode();
//
//        switch (statusCode) {
//            case HttpStatus.SC_OK:
//                HttpEntity entity = response.getEntity();
//                String result = DataUtil.inputStream2String(entity.getContent());
//                return result;
//            default:
//                BaseRespInfo respInfo = new BaseRespInfo();
//                respInfo.setCode("-1");
//                respInfo.setDescription("http error code :" + statusCode);
//                return JSON.toJSONString(JSON.toJSONString(respInfo));
//        }
//    }

    public static String postRuleService(String json) throws Exception {
        return post(ConfigUtil.getRule_url(), "/service", json);
    }


    public static String post(String httpUrl, String uri, String json) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
        URL url = new URL(httpUrl + uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(3600 * 60 * 100);
        connection.setReadTimeout(3600 * 60 * 100);

        byte[] buff = new byte[1024];
        int count;
        while ((count = bais.read(buff)) != -1) {
            connection.getOutputStream().write(buff, 0, count);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset();

        while ((count = connection.getInputStream().read(buff)) != -1) {
            baos.write(buff, 0, count);
        }
        String jsonFromData = new String(baos.toByteArray());
        bais.close();
        baos.close();
        return jsonFromData;
    }

    public static String get(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(3600 * 60 * 100);
        connection.setReadTimeout(3600 * 60 * 100);
        byte[] buff = new byte[1024];
        int count;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset();
        while ((count = connection.getInputStream().read(buff)) != -1) {
            baos.write(buff, 0, count);
        }
        String jsonFromData = new String(baos.toByteArray());
        baos.close();
        return jsonFromData;
    }


    public static void main(String args[]) throws Exception {
        String json = "{\n" +
                "  \"serviceName\":\"根据用户名查询信息\",\n" +
                "  \"format\":\"json\",\n" +
                "  \"param\":{\"user_name\":\"\"},\n" +
                "  \"needAll\":\"0\"\n" +
                "}";
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            post("http://192.168.0.22:3032", "/service", json);
            //post("http://192.168.0.22:3032/service",json);
        }
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
    }


}
