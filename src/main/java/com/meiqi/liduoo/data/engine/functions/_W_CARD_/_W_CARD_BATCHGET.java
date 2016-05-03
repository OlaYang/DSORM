package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import com.meiqi.liduoo.fastweixin.api.response.CardBatchGetResponse;

/**
 * 批量查询卡券列表
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、offset: 查询卡列表的起始偏移量，从0开始，即offset: 5是指从从列表里的第六个开始读取。
 * 4、数量 : 需要查询的卡片的数量（数量最大50）。
 * 5、【可选】指定状态的卡券列表，例：仅拉出通过审核的卡券。
  		“CARD_STATUS_NOT_VERIFY”,待审核；
		“CARD_STATUS_VERIFY_FAIL”,审核失败；
		“CARD_STATUS_VERIFY_OK”，通过审核；
		“CARD_STATUS_USER_DELETE”，卡券被商户删除；
		“CARD_STATUS_DISPATCH”，在公众平台投放过的卡券
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
	"card_id_list":["ph_gmt7cUVrlRk8swPwx7aDyF-pg"],
  	"total_num":1
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月28日
 */
public class _W_CARD_BATCHGET extends WeChatFunction {
	public static final String NAME = _W_CARD_BATCHGET.class.getSimpleName();

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
		final int offset = Integer.valueOf(DataUtil.getStringValue(args[2]));
		int count = Integer.valueOf(DataUtil.getStringValue(args[3]));
		if (count > 50) {
			count = 50;
		}
		String statusStr = args.length > 4 ? DataUtil.getStringValue(args[4]) : "";
		statusStr = statusStr == null ? "" : statusStr.toUpperCase();
		
		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);
		String[] statusArr = null;
		if (statusStr.startsWith("[")) {
			List<String> list = JSON.parseArray(statusStr, String.class);
			statusArr = (String[]) list.toArray();
		} else {
			statusArr = StringUtils.split(statusStr, ",");
		}
		CardBatchGetResponse result = api.batchGet(offset, count, statusArr);
		if (!result.verifyWechatResponse(false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}