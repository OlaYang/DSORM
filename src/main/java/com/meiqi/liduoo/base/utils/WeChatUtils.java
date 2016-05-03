package com.meiqi.liduoo.base.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.OauthScope;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 微信渠道相关的通用静态方法（适用于函数内部等无法Autowired方式创建实例的场景）
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午1:13:31
 */
public class WeChatUtils {
	// static IChannelService channelBean = null;
	//
	// static {
	// ApplicationContext applicationContext =
	// MyApplicationContextUtil.getContext();
	// channelBean = (IChannelService)
	// applicationContext.getBean("channelServiceImpl");
	// }
	//
	// public static Map<String, String> getChannelProperties(int channelId) {
	// return channelBean.getChannelProperties(channelId);
	// }
	//
	// public static String getChannelProperty(int channelId, String propName) {
	// return channelBean.getChannelProperty(channelId, propName);
	// }

	public static ApiConfig initApiConfig(String appId, String appSecret) {
		return ApiConfig.getInstance(appId, appSecret);
	}

	/**
	 * 获取重定向URL
	 * 
	 * @param appId
	 * @param redirectUrl
	 * @param scope
	 * @param state
	 * @return
	 */
	public static String getOauthPageUrl(String appId, String redirectUrl, OauthScope scope, String state) {
		BeanUtil.requireNonNull(redirectUrl, "redirectUrl is null");
		BeanUtil.requireNonNull(scope, "scope is null");
		String userState = StrUtil.isBlank(state) ? "STATE" : state;
		String url = null;
		if (redirectUrl.indexOf("/") >= 0) {
			try {
				url = URLEncoder.encode(redirectUrl, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LogUtil.error(e);
			}
		}
		StringBuilder stringBuilder = new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?");
		stringBuilder.append("appid=").append(appId).append("&redirect_uri=").append(url)
				.append("&response_type=code&scope=").append(scope.toString()).append("&state=").append(userState)
				.append("#wechat_redirect");
		return stringBuilder.toString();
	}
}
