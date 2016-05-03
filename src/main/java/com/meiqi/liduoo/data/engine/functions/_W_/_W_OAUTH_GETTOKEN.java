/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.OauthAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.OauthGetTokenResponse;

/**
 * 通过code换取网页授权access_token。
 * 
 * 这里通过code换取的是一个特殊的网页授权access_token,与基础支持中的access_token（该access_token用于调用其他接口）
 * 不同。
 * 公众号可通过下述接口来获取网页授权access_token。如果网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token
 * 的同时，也获取到了openid，snsapi_base式的网页授权流程即到此为止。
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、code：填写第一步获取的code参数
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 *  errcode :"0",
 *  errmsg :"",
   "access_token":"ACCESS_TOKEN",
   "expires_in":7200,
   "refresh_token":"REFRESH_TOKEN",
   "openid":"OPENID",
   "scope":"SCOPE",
   "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_OAUTH_GETTOKEN extends WeChatFunction {
	public static final String NAME = _W_OAUTH_GETTOKEN.class.getSimpleName();

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
		final String code = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + code + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		OauthGetTokenResponse info = noCache ? null : (OauthGetTokenResponse) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			OauthAPI api = new OauthAPI(config);
			OauthGetTokenResponse result = api.getToken(code);

			CacheUtils.putCache(key, result);
			info = result;
		}

		return info;
	}
}
