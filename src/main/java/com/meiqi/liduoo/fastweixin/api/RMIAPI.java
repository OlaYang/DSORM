package com.meiqi.liduoo.fastweixin.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.fastweixin.api.config.WxConfig;
import com.meiqi.liduoo.fastweixin.api.config.WxProperty;
import com.meiqi.liduoo.fastweixin.util.HttpBaseKit;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 远程DSROM调用API
 *
 * @author FrankGui
 */
public class RMIAPI {

	private static final Logger LogUtil = LoggerFactory.getLogger(RMIAPI.class);

	/**
	 * 获取远程规则函数数据
	 * 
	 * @param serviceName
	 *            规则函数名称
	 * @param param
	 *            调用参数
	 * @return JSON格式执行结果
	 */
	public static String getData(String serviceName, Map<String, Object> param) {

		// "action":"getAction","method":"get","param":{"serviceName":"T_HSV_1QuickMark","param":{"ad_code":"QuickMark_lejj_pc"},"return":"header_ewm","needAll":1}},走接口
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("serviceName", serviceName);
		p.put("param", param);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action", "getAction");
		params.put("method", "get");
		params.put("param", p);
		params.put("needAll", "1");
		params.put("type", "1");

		String url = LdConfigUtil.getConfig("remote_dsorm_url");// +
																// "/DSORM/openService.do";
		String jsonString = JSON.toJSONString(params);
		LogUtil.info("Call remote DSROM:" + url + ", param=" + jsonString);
		String r = HttpBaseKit.post(url, jsonString);
		return r;
	}

	/**
	 * 根据微信渠道ID获取相关配置
	 * 
	 * @param wxId
	 *            微信ID
	 * @return
	 */
	public static Map<String, String> getWxConfig(int wxId) {
		String cacheKey = "WXCONFIG_WXID_" + wxId;
		Map<String, String> configMap = (Map) CacheUtils.getCache(cacheKey);
		if (configMap != null && StrUtil.isNotBlank(configMap.get("WECHAT_APPID"))
				&& StrUtil.isNotBlank(configMap.get("WECHAT_APPID"))) {
			return configMap;
		}
		// "action":"getAction","method":"get","param":{"serviceName":"T_HSV_1QuickMark","param":{"ad_code":"QuickMark_lejj_pc"},"return":"header_ewm","needAll":1}},走接口
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("linkid", wxId);
		param.put("linktype", 6);
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("serviceName", "LDO_HSV1_CommonProperties");
		p.put("param", param);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action", "getAction");
		params.put("method", "get");
		params.put("param", p);
		params.put("needAll", "1");
		params.put("type", "1");

		String url = LdConfigUtil.getConfig("wxconfig_dsorm_url");
		String jsonString = JSON.toJSONString(params);
		LogUtil.info("Call wexconfig DSROM:" + url + ", param=" + jsonString);
		String r = HttpBaseKit.post(url, jsonString);

		WxConfig config = JSONUtil.toBean(r, WxConfig.class);
		if (!"0".equals(config.getCode())) {
			throw new IllegalStateException("无法获取微信配置：" + config.getDescription());
		}
		configMap = new HashMap<String, String>();
		for (WxProperty prop : config.getRows()) {
			configMap.put(prop.getName(), prop.getValue());
		}
		CacheUtils.putCache(cacheKey, configMap, 3600*1000L);
		
		return configMap;
	}
}
