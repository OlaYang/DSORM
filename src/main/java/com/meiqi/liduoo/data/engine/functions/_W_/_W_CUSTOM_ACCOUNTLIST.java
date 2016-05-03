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
import com.meiqi.liduoo.fastweixin.api.CustomAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetCustomAccountsResponse;

/**
 * 获取所有客服帐号信息
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：JSON
 * {
 *  errcode :"0",
 *  errmsg :"",
    "kf_list" : [
       {
          "kf_account" : "test1@test",
          "kf_headimgurl" : "http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0",
          "kf_id" : "1001",
          "kf_nick" : "ntest1"
       },
       ...
    ]
 }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_CUSTOM_ACCOUNTLIST extends WeChatFunction {
	public static final String NAME = _W_CUSTOM_ACCOUNTLIST.class.getSimpleName();

	/**
	 * 规则函数执行方法
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
		GetCustomAccountsResponse accounts = noCache ? null : (GetCustomAccountsResponse) CacheUtils.getCache(key);
		if (accounts == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			CustomAPI api = new CustomAPI(config);

			GetCustomAccountsResponse result = api.getCustomAccountList();
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			accounts = result;
		}

		return JSON.toJSONString(accounts);
	}

}
