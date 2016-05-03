/**
 * 
 */
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
import com.meiqi.liduoo.fastweixin.api.response.CardUpdateResponse;

/**
 * 创建卡券
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、微信卡券JSON：
 *   {
 *    "card_id":"ph_gmt7cUVrlRk8swPwx7aDyF-pg",
 *     "member_card": {        //填写该cardid相应的卡券类型（小写）。
       		"base_info": {....}
   		    "bonus_cleared": "aaaaaaaaaaaaaa",//积分清零规则。
            "bonus_rules": "aaaaaaaaaaaaaa",//积分规则。
            "prerogative": ""               //特权说明。
	   }
	 }
 * 
 * 返回JSON：
 * {
 * 	errcode :"0",
 * 	errmsg :"",
 *  "send_check":false //是否提交审核，false为修改后不会重新提审，true为修改字段后重新提审，该卡券的状态变为审核中。
 * }
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_UPDATE extends WeChatFunction {
	public static final String NAME = _W_CARD_UPDATE.class.getSimpleName();

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
		
		String cardStr = DataUtil.getStringValue(args[2]);

		cardStr = checkCardLogo(calInfo, appId, appSecret, cardStr);
		
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);
		// Menu menu = JSONUtil.toBean(menuStr, Menu.class);
		CardUpdateResponse result = api.updateCard(cardStr);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
