/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.SystemAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;

/**
 * 如果公众号基于安全等考虑，需要获知微信服务器的IP地址列表，以便进行相关限制，可以通过该接口获得微信服务器IP地址列表。
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：JSON
 * {"127.0.0.1","..."}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SYS_CALLBACKIP extends WeChatFunction {
	public static final String NAME = _W_SYS_CALLBACKIP.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));

		String key = appId + "@" + appSecret + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		List<String> ipList = noCache ? null : (List<String>) CacheUtils.getCache(key);

		if (ipList == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			SystemAPI api = new SystemAPI(config);

			List<String> result = api.getCallbackIP();
			CacheUtils.putCache(key, result);
			ipList = result;
		}

		return JSON.toJSONString(ipList);
	}

}