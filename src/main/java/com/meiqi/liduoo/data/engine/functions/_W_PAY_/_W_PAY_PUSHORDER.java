package com.meiqi.liduoo.data.engine.functions._W_PAY_;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.fastweixin.api.PaymentAPI;
import com.meiqi.liduoo.fastweixin.api.PaymentAPI.TradeType;
import com.meiqi.liduoo.fastweixin.util.PaymentKit;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 统一支付接口
 * 
 * <pre>
 * 需要参数：
 * 1、微信AppId ，default是表示使用默认配置的AppID
 * 2、签名API密钥，用于签名，default是表示使用默认配置//WECHAT_MERCHANT_KEY
 * 3、商户ID，default是表示使用默认配置//WECHAT_MERCHANT_ID
 * 4、商户订单号
 * 5、产品描述
 * 6、支付用户OpenID
 * 7、支付金额（分为单位，原单位为元的话需要先乘100）
 * 8、附加数据，用来查找回调通知时的支付规则
 * 
 * 返回：JSON字符串（共JS端直接使用）：
 * {
    "return_code":"SUCCESS",
    "return_msg":"发放成功",
	"appId":
	"timeStamp":xx
	"nonceStr":xx
	"package":xx
	"signType":xx
	"paySign":xx
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_PAY_PUSHORDER extends WeChatPayFunction {
	public static final String NAME = _W_PAY_PUSHORDER.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 8) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String signKey = getSignKey(DataUtil.getStringValue(args[1]));// WECHAT_MERCHANT_KEY,用于签名
		final String mch_id = getMchId(DataUtil.getStringValue(args[2]));// 同时用于后面发生SSL
		if (StrUtil.isBlank(appId) || StrUtil.isBlank(signKey) || StrUtil.isBlank(mch_id)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： 微信支付相关配置不全");
		}
		final String out_trade_no = DataUtil.getStringValue(args[3]);
		final String product_name = DataUtil.getStringValue(args[4]);
		final String openid = DataUtil.getStringValue(args[5]);
		final String money = DataUtil.getStringValue(args[6]);
		final String attach = DataUtil.getStringValue(args[7]);
		final String client_ip = LdConfigUtil.getDsorm_setver_ip();

		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", appId);// 商户appid
		map.put("out_trade_no", out_trade_no);// 商户系统内部的订单号,32个字符内、可包含字母,
												// 其他说明见商户订单号
		map.put("mch_id", mch_id);// 商户号
		map.put("body", product_name);// 商品或支付单简要描述
		map.put("total_fee", String.valueOf(money));// 付款金额
		map.put("spbill_create_ip", client_ip);
		map.put("trade_type", TradeType.JSAPI.name());
		map.put("nonce_str", PaymentKit.getUUID());// 随机字符串
		map.put("attach", attach);// 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
		map.put("notify_url", LdConfigUtil.getConfig("wechat_pay_notifyURL"));
		map.put("openid", openid);
		map.put("sign", PaymentKit.createSign(map, signKey));// 签名

		try {
			String result = PaymentAPI.pushOrder(map);
			Map<String, String> resultMap = PaymentKit.xmlToMap(result);
			if (!PaymentKit.verifyPayResponse(resultMap, false)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + JSON.toJSONString(resultMap));
			}
			// 上面的通用方法只坚持了return_code,还需要检查result_code
			String result_code = resultMap.get("result_code");
			if (StrUtil.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + JSON.toJSONString(resultMap));
			}

			String prepay_id = resultMap.get("prepay_id");
			Map<String, String> packageParams = new HashMap<String, String>();
			packageParams.put("appId", appId);
			packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
			packageParams.put("nonceStr", System.currentTimeMillis() + "");
			packageParams.put("package", "prepay_id=" + prepay_id);
			packageParams.put("signType", "MD5");
			String packageSign = PaymentKit.createSign(packageParams, signKey);
			packageParams.put("paySign", packageSign);

			packageParams.put("return_code", "SUCCESS");
			packageParams.put("result_code", "SUCCESS");

			return JSON.toJSONString(packageParams);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + e.getMessage());
		}
	}
}