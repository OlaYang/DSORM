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
 * 刷新access_token（如果需要）
 * 
 * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，refresh_token
 * 拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、用户刷新access_token
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
   "scope":"SCOPE"
}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_OAUTH_REFRESHTOKEN extends WeChatFunction {
	public static final String NAME = _W_OAUTH_REFRESHTOKEN.class.getSimpleName();

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
		final String refreshToken = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + refreshToken + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		OauthGetTokenResponse info = noCache ? null : (OauthGetTokenResponse) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			OauthAPI api = new OauthAPI(config);
			OauthGetTokenResponse result = api.refreshToken(refreshToken);

			CacheUtils.putCache(key, result);
			info = result;
		}

		return info;
	}
}
