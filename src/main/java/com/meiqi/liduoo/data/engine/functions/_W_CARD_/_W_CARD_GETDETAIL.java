/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;

/**
 * 查询卡券详情
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID。不填写时默认查询当前appid下的卡券。
 * 
 * 返回JSON：
 *  {
	"errcode":0,
	"errmsg":"ok",
	"card": 
      	{
       		"card_type": "GROUPON",
       		"groupon": {
           		"base_info": {....}
           		"deal_detail": "以下锅底2选1（有菌王锅、麻辣锅、大骨锅、番茄锅、清补 凉锅、酸菜鱼锅可选）：\n大锅1份 12元\n小锅2份 16元 "
       		}
		}
	}
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_CARD_GETDETAIL extends WeChatFunction {
	public static final String NAME = _W_CARD_GETDETAIL.class.getSimpleName();

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
		final String cardId = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		String result = api.getCardDetail(cardId);
		// if (!result.verifyWechatResponse(false, config)) {
		// throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： "
		// + result.toJsonString());
		// }
		return result;// JSON.toJSONString(result);
	}

}
