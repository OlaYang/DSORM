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
 * 6、发送方名称，如企业名称
 * 7、发送用户OpenID
 * 8、发送金额（分为单位，原单位为元的话需要先乘100）
 * 9、发放数量
 * 10、祝福语
 * 11、活动名称
 * 12、备注
 * 
 * 返回：JSON字符串：
 * {
    "return_code":"SUCCESS",
    "return_msg":"发放成功",
    "total_amount":"100",
    "result_code":"SUCCESS",
    "mch_id":"1245192402",
    "send_time":"20151224210151",
     "mch_billno":"WP365TEST-0002",
     "send_listid":"0010250811201512240373402584", //微信单号	
     "wxappid":"wx50f87d9aeb64af37",
     "re_openid":"oIC7osr4xCGFzqrVrd1ZN1wtEW1g",
     "#text":"\n",
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_PAY_REDPACK_SEND extends WeChatPayFunction {
	public static final String NAME = _W_PAY_REDPACK_SEND.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 12) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String keyStoreFile = DataUtil.getStringValue(args[1]); // 证书文件路径
		final String signKey = DataUtil.getStringValue(args[2]);// WECHAT_MERCHANT_KEY,用于签名
		final String mch_id = DataUtil.getStringValue(args[3]);// 同时用于后面发生SSL
		final String mch_billno = DataUtil.getStringValue(args[4]);
		final String send_name = DataUtil.getStringValue(args[5]);
		final String re_openid = DataUtil.getStringValue(args[6]);
		final String money = DataUtil.getStringValue(args[7]);
		final String total_num = DataUtil.getStringValue(args[8]);
		final String wishing = DataUtil.getStringValue(args[9]);
		final String act_name = DataUtil.getStringValue(args[10]);
		final String remark = DataUtil.getStringValue(args[11]);
		final String client_ip = LdConfigUtil.getDsorm_setver_ip();

		Map<String, String> map = new HashMap<String, String>();
		map.put("wxappid", appId);// 商户appid
		map.put("mch_billno", mch_billno);// 商户订单
		map.put("mch_id", mch_id);// 商户号
		map.put("nick_name", send_name);// 提供方名称
		map.put("send_name", send_name);// 用户名
		map.put("re_openid", re_openid);// 用户openid
		map.put("total_amount", String.valueOf(money));// 付款金额
		map.put("min_value", String.valueOf(money));// 最小红包
		map.put("max_value", String.valueOf(money));// 最大红包
		map.put("total_num", String.valueOf(total_num));// 红包发送总人数
		map.put("wishing", wishing);// 红包祝福语
		map.put("client_ip", client_ip);// ip地址
		map.put("act_name", act_name);// 活动名称
		map.put("remark", remark);// 备注
		map.put("nonce_str", PaymentKit.getUUID());// 随机字符串

		map.put("sign", PaymentKit.createSign(map, signKey));// 签名

		try {
			String xml = PaymentKit.toXml(map);

			String localKeyStoreFile = getKeyStoreFile(keyStoreFile, mch_id);
			String result = RedPackAPI.sendRedPack(xml, localKeyStoreFile, mch_id);
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