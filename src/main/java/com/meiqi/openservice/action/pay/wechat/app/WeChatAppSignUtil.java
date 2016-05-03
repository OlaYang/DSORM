package com.meiqi.openservice.action.pay.wechat.app;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.dsmanager.util.SysConfig;

@Service
public class WeChatAppSignUtil {
	
	/**
	 * 封装加签参数，业务参数由外部传入，此处只封装微信相关配置参数
	 * @param urlParamMap
	 * @return //返回参数包含签名的map集合
	 * @throws UnsupportedEncodingException 
	 */
	public Map<String, String> generateSignParam(Map<String, String> urlParamMap){
			String userAgent=ContentUtils.PLAT_ANDROID; //默认为安卓对应的支付方式
			if(urlParamMap.containsKey("userAgent")){
				userAgent=urlParamMap.get("userAgent").toLowerCase();
			    urlParamMap.remove("userAgent");
			}

			String openID=urlParamMap.get("openid");
			if(urlParamMap.containsKey("openid")){
				urlParamMap.remove("openid");
			}
			Integer site_id=Integer.parseInt(SysConfig.getValue("siteId"));//；临时先这样处理，以后DSOTRM合并为一个后要客服端传入
			String app_notify_url = "";//异步通知
            String mdb_app_notify_url = "";//异步通知
            String js_notify_url = "";//异步通知
            String native_notify_url = "";//异步通知
             switch (site_id) {
              case 0:
                   //优家购
                  app_notify_url = WeChatAppPayConfig.getWechatPayConfigValue("APP_NOTIFY_URL");
                  mdb_app_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("MDB_APP_NOTIFY_URL");
                  js_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("JS_NOTIFY_URL");
                  native_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("NATIVE_NOTIFY_URL");
                  break;
              case 1:
                   //韩丽
                  app_notify_url = WeChatAppPayConfig.getWechatPayConfigValue("APP_NOTIFY_URL");
                  mdb_app_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("MDB_APP_NOTIFY_URL_hanli");
                  js_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("JS_NOTIFY_URL_hanli");
                  native_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("NATIVE_NOTIFY_URL_hanli");
                  break;
              case 2:
                   //好来客
                  app_notify_url = WeChatAppPayConfig.getWechatPayConfigValue("APP_NOTIFY_URL_haoli");
                  mdb_app_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("MDB_APP_NOTIFY_URL_haolk");
                  js_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("JS_NOTIFY_URL_haolk");
                  native_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("NATIVE_NOTIFY_URL_haolk");
                  break;
              case 3:
                  //梦百合
                  app_notify_url = WeChatAppPayConfig.getWechatPayConfigValue("APP_NOTIFY_URL_mbh");
                  mdb_app_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("MDB_APP_NOTIFY_URL_mbh");
                  js_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("JS_NOTIFY_URL_mbh");
                  native_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("NATIVE_NOTIFY_URL_mbh");
                  break;
              case 4:
                  //海尔
                  app_notify_url = WeChatAppPayConfig.getWechatPayConfigValue("APP_NOTIFY_URL_haier");
                  mdb_app_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("MDB_APP_NOTIFY_URL_haier");
                  js_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("JS_NOTIFY_URL_haier");
                  native_notify_url=WeChatAppPayConfig.getWechatPayConfigValue("NATIVE_NOTIFY_URL_haier");
                  break;
              default:
                  break;
              }
		    String appKey=null;
		    if(userAgent.contains(ContentUtils.PLAT_ANDROID)){
		    	urlParamMap.put("appid", WeChatAppPayConfig.APP_APP_ID);
		    	urlParamMap.put("mch_id", WeChatAppPayConfig.APP_MCH_ID);
				urlParamMap.put("notify_url", app_notify_url);
		    	appKey=WeChatAppPayConfig.APP_APP_KEY;
		    	urlParamMap.put("trade_type", "APP");
		    }else if(userAgent.contains(ContentUtils.PLAT_IPHONE)){
		    	if(userAgent.contains(ContentUtils.MDB)){
		    		urlParamMap.put("appid", WeChatAppPayConfig.MDB_APP_APP_ID);
			    	urlParamMap.put("mch_id", WeChatAppPayConfig.MDB_APP_MCH_ID);
					urlParamMap.put("notify_url", mdb_app_notify_url);
			    	appKey=WeChatAppPayConfig.MDB_APP_APP_KEY;
		    	}else{
		    		urlParamMap.put("appid", WeChatAppPayConfig.APP_APP_ID);
			    	urlParamMap.put("mch_id", WeChatAppPayConfig.APP_MCH_ID);
					urlParamMap.put("notify_url", app_notify_url);
			    	appKey=WeChatAppPayConfig.APP_APP_KEY;
		    	}
		    	urlParamMap.put("trade_type", "APP");
		    }else if(userAgent.contains(ContentUtils.PLAT_IPAD)){
		    	if(userAgent.contains(ContentUtils.MDB)){
		    		urlParamMap.put("appid", WeChatAppPayConfig.MDB_APP_APP_ID);
			    	urlParamMap.put("mch_id", WeChatAppPayConfig.MDB_APP_MCH_ID);
					urlParamMap.put("notify_url", mdb_app_notify_url);
			    	appKey=WeChatAppPayConfig.MDB_APP_APP_KEY;
		    	}else{
		    		urlParamMap.put("appid", WeChatAppPayConfig.APP_APP_ID);
			    	urlParamMap.put("mch_id", WeChatAppPayConfig.APP_MCH_ID);
					urlParamMap.put("notify_url", app_notify_url);
			    	appKey=WeChatAppPayConfig.APP_APP_KEY;
		    	}
		    	
		    	urlParamMap.put("trade_type", "APP");
		    }else if("Native".equalsIgnoreCase(userAgent)){
		    	urlParamMap.put("appid", WeChatAppPayConfig.NATIVE_APP_ID);
		    	urlParamMap.put("mch_id", WeChatAppPayConfig.NATIVE_MCH_ID);
				urlParamMap.put("notify_url", native_notify_url);
		    	appKey=WeChatAppPayConfig.NATIVE_APP_KEY;
		    	urlParamMap.put("trade_type", "NATIVE");
		    }else{//默认js支付
		    	urlParamMap.put("appid", WeChatAppPayConfig.JS_APP_ID);
		    	urlParamMap.put("mch_id", WeChatAppPayConfig.JS_MCH_ID);
		    	urlParamMap.put("notify_url", js_notify_url);
		    	appKey=WeChatAppPayConfig.JS_APP_KEY;
		    	urlParamMap.put("openid", openID);
		    	urlParamMap.put("trade_type", "JSAPI");
		    }
		    
			urlParamMap.put("nonce_str", nonceString());
			
			String sign=sign(urlParamMap,appKey);
			if(null==sign){
				return null;
			}else{
				urlParamMap.put("sign", sign);
				urlParamMap.put("appKey", appKey);
				return urlParamMap;
			}
	}
	
