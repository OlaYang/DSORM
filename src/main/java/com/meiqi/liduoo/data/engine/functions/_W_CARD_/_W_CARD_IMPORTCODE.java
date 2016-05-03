package com.meiqi.liduoo.data.engine.functions._W_CARD_;

import java.util.ArrayList;
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
import com.meiqi.liduoo.fastweixin.api.response.CardDepositCodeResponse;

/**
 * 导入卡券自定义Code
 * 
 * <pre>
 *需要参数：
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、卡券ID
 * 4、自定义Code列表,用逗号分隔字符串或者JSON格式String数组
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
     succ_code：100,	//成功个数
	 duplicate_code:0,	//重复导入的code会自动被过滤。
	 fail_code:0	//失败个数。
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月28日
 */
public class _W_CARD_IMPORTCODE extends WeChatFunction {
	public static final String NAME = _W_CARD_IMPORTCODE.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 4) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		String cardId = DataUtil.getStringValue(args[2]);
		final String codeStr = DataUtil.getStringValue(args[3]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CardAPI api = new CardAPI(config);

		String[] codeIds = null;
		if (codeStr.startsWith("[")) {
			List<Object> list = JSON.parseArray(codeStr, Object.class);
			codeIds = new String[list.size()];
			for (Object o : list) {
				codeIds[codeIds.length] = o.toString();
			}
		} else {
			codeIds = StringUtils.split(codeStr, ",");
		}

		List<String> succ_code = new ArrayList<String>();
		List<String> duplicate_code = new ArrayList<String>(); // 重复导入的code会自动被过滤。
		List<String> fail_code = new ArrayList<String>();
		Object[] cardIdGroups = splitAry(codeIds, 100);
		for (Object group : cardIdGroups) {
			CardDepositCodeResponse result2 = api.depositCode(cardId, (String[]) group);
			if (!result2.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result2.toJsonString());
			}
			succ_code.addAll( result2.getSuccCode());
			duplicate_code.addAll( result2.getDuplicateCode());
			fail_code.addAll( result2.getFailCode());
		}
		CardDepositCodeResponse result = new CardDepositCodeResponse();
		result.setSuccCode(succ_code);
		result.setFailCode(fail_code);
		result.setDuplicateCode(duplicate_code);

		return JSON.toJSONString(result);
	}

	private Object[] splitAry(String[] ary, int subSize) {
		int count = ary.length % subSize == 0 ? ary.length / subSize : ary.length / subSize + 1;

		List<List<String>> subAryList = new ArrayList<List<String>>();

		for (int i = 0; i < count; i++) {
			int index = i * subSize;

			List<String> list = new ArrayList<String>();
			int j = 0;
			while (j < subSize && index < ary.length) {
				list.add(ary[index++]);
				j++;
			}

			subAryList.add(list);
		}
		Object[] subAry = new Object[subAryList.size()];

		for (int i = 0; i < subAryList.size(); i++) {
			List<String> subList = subAryList.get(i);

			String[] subAryItem = new String[subList.size()];
			for (int j = 0; j < subList.size(); j++) {
				subAryItem[j] = subList.get(j);
			}

			subAry[i] = subAryItem;
		}

		return subAry;
	}
}