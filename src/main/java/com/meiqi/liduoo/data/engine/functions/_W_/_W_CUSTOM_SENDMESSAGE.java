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
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.CustomAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * 发布客服消息
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、发送粉丝的OpenID
 * 4、消息类型：text、image、voice、video、music、news
 * 5、发送消息内容JSON字符串:
 * 	Text JSON；  { "content":"Hello World"}
	图片语音JSON：{ "media_id":"MEDIA_ID"}
	视频：{
	      "media_id":"MEDIA_ID",
	      "thumb_media_id":"MEDIA_ID",
	      "title":"TITLE",
	      "description":"DESCRIPTION"
	    }
	音乐： {
	      "title":"MUSIC_TITLE",
	      "description":"MUSIC_DESCRIPTION",
	      "musicurl":"MUSIC_URL",
	      "hqmusicurl":"HQ_MUSIC_URL",
	      "thumb_media_id":"THUMB_MEDIA_ID" 
	    }
	图文消息：
	{"articles": [
	         {
	             "title":"Happy Day",
	             "description":"Is Really A Happy Day",
	             "url":"URL",
	             "picurl":"PIC_URL"
	         },
	         {
	             "title":"Happy Day",
	             "description":"Is Really A Happy Day",
	             "url":"URL",
	             "picurl":"PIC_URL"
	         }
	    ]
	}
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_CUSTOM_SENDMESSAGE extends WeChatFunction {
	public static final String NAME = _W_CUSTOM_SENDMESSAGE.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 5) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String openid = DataUtil.getStringValue(args[2]);
		final String msgType = DataUtil.getStringValue(args[3]);
		final String msg = DataUtil.getStringValue(args[4]);

		// 操作不考虑缓存
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CustomAPI api = new CustomAPI(config);
		Class clazz = CommonUtils.getMsgClass(msgType);
		BaseMsg baseMsg = (BaseMsg) JSONUtil.toBean(msg, clazz);
		BaseResponse result = api.sendCustomMessage(openid, baseMsg);
		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}