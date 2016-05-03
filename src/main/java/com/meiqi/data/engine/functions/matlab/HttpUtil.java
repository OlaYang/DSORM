package com.meiqi.data.engine.functions.matlab;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-4-21
 * Time: 下午2:41
 * To change this template use File | Settings | File Templates.
 */
public class HttpUtil {
    public static String getJsonFromData(String uri, String json) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
        return getJsonFromData(uri, bais);
    }

    public static String getJsonFromData(String uri, InputStream json) throws Exception {
        URL url = new URL("http://"
                + "192.168.0.22"
                + ":"
                + "8083"
                + uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(3600 * 60 * 100);
        connection.setReadTimeout(3600 * 60 * 100);

        byte[] buff = new byte[1024];
        int count;
        while ((count = json.read(buff)) != -1) {
            connection.getOutputStream().write(buff, 0, count);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset();

        while ((count = connection.getInputStream().read(buff)) != -1) {
            baos.write(buff, 0, count);
        }

        String jsonFromData = new String(baos.toByteArray());
        return jsonFromData;
    }
}
