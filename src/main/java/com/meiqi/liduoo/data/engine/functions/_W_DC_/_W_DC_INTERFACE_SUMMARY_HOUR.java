/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_DC_;

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
import com.meiqi.liduoo.fastweixin.api.DataCubeAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetInterfaceSummaryHourResponse;

/**
 * 获取接口发送每日分时数据
 * 
 * 参见<a href=
 * "http://mp.weixin.qq.com/wiki/13/32ef6cbf7bf4454c5826edf0136a2431.html">微信文档
 * - 接口分析数据接口</a>
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、日期：long类型或者yyyy-MM-dd格式字符串
 * 4、nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
    "list": [ 
         { 
           "ref_date": "2014-12-01", 
           "ref_hour": 0, 
           "callback_count": 331, 
           "fail_count": 18, 
           "total_time_cost": 167870, 
           "max_time_cost": 5042
       }
	//后续还有不同ref_hour的数据
    ]
}
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_DC_INTERFACE_SUMMARY_HOUR extends WeChatFunction {
	public static final String NAME = _W_DC_INTERFACE_SUMMARY_HOUR.class.getSimpleName();

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
		final String dayStr = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + dayStr + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetInterfaceSummaryHourResponse summary = noCache ? null
				: (GetInterfaceSummaryHourResponse) CacheUtils.getCache(key);
		if (summary == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			DataCubeAPI api = new DataCubeAPI(config);

			Date day = null;

			try {
				day = CommonUtils.toDate(dayStr, "yyyy-MM-dd");
			} catch (ParseException e) {
				throw new RengineException(calInfo.getServiceName(), NAME + "日期格式错误： " + dayStr);
			}
			GetInterfaceSummaryHourResponse result = api.getInterfaceSummaryHour(day);

			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);

			summary = result;
		}

		return JSON.toJSONString(summary);
	}

}
