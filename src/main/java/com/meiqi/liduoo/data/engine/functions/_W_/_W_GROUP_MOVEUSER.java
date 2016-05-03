package com.meiqi.liduoo.data.engine.functions._W_;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.UserAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 移动粉丝所在分组
 * 
 * <pre>
 *  需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、分组ID
 * 4、粉丝OpenID，多个粉丝的话用,分隔
 * 
 * 返回JSON：
 * {
	"errcode":"0",
	"errmsg":""，
  }
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_GROUP_MOVEUSER extends WeChatFunction {
	public static final String NAME = _W_GROUP_MOVEUSER.class.getSimpleName();

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
		final String groupid = DataUtil.getStringValue(args[2]);
		final String openidStr = DataUtil.getStringValue(args[3]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		UserAPI api = new UserAPI(config);
		String[] openIds = StringUtils.split(openidStr, ",");
		BaseResponse result = api.moveGroupUser(openIds, groupid);

		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
