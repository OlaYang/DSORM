package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.TemplateMsgAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.Industry;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 设置所属行业: 设置行业可在MP中完成，每月可修改行业1次，账号仅可使用所属行业中相关的模板，
 * 为方便第三方开发者，提供通过接口调用的方式来修改账号所属行业，具体如下：
 * 
 * <pre>
 * 需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、公众号模板消息所属行业编号ID1
 * 4、公众号模板消息所属行业编号ID2
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_TEMPLATE_SETINDUSTRY extends WeChatFunction {
	public static final String NAME = _W_TEMPLATE_SETINDUSTRY.class.getSimpleName();

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
		final String industry1 = DataUtil.getStringValue(args[2]);
		final String industry2 = DataUtil.getStringValue(args[3]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		TemplateMsgAPI api = new TemplateMsgAPI(config);

		Industry industry = new Industry();
		industry.setIndustryId1(industry1);
		industry.setIndustryId2(industry2);

		BaseResponse result = api.setIndustry(industry);
		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}