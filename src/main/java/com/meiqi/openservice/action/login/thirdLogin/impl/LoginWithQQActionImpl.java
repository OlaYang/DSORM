/**   
* @Title: LoginWith3rdAction.java 
* @Package com.meiqi.openservice.action.login.thirdLogin 
* @Description: TODO(用一句话描述该文件做什么) 
* @author wanghuanwei
* @date 2015年7月9日  下午14:52:08 
* @version V1.0
* copyright（c） meiqi.com   
*/
package com.meiqi.openservice.action.login.thirdLogin.impl;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.qq.connect.utils.QQConnectConfig;

/**
 * 通过qq登录 工具类，
 * */

@Service
public class LoginWithQQActionImpl {
	/**
	 * 获取用户信息
	 * @param jsonObject
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public JSONObject getQQUser(String accessTokenString,String openId,String type) throws UnsupportedEncodingException{
		String url="";
		if(null!=type&&type.contains("aiuw")){
			url="https://graph.qq.com/user/get_user_info?oauth_consumer_key="+QQConnectConfig.getValue("aiuw_app_ID")+"&access_token="+accessTokenString+"&openid="+openId+"&format=json";
		}else{
			url="https://graph.qq.com/user/get_user_info?oauth_consumer_key="+QQConnectConfig.getValue("youjiagou_app_ID")+"&access_token="+accessTokenString+"&openid="+openId+"&format=json";
		}
		String jsonString=HttpUtil.httpGet(url, null, 3000);
		return JSONObject.parseObject(jsonString);
	}
	
	
	/**
	 * 获取openid
	 * @param code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public JSONObject getOpenId(String accessToken) throws UnsupportedEncodingException{
		String url="https://graph.qq.com/oauth2.0/me?access_token="+accessToken;
		String jsonString=HttpUtil.httpGet(url, null, 3000);
		jsonString=jsonString.substring(9, jsonString.length()-3).trim();
		return JSONObject.parseObject(jsonString);
	}
	
	/**
	 * 获取getAccessToken
	 * @param code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public JSONObject getAccessToken(String code,String type) throws UnsupportedEncodingException{
		String url="";
		if(null!=type&&type.contains("aiuw")){
			url="https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="+QQConnectConfig.getValue("aiuw_app_ID")+"&client_secret="+QQConnectConfig.getValue("aiuw_app_KEY")+"&code="+code+"&redirect_uri="+QQConnectConfig.getValue("aiuw_redirect_URI");
		}else{
			url="https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="+QQConnectConfig.getValue("youjiagou_app_ID")+"&client_secret="+QQConnectConfig.getValue("youjiagou_app_KEY")+"&code="+code+"&redirect_uri="+QQConnectConfig.getValue("youjiagou_redirect_URI");
		}
		String jsonString=HttpUtil.httpGet(url, null, 3000);
		JSONObject json=new JSONObject();
		String[] urls=jsonString.split("&");
		for(String tempMap:urls){
			String[] temp=tempMap.split("=");
			if(2==temp.length){
				json.put(temp[0], temp[1]);
			}
		}
		return json;
	}
}
