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
import com.meiqi.liduoo.fastweixin.api.enums.OauthScope;

/**
 * 生成回调url，这个结果要求用户在微信中打开，即可获得token，并指向redirectUrl
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、scope：snsapi_base或者snsapi_userinfo
 * 4、redirectUrl：回调URL
 * 5、State状态
 * 6、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：
 * 微信回调URL
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_OAUTH_GETPAGEURL extends WeChatFunction {
	public static final String NAME = _W_OAUTH_GETPAGEURL.class.getSimpleName();

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
		final String scope = DataUtil.getStringValue(args[2]);
		final String redirectUrl = DataUtil.getStringValue(args[3]);
		final String state = DataUtil.getStringValue(args[4]);

		OauthScope authScope = null;
		if (scope == null || scope.toLowerCase().indexOf("base") >= 0) {
			authScope = OauthScope.SNSAPI_BASE;
		} else {
			authScope = OauthScope.SNSAPI_USERINFO;
		}

		String key = appId + "@" + appSecret + "@" + redirectUrl + "@" + authScope + "@" + state + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		String info = noCache ? null : (String) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			OauthAPI api = new OauthAPI(config);
			String result = api.getOauthPageUrl(redirectUrl, authScope, state);

			CacheUtils.putCache(key, result);
			info = result;
		}

		return info;
	}
}
