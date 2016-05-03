/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.util.internal.StringUtil;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.UserAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.UserInfo;
import com.meiqi.liduoo.fastweixin.api.response.GetUserInfoListResponse;

/**
 * 批量获取粉丝详细信息
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、粉丝OpenID列表，用逗号分隔字符串或者JSON格式String数组
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 *  errcode:0,
 *  errmsg:"",
 *  "user_info_list":[ {"city":"深圳","country":"中国",
 * "groupid":0,"headimgurl":"","language":"zh_CN","nickname":"ss鹏",
 * "openid":"o4LBPuJxK03MsOWqoLF1tbrQqAZI","province":"广东",
 * "remark":"","sex":1,"subscribe":1,"subscribe_time":1448625321},{}]
 
 * }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_USER_INFOLIST extends WeChatFunction {
	public static final String NAME = _W_USER_INFOLIST.class.getSimpleName();

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
		String openIdStr = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + openIdStr + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetUserInfoListResponse info = noCache ? null : (GetUserInfoListResponse) CacheUtils.getCache(key);

		if (info == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			UserAPI api = new UserAPI(config);
			List<UserInfo> userList = new ArrayList<UserInfo>();

			if (openIdStr.startsWith("[")) {
				List<String> list = JSON.parseArray(openIdStr, String.class);
				for (String str : list) {
					userList.add(new UserInfo(str));
				}
			} else {
				String[] strs = StringUtil.split(openIdStr, ',');
				for (String str : strs) {
					userList.add(new UserInfo(str));
				}
			}
			GetUserInfoListResponse result = api.getUserInfoList(userList);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			info = result;
			CacheUtils.putCache(key, info);
		}

		return JSON.toJSONString(info);
	}

}
