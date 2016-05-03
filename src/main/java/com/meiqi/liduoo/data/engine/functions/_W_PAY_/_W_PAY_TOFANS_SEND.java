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
import com.meiqi.liduoo.fastweixin.api.RedPackAPI;
import com.meiqi.liduoo.fastweixin.util.PaymentKit;

/**
 * 发送模板消息
 * 
 * <pre>
 * 需要参数：
 * 1、微信AppId 
 * 2、证书文件路径：WECHAT_MERCHANT_CA_CERT
 * 3、签名API密钥，用于签名//WECHAT_MERCHANT_KEY
 * 4、商户ID： //WECHAT_MERCHANT_ID
 * 5、商户订单号
 * 6、发送用户OpenID
 * 7、用户真实姓名：不检查设置为空即可
 * 8、付款金额（分为单位，原单位为元的话需要先乘100）
 * 9、说明
 * 
 * 返回：JSON字符串：
 * {
    "return_code":"SUCCESS", //FAIL
    "return_msg":"发放成功",
    "result_code":"SUCCESS",
    "mchid":"1245192402",
    "payment_time":"20151224210151",
     "partner_trade_no":"WP365TEST-0002",
     "payment_no":"0010250811201512240373402584", //微信单号	
     "mch_appid":"wx50f87d9aeb64af37",
     "device_info":""
     
    err_code：SYSTEMERROR
	err_code_des：系统繁忙,请稍后再试
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_PAY_TOFANS_SEND extends WeChatPayFunction {
	public static final String NAME = _W_PAY_TOFANS_SEND.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 9) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String keyStoreFile = DataUtil.getStringValue(args[1]); // 证书文件路径
		final String signKey = DataUtil.getStringValue(args[2]);// WECHAT_MERCHANT_KEY,用于签名
		final String mch_id = DataUtil.getStringValue(args[3]);// 同时用于后面发生SSL
		final String mch_billno = DataUtil.getStringValue(args[4]);
		final String re_openid = DataUtil.getStringValue(args[5]);
		final String send_name = DataUtil.getStringValue(args[6]);
		final String money = DataUtil.getStringValue(args[7]);
		final String remark = DataUtil.getStringValue(args[8]);

		Map<String, String> map = new HashMap<String, String>();
		map.put("mch_appid", appId);// 商户appid
		map.put("partner_trade_no", mch_billno);// 商户订单号
		map.put("mchid", mch_id);// 商户号
		map.put("openid", re_openid);// 用户openid
		map.put("re_user_name", send_name);// 收款用户真实姓名。
		map.put("amount", String.valueOf(money));// 付款金额
		map.put("spbill_create_ip", LdConfigUtil.getDsorm_setver_ip());// ip地址
		map.put("desc", remark);// 备注
		map.put("check_name", "OPTION_CHECK");// 针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）

		map.put("nonce_str", PaymentKit.getUUID());// 随机字符串
		map.put("sign", PaymentKit.createSign(map, signKey));// 签名

		try {
			String xml = PaymentKit.toXml(map);

			String localKeyStoreFile = getKeyStoreFile(keyStoreFile, mch_id);
			String result = RedPackAPI.sendToFans(xml, localKeyStoreFile, mch_id);
			Map<String, String> resultMap = PaymentKit.xmlToMap(result);
			if (!PaymentKit.verifyPayResponse(resultMap, false)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + JSON.toJSONString(resultMap));
			}

			return JSON.toJSONString(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + e.getMessage());
		}

	}
}