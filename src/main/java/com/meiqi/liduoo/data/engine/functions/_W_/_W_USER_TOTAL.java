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
import com.meiqi.liduoo.fastweixin.api.UserAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetUsersResponse;

/**
 * 所有粉丝总数：
 * 
 * <pre>
 * 参数
 * 1、微信AppId
 * 2、微信AppSecret
 * 3、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：粉丝总数
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_USER_TOTAL extends WeChatFunction {
	public static final String NAME = _W_USER_TOTAL.class.getSimpleName();

	/**
	 * 函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		String key = appId + "@" + appSecret + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		Long count = noCache ? null : (Long) CacheUtils.getCache(key);
		if (count == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			;
			UserAPI api = new UserAPI(config);

			GetUsersResponse result = api.getUsers(null);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			count = result.getTotal();
			CacheUtils.putCache(key, count);
		}

		return count;
	}

}
