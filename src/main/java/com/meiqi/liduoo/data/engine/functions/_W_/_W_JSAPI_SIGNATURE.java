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
import com.meiqi.liduoo.fastweixin.api.JsAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetSignatureResponse;

/**
 * * 获取js-sdk所需的签名，给调用者最大的自由度，控制生成签名的参数
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、当前网页的URL，不包含#及其后面部分
 * 4、nonceStr:随机字符串
 * 5、timestame：时间戳
 * 
 * 4、5两个参数可选，但是必须同时提供或者不提供，如果不提供，则自动生成随机字符串和时间戳
 *           
 * 
 * 返回JSON：
 * {
 * 	errcode :"0",
 * 	errmsg :"",
 *  "appId":"",
 * 	"nonceStr" : "",
 * 	"timestamp":timestamp,
 * 	"url"    :url,
 * 	"signature" :signature, //生成的签名
 * }
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_JSAPI_SIGNATURE extends WeChatFunction {
	public static final String NAME = _W_JSAPI_SIGNATURE.class.getSimpleName();

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
		final String url = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		JsAPI api = new JsAPI(config);
		GetSignatureResponse result = null;

		if (args.length == 3) {
			result = api.getSignature(url);
		} else if (args.length == 4) {
			throw new ArgsCountError(NAME);
		} else {
			String nonceStr = DataUtil.getStringValue(args[3]);
			Long timestame = (Long) DataUtil.getNumberValue(args[4]);
			result = api.getSignature(nonceStr, timestame, url);
		}
		if (!result.verifyWechatResponse( false,config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		result.setAppId(appId);
		
		return JSON.toJSONString(result);
	}

}
