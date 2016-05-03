package com.meiqi.wechat.service;

import com.alibaba.fastjson.JSONObject;

public interface IWeChatUserService {
	/**
	 * 设置用户备注名
	 * @param userJson
	 * @return
	 */
	public JSONObject setRemark(JSONObject userJson);
	
	/**
	 * 根据openid获取用户信息
	 * @param openId
	 * @return
	 */
	public JSONObject getUser(String openId);
}
