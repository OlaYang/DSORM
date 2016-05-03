/**
 * 
 */
package com.meiqi.liduoo.data.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.liduoo.base.exception.IllegalWechatResponseException;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * @author FrankGui 2016年2月19日
 */
public class WxCardSignature implements IWxApiInterface {
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
		String cardApiTicket = config.getCardApiTicket();
		CardAPI api = new CardAPI(config);
		if (jsonStr.startsWith("[")) {
			List<Map> list = JSON.parseArray(jsonStr, Map.class);
			if (list == null || list.size() == 0) {
				throw new IllegalWechatResponseException("请求参数为空 ");
			}
			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

			for (Map req : list) {
				Map<String, Object> ret = api.getSignature(req);
				if (!"0".equals(ret.get("errcode"))) {
					throw new IllegalWechatResponseException("" + ret.get("errmsg"));
				}
				returnList.add(ret);
			}	
			// Map<String, Object> returnMap = new HashMap<String, Object>();
			// returnMap.put("errcode", 0);
			// returnMap.put("errmsg", "");
			// returnMap.put("signs", returnList);
			response.setErrmsg(JSON.toJSONString(returnList));
		} else {
			Map<String, Object> params = JSONUtil.toMap(jsonStr);
			Map<String, Object> ret = api.getSignature(params, cardApiTicket);
			if (!"0".equals(ret.get("errcode"))) {
				throw new IllegalWechatResponseException("" + ret.get("errmsg"));
			}
			response.setErrmsg(JSON.toJSONString(ret));
		}
		return response;
	}

}
