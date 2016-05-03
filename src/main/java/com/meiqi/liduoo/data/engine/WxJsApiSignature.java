/**
 * 
 */
package com.meiqi.liduoo.data.engine;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.liduoo.base.exception.IllegalWechatResponseException;
import com.meiqi.liduoo.fastweixin.api.JsAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.api.response.GetSignatureResponse;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * @author FrankGui 2016年2月19日
 */
public class WxJsApiSignature implements IWxApiInterface {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.liduoo.data.engine.IWxApiInterface#execute(com.meiqi.liduoo.
	 * fastweixin.api.config.ApiConfig, java.lang.String)
	 */
	@Override
	public BaseResponse execute(ApiConfig config, String jsonStr) {
		BaseResponse response = new BaseResponse();

		JsAPI api = new JsAPI(config);
		GetSignatureResponse result = null;

		String url = null;
		String nonceStr = null;
		Long timestamp = null;
		if (jsonStr.startsWith("{")) {
			Map<String, Object> jsonMap = JSONUtil.toMap(jsonStr);
			url = (String) jsonMap.get("url");
			nonceStr = (String) jsonMap.get("nonceStr");
			if (jsonMap.get("timestamp") != null) {
				timestamp = Long.valueOf("" + jsonMap.get("timestamp"));
			}
		} else {
			url = jsonStr;
		}
		if (StrUtil.isBlank(nonceStr)) {
			result = api.getSignature(url);
		} else {
			result = api.getSignature(nonceStr, timestamp, url);
		}
		if (!result.verifyWechatResponse(false, config)) {
			throw new IllegalWechatResponseException(result.toJsonString());
		}
		result.setAppId(config.getAppid());
		
		response.setErrmsg(JSON.toJSONString(result));
		
		return response;
	}

}
