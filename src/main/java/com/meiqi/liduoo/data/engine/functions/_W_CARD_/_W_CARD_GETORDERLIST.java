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
 * 查询流水详情接口
 * 
 * 本接口用于查询券点的流水详情。
 * 
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、过滤条件JSON：
 * 		{
           "offset": 0,
           "count": 5,
           "order_type": "ORDER_TYPE_WXPAY",
           "nor_filter": {
               "status": "ORDER_STATUS_SUCC"
           },
           "sort_info": {
               "sort_key": "SORT_BY_TIME",
               "sort_type": "SORT_DESC"
           },
           "begin_time": "1440420538",
           "end_time": "1450713203"
		}
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
	"total_num": 1,
 	"order_list": [
	   {
	     "order_id": "100005790120151221401000171",
	     "status": "ORDER_STATUS_FINANCE_SUCC",
	     "create_time": 1450712798,
	     "pay_finish_time": 1450712905,
	     "desc": "微信支付充值",
	     "free_coin_count": "0",
	     "pay_coin_count": "1",
	     "refund_free_coin_count": "0",
	     "refund_pay_coin_count": "0",
	     "openid": "oWE-GwF1gGoyVVZC5PG6GXd4cKMY",
	     "order_type": "ORDER_TYPE_WXPAY"
	   }
	 ]
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月30日
 */
public class _W_CARD_GETORDERLIST extends WeChatFunction {
	public static final String NAME = _W_CARD_GETORDERLIST.class.getSimpleName();

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
		final String filterJson = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		String result = api.getOrderList(filterJson);
		// if (!result.verifyWechatResponse(false, config)) {
		// throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： "
		// + result.toJsonString());
		// }

		return result;// JSON.toJSONString(result);
	}

}