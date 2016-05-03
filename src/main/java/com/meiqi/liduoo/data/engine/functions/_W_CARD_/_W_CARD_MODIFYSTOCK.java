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
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 修改卡券库存
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID。
   4、数量：正数表示增加库存，负数表示减少库存
 * 
 * 返回JSON：
 *  {
	"errcode":0,
	"errmsg":"ok",
	}
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_MODIFYSTOCK extends WeChatFunction {
	public static final String NAME = _W_CARD_MODIFYSTOCK.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 4) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String cardId = DataUtil.getStringValue(args[2]);
		final int qty = Integer.valueOf(DataUtil.getStringValue(args[3]));

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		BaseResponse result = api.modifyStock(cardId, qty);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
