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
import com.meiqi.liduoo.fastweixin.api.response.GetUpstreamMsgMonthResponse;

/**
 * 获取消息发送月数据
 * 
 * 参见<a href=
 * "http://mp.weixin.qq.com/wiki/14/e50e6b75fbf74470f60350ac02570c66.html">微信文档
 * - 消息分析数据接口</a>
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、开始日期：long类型或者yyyy-MM-dd格式字符串
 * 4、结束日期：long类型或者yyyy-MM-dd格式字符串
 * 5、nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
    "list": [ 
        { 
           "ref_date": "2014-12-07", 
           "msg_type": 1,  //消息类型，代表含义如下：1代表文字 2代表图片 3代表语音 4代表视频 6代表第三方应用消息（链接消息）
           "msg_user": 282, 
           "msg_count": 817
       }
	//后续还有同一ref_date的不同msg_type的数据，以及不同ref_date（在时间范围内）的数据
    ]
}
 * </pre>
 * 
 * @author FrankGui 2015年12月15日
 */
public class _W_DC_MSG_MONTH extends WeChatFunction {
	public static final String NAME = _W_DC_MSG_MONTH.class.getSimpleName();

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
		final String beginStr = DataUtil.getStringValue(args[2]);
		final String endStr = DataUtil.getStringValue(args[3]);

		String key = appId + "@" + appSecret + "@" + beginStr + "@" + endStr + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetUpstreamMsgMonthResponse summary = noCache ? null : (GetUpstreamMsgMonthResponse) CacheUtils.getCache(key);
		if (summary == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			DataCubeAPI api = new DataCubeAPI(config);

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
			GetUpstreamMsgMonthResponse result = api.getUpstreamMsgMonth(beginDate, endDate);

			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);

			summary = result;
		}

		return JSON.toJSONString(summary);
	}

}
