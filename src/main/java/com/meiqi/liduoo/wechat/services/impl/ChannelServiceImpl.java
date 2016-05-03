package com.meiqi.liduoo.wechat.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.base.services.ILiduooDataService;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;

/**
 * 渠道处理相关服务对象
 * 
 * @author FrankGui
 * @date 2015年12月5日 上午10:49:24
 */
@Service
public class ChannelServiceImpl implements IChannelService {
	@Autowired
	private ILiduooDataService dataService;

	@Autowired
	private IMemcacheAction memcacheService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, String> getChannelProperties(int channelId) {
		// 获取渠道配置，按照WECHAT_APPID->xxx,WECHAT_APPSECRET->xxx这样的方式返回
		String key = "CHANNEL_PROPS_" + channelId;
		Map<String, String> retMap = null;
		Object obj = memcacheService.getCache(key);
		if (obj != null) {
			retMap = (Map<String, String>) obj;
		}
		if (retMap != null && retMap.size() > 0) {
			return retMap;
		}

		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName("LDO_HSV1_ChannelInfo");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("channelid", channelId);
		param.put("type", "4");// 4代表dataPage登录请求
		serviceInfo.setParam(param);
		retMap = new HashMap<String, String>();
		Map channelInfo = dataService.getOneRow(serviceInfo);

		retMap.put("CHANNEL_ISV_ID", (String) channelInfo.get("fisvid"));
		retMap.put("CHANNEL_ID", (String) channelInfo.get("fchannelid"));
		retMap.put("CHANNEL_NAME", (String) channelInfo.get("fchannelname"));

		ServiceReqInfo serviceInfo2 = new ServiceReqInfo();
		serviceInfo2.setServiceName("LDO_HSV1_CommonProperties");
		Map<String, Object> param2 = new HashMap<String, Object>();
		param2.put("linkid", channelId);
		param2.put("linktype", 6);// 6代表渠道
		serviceInfo2.setParam(param2);
		List<Map> propList = dataService.getListData(serviceInfo2);
		for (Map map : propList) {
			if ("text".equalsIgnoreCase((String) map.get("fpropertytype"))) {
				retMap.put((String) map.get("fproperty"), (String) map.get("flongvalue"));
			} else {
				retMap.put((String) map.get("fproperty"), (String) map.get("fvalue"));
			}
		}
		memcacheService.putCache(key, retMap);
		return retMap;
	}

	@Override
	public String getChannelProperty(int channelId, String propName) {
		Map<String, String> map = getChannelProperties(channelId);
		LogUtil.info("Channel Properties: " + map + "");
		if (map == null || !map.containsKey(propName)) {
			return null;
		}
		return map.get(propName);
	}

	@Override
	public ApiConfig initApiConfig(int channelId) {
		Map<String, String> channelMap = getChannelProperties(channelId);

		String appId = channelMap.get("WECHAT_APPID");
		String appSecret = channelMap.get("WECHAT_APPSECRET");

		return initApiConfig(appId, appSecret);
	}

	@Override
	public ApiConfig initApiConfig(String appId, String appSecret) {
		return ApiConfig.getInstance(appId, appSecret);
	}
	@Override
	public String getIsvSetting(int isvId, String property) {
		// 获取渠道配置，按照WECHAT_APPID->xxx,WECHAT_APPSECRET->xxx这样的方式返回
		String key = "ISV_SETTINGS_" + isvId + "_" + property;
		Object obj = memcacheService.getCache(key);
		if (obj != null) {
			return (String) obj;
		}

		ServiceReqInfo serviceInfo = new ServiceReqInfo();
		serviceInfo.setServiceName("LDO_BUV1_tIsvSettings");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("isv_id", isvId);
		param.put("property", property);
		param.put("type", "4");// 4代表dataPage登录请求
		serviceInfo.setParam(param);
		Map result = dataService.getOneRow(serviceInfo);
		if (result!=null && result.get("fvalue") != null) {
			memcacheService.putCache(key, result.get("fvalue"));
			return (String) result.get("fvalue");
		}
		return null;
	}
}