	/**
	 * 支付成功后的签名验证
	 * @param json
	 * @return
	 */
	public String generateSignCheck(JSONObject json){
		Map<String, String> map=JSONObject.toJavaObject(json, Map.class);
		String appKey=map.get("appKey");
		map.remove("appKey");
		String sign=sign(map,appKey);
		return sign;
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
	 * 微信支付签名生成
	 * @param urlParamMap
	 * @return
	 */
	public String sign(Map<String, String> urlParamMap,String appKey) {
		List<String> keys = new ArrayList<String>(paraFilter(urlParamMap).keySet());
		Collections.sort(keys);
		StringBuilder stringBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String key : keys) {
			if ("" != urlParamMap.get(key)) {
				if (isFirst) {
					isFirst = false;
					stringBuilder.append(key).append("=").append(urlParamMap.get(key));
				} else {
					stringBuilder.append("&").append(key).append("=").append(urlParamMap.get(key));
				}
			}
		}
		stringBuilder.append("&key=").append(appKey);
		String stringSignTemp = stringBuilder.toString();
		return MD5Util.MD5(stringSignTemp).toUpperCase();
	}
	
	/**
	 * 随机字符串生成
	 * @return
	 */
	private String nonceString(){
		//使用使用Apache的commons-lang.jar 生成18位随机字符串
		return RandomStringUtils.randomAlphabetic(18);
	}
	
}
