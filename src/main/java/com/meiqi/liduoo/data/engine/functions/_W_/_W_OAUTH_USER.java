/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.controller.AuthInfo;
import com.meiqi.liduoo.controller.HttpCallStream;
import com.meiqi.liduoo.controller.HttpStreamManager;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.enums.OauthScope;
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.OauthGetTokenResponse;
import com.meiqi.util.LogUtil;

/**
 * 网页授权获取用户信息
 * 
 * 注意，网页测试时Chrome不通过，需要使用IE测试
 * 
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、授权类型：base基本类型，userInfo纤细信息
 * 4、【可选】授权后跳转回指定URL时需马上执行的回调方法
 * 5、【可选】授权后跳转URL，默认使用当前页面的URL
 * 
 * 返回JSON：
 * {
 *  errcode :"0",
 *  errmsg :"",
 *  "base_info":{
	   "access_token":"ACCESS_TOKEN",
	   "expires_in":7200,
	   "refresh_token":"REFRESH_TOKEN",
	   "openid":"OPENID",
	   "scope":"SCOPE",
	   "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
   		},
   "user_info":{ //如果授权类型为base则此部分为空
	   "city":"潮州","country":"中国","errcode":"0","errmsg":"","groupid":1,
	   "headimgurl":"http://wx.qlogo.cn/mmopen//0","language":"zh_CN","nickname":"燕玉",
	   "openid":"oanfIt5F4oVc7ZVDbqP2SyDd-KQo","province":"广东","remark":"","sex":2,
	   "subscribe":1,"subscribe_time":1443692666,"unionid":"oiKIRuPvoMNmbCOpyeI2pwYvYAGM"}
   		}
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月19日
 */
public class _W_OAUTH_USER extends WeChatFunction {
	public static final String NAME = _W_OAUTH_USER.class.getSimpleName();

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
		HttpCallStream http = HttpStreamManager.get();
		if (http == null) {
			throw new RengineException(calInfo.getServiceName(), "函数调用错误，此函数必须通过规则调用");
		}

		HttpServletRequest request = http.getRequest();
		HttpServletResponse response = http.getResponse();
		HttpSession session = request.getSession();
		String rootKey = http.getRootKey();

		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String scope = DataUtil.getStringValue(args[2]);
		OauthScope authScope = null;
		if (scope == null || scope.toLowerCase().indexOf("base") >= 0) {
			authScope = OauthScope.SNSAPI_BASE;
		} else {
			authScope = OauthScope.SNSAPI_USERINFO;
		}

		final String auth_cb = args.length > 3 ? DataUtil.getStringValue(args[3]) : "";
		String fromUrl = args.length > 4 ? DataUtil.getStringValue(args[4])
				: request.getRequestURL() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());

		try {
			OauthGetTokenResponse userBase = (OauthGetTokenResponse) session.getAttribute("CURWX_FANS_AUTHTOKEN");
			GetUserInfoResponse userInfo = (GetUserInfoResponse) session.getAttribute("CURWX_FANS_INFO");
			if (userBase == null || (authScope == OauthScope.SNSAPI_USERINFO && !"0".equals(userInfo.getErrcode()))) {
				String callbackUrl = LdConfigUtil.getWx_auth_callback();//"http://dev.liduoo.com/DSORM/wxcb.do";

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("scope", authScope);
				param.put("sessionid", session.getId());
				param.put("appid", appId);
				param.put("appsecret", appSecret);
				if (StringUtils.isNotEmpty(auth_cb)) {
					fromUrl = fromUrl + (fromUrl.indexOf("?") > 0 ? "&" : "?") + "auth_cb=" + auth_cb;
				}
				param.put("url", fromUrl);

				CachePool.getInstance().putCacheItem(rootKey, param);
				String redirectUrl = WeChatUtils.getOauthPageUrl(appId, callbackUrl, authScope, rootKey);

				response.addHeader("redirecrUrl", redirectUrl);
				response.setContentType("text/xml;charset=utf-8");
				response.setHeader("Connection", "Keep-Alive");

				ServletOutputStream outputStream = response.getOutputStream();

				outputStream.write("12345".getBytes());
				outputStream.flush();
				LogUtil.debug(NAME+", flush, return header="+redirectUrl);

				String syncObjKey = rootKey + "-" + session.getId() + "-syncobj";
				AuthInfo authInfo = (AuthInfo) CachePool.getInstance().getCacheItem(syncObjKey);
				authInfo.setNeedCacheResult(true);

				LogUtil.debug(NAME+"--will waiting..="+authInfo);
				synchronized (authInfo) {
					if (!authInfo.isAuthed()) {
						authInfo.wait(1 * 60 * 1000);//等待1分钟3 * 60 * 1000
					}
				}

				LogUtil.debug(NAME+"--after waiting..="+authInfo);
				if (authInfo.isAuthed()) {
					String jsonStr =  JSON.toJSONString(authInfo.getAuthResult());
					LogUtil.debug(NAME+"-- Auth result..="+jsonStr);
					return jsonStr;
				} else {
					CachePool.getInstance().clearCacheItem(syncObjKey);
					return "{\"errcode\":-1,\"errmsg\":\"授权未通过\"}";
				}
			} else {
				Map<String, Object> authResult = new HashMap<String, Object>();
				authResult.put("base_info", userBase);
				authResult.put("user_info", userInfo);
				authResult.put("errcode", 0);
				authResult.put("errmsg", "");

				LogUtil.debug(NAME+"-- return directly.="+authResult);
				return JSON.toJSONString(authResult);
			}
		} catch (Exception e) {
			LogUtil.error(e);
			throw new RengineException(calInfo.getServiceName(), e.getMessage());
		}
	}
}
