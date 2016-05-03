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
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.CustomAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetCustomOnlineAccountsResponse;

/**
 * 获取所有在线客服帐号信息
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
   "kf_online_list": [
       {
           "kf_account": "test1@test", 
           "status": 1, 
           "kf_id": "1001", 
           "auto_accept": 0, 
           "accepted_case": 1
       },
       {
           "kf_account": "test2@test", 
           "status": 1, 
           "kf_id": "1002", 
           "auto_accept": 0, 
           "accepted_case": 2
       }
   ]
 }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_CUSTOM_ONLINELIST extends WeChatFunction {
	public static final String NAME = _W_CUSTOM_ONLINELIST.class.getSimpleName();

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
		
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CustomAPI api = new CustomAPI(config);

		GetCustomOnlineAccountsResponse result = api.getCustomOnlineAccountList();
		if (!result.verifyWechatResponse( false,config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}
