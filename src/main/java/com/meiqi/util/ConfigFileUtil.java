package com.meiqi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.meiqi.app.action.BaseAction;

public final class ConfigFileUtil
{
    
    private static final Logger LOGGER = Logger.getLogger(ConfigFileUtil.class);
    
    public static PropertiesConfiguration getPropertiesReader(String pathname)
    {
        try
        {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setEncoding("UTF-8");
            config.load(pathname);
            return config;
        }
        catch (Exception e)
        {
            LOGGER.error("get properties config file : " + pathname + " error!" + e.getMessage(), e);
        }
        return null;
    }
    
    public static Properties propertiesReader(String pathname)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(pathname), Charset.forName("UTF-8")));
            getExtraProperties(properties);
        }
        catch (Exception e)
        {
            LOGGER.error("get properties config file : " + pathname + " error!" + e.getMessage(), e);
        }
        return properties;
    }
    
    public static Properties appServicePropertiesReader(String pathname)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(pathname), Charset.forName("UTF-8")));
        }
        catch (Exception e)
        {
            LOGGER.error("get properties config file : " + pathname + " error!" + e.getMessage(), e);
        }
        return properties;
    }
    
   
	
	 /**
     * 
     * @Title: getExtraProperties
     * @Description:额外的配置
     * @param @return
     * @return Properties
     * @throws
     */
	public static Properties getExtraProperties(Properties properties) {
		String extraSysconfigPath = BaseAction.basePath + properties.get("extra_sysconfig_path");
		Properties extraProperties = loadProperties(extraSysconfigPath);
		properties.putAll(extraProperties);
		
		return properties;
	}
	/**
	 * 根据配置文件路径直接获取配置信息
	 * 
	 * @param extraConfigPath
	 * @return
	 */
	public static Properties getExtraProperties(String extraConfigPath) {
		return loadProperties(extraConfigPath);
	}
	
    private static Properties loadProperties(String extraSysconfigPath)
    {
        Properties extraProperties = new Properties();
        //String extraSysconfigPath = BaseAction.basePath + properties.get("extra_sysconfig_path");
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try
        {
			File file = new File(extraSysconfigPath);
			if (file.exists()) {
				inputStream = new FileInputStream(new File(extraSysconfigPath));
				inputStreamReader = new InputStreamReader(inputStream);
				extraProperties.load(inputStreamReader);
			} else {
				LOGGER.error("get properties config file : " + extraSysconfigPath + " NOT exists.");
			}
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("get properties config file : " + extraSysconfigPath + " error!" + e.getMessage(), e);
        }
        catch (IOException e)
        {
            LOGGER.error("get properties config file : " + extraSysconfigPath + " error!" + e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (null != inputStreamReader)
                {
                    inputStreamReader.close();
                }
                if (null != inputStream)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
        return extraProperties;
    }
}
