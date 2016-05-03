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
import com.meiqi.liduoo.fastweixin.api.response.CardPayActivateResponse;

/**
 * 开通券点账户接口
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
      "reward": 200 //奖励券点数量，以元为单位，微信卡券对每一个新开通券点账户的商户奖励200个券点，
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月30日
 */
public class _W_CARD_PAYACTIVATE extends WeChatFunction {
	public static final String NAME = _W_CARD_PAYACTIVATE.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardPayActivateResponse result = api.payActivate();
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}