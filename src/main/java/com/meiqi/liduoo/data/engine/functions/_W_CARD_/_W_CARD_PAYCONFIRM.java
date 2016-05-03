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
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 确认兑换库存接口：用于确认兑换库存，确认后券点兑换为库存，过程不可逆。
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID
 * 4、quantity：本次需要兑换的库存数目
 * 5、order_id：仅可以使用上面得到的订单号，保证批价有效性
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月30日
 */
public class _W_CARD_PAYCONFIRM extends WeChatFunction {
	public static final String NAME = _W_CARD_PAYCONFIRM.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 5) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String carId = DataUtil.getStringValue(args[2]);
		final int quantity = Integer.valueOf(DataUtil.getStringValue(args[3]));
		final String orderId = DataUtil.getStringValue(args[4]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		BaseResponse result = api.payConfirm(carId, quantity, orderId);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}