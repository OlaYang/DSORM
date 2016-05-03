package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MessageAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetSendMessageResponse;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * 消息预览
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、消息类型：text、image、voice、video、music、mpnews、wxcard(注意此处不支持news类型 )
 * 4、发送消息内容JSON字符串
 * 5、预览用户的OpenID或者微信ID号
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
    "msg_id":34182,  //消息发送任务的ID
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MESSAGE_PREVIEW extends WeChatFunction {
	public static final String NAME = _W_MESSAGE_PREVIEW.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 5) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		String msgType = DataUtil.getStringValue(args[2]);
		String msg = DataUtil.getStringValue(args[3]);
		final String openid = DataUtil.getStringValue(args[4]);
		
		if("news".equalsIgnoreCase(msgType)) {
			msgType = "mpnews";//发送消息时强制将news类型改成mpnews，msg JSON字符串中不管他
		}
		msg = msg.replaceAll("\"msgType\"\\s*:\\s*\"news\"", "\"msgType\":\"mpnews\"");
		
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		MessageAPI api = new MessageAPI(config);
		
		Class clazz = CommonUtils.getMsgClass(msgType);
		BaseMsg baseMsg = (BaseMsg) JSONUtil.toBean(msg, clazz);
		GetSendMessageResponse result = api.previewMessage(baseMsg, openid);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}