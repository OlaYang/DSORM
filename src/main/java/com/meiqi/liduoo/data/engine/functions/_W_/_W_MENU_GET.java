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
import com.meiqi.liduoo.fastweixin.api.MenuAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.GetMenuResponse;

/**
 * 获取公众号菜单
 *
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
 * {"menu":
 *     {"button":[{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","sub_button":[]},
 *           {"type":"click","name":"歌手简介","key":"V1001_TODAY_SINGER","sub_button":[]},
 *           {"name":"菜单",
 *             "sub_button":[{"type":"view","name":"搜索","url":"http://www.soso.com/",
 *              "sub_button":[]
 *           },
 *           {"type":"view","name":"视频","url":"http://v.qq.com/","sub_button":[]},
 *           {"type":"click","name":"赞一下我们","key":"V1001_GOOD","sub_button":[]}]}]}}
 * }
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_MENU_GET extends WeChatFunction {
	public static final String NAME = _W_MENU_GET.class.getSimpleName();

	/**
	 * 规则函数执行方法
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
		GetMenuResponse menu = noCache ? null : (GetMenuResponse) CacheUtils.getCache(key);
		if (menu == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MenuAPI api = new MenuAPI(config);

			GetMenuResponse result = api.getMenu();
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			menu = result;
		}

		return JSON.toJSONString(menu);
	}

}
