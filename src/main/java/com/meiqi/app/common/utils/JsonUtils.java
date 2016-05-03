package com.meiqi.app.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.XML;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meiqi.app.pojo.ResponseData;

/**
 * 
 * @ClassName: JsonObjectUtils
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 上午10:29:51
 *
 */
public class JsonUtils {
    /**
     * 
     * 过滤多余的字段
     *
     * @param propertyList
     * @return
     */
    public static PropertyFilter getNullPropertyFilter(final List<String> propertyList) {
        PropertyFilter propertyFilter = new PropertyFilter() {
            @Override
            public boolean apply(Object paramObject1, String paramString, Object paramObject2) {
                boolean reault = true;
                if (!CollectionsUtils.isNull(propertyList) && !propertyList.contains(paramString)) {
                    reault = false;
                } else if (null == paramObject1 || StringUtils.isBlank(paramObject1.toString())) {
                    reault = false;
                } else {
                    String objectType = paramObject1.getClass().toString().toLowerCase();
                    if (objectType.contains("double") && 0 == (Double) paramObject1) {
                        reault = false;
                    }
                }
                return reault;
            }
        };
        return propertyFilter;
    }



    /**
     * 
     * @Title: objectFormatToString
     * @Description:生成json对象
     * @param object
     * @param @return
     * @return String
     * @throws
     */
    public static String objectFormatToString(Object object) {
        String result = null;
        if (null != object) {
            result = JSONObject.toJSONString(object, getNullPropertyFilter(null),
                    SerializerFeature.DisableCircularReferenceDetect);
        }
        return result;
    }



    /**
     * 
     * @Title: objectFormatToString
     * @Description:可以 指定返回的属性 来生成json对象
     * @param @param object
     * @param @param propertyList
     * @param @return
     * @return String
     * @throws
     */
    public static String objectFormatToString(Object object, List<String> propertyList) {
        String result = null;
        if (null != object) {
            setProperty(propertyList);
            result = JSONObject.toJSONString(object, getNullPropertyFilter(propertyList),
                    SerializerFeature.DisableCircularReferenceDetect);
        }
        return result;
    }



    /**
     * 
     * @Title: listFormatToString
     * @Description:生成json对象
     * @param @param list
     * @param @return
     * @return String
     * @throws
     */
    public static String listFormatToString(List list) {
        String result = null;
        if (!CollectionsUtils.isNull(list)) {
            result = JSONArray.toJSONString(list, getNullPropertyFilter(null),
                    SerializerFeature.DisableCircularReferenceDetect);
        }
        return result;

    }



    /**
     * 
     * @Title: listFormatToString
     * @Description:可以 指定返回的属性 来生成json对象
     * @param @param list
     * @param @param porpertyList
     * @param @return
     * @return String
     * @throws
     */
    public static String listFormatToString(List list, List<String> propertyList) {
        String result = null;
        if (!CollectionsUtils.isNull(list)) {
            result = JSONArray.toJSONString(list, getNullPropertyFilter(propertyList),
                    SerializerFeature.DisableCircularReferenceDetect);
        }
        return result;
    }



    public static Map<String, Object> getValueToMap(Object obj, List<String> propertyList) {
        if (null == obj) {
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        // 获取f对象对应类中的所有属性域
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            if (!propertyList.contains(varName)) {
                continue;
            }
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o = fields[i].get(obj);
                if (o != null) {
                    map.put(varName, o);
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }



    public static String compress(String str) {
        String result = null;
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            // 将 b.length 个字节写入此输出流
            gzip.write(str.getBytes("UTF-8"));
            gzip.close();
            result = out.toString("UTF-8");
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    /**
     * 字符串的解压
     * 
     * @param str
     *            对字符串解压
     * @return 返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n = 0;
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("GBK");
    }



    /**
     * 
     * @Title: readJsonFile
     * @Description:read json file
     * @param @param filePath
     * @param @return
     * @return String
     * @throws
     */
    public static String readJsonFile(String filePath) {
        StringBuffer jsonStringBuffer = new StringBuffer(100000);
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                jsonStringBuffer.append(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileReader) {
                    fileReader.close();
                }
                if (null != reader) {
                    reader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return jsonStringBuffer.toString();
    }



    /**
     * 
     * @Title: getErrorJson
     * @Description:错误response message
     * @param @param message
     * @param @param errors
     * @param @return
     * @return String
     * @throws
     */
    public static String getErrorJson(String message, List<String> errors) {
        ResponseData repMessage = new ResponseData(1, message, errors, null);
        return objectFormatToString(repMessage);
    }



    /**
     * 获取授权失败的error message
     * 
     * @return
     */
    public static String getAuthorizationErrorJson() {
        return getErrorJson("授权失败!", null);
    }



    /**
     * 获取请求频繁的error message
     * 
     * @return
     */
    public static String getFrequentRequestErrorJson() {
        return getErrorJson("请求频繁!", null);
    }



    /**
     * 
     * @Title: getSuccessJson
     * @Description:成功response message
     * @param @return
     * @return String
     * @throws
     */
    public static String getSuccessJson(Object object) {
        ResponseData repMessage = new ResponseData(200, "success", null, object);
        return objectFormatToString(repMessage);
    }



    /**
     * 
     * @Title: getSuccessJson
     * @Description:成功response message
     * @param @return
     * @return String
     * @throws
     */
    public static String getSuccessJson(Object object, List<String> propertyList) {
        ResponseData repMessage = new ResponseData(200, "success", null, object);
        return objectFormatToString(repMessage, propertyList);
    }



    /**
     * 
     * @Title: setProperty
     * @Description:设置responseData 属性不被过滤
     * @param @param propertyList
     * @return void
     * @throws
     */
    private static void setProperty(List<String> propertyList) {
        if (!CollectionsUtils.isNull(propertyList)) {
            propertyList.add("statusCode");
            propertyList.add("message");
            propertyList.add("errors");
            propertyList.add("data");
        }
    }



    /**
     * 
     * @Title: formartJsonString
     * @Description:格式化json
     * @param @param json
     * @param @return
     * @return String
     * @throws
     */
    public static String formartJsonString(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (json.contains(",{}")) {
            json = json.replaceAll("\\,\\{\\}", "");
        }
        if (json.contains(":{}")) {
            json = json.replaceAll("\\:\\{\\}", ":\"\"");
        }

        return json;
    }



    /**
     * 
     * @Title: xmlStringToJson
     * @Description:xml 字符串转成 json
     * @param @param xmlString
     * @param @return
     * @return String
     * @throws
     */
    public static String xmlStringToJson(String xmlString) {
        if (StringUtils.isBlank(xmlString)) {
            return "";
        }
        String json = "";
        try {
            json = XML.toJSONObject(xmlString).toString();
            json = formartJsonString(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
