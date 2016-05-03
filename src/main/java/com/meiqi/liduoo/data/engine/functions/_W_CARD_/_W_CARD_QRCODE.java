package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.QrcodeType;
import com.meiqi.liduoo.fastweixin.api.response.QrcodeResponse;

/**
 * 生成带参数的二维码
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、二维码类型，QR_CARD为单个卡券，QR_MULTI_CARD为多个
 * 4、卡券信息，JSON格式
 * 5、该二维码有效时间，以秒为单位。不填默认永久有效
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
 * "ticket":"==", //获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
 * "expire_seconds":60, //该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。
 * "url":"http:\/weixin.qq.com/q/kZgfwMTm72WWPkovabbI" //二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
 * }
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_CARD_QRCODE extends WeChatFunction {
	public static final String NAME = _W_CARD_QRCODE.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 4) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String actionNameStr = DataUtil.getStringValue(args[2]);
		QrcodeType qrcodeType = QrcodeType.QR_SCENE;
		if (actionNameStr.toUpperCase().indexOf("MULTIPLE") >= 0) {
			qrcodeType = QrcodeType.QR_MULTIPLE_CARD;
		} else {
			qrcodeType = QrcodeType.QR_CARD;
		}
		final String cardInfo = DataUtil.getStringValue(args[3]);
		Integer expireSeconds = 0;// 卡券二维码不填默认永久有效
		if (args.length > 4) {
			expireSeconds = Integer.getInteger(DataUtil.getStringValue(args[4]));
		}

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		QrcodeResponse result = api.createQrcode(qrcodeType, cardInfo, expireSeconds);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
