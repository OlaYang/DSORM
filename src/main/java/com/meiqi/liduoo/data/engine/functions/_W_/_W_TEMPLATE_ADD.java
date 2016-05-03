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
import com.meiqi.liduoo.fastweixin.api.response.AddTemplateResponse;

/**
 * 获取模板ID
 * 
 * <pre>
 * 需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、模板库中模板的编号，有“TM**”和“OPENTMTM**”等形式
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
	"template_id":"Doclyl5uP7Aciu-qZ7mJNPtWkbkYnWBWVja26EGbNyk"
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_TEMPLATE_ADD extends WeChatFunction {
	public static final String NAME = _W_TEMPLATE_ADD.class.getSimpleName();

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
		final String template_id_short = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		TemplateMsgAPI api = new TemplateMsgAPI(config);

		AddTemplateResponse result = api.addTemplate(template_id_short);
		if (!result.verifyWechatResponse( false,config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}