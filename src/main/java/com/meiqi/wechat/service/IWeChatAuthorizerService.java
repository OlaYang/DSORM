package com.meiqi.wechat.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 三方平台管理公众平台相关服务，
 * @author meiqidr
 *
 */

public interface IWeChatAuthorizerService {
	/**
	 * 获取公众平台授权地址
	 * @return
	 */
	public String getAuthorizerUrl();
	
	public JSONObject getPublicInfo(String authorizerAppid);
}
