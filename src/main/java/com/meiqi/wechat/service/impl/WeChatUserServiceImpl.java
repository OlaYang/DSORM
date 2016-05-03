package com.meiqi.wechat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.wechat.service.IWeChatUserService;
import com.meiqi.wechat.tools.PublicWeChat;

@Service
public class WeChatUserServiceImpl implements IWeChatUserService{

	@Autowired
	private PublicWeChat publicWeChat;
	
	@Override
	public JSONObject setRemark(JSONObject userJson) {
		//调用公众方法获取access_token
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token="+ACCESS_TOKEN, userJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject getUser(String openId) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+ACCESS_TOKEN+"&openid="+openId+"&lang=zh_CN", null, 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

}
