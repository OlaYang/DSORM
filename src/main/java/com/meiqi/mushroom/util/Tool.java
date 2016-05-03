package com.meiqi.mushroom.util;

import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * 获取客户ip工具类
 * @author 
 * @date 2015年6月18日
 */
public class Tool {
	
	/**
	 * 获取请求ip，
	 * @param request 接收到的request
	 * @return 获取到的ip地址
	 */
    public static String getNativeIp(HttpRequest request) {
        try {
        	//如果服务器使用了负载均衡则从负载均衡封装的参数获取客户ip
            String ip = request.getHeader("X-Forwarded-For");
            //如果没使用代理获取客户ip
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("http_client_ip");
            }
          //如果以上无法获取 获取tomcat封装了参数获取请求ip
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
          //如果以上无法获取 获取weblogic封装了参数获取请求ip
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

    
    /**
	 * 获取ip 当客户使用透明代理情况下获取到客户真实ip 当客户使用普通代理时，获取代理服务器ip 当客户使用欺诈性代理时，获取ip为随机不确定
	 * 
	 * @param request
	 * @return
	 */
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
}
