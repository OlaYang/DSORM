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
import com.meiqi.liduoo.fastweixin.api.response.CardGetPayPriceResponse;

/**
 * 对优惠券批价,查询本次新增库存需要多少券点
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID
 * 4、quantity：本次需要兑换的库存数目
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
    "order_id": 本次批价的订单号，用于下面的确认充值库存接口，仅对当前订单有效且仅可以使用一次，60s内可用于兑换库存。
 	"price": "0.2", 本次需要支付的券点总额度
 	"free_coin": "0.2",本次需要支付的免费券点额度
 	"pay_coin": "0" 本次需要支付的付费券点额度
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月30日
 */
public class _W_CARD_GETPAYPRICE extends WeChatFunction {
	public static final String NAME = _W_CARD_GETPAYPRICE.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 4) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String carId = DataUtil.getStringValue(args[2]);
		final int quantity = Integer.valueOf(DataUtil.getStringValue(args[3]));

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardGetPayPriceResponse result = api.getPayPrice(carId, quantity);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}