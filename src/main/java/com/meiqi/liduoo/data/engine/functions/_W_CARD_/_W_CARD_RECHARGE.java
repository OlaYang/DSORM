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
import com.meiqi.liduoo.fastweixin.api.response.CardRechargeResponse;

/**
 * 充值券点接口
 * 
 * 开发者可以通过此接口为券点账户充值券点，1元等于1点。开发者调用接口后可以获得一个微信支付的支付二维码链接，
 *  开发者可以将链接转化为二维码扫码支付。
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、coin_count:需要充值的券点数目，1点=1元

 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
	"order_id": "100005790120***221401000171",
 	"qrcode_url": "weixin://wxpay/bizpayurl?pr=xxxxxxxxx",
 	"qrcode_buffer": "pwxs*************xxxxxxxxxx",
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月30日
 */
public class _W_CARD_RECHARGE extends WeChatFunction {
	public static final String NAME = _W_CARD_RECHARGE.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 3) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final int coin_count = Integer.valueOf(DataUtil.getStringValue(args[2]));

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardRechargeResponse result = api.recharge(coin_count);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}