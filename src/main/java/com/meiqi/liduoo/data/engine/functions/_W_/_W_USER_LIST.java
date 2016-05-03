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
import com.meiqi.liduoo.fastweixin.api.response.GetUsersResponse;

/**
 * 获取粉丝列表
 * 
 * <pre>
 * 1、微信AppId
 * 2、微信AppSecret
 * 3、下一个粉丝OpenID
 * 4、nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
  "total":23000,
  "count":10000,
  "data":{"
     openid":[
        "OPENID1",
        "OPENID2",
        ...,
        "OPENID10000"
     ]
   },
   "next_openid":"OPENID10000"
}
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_USER_LIST extends WeChatFunction {
	public static final String NAME = _W_USER_LIST.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 3) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String nextOpenId = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + nextOpenId + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetUsersResponse users = noCache ? null : (GetUsersResponse) CacheUtils.getCache(key);
		if (users == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			UserAPI api = new UserAPI(config);

			GetUsersResponse result = api.getUsers(nextOpenId);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);

			users = result;
		}

		return JSON.toJSONString(users);
	}

}
