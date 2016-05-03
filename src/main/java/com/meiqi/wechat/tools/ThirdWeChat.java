package com.meiqi.wechat.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.util.CacheUtil;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.util.SysConfig;

/**
 * 微信开放平台 公众号三方平台
 *
 */
@Service
public class ThirdWeChat {
	
	@Autowired
	private IMemcacheAction memcacheAction;
	
	//获取 开放平台 公众号三方平台的accesstoken
	public String getComponentAccessToken(){
		//从缓存中取第三方component_access_token
		Object componentAccessTokenObject=memcacheAction.getCache(WeChatConfig.weChatThirdComponentAccessToken);
		//取不到access_token则从微信去拿
		if(null==componentAccessTokenObject){
			String ticket=(String)memcacheAction.getCache(WeChatConfig.weChatThirdTicket);
			JSONObject postData=new JSONObject();
			postData.put("component_appid", SysConfig.getValue("open_third_appId"));
			postData.put("component_appsecret", SysConfig.getValue("open_third_appSecret"));
			postData.put("component_verify_ticket", ticket);
			String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/component/api_component_token", postData.toJSONString(), 3000);
			JSONObject json=JSONObject.parseObject(result);
			String accessToken=json.get("component_access_token").toString().trim();
			String expiresIn=json.get("expires_in").toString().trim();
			if("".equals(accessToken)||"".equals(expiresIn)){
				return "";
			}
			//设置access_token到内存中，并且设置过期时间，比微信提供的7200提前200秒失效，保证每次拿到的access_token在微信中都是可用的
			memcacheAction.putCache(WeChatConfig.weChatThirdComponentAccessToken, accessToken, (Long.parseLong(expiresIn)-200)*1000);
			return accessToken;
		}else{
			return componentAccessTokenObject.toString();
		}
	}
	
	
	//获取三方平台预授权吗
	public String getPreAuthCode(){
		String appId=SysConfig.getValue("open_third_appId");
		String component_access_token=getComponentAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="+component_access_token, "{\"component_appid\":\""+appId+"\"}", 3000);
		JSONObject json=JSONObject.parseObject(result);
		return json.getString("pre_auth_code");
	}
	
	
	/**
	 * 使用授权码换区公众号授权信息，拿到accessToken
	 * @return
	 */
	public String getPublicAuth(String authCode){
		String component_access_toke=getComponentAccessToken();
		JSONObject authJson=new JSONObject();
		authJson.put("component_appid", SysConfig.getValue("open_third_appId"));
		authJson.put("authorization_code", authCode);
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+component_access_toke, authJson.toJSONString(), 3000);
		return authCode;
	}
	
}
