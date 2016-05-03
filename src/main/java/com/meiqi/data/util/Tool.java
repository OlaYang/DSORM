package com.meiqi.data.util;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.user.handler.service.ServiceReqInfo;

import org.jboss.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-6-5
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class Tool {
    public final static String systemVariablesRule = "系统变量表";

    public interface systemVariablesName {
        String RULE_CONVERT_PINYIN = "rule_convert_pinyin";
    }


    public static String getNativeIp(HttpRequest request) {
        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("http_client_ip");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            // 如果是多级代理，那么取第一个ip为客户ip
            if (ip != null) {
                String[] ips = ip.split(",");
                if (ips != null) {
                    if (ips.length > 0) {
                        ip = ips[0];
                    }
                }
            }
            return ip;
        } catch (Exception e) {
            return "获取ip异常";
        }
    }

    public static String getNativeIpByHttp(HttpRequest request) {
        try {
            String value = request.getHeader("HTTP_X_FORWARDED_FOR");
            String ip = "";
            if (value != null) {
                if (!"unknown".equalsIgnoreCase(value)) {
                    String ipFromNginx = value;
                    if (ipFromNginx != null) {
                        String[] ips = ipFromNginx.split(",");
                        if (ips != null) {
                            if (ips.length > 0) {
                                ip = ips[0];
                            }
                        }
                    }
                }
            }
            if ("".equals(ip)) {
                return null;
            } else {
                return ip;
            }
        } catch (Exception e) {
            return "获取ip异常";
        }

    }

    public static String getSystemVariables(String variablesName) throws Exception {
        ServiceReqInfo reqInfo = new ServiceReqInfo();
        reqInfo.setServiceName(systemVariablesRule);
        reqInfo.setFormat("json");
        reqInfo.setDbLang("zh");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("var_name", variablesName);
        reqInfo.setParam(param);
        return HttpClientUtil.postRuleService(JSON.toJSONString(reqInfo));
    }
}
