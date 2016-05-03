package com.meiqi.data.util;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: 
 * Date: 13-8-15
 * Time: 下午5:31
 */
public class ConfigUtil {
    private static final HashMap<String, String> config;
    private static boolean tlsEnabled = true;
    private static int config_cache_second = 1;
    private static InetSocketAddress crow = new InetSocketAddress("crow", 3200);
    private static int calTimeout = 3000;
    private static int file_threshold = 20971520;
    private static int db_row_max = 200000;
    private static int thread_count = 100;
    private static int waiting_threshold = 1000;
    private static boolean call_log = false;
    private static boolean debug_null_json = false;
    private static String solr_host = "www.lejj.com";
    private static int connect_timeout = 500;
    private static int read_timeout = 500;
    private static int project_center = 0; // 0为线上环境，1为项目中心环境
    private static String rule_url = "http://localhost:3032";

    static {
        config = new HashMap<String, String>();

        Properties properties = new Properties();
        try {
            properties.load(ConfigUtil.class.getClassLoader().getResourceAsStream("dataConfig.properties"));

            for (Map.Entry entry : properties.entrySet()) {
                config.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }

            try {
                config_cache_second = Integer.parseInt(config.get("config_cache_second"));
            } catch (Throwable t) {
                //
            }

            try {
                tlsEnabled = Integer.parseInt(config.get("tls_enabled")) == 1;
            } catch (Throwable t) {
                //
            }

            try {
                crow = new InetSocketAddress(config.get("crow_ip"),
                        Integer.valueOf(config.get("crow_port")));
            } catch (Throwable t) {
                //
            }

            try {
                calTimeout = Integer.valueOf(config.get("cal_timeout"));
            } catch (Throwable t) {
                //
            }

            try {
                file_threshold = Integer.valueOf(config.get("file_threshold"));
            } catch (Throwable t) {
                //
            }

            try {
                db_row_max = Integer.valueOf(config.get("db_row_max"));
            } catch (Throwable t) {
                //
            }

            try {
                thread_count = Integer.valueOf(config.get("thread_count"));
            } catch (Throwable t) {
                //
            }

            try {
                waiting_threshold = Integer.valueOf(config.get("waiting_threshold"));
            } catch (Throwable t) {
                //
            }

            try {
                call_log = Integer.valueOf(config.get("call_log")) == 1; //开启调用记录
            } catch (Throwable t) {
                //
            }
            try {
                debug_null_json = Integer.valueOf(config.get("debug_null_json")) == 1; //开启调用记录
            } catch (Throwable t) {
                //
            }

            try {
                solr_host = config.get("solr_host");
            } catch (Throwable t) {
                //
            }

            try {
                rule_url = config.get("rule_url");
            } catch (Throwable t) {
                //
            }

            try {
                connect_timeout = Integer.valueOf(config.get("connect_timeout"));
            } catch (Throwable t) {
                //
            }

            try {
                read_timeout = Integer.valueOf(config.get("read_timeout"));
            } catch (Throwable t) {
                //
            }

            try {
                project_center = Integer.valueOf(config.get("project_center"));
            } catch (Throwable t) {
                //
            }

        } catch (Throwable e) {
            LogUtil.error("load config error, " + e.getMessage());
        }

    }

    public static int getConfig_cache_second() {
        return config_cache_second;
    }

    public static boolean getTlsEnabled() {
        return tlsEnabled;
    }

    public static InetSocketAddress getCrowServerAddress() {
        return crow;
    }

    public static int getCalTimeout() {
        return calTimeout;
    }

    public static int getFile_threshold() {
        return file_threshold;
    }

    public static int getDb_row_max() {
        return db_row_max;
    }

    public static int getThread_count() {
        return thread_count;
    }

    public static int getWaiting_threshold() {
        return waiting_threshold;
    }

    public static boolean getCall_log() {
        return call_log;
    }

    public static boolean getDebug_null_json() {
        return debug_null_json;
    }

    public static String getSolr_host() {
        return solr_host;
    }

    public static int getConnect_timeout() {
        return connect_timeout;
    }

    public static int getRead_timeout() {
        return read_timeout;
    }

    public static int getProject_center(){
        return project_center;
    }

    public static String getRule_url(){
        return rule_url;
    }
}
