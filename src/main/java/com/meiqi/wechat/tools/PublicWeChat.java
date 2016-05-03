package com.meiqi.wechat.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.util.CacheUtil;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.openservice.commons.config.SysConfig;

/**
 * 微信公众平台
 *
 */
@Service
public class PublicWeChat {
	
	@Autowired
	private ThirdWeChat thirdWeChat;
	@Autowired
	private IMemcacheAction memcacheAction;
	
	//获取公众平台在开放平台的unionid
	public String getUserUnionId(String openId){
		JSONObject jsonObject=getUserInfoByOpenId(openId);
		return jsonObject.getString("unionid");
	}
	
	
	//获取公众平台的用户信息
	public JSONObject getUserInfoByOpenId(String openId){
		if("".equals(openId)){
			return null;
		}
		String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token="+getPublicAccessToken()+"&openid="+openId+"&lang=zh_CN", null, 3000);
		return JSONObject.parseObject(result);
	}
	
	
	
	/**
	 * 获取加密签名信息
	 * @return
	 */
	public Map<String,String> getSignature(Map<String,String> paramMap){
		paramMap.put("noncestr", RandomStringUtils.randomAlphabetic(16));
		paramMap.put("jsapi_ticket", getJsApiTicket());
		paramMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
		List<String> keys = new ArrayList<String>(paraFilter(paramMap).keySet());
		Collections.sort(keys);
		StringBuilder stringBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String key : keys) {
			if ("" != paramMap.get(key)) {
				if (isFirst) {
					isFirst = false;
					stringBuilder.append(key).append("=").append(paramMap.get(key));
				} else {
					stringBuilder.append("&").append(key).append("=").append(paramMap.get(key));
				}
			}
		}
		paramMap.put("signature", DigestUtils.sha1Hex(stringBuilder.toString()));
		paramMap.put("appId", SysConfig.getValue("public_appid"));
		return paramMap;
	}
	
	
	/** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    private  Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value.trim());
        }

        return result;
    }
	
	/**
	 * 获取jsapiticket
	 * @return
	 */
	public String getJsApiTicket(){
		//从缓存中取js_api_ticket
		Object jsApiTicketObejct=memcacheAction.getCache(WeChatConfig.publicJsApiTicket);
		if(null==jsApiTicketObejct){
			String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+getPublicAccessToken()+"&type=jsapi", null, 3000);
			JSONObject json=JSONObject.parseObject(result);
			String ticket=json.get("ticket").toString().trim();
			String expiresIn=json.get("expires_in").toString().trim();
			if("".equals(ticket)||"".equals(expiresIn)){
				return "";
			}
			memcacheAction.putCache(WeChatConfig.publicJsApiTicket, ticket, (Long.parseLong(expiresIn)-200)*1000);
			return ticket;
		}else{
			return jsApiTicketObejct.toString();
		}
	}
	
	//公众平台获取公众平台的access_token
	public String getPublicAccessToken(){
		//从缓存中取access_token
		Object accessTokenObject=memcacheAction.getCache(WeChatConfig.publicAccessToken);
		//取不到access_token则从微信去拿
		if(null==accessTokenObject){
			String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+SysConfig.getValue("public_appid")+"&secret="+SysConfig.getValue("public_secret"), null, 3000);
			JSONObject json=JSONObject.parseObject(result);
			String accessToken=json.get("access_token").toString().trim();
			String expiresIn=json.get("expires_in").toString().trim();
			if("".equals(accessToken)||"".equals(expiresIn)){
				return "";
			}
			//设置access_token到内存中，并且设置过期时间，比微信提供的7200提前200秒失效，保证每次拿到的access_token在微信中都是可用的
			memcacheAction.putCache(WeChatConfig.publicAccessToken, accessToken, (Long.parseLong(expiresIn)-200)*1000);
			return accessToken;
		}else{
			return accessTokenObject.toString();
		}
	}
	
}
