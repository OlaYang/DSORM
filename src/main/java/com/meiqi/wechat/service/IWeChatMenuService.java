package com.meiqi.wechat.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信自定义菜单功能
 * @author meiqidr
 *
 */
public interface IWeChatMenuService {
	/**
	 * 创建自定义菜单
	 * @param menuJson
	 */
	public JSONObject createMenu(JSONObject menuJson);
	
	/**
	 * 删除自定义菜单
	 * @return
	 */
	public JSONObject deleteMenu();
	
	/**
	 * 获取当前的自定义菜单
	 * @return
	 */
	public JSONObject getMenu();
}
