/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * * 获取cardapi-sdk所需的签名
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、输入参数JSON字符串（Map格式Key-Value对）,如果参数以[]包起来，表示需要同时生成多个签名
 *           
 * 
 * 返回JSON：
 * {
 * 	errcode :"0",
 * 	errmsg :"",
 *  //只生成一个签名时：
	  	shopId: '', // 门店Id,输入参数，原样返回
	    cardType: '', // 卡券类型,输入参数，原样返回
	    cardId: '', // 卡券Id,输入参数，原样返回
	    
	    timestamp: 0, // 卡券签名时间戳，输入参数如果存在此值，原样返回；否则后台生成后返回
	    nonceStr: '', // 卡券签名随机串，输入参数如果存在此值，原样返回；否则后台生成后返回
	    signType: '', // 签名方式，默认'SHA1'
	    
	    cardSign: '', // 卡券签名
	//需要生成多个签名时:
	 signs:[{格式同单个},{...}]
 * }
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_SIGNATURE extends WeChatFunction {
	public static final String NAME = _W_CARD_SIGNATURE.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 3) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String jsonStr = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);
		String cardApiTicket = config.getCardApiTicket();

		if (jsonStr.startsWith("[")) {
			List<Map> list = JSON.parseArray(jsonStr, Map.class);
			if (list == null || list.size() == 0) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误：请求参数为空 ");
			}
			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

			for (Map req : list) {
				Map<String, Object> ret = api.getSignature(req);
				if (!"0".equals(ret.get("errcode"))) {
					throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + ret.get("errmsg"));
				}
				returnList.add(ret);
			}
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("errcode", 0);
			returnMap.put("errmsg", "");
			returnMap.put("signs", returnList);

			return JSON.toJSONString(returnMap);
		} else {
			Map<String, Object> params = JSONUtil.toMap(jsonStr);
			Map<String, Object> ret = api.getSignature(params, cardApiTicket);
			if (!"0".equals(ret.get("errcode"))) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + ret.get("errmsg"));
			}
			return JSON.toJSONString(ret);
		}

	}

}
