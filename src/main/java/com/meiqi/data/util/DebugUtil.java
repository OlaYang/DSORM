package com.meiqi.data.util;

import com.meiqi.data.engine.RengineException;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-2
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class DebugUtil {
    private static final boolean debug_null_json = ConfigUtil.getDebug_null_json();

    public static void debugNull(String serviceName, String content, String desc) throws RengineException {
        if (debug_null_json) {
            if (serviceName == null || "".equals(serviceName.trim())) {
                if (desc != null) {
                    throw new RengineException(serviceName, "数据源未找到;" + desc + ",content:" + content);
                }else {
                    throw new RengineException(serviceName, "数据源未找到;content:" + content);
                }
            }
        }
    }

    public static void debugJSON(String message, String content) throws RengineException {
        if (debug_null_json) {
            throw new RengineException(null, "JSON解析错误, " + message + ";content:" + content);
        }
    }
}
