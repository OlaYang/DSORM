package com.meiqi.wechat.tools;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.openservice.commons.config.SysConfig;

/**
 * 微信开放平台
 *
 */
@Service
public class OpenWeChat {
	/**
	 * 获取开放平台微信扫描登录地址
	 * @return
	 */
	public String getWxLoginUrl(){
		String url="https://open.weixin.qq.com/connect/qrconnect?appid="+SysConfig.getValue("web_open_appid")+"&redirect_uri="+SysConfig.getValue("web_open_redirect")+"&response_type=code&scope=SCOPE#wechat_redirect";
		return url;
	}
	
	/**
	 * 获取开放平台的access_token 包括unionid
	 * @param code
	 * @return
	 */
	public JSONObject getOpenAccessToken(String code,String type){
		if(null==code||"".equals(code)){
			return null;
		}
		String url="";
		if(null!=type&&type.contains("aiuw")){
			 url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+SysConfig.getValue("aiuw_web_open_appid")+"&secret="+SysConfig.getValue("aiuw_web_open_secret")+"&code="+code+"&grant_type=authorization_code";
		}else{
			 url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+SysConfig.getValue("web_open_appid")+"&secret="+SysConfig.getValue("web_open_secret")+"&code="+code+"&grant_type=authorization_code";
		}
		String jsonString=HttpUtil.httpGet(url, null, 3000);
		return JSONObject.parseObject(jsonString);
	}
	
	/**
	 * 获取用户信息
	 * @param jsonObject
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public JSONObject getOpenUser(JSONObject jsonObject) throws UnsupportedEncodingException{
		String access_token=jsonObject.getString("access_token");
		if(null==access_token||"".equals(access_token)){
			return null;
		}
		String openid=jsonObject.getString("openid");
		if(null==openid||"".equals(openid)){
			return null;
		}
		String url="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
		String jsonString=HttpUtil.httpGet(url, null, 3000);
		
		jsonString =new String(jsonString.getBytes("ISO8859_1"),"utf-8");
		return JSONObject.parseObject(jsonString);
	}
}
