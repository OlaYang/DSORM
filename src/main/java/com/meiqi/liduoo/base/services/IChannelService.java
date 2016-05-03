package com.meiqi.liduoo.base.services;

import java.util.Map;

import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;

public interface IChannelService {
	Map<String, String> getChannelProperties(int channelId);

	String getChannelProperty(int channelId, String propName);

	/**
	 * @param channelId
	 * @return
	 */
	ApiConfig initApiConfig(int channelId);

	ApiConfig initApiConfig(String appId, String appSecret);

	/**
	 * @param isvId
	 * @param property
	 * @return
	 */
	String getIsvSetting(int isvId, String property);

}
