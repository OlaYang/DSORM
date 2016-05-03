package com.meiqi.liduoo.fastweixin.api;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.liduoo.fastweixin.util.DateKit;
import com.meiqi.liduoo.fastweixin.util.HttpBaseKit;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * SMS发送API
 *
 * @author FrankGui
 */
public class SmsAPI {

	public static void main(String args[]) throws Exception {
		String result = SmsAPI.sendVerifyCode("bind_user", "13632552289", "234567");
		// if (!PaymentKit.verifyPayResponse(resultMap, false)) {
		// throw new RengineException(calInfo.getServiceName(), NAME +
		// "出现错误： " + JSON.toJSONString(resultMap));
		// }
		Map t = JSONUtil.toMap(result);
		JSONObject obj = (JSONObject)t.get("resp");
		int  i = obj.getIntValue("respCode");
		System.out.println(result);
	}

	// 短信发送代理对象
	private static final SmsDelegate delegate;

	static {
		delegate = new YunzhixunDelegate();
	}

	private SmsAPI() {
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param smsType
	 *            验证码类型，目前支持bind_user,join_act,isv_register,reset_password四种，
	 *            对应不同短信消息模板
	 * @param mobile
	 *            手机号
	 * @param verify_code
	 *            验证码
	 * @return
	 * @throws IOException
	 */
	public static String sendVerifyCode(String smsType, String mobile, String verify_code) throws IOException {
		return delegate.sendVerifyCode(smsType, mobile, verify_code);
	}

	/**
	 * 短信发送工具 委托
	 */
	private interface SmsDelegate {
		String sendVerifyCode(String smsType, String mobile, String verify_code) throws IOException;
	}

	/**
	 * 短信发送实现类
	 */
	private static class YunzhixunDelegate implements SmsDelegate {
		private static int UCPASS_TEMPLATE_ID_BIND_USER = 9135;
		private static int UCPASS_TEMPLATE_ID_JOIN_ACT = 10104;
		private static int UCPASS_TEMPLATE_ID_ISV_REGISTER = 17315;
		private static int UCPASS_TEMPLATE_ID_RESET_PASSWORD = 17314;
		private static String UCPASS_ACCOUNT_SID = "0c591bdce0905bbc07ac56a012ccb33e";
		private static String UCPASS_TOKEN = "a698d04d4a4d43a543ca34573860bd7d";
		private static String UCPASS_LIDUOO_APP_ID = "1a30cea6c75b41839a6de9363125e7b9";
		private static String UCPASS_SOFT_VERSION = "2014-06-30";

		@Override
		public String sendVerifyCode(String smsType, String mobile, String verify_code) throws IOException {
			int templateId = getTemplateId(smsType);
			String timestamp = DateKit.dateToStr(new Date(), DateKit.DATE_TIME_NO_SLASH);// 时间戳
			String signature = MD5Util.MD5(UCPASS_ACCOUNT_SID + UCPASS_TOKEN + timestamp).toUpperCase();
			StringBuilder sb = new StringBuilder("https://api.ucpaas.com/");

			String url = sb.append(UCPASS_SOFT_VERSION).append("/Accounts/").append(UCPASS_ACCOUNT_SID)
					.append("/Messages/templateSMS").append("?sig=").append(signature).toString();
			Map<String, String> templateSMS = new HashMap<String, String>();
			templateSMS.put("appId", UCPASS_LIDUOO_APP_ID);
			templateSMS.put("templateId", String.valueOf(templateId));
			templateSMS.put("to", mobile);
			templateSMS.put("param", verify_code);
			String body = JSON.toJSONString(templateSMS);
			body = "{\"templateSMS\":" + body + "}";
			String result = post(timestamp, url, body);
			// {"resp":{"respCode":"000000","templateSMS":{"createDate":"20151225191922","smsId":"7517f912593138a50af15f53eed35157"}}}
			return result;
		}

		private int getTemplateId(String smsType) {
			if (StrUtil.isBlank(smsType)) {
				throw new IllegalArgumentException("短信类型码才能为空");
			}
			smsType = smsType.toUpperCase().trim();
			if ("BIND_USER".equals(smsType)) {
				return UCPASS_TEMPLATE_ID_BIND_USER;
			} else if ("JOIN_ACT".equals(smsType)) {
				return UCPASS_TEMPLATE_ID_JOIN_ACT;
			} else if ("ISV_REGISTER".equals(smsType)) {
				return UCPASS_TEMPLATE_ID_ISV_REGISTER;
			} else if ("RESET_PASSWORD".equals(smsType)) {
				return UCPASS_TEMPLATE_ID_RESET_PASSWORD;
			}
			throw new IllegalArgumentException("无法识别的短信类型码：" + smsType);
		}

		private String post(String timestamp, String url, String body) throws IOException {
			Map<String, String> headers = new HashMap<String, String>();
			String src = UCPASS_ACCOUNT_SID + ":" + timestamp;
			// String auth = Base64.encodeBase64String(src.getBytes("UTF-8"));
			String auth = new sun.misc.BASE64Encoder().encode(src.getBytes("UTF-8"));
			headers.put("Accept", "application/json");
			headers.put("Content-Type", "application/json;charset=utf-8");
			headers.put("Authorization", auth);
			String ret = HttpBaseKit.post(url, body, headers);
			return ret;
		}
	}

}
