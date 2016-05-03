/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.Map;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.fastweixin.api.RMIAPI;
import com.meiqi.liduoo.fastweixin.api.WxWebAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 通用微信API调用函数
 * 
 * <pre>
 * 调用参数：
 * 1、调用的微信API名称
 * 2、微信公众号ＩＤ
 * 3、接口报文
 * 
 * 返回值：
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class WX_WEB_API extends Function {
	public static final String NAME = WX_WEB_API.class.getSimpleName();

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
		final String apiName = DataUtil.getStringValue(args[0]);
		final int wxid = Integer.valueOf(DataUtil.getStringValue(args[1]));
		final String json = DataUtil.getStringValue(args[2]);
		try {
			Map<String, String> configMap = RMIAPI.getWxConfig(wxid);
			ApiConfig config = WeChatUtils.initApiConfig(configMap.get("WECHAT_APPID"),
					configMap.get("WECHAT_APPSECRET"));

			WxWebAPI api = new WxWebAPI(config);

			BaseResponse result = api.apiCall(apiName, json);
			if (!result.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}

			// return JSON.toJSONString(result);
			// 正确执行时，返回值JSON在：result.getErrmsg();
			return result.getErrmsg();
			
		} catch (RengineException ex) {
			throw ex;
		} catch (Exception ex2) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + ex2.getMessage());
		}
	}

}
