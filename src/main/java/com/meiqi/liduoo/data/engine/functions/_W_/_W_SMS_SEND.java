package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.SmsAPI;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 发送短信验证码
 * 
 * <pre>
 * 需要参数：
 * 1、验证码类型：bind_user,reset_password,isv_register,join_act 
 * 2、手机号码
 * 
 * 返回：JSON字符串：
 * {
    "errcode":"0",
    "errmsg":"发放成功",
    "verify_code":"100",
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SMS_SEND extends WeChatFunction {
	public static final String NAME = _W_SMS_SEND.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String smsType = DataUtil.getStringValue(args[0]);
		final String mobile = DataUtil.getStringValue(args[1]); // 证书文件路径
		final String verify_code = StrUtil.getRandNum(6);

		try {
			String result = SmsAPI.sendVerifyCode(smsType, mobile, verify_code);
			// {"resp":{"respCode":"000000","templateSMS":{"createDate":"20151225191922","smsId":"7517f912593138a50af15f53eed35157"}}}
			Map<String, Object> t = JSONUtil.toMap(result);
			JSONObject obj = (JSONObject) t.get("resp");
			int intValue = obj.getIntValue("respCode");
			if (intValue == 0) {
				return "{\"errcode\":0,\"errmsg\":\"\",\"verify_code\":\"" + verify_code + "\"}";
			} else {
				return "{\"errcode\":\"" + intValue + "\",\"errmsg\":\"发送失败\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + e.getMessage());
		}

	}

}