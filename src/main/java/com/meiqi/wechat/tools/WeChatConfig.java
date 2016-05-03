package com.meiqi.wechat.tools;

public interface WeChatConfig {
	//三方平台
	
	//缓存的微信第三方 公众平台第三方AccessToken
	public String weChatThirdComponentAccessToken="wechat_third_component_access_token";
	//微信 公众平台第三方 10分钟推送的ticket
	public String weChatThirdTicket="wechat_third_ticket";
	
	//公众平台
	
	//公众平台的AccessToken
	public String publicAccessToken="public_access_token";
	//公众平台的 js ticket
	public String publicJsApiTicket="public_js_api_ticket";
}
