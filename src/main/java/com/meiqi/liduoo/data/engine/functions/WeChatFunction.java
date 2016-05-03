/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.util.LogUtil;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_UPLOADIMAGE;
import com.meiqi.liduoo.fastweixin.api.RMIAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.util.IpKit;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * @author FrankGui 2015年12月24日
 */
public abstract class WeChatFunction extends Function {
	protected Map<String, String> configMap = null;
	protected boolean isNumberAppId = false;

	protected String getAppId(String appid) {
		if ("default".equalsIgnoreCase(appid)) {
			appid = LdConfigUtil.getDefault_app_id();
		} else {
			try {
				int channelId = Integer.parseInt(appid.trim());
				this.configMap = RMIAPI.getWxConfig(channelId);
				this.isNumberAppId = true;
				return this.configMap.get("WECHAT_APPID");
			} catch (NumberFormatException ex) {
				// 不是数字，直接返回
			}
		}
		return appid;
	}

	protected String getAppSecret(String appSecret) {
		if (this.isNumberAppId) {
			return this.configMap.get("WECHAT_APPSECRET");
		}
		if ("default".equalsIgnoreCase(appSecret)) {
			appSecret = LdConfigUtil.getDefault_app_secret();
		}
		return appSecret;
	}

	protected String getKeyStoreFile(String keyStoreFile) {
		if ("-1".equals(keyStoreFile) || "default".equalsIgnoreCase(keyStoreFile)) {
			keyStoreFile = LdConfigUtil.getConfig("default_key_storefile");
		}
		return keyStoreFile;
	}

	protected String getSignKey(String signKey) {
		if ("-1".equals(signKey) || "default".equalsIgnoreCase(signKey)) {
			signKey = LdConfigUtil.getConfig("default_sign_key");
		}
		return signKey;
	}

	protected String getMchId(String mchId) {
		if ("-1".equals(mchId) || "default".equalsIgnoreCase(mchId)) {
			mchId = LdConfigUtil.getConfig("default_mch_id");
		}
		return mchId;
	}

	protected String checkCardLogo(CalInfo calInfo, String appId, String appSecret, String cardStr) {
		String newContent = cardStr;
		String[][] pairs = { { "\"?logo_url\"?\\s*:\\s*\"([^\"]+)\"", "\"logo_url\":\"#\"" },
				{ "\"?icon_url_list\"?\\s*:\\s*\\[\\s*\"([^\"]+)\"", "\"icon_url_list\":\\[\"#\"" } };

		for (int i = 0; i < pairs.length; i++) {
			Pattern p = Pattern.compile(pairs[i][0]);
			Matcher m = p.matcher(cardStr);
			if (m.find()) {
				if (m.groupCount() < 1) {
					return newContent;
				}
				String src = m.group(1);

				if (StrUtil.isBlank(src)) {
					return newContent;
				}
				String newSrc = uploadImage(calInfo, appId, appSecret, src);

				if (newSrc != null) {
					newContent = newContent.replaceAll(pairs[i][0], pairs[i][1].replace("#", newSrc));
				}
			}
		}
		return newContent;
	}

	private String uploadImage(CalInfo calInfo, String appId, String appSecret, String orgSrc) {
		_W_MEDIA_UPLOADIMAGE upload = new _W_MEDIA_UPLOADIMAGE();

		URL url = null;
		try {
			url = new URL(CommonUtils.getWebImagePath(orgSrc));
		} catch (MalformedURLException e) {
			LogUtil.warn("Invalid image url: " + orgSrc + ". Error:" + e.getMessage());
			return null;
		}
		if (StrUtil.isBlank(url.getHost())) {
			orgSrc = CommonUtils.getWebImagePath(orgSrc);
		}
		if (IpKit.isTencentDomain(url.getHost())) {
			return null;
		}
		Object ret = null;
		try {
			ret = upload.eval(calInfo, new String[] { appId, appSecret, orgSrc });
		} catch (RengineException | CalculateError e) {
			LogUtil.warn("Failed to upload content image: " + orgSrc + ". Error:" + e.getMessage());
			return null;
		}
		Map<String, Object> retMap = JSONUtil.toMap(ret.toString());
		if ("0".equals(retMap.get("errcode"))) {
			String newSrc = (String) retMap.get("url");
			return newSrc;
		}
		return null;
	}
}
