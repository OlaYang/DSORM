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
import com.meiqi.liduoo.fastweixin.api.SystemAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;

/**
 * 将一条长链接转成短链接。
 * 
 * 主要使用场景： 开发者用于生成二维码的原链接（商品、支付二维码等）太长导致扫码速度和成功率下降，将原长链接通过此接口转成短链接再生成二维码
 * 将大大提升扫码速度和成功率。
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、Long URL
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：
 *   字符串URL
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SYS_SHORTURL extends WeChatFunction {
	public static final String NAME = _W_SYS_SHORTURL.class.getSimpleName();

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
		final String longurl = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + longurl + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		String shortUrl = noCache ? null : (String) CacheUtils.getCache(key);

		if (shortUrl == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			SystemAPI api = new SystemAPI(config);

			String result = api.getShortUrl(longurl);
			CacheUtils.putCache(key, result);
			shortUrl = result;
		}

		return shortUrl;
	}

}