package com.meiqi.liduoo.fastweixin.util;

import static com.meiqi.liduoo.fastweixin.util.BeanUtil.requireNonNull;
import static com.meiqi.liduoo.fastweixin.util.StrUtil.isBlank;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meiqi.data.engine.excel.StringPool;

/**
 * json操作工具类，基于fastjson封装
 *
 * @author peiyu
 */
public final class JSONUtil {

    /**
     * 默认json格式化方式
     */
    public static final SerializerFeature[] DEFAULT_FORMAT = {SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteEnumUsingToString,
            SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
            SerializerFeature.SortField, SerializerFeature.PrettyFormat};

    private JSONUtil() {
    }

    /**
     * 从json获取指定key的字符串
     *
     * @param json json字符串
     * @param key  字符串的key
     * @return 指定key的值
     */
    public static Object getStringFromJSONObject(final String json, final String key) {
        requireNonNull(json, "json is null");
        return JSON.parseObject(json).getString(key);
    }

    /**
     * 将字符串转换成JSON字符串
     *
     * @param jsonString json字符串
     * @return 转换成的json对象
     */
    public static JSONObject getJSONFromString(final String jsonString) {
        if (isBlank(jsonString)) {
            return new JSONObject();
        }
        return JSON.parseObject(jsonString);
    }

    /**
     * 将json字符串，转换成指定java bean
     *
     * @param jsonStr   json串对象
     * @param beanClass 指定的bean
     * @param <T>       任意bean的类型
     * @return 转换后的java bean对象
     */
    public static <T> T toBean(String jsonStr, Class<T> beanClass) {
        requireNonNull(jsonStr, "jsonStr is null");
        JSONObject jo = JSON.parseObject(jsonStr);
        jo.put(JSON.DEFAULT_TYPE_KEY, beanClass.getName());
        return JSON.parseObject(jo.toJSONString(), beanClass);
    }

    /**
     * @param obj 需要转换的java bean
     * @param <T> 入参对象类型泛型
     * @return 对应的json字符串
     */
    public static <T> String toJson(T obj) {
        requireNonNull(obj, "obj is null");
        return JSON.toJSONString(obj, DEFAULT_FORMAT);
    }

    /**
     * 通过Map生成一个json字符串
     *
     * @param map 需要转换的map
     * @return json串
     */
    public static String toJson(Map<String, Object> map) {
        requireNonNull(map, "map is null");
        return JSON.toJSONString(map, DEFAULT_FORMAT);
    }

    /**
     * 美化传入的json,使得该json字符串容易查看
     *
     * @param jsonString 需要处理的json串
     * @return 美化后的json串
     */
    public static String prettyFormatJson(String jsonString) {
        requireNonNull(jsonString, "jsonString is null");
        return JSON.toJSONString(getJSONFromString(jsonString), true);
    }

    /**
     * 将传入的json字符串转换成Map
     *
     * @param jsonString 需要处理的json串
     * @return 对应的map
     */
    public static Map<String, Object> toMap(String jsonString) {
        requireNonNull(jsonString, "jsonString is null");
        return getJSONFromString(jsonString);
    }


	/**
	 * 将xmlStr转为 w3c Document
	 * 
	 * @param xmlStr
	 * @return
	 */
	public static Object get(String json, String... keys) {
		Object obj = null;
		try {
			obj = JSON.parseObject(json, Map.class);
		} catch (JSONException e) {
			try {
				obj = JSON.parseObject(json, List.class);
			} catch (JSONException e1) {
				// throw new RengineException(calInfo.getServiceName(), NAME +
				// "传入的json不是Map结构或者List结构");
				return StringPool.EMPTY;
			}
		}

		Object result = obj;
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];// DataUtil.getStringValue(args[i]);
			if (result instanceof Map) {
				result = ((Map) result).get(key);
				if (i == keys.length - 1) {
					if (result instanceof Map || result instanceof List) {
						return JSON.toJSONString(result);
					} else {
						return result;
					}
				} else {
					if (result instanceof List) {
						if (((List) result).size() == 1) {
							result = ((List) result).get(0);
						} else {
							return StringPool.EMPTY;
						}
					}
				}
			} else if (result instanceof List) {
				if (((List) result).size() == 1) {
					result = JSON.toJSONString(((List) result).get(0));
				} else {
					result = JSON.toJSONString(result);
				}

			}
		}

		return StringPool.EMPTY;
	}

	public static String set(String json, Object value, String... keys) {
		Object obj = null;
		try {
			obj = JSON.parseObject(json, Map.class);
		} catch (JSONException e) {
			try {
				obj = JSON.parseObject(json, List.class);
			} catch (JSONException e1) {
				// throw new RengineException(calInfo.getServiceName(), NAME +
				// "传入的json不是Map结构或者List结构");
				return json;
			}
		}

		Object result = obj;
		for (int i = 0; i < keys.length - 1; i++) {
			String key = keys[i];// DataUtil.getStringValue(args[i]);
			if (result instanceof Map) {
				result = ((Map) result).get(key);
				if (i == keys.length - 2) {
					if (result instanceof Map) {
						((Map) result).put(keys[i + 1], value);
						break;
					}
				}
			} else {
				if (result instanceof List) {
					if (((List) result).size() > 0) {
						result = ((List) result).get(0);
					} else {
						break;
					}
				} else if (result instanceof JSONObject) {
					result = ((JSONObject) result).get(key);
				}
			}
		}

		return JSON.toJSONString(obj);
	}
}
