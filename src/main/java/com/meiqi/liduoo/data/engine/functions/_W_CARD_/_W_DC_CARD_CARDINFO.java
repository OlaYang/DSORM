/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.text.ParseException;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.card.CardAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetCardCardInfoResponse;

/**
 * 获取免费券数据接口
 * 
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、开始日期：long类型或者yyyy-MM-dd格式字符串
 * 4、结束日期：long类型或者yyyy-MM-dd格式字符串
 * 4、卡券ID
 * 5、卡券来源，0为公众平台创建的卡券数据、1是API创建的卡券数据
 * 6、nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
    "list": [ 
        { 
            "ref_date": "2014-12-07",  //数据的日期
            card_id：1,//	卡券ID
			card_type:1,//	cardtype:0：折扣券，1：代金券，2：礼品券，3：优惠券，
						//4：团购券（暂不支持拉取特殊票券类型数据，电影票、飞机票、会议门票、景区门票）
          	view_cnt:1,//	浏览次数
			view_user:1,//	浏览人数
			receive_cnt:1,//	领取次数
			receive_user:1,//	领取人数
			verify_cnt:1,//	使用次数
			verify_user:1,//	使用人数
			given_cnt:1,//	转赠次数
			given_user:1,//	转赠人数
			expire_cnt:1,//	过期次数
			expire_user:1,//	过期人数
        }
	//后续还有ref_date在begin_date和end_date之间的数据
    ]
}
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_DC_CARD_CARDINFO extends WeChatFunction {
	public static final String NAME = _W_DC_CARD_CARDINFO.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 5) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String beginStr = DataUtil.getStringValue(args[2]);
		final String endStr = DataUtil.getStringValue(args[3]);
		final String cardId = DataUtil.getStringValue(args[4]);
		final int condSource = Integer.valueOf(DataUtil.getStringValue(args[5]));

		String key = appId + "@" + appSecret + "@" + cardId + "@" + beginStr + "@" + endStr + "@" + condSource + "@"
				+ NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetCardCardInfoResponse summary = noCache ? null : (GetCardCardInfoResponse) CacheUtils.getCache(key);
		if (summary == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			CardAPI api = new CardAPI(config);

			Date beginDate = null, endDate = null;

			try {
				beginDate = CommonUtils.toDate(beginStr, null);
			} catch (ParseException e) {
				throw new RengineException(calInfo.getServiceName(), NAME + "日期格式错误： " + beginStr);
			}
			try {
				endDate = CommonUtils.toDate(endStr, null);
			} catch (ParseException e) {
				throw new RengineException(calInfo.getServiceName(), NAME + "日期格式错误： " + endStr);
			}
			GetCardCardInfoResponse result = api.getCardCardInfo(beginDate, endDate, cardId, condSource);

			if (!result.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);

			summary = result;
		}

		return JSON.toJSONString(summary);
	}

}
