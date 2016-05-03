package com.meiqi.dsmanager.util;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public final class ConfigFileUtil {

    private static final Logger LOGGER = Logger.getLogger(ConfigFileUtil.class);



    public static PropertiesConfiguration getPropertiesReader(String pathname) {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setEncoding("UTF-8");
            config.load(pathname);
            return config;
        } catch (Exception e) {
            LOGGER.error("get properties config file : " + pathname + " error!" + e.getMessage(), e);
        }
        return null;
    }



    public static Properties propertiesReader(String pathname) {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(pathname), Charset.forName("UTF-8")));
        } catch (Exception e) {
            LOGGER.error("get properties config file : " + pathname + " error!" + e.getMessage(), e);
        }
        return properties;
    }
}
