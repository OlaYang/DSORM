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
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;

/**
 * 网页授权方式拉取用户信息(需scope为 snsapi_userinfo)
 * 
 * 如果网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息了。
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、网页授权专用的access_token
 * 4、粉丝openid
 * 5、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 *  errcode :"0",
 *  errmsg :"",
    "openid":" OPENID",
   "nickname": NICKNAME,
   "sex":"1",
   "province":"PROVINCE"
   "city":"CITY",
   "country":"COUNTRY",
    "headimgurl": "", 
    "privilege":[
	"PRIVILEGE1"
	"PRIVILEGE2"
    ],
    "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_OAUTH_GETUSERINFO extends WeChatFunction {
	public static final String NAME = _W_OAUTH_GETUSERINFO.class.getSimpleName();

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
		final String access_token = DataUtil.getStringValue(args[2]);
		final String openid = DataUtil.getStringValue(args[3]);

		String key = access_token + "@" + openid + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetUserInfoResponse info = noCache ? null : (GetUserInfoResponse) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			OauthAPI api = new OauthAPI(config);
			GetUserInfoResponse result = api.getUserInfo(access_token, access_token);

			CacheUtils.putCache(key, result);
			info = result;
		}

		return info;
	}
}
