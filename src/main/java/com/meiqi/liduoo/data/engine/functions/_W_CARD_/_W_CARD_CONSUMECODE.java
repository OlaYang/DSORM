/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_CARD_;

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
import com.meiqi.liduoo.fastweixin.api.response.CardConsumeCodeResponse;

/**
 * 核销卡券
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、微信卡券code：
 * 4、卡券ID：可选，但是自定义卡券必须提供
 * 
 * 返回JSON：
 *  {
	"errcode":0,
	"errmsg":"ok",
	"openid":"oFS7Fjl0WsZ9AMZqrI80nbIq8xrA",
	"card":{
		"card_id":"pFS7Fjg8kV1IdDz01r4SQwMkuCKc",
  		}
	}
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_CONSUMECODE extends WeChatFunction {
	public static final String NAME = _W_CARD_CONSUMECODE.class.getSimpleName();

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
		final String code = DataUtil.getStringValue(args[2]);
		final String cardId = args.length > 3 ? DataUtil.getStringValue(args[3]) : "";

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardConsumeCodeResponse result = api.consumeCode(code, cardId);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
