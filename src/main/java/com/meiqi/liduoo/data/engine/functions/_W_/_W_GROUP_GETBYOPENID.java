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

/**
 * 根据粉丝OpenID得到起所属的组信息：
 * 
 * 参数：
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、粉丝OpenID
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：
 *  字符串Group Name
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_GROUP_GETBYOPENID extends WeChatFunction {
	public static final String NAME = _W_GROUP_GETBYOPENID.class.getSimpleName();

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
		final String openid = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + openid + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		String group = noCache ? null : (String) CacheUtils.getCache(key);

		if (group == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			UserAPI api = new UserAPI(config);

			String result = api.getGroupIdByOpenid(openid);
			CacheUtils.putCache(key, result);
			group = result;
		}

		return group;
	}

}
