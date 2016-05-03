package com.meiqi.openservice.commons.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import Decoder.BASE64Decoder;

import com.meiqi.util.LogUtil;

public class UploadUtil {
    private static final String TAG      = "uploadFile";
    private static final int    TIME_OUT = 1000 * 30;
    private static final String CHARSET  = "utf-8";



    public static String uploadFile(String imgStr, String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";

        String response = null;
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", CHARSET);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            byte[] imgByte = DecodeBase64Img(imgStr);
            if (imgByte != null) {
                OutputStream outputSteam = conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + BOUNDARY + "\""
                        + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                dos.write(imgByte);
                
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int res = conn.getResponseCode();
                LogUtil.info(TAG + " response code:" + res);
                if (res == 200) {
                    InputStream inputStream = conn.getInputStream();
                    response = convertStreamToString(inputStream);
                    LogUtil.info(TAG + "response data: " + response);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            response = e.getMessage();
        }
        return response;
    }



    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



    public static byte[] DecodeBase64Img(String imgStr) {
        if (imgStr == null) {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            return b;
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }
}
