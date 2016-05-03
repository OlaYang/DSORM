/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
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
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoResponse;

/**
 * 获取粉丝详细信息
 * 
 * <pre>
 *  需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、粉丝OpenID
 * 4、nocache：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSONS String：
 * 
 * {"city":"深圳","country":"中国",
 * "groupid":0,"headimgurl":"http://wx.qlogo.cn/mmopen/IuPY5Lg2TiaNibHoCExLiaVk8Yxy71DNYu1icxnyzdX8evuic6V9nrEnSBe0c9UG44Tf8LJ7dtcrTVaSrgFJ9aCMvJGOtnqdTBicWz/0","language":"zh_CN","nickname":"ss鹏","openid":"o4LBPuJxK03MsOWqoLF1tbrQqAZI","province":"广东","remark":"","sex":1,"subscribe":1,"subscribe_time":1448625321}
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_USER_INFO extends WeChatFunction {
	public static final String NAME = _W_USER_INFO.class.getSimpleName();

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
		GetUserInfoResponse info = noCache ? null : (GetUserInfoResponse) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			UserAPI api = new UserAPI(config);

			GetUserInfoResponse result = api.getUserInfo(openid);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			info = result;
		}
		return JSON.toJSONString(info);
	}

}
