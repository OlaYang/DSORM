package com.meiqi.openservice.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meilele.datalayer.common.data.builder.BeanBuilder;

public class DataUtil {

    public static <T> T parse(String content, Class<T> clazz) {
        if (StringUtils.isEmpty(content)) {
            content = "{}";
        }

        try {
            return JSON.parseObject(content, clazz);
        } catch (Throwable e) {
            throw new RuntimeException("JSON解析错误, " + e.getMessage());
        }
    }



    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> List<T> parse(List mapList, Class<T> clazz) {
        JSONArray json = JSONArray.fromObject(mapList);
        List beanList = new ArrayList();
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                beanList.add(BeanBuilder.buildBean(clazz.newInstance(), obj));
            } catch (Exception e) {
                throw new RuntimeException("JSON解析错误, " + e.getMessage());
            }
        }
        return beanList;

    }



    public static Map<String, Object> parse(String content) {
        if (StringUtils.isEmpty(content)) {
            content = "{}";
        }

        try {
            return JSON.parseObject(content);
        } catch (Throwable e) {
            throw new RuntimeException("JSON解析错误, " + e.getMessage());
        }
    }



    /**
     * 
     * Object to json String
     *
     * @param object
     * @return String
     */
    public static String toJSONString(Object object) {
        if (null == object) {
            object = new Object();
        }

        try {
            return JSONObject.toJSONString(object);
        } catch (Throwable e) {
            throw new RuntimeException("JSON解析错误, " + e.getMessage());
        }
    }



    /**
     * 
     * Object to json
     *
     * @param object
     * @return
     */
    public static Object toJSON(Object object) {
        if (null == object) {
            object = new Object();
        }

        try {
            return JSONObject.toJSON(object);
        } catch (Throwable e) {
            throw new RuntimeException("JSON解析错误, " + e.getMessage());
        }
    }



    /**
     * @description: 获取GET请求传入的参数
     * @param request
     *            ，content
     * @return:String
     */
    public static String getNoKeyParamValue(HttpServletRequest request, String content) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Iterator<Entry<String, String[]>> iterator = parameterMap.entrySet().iterator(); iterator.hasNext();) {
            Entry<String, String[]> entry = iterator.next();
            // System.out.println(entry.getKey()+entry.getValue()[0]);
            if (entry.getValue().length == 1 && "".equalsIgnoreCase(entry.getValue()[0])) {
                content = entry.getKey();
                break;
            }
            if (!entry.getKey().endsWith("}") && !"".equalsIgnoreCase(entry.getValue()[0])
                    && entry.getValue()[0].startsWith("\"")) {// 处理{"op":"="}串
                content = entry.getKey() + "=" + entry.getValue()[0];
                break;
            }
        }
        return content;
    }



    /**
     * 去除php请求报文尾部php程序加上的=号
     * 
     * @param content
     * @return
     */
    public static String clearEq(String content) {
        if (content.endsWith("=")) {
            content = content.substring(0, content.length() - 1);
        }
        return content;
    }



    public static String inputStream2String(InputStream inputStream) throws IOException {
        byte[] buff = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int count;
        while ((count = inputStream.read(buff)) != -1) {
            baos.write(buff, 0, count);
        }
        String result = new String(baos.toByteArray());
        baos.close();
        return result;
    }

}
