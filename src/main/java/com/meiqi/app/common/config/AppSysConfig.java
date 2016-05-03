package com.meiqi.app.common.config;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.dsmanager.util.SysConfig;

public class AppSysConfig {

    private static Properties  properties           = new Properties();
    // app service mapping
    private static Properties  appServiceProperties = new Properties();
    public static final Logger logger               = Logger.getLogger(AppSysConfig.class);

    static {
        readProperties();
    }



    public static String getValue(String key) {
        String result = properties.getProperty(key);
        if (StringUtils.isBlank(result)) {
            result = SysConfig.getValue(key);
        }
        return result;
    }



    public static void readProperties() {
        try {
            logger.info("SysConfig static init start.");
            properties = ConfigFileUtil.propertiesReader("appSysConfig.properties");
            appServiceProperties = ConfigFileUtil.appServicePropertiesReader("appActionMapping.properties");
        } catch (Exception e) {
            logger.error("fail to find config file sysConfig.properties", e);
        }
    }



    /**
     * 
     * @Title: clearAndReadProperties
     * @Description:清空proprties 并再次读入（用于配置了新的property）
     * @param
     * @return void
     * @throws
     */
    public static void clearAndReadProperties() {
        readProperties();
    }



    public static Properties getAppServiceProperties() {
        return appServiceProperties;
    }

}
