package com.meiqi.liduoo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.MapUtil;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.fastweixin.api.OauthAPI;
import com.meiqi.liduoo.fastweixin.api.UserAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.OauthScope;
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.OauthGetTokenResponse;
import com.meiqi.liduoo.wechat.services.IFansService;

@RestController
// @Controller
@RequestMapping(value = "/wxcb")
public class WeixinAuthCallbackController {
	// @Autowired
	// private IChannelService channelService;
	@Autowired
	private IFansService fansService;

	/**
	 * 微信消息交互处理
	 *
	 * @param request
	 *            http 请求对象
	 * @param response
	 *            http 响应对象
	 * @throws ServletException
	 *             异常
	 * @throws IOException
	 *             IO异常
	 * @throws InterruptedException
	 */
	@RequestMapping(method = RequestMethod.POST)
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		Cookie cookie = new Cookie("JSESSIONID", session.getId());
		cookie.setPath("/");
		response.addCookie(cookie);

		LogUtil.debug("-网页授权回调页面URL=" + request.getRequestURL() + "?" + request.getQueryString());

		String rootKey = request.getParameter("state");
		Map param = (Map) CachePool.getInstance().getCacheItem(rootKey);
		LogUtil.debug("-网页授权回调参数Map=" + param);
		if (MapUtil.isNullOrEmpty(param)) {
			LogUtil.warn("网页授权回调页面没有找到原始请求参数...");
			return;
		}

		String fromUrl = (String) param.get("url");

		try {
			AuthInfo authInfo = (AuthInfo) CachePool.getInstance()
					.getCacheItem(rootKey + "-" + param.get("sessionid") + "-syncobj");
			String code = request.getParameter("code");

			if (StringUtils.isEmpty(code)) {
				LogUtil.warn("网页授权回调页面没有找到code参数，很可能是用户不同意授权...");
				notifyAuthInfo(authInfo, false, null);
				return;
			}
			LogUtil.debug("网页授权获得的Code：" + code);
			OauthScope scope = (OauthScope) param.get("scope");

			String appid = (String) param.get("appid");
			String appSecret = (String) param.get("appsecret");
			ApiConfig config = WeChatUtils.initApiConfig(appid, appSecret);
			OauthAPI api = new OauthAPI(config);

			OauthGetTokenResponse oauthInfo = api.getToken(code);
			if (!oauthInfo.verifyWechatResponse(false, config)) {
				LogUtil.error("网页授权出错：" + oauthInfo.toJsonString());
				return;
			}
			LogUtil.debug("网页授权用户基本信息：" + oauthInfo.toJsonString());
			session.setAttribute("CURWX_FANS_OPENID" + appid, oauthInfo.getOpenid());
			session.setAttribute("CURWX_FANS_UNIONID" + appid, oauthInfo.getUnionid());
			session.setAttribute("CURWX_FANS_AUTHTOKEN" + appid, oauthInfo);
			// -----从DB中查询粉丝信息，确定是否需要进行网页授权------------
			Map<String, Object> fansInfo = fansService.getFansByOpenId(oauthInfo.getOpenid(), true, appid);
			boolean isOauthed = false;
			GetUserInfoResponse userInfo = null;
			if (!MapUtils.isEmpty(fansInfo)) {
				isOauthed = CommonUtils.toBoolean(String.valueOf(fansInfo.get("foauthed")));
			}
			if (scope == OauthScope.SNSAPI_USERINFO) {
				if (!isOauthed) {
					userInfo = api.getUserInfo(oauthInfo.getAccessToken(), oauthInfo.getOpenid());
					if (!userInfo.verifyWechatResponse(false, config)) {
						LogUtil.error("网页授权出错：" + userInfo.toJsonString());
						notifyAuthInfo(authInfo, false, null);
						return;
					}
					fansService.setFansInfo(oauthInfo.getOpenid(), userInfo, appid);
				} else {
					UserAPI userApi = new UserAPI(config);
					userInfo = userApi.getUserInfo(oauthInfo.getOpenid());
					if (!userInfo.verifyWechatResponse(false, config)) {
						LogUtil.error("获取关注粉丝信息出错：" + userInfo.toJsonString());
						notifyAuthInfo(authInfo, false, null);
						return;
					}
				}
				isOauthed = true;
				session.setAttribute("CURWX_FANS_INFO" + appid, userInfo);

				LogUtil.debug("网页授权用户详细信息：" + userInfo.toJsonString());
			}
			Map<String, Object> authResult = new HashMap<String, Object>();
			authResult.put("base_info", oauthInfo);
			authResult.put("user_info", userInfo);
			authResult.put("errcode", 0);
			authResult.put("errmsg", "");

			notifyAuthInfo(authInfo, true, authResult);// 通知原挂起线程继续执行

			// 等待原来挂起线程执行结果
			if (authInfo != null) {
				LogUtil.debug("--After authed,will waiting prior running result..=" + authInfo);
				synchronized (authInfo) {
					if (authInfo.getServiceResult() == null) {
						try {
							authInfo.wait(1 * 60 * 1000);// 等待1分钟3 * 60 * 1000
						} catch (InterruptedException e) {
							throw new ServletException(e);
						}
					}
				}

				Object serviceResult = authInfo.getServiceResult();
				LogUtil.debug("--After authed,waiting done. Result..=" + serviceResult);
				if (serviceResult != null) {
					CachePool.getInstance().putCacheItem(rootKey + "_sr", serviceResult);
					fromUrl = fromUrl + (fromUrl.indexOf("?") > 0 ? "&" : "?") + "cacheKey=" + rootKey + "_sr";
				}
			}
		} finally {
			// 无论授权是否成功，都转向fromUrl，避免手机页面没有任何反应。
			LogUtil.debug("网页授权回调页面Redirect to: " + fromUrl);
			response.sendRedirect(fromUrl);
			// response.flushBuffer();
		}
		return;
	}

	private void notifyAuthInfo(AuthInfo authInfo, boolean isSuccess, Object authResult) {
		LogUtil.debug("网页授权回调页面设置authInfo：isSuccess=" + isSuccess + ", authResult=" + authResult);
		if (authInfo != null) {
			synchronized (authInfo) {
				authInfo.setAuthed(isSuccess);
				authInfo.setAuthResult(authResult);
				authInfo.notifyAll();
			}
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.doPost(request, response);

	}
}
