/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.CustomAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.CustomAccount;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 设置客服信息
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、客服账号
 * 4、客服昵称
 * 5、客服密码
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
public class _W_CUSTOM_UPDATE_ACCOUNT extends WeChatFunction {
	public static final String NAME = _W_CUSTOM_UPDATE_ACCOUNT.class.getSimpleName();

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
		final String accountName = DataUtil.getStringValue(args[2]);
		final String nickName = DataUtil.getStringValue(args[3]);
		final String password = DataUtil.getStringValue(args[4]);

		CustomAccount customAccount = new CustomAccount();
		customAccount.setAccountName(accountName);
		customAccount.setNickName(nickName);
		customAccount.setPassword(password);

		// 删除操作不考虑缓存
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CustomAPI api = new CustomAPI(config);

		BaseResponse result = api.updateCustomAccount(customAccount);
		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}