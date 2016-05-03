package com.meiqi.liduoo.base.utils;

import java.util.Properties;

import com.meiqi.util.ConfigFileUtil;
import com.meiqi.util.LogUtil;

/**
 * Liduoo专用配置项目
 * 
 * @author FrankGui
 * @date 2015年12月16日 下午19:13:31
 */
public class LdConfigUtil {
	private static Properties properties = new Properties();
	private static String upload_server; // 文件上载服务器URL
	private static String image_root_url; // 图片服务器URL（函数中无法自动获取）
	private static String web_root_url; // WEB服务器URL（函数中无法自动获取）
	private static String dsorm_setver_ip; // 服务器IP，微信支付时需要指定
	private static String wx_auth_callback;

	private static String default_app_id;

	private static String default_app_secret;

	static {
		try {
			properties.load(LdConfigUtil.class.getClassLoader().getResourceAsStream("liduoo_config.properties"));
			if (properties.getProperty("extra_sysconfig_path") != null) {
				Properties props = ConfigFileUtil.getExtraProperties(properties.getProperty("extra_sysconfig_path"));
				properties.putAll(props);
			}

			wx_auth_callback = properties.getProperty("wx_auth_callback");
			upload_server = properties.getProperty("upload_server");
			image_root_url = properties.getProperty("image_root_url");
			web_root_url = properties.getProperty("web_root_url");
			dsorm_setver_ip = properties.getProperty("dsorm_setver_ip");
			default_app_id = properties.getProperty("default_app_id");
			default_app_secret = properties.getProperty("default_app_secret");
			LogUtil.info("========LIST LIDUOO CONFIGS============BEGIN===========");
			for (Object key : properties.keySet()) {
				LogUtil.info(key + "=" + properties.getProperty((String) key));
			}
			LogUtil.info("========LIST LIDUOO CONFIGS============END===========");
		} catch (Throwable e) {
			LogUtil.error("load config error, " + e.getMessage());
		}

	}

	public static String getConfig(String key) {
		return properties.getProperty(key);
	}

	/**
	 * @return the wx_auth_callback
	 */
	public static String getWx_auth_callback() {
		return wx_auth_callback;
	}

	/**
	 * @return the uplaod_server
	 */
	public static String getUpload_server() {
		return upload_server;
	}

	/**
	 * @return the image_root_url
	 */
	public static String getImage_root_url() {
		return image_root_url;
	}

	/**
	 * @return the web_root_url
	 */
	public static String getWeb_root_url() {
		return web_root_url;
	}

	/**
	 * @return the dsorm_setver_ip
	 */
	public static String getDsorm_setver_ip() {
		return dsorm_setver_ip;
	}

	/**
	 * @return the default_app_id
	 */
	public static String getDefault_app_id() {
		return default_app_id;
	}

	/**
	 * @return the default_app_secret
	 */
	public static String getDefault_app_secret() {
		return default_app_secret;
	}

}
