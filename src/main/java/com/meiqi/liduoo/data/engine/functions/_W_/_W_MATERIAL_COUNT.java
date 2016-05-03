/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MaterialAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetMaterialTotalCountResponse;

/**
 * 所有永久素材总数：
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
	{
	  "errcode":"0",
	   "errmsg":"",
	  "voice_count":COUNT,
	  "video_count":COUNT,
	  "image_count":COUNT,
	  "news_count":COUNT
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_MATERIAL_COUNT extends WeChatFunction {
	public static final String NAME = _W_MATERIAL_COUNT.class.getSimpleName();

	/**
	 * 函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		String key = appId + "@" + appSecret + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetMaterialTotalCountResponse materialCount = noCache ? null
				: (GetMaterialTotalCountResponse) CacheUtils.getCache(key);

		if (materialCount == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MaterialAPI api = new MaterialAPI(config);

			GetMaterialTotalCountResponse result = api.countMaterial();
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			materialCount = result;
			CacheUtils.putCache(key, materialCount);
		}

		return JSON.toJSONString(materialCount);
	}

}
