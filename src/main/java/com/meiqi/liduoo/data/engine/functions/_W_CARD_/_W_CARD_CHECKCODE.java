package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import com.meiqi.liduoo.fastweixin.api.response.CardCheckCodeResponse;

/**
 * 核查Code有效性
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID
 * 4、Code列表,用逗号分隔字符串或者JSON格式String数组
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
    "exist_code":["11111","22222","33333"],
    "not_exist_code":["44444","55555"]
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月28日
 */
public class _W_CARD_CHECKCODE extends WeChatFunction {
	public static final String NAME = _W_CARD_CHECKCODE.class.getSimpleName();

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
		String cardId = DataUtil.getStringValue(args[2]);
		final String codeStr = DataUtil.getStringValue(args[3]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		String[] codeIds = null;
		if (codeStr.startsWith("[")) {
			List<String> list = JSON.parseArray(codeStr, String.class);
			codeIds = (String[]) list.toArray();
		} else {
			codeIds = StringUtils.split(codeStr, ",");
		}
		CardCheckCodeResponse result = api.checkCode(cardId, codeIds);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}