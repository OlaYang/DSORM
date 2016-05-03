/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.data.engine.functions._W_._W_MEDIA_UPLOADIMAGE;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.CardCreateResponse;
import com.meiqi.liduoo.fastweixin.util.IpKit;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 创建卡券
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、微信卡券JSON：
 *   （1）团购券：card_type=GROUPON
 *   （2）代金券：card_type	=CASH
 *   （3）折扣券：card_type	=DISCOUNT
 *   （4）礼品券：card_type=GIFT
 *   （5）优惠券：card_type=GENERAL_COUPON
 *   
 *   所有卡券，base_info部分含义相同，JSON格式大致如下
 *   (详情参照文档http://mp.weixin.qq.com/wiki/15/e33671f4ef511b77755142b37502928f.html)：
 *   {
      	"card": 
      	{
       		"card_type": "GROUPON",
       		"groupon": {
           		"base_info": {....}
           		"deal_detail": "以下锅底2选1（有菌王锅、麻辣锅、大骨锅、番茄锅、清补 凉锅、酸菜鱼锅可选）：\n大锅1份 12元\n小锅2份 16元 "
       		}
		}
	}
 * 
 * 返回JSON：
 * {
 * 	errcode :"0",
 * 	errmsg :"",
 *  card_id:""
 * }
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_CREATE extends WeChatFunction {
	public static final String NAME = _W_CARD_CREATE.class.getSimpleName();

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
		CardCreateResponse result = api.createCard(cardStr);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}


}
