/**
 * 
 */
package com.meiqi.liduoo.wechat.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.fastweixin.api.OauthAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.OauthScope;
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;
import com.meiqi.liduoo.fastweixin.api.response.OauthGetTokenResponse;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.util.LogUtil;

/**
 * 公众号网页授权管理(过时.......)
 * 
 * @author FrankGui 2015年12月12日
 */
@Service
public class WeChatAuthAction extends BaseAction {
	@Autowired
	private IChannelService channelService;
	// @Autowired
	// private IMemcacheAction memcacheService;

	/**
	 * 获取网页授权的重定向URL
	 * 
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	public String redirectUrl(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
		// 提取请求参数参数
		ResponseInfo respInfo = new ResponseInfo();
		Map param = null;
		try {
			param = DataUtil.parse(repInfo.getParam(), Map.class);

			String scodeStr = (String) param.get("scope");
			int cid = Integer.valueOf(DataUtil.getStringValue(param.get("cid")));
			String url = (String) param.get("url");
			if (StringUtils.isEmpty(url)) {
				url = request.getRequestURL().toString();
				if (!StringUtils.isEmpty(request.getQueryString())) {
					url += "?" + request.getQueryString();
				}
			}
			OauthScope scope = OauthScope.SNSAPI_USERINFO;
			if (scodeStr == null || scodeStr.toLowerCase().indexOf("userinfo") < 0) {
				scope = OauthScope.SNSAPI_BASE;
			}
			ApiConfig config = channelService.initApiConfig(cid);// 网页授权不需要AppSecret
			OauthAPI api = new OauthAPI(config);
			String redirectUrl = api.getOauthPageUrl(url, scope, "1");

			respInfo.setCode(DsResponseCodeData.SUCCESS.code);
			respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
			respInfo.setObject(redirectUrl);
		} catch (Exception e) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription(DsResponseCodeData.ERROR.description);
			LogUtil.error(e);
		}
		return JSONObject.toJSONString(respInfo);
	}

	/**
	 * 根据微信返回的Code获取用户信息
	 * 
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	public String getInfoByCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
		// 提取请求参数参数
		ResponseInfo respInfo = new ResponseInfo();
		Map param = null;
		try {
			param = DataUtil.parse(repInfo.getParam(), Map.class);

			String scodeStr = (String) param.get("scope");
			int cid = Integer.valueOf(DataUtil.getStringValue(param.get("cid")));
			String code = (String) param.get("code");
			if (StringUtils.isEmpty(code)) {
				throw new IllegalArgumentException("code参数不能为空");
			}
			OauthScope scope = OauthScope.SNSAPI_USERINFO;
			if (scodeStr == null || scodeStr.toLowerCase().indexOf("userinfo") < 0) {
				scope = OauthScope.SNSAPI_BASE;
			}
			ApiConfig config = channelService.initApiConfig(cid);// 网页授权不需要AppSecret
			OauthAPI api = new OauthAPI(config);
			OauthGetTokenResponse oauthInfo = api.getToken(code);
			oauthInfo.verifyWechatResponse(true, config);

			if (scope == OauthScope.SNSAPI_USERINFO) {
				GetUserInfoResponse userInfo = api.getUserInfo(oauthInfo.getAccessToken(), oauthInfo.getOpenid());
				userInfo.verifyWechatResponse(true, config);
				respInfo.setObject(userInfo);
			} else {
				respInfo.setObject(oauthInfo);
			}
			respInfo.setCode(DsResponseCodeData.SUCCESS.code);
			respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

		} catch (Exception e) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription(DsResponseCodeData.ERROR.description);
			LogUtil.error(e);
		}
		return JSONObject.toJSONString(respInfo);
	}
}
