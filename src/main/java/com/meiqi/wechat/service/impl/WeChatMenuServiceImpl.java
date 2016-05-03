package com.meiqi.wechat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.wechat.service.IWeChatMenuService;
import com.meiqi.wechat.tools.PublicWeChat;
/**
 * 微信自定义菜单
 * @author duanran
 *
 */
@Service
public class WeChatMenuServiceImpl implements IWeChatMenuService{
	@Autowired
	private PublicWeChat publicWeChat;
	
	@Override
	public JSONObject createMenu(JSONObject menuJson) {
		// TODO Auto-generated method stub
		//调用公众方法获取access_token
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		//调用微信http接口创建自定义目录
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+ACCESS_TOKEN, menuJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject deleteMenu() {
		// TODO Auto-generated method stub
		//调用公众方法获取access_token
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token="+ACCESS_TOKEN, null, 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject getMenu() {
		// TODO Auto-generated method stub
		
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/menu/get?access_token="+ACCESS_TOKEN, null, 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

}
