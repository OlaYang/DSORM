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
import com.meiqi.liduoo.fastweixin.api.response.CardGetMpnewsContentResponse;

/**
 * 获取卡券嵌入图文消息的标准格式代码，将返回代码填入上传图文素材接口中content字段，即可获取嵌入卡券的图文消息素材。
 * <p>
 * 目前该接口仅支持填入非自定义code的卡券,自定义code的卡券需先进行code导入后调用。
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
    "content":"<iframe class=\"res_iframe card_iframe js_editor_card\" data-src=\"http: \/\/mp.weixin.qq.com\/bizmall\/appmsgcard?action=show&biz=MjM5OTAwODk4MA%3D%3D&cardid=p1Pj9jnXTLf2nF7lccYScFUYqJ0&wechat_card_js=1#wechat_redirect\">"
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月28日
 */
public class _W_CARD_MPNEWSCONTENT extends WeChatFunction {
	public static final String NAME = _W_CARD_MPNEWSCONTENT.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 3) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		String cardId = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		CardGetMpnewsContentResponse result = api.getMpNewsContent(cardId);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}