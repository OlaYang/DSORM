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
import com.meiqi.liduoo.fastweixin.api.response.CardGetUserCardsResponse;

/**
 * 查询用户已领取的卡券
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、需要查询的用户openid
 * 4、卡券ID。不填写时默认查询当前appid下的卡券。
 * 
 * 返回JSON：
 *  {
	"errcode":0,
	"errmsg":"ok",
	"card_list": [
      {"code": "xxx1434079154", "card_id": "xxxxxxxxxx"},
      {"code": "xxx1434079155", "card_id": "xxxxxxxxxx"}
      ]
	}
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_GETUSERCARDS extends WeChatFunction {
	public static final String NAME = _W_CARD_GETUSERCARDS.class.getSimpleName();

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
		final String openid = DataUtil.getStringValue(args[2]);
		final String cardId = args.length > 3 ? DataUtil.getStringValue(args[3]) : "";

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardGetUserCardsResponse result = api.getUserCards(openid, cardId);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
