package com.meiqi.wechat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.util.SysConfig;
import com.meiqi.wechat.service.IWeChatAuthorizerService;
import com.meiqi.wechat.tools.ThirdWeChat;

@Service
public class WeChatAuthorizerServiceImpl implements IWeChatAuthorizerService{

	@Autowired
	private ThirdWeChat thirdWeChat;
	
	/**
	 * 获取三方平台授权地址url
	 */
	@Override
	public String getAuthorizerUrl() {
		String preAuthCode= thirdWeChat.getPreAuthCode();
		String appId=SysConfig.getValue("open_third_appId");
		String url="https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+appId+"&pre_auth_code="+preAuthCode+"&redirect_uri=http%3a%2f%2fwww.zxljj.com%2fDSORM%2fwx%2fopen%2fevent%2fauthorizeSuccess.do";
		return url;
	}

	//获取公众号信息
	@Override
	public JSONObject getPublicInfo(String authorizerAppid) {
		// TODO Auto-generated method stub
		String componentAccessToken=thirdWeChat.getComponentAccessToken();
		String componentAppid=SysConfig.getValue("open_third_appId");
		JSONObject temp=new JSONObject();
		temp.put("component_appid", componentAppid);
		temp.put("authorizer_appid", authorizerAppid);
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+componentAccessToken, temp.toJSONString(), 3000);
		temp=JSONObject.parseObject(result);
		return temp.getJSONObject("authorizer_info");
	}

}
