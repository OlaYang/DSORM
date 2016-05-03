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
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MenuAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.Menu;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * 生成公众号菜单
 * 
 * <pre>
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、menu JSON 字符串:
 *  {
     "button":[
     {	
          "type":"CLICK",
          "name":"今日歌曲",
          "key":"V1001_TODAY_MUSIC"
      },
      {
           "name":"菜单",
           "sub_button":[
           {	
               "type":"VIEW",
               "name":"搜索",
               "url":"http://www.soso.com/"
            },
            {
               "type":"VIEW",
               "name":"视频",
               "url":"http://v.qq.com/"
            },
            {
               "type":"CLICK",
               "name":"赞一下我们",
               "key":"V1001_GOOD"
            }]
       }],
       "matchrule":{
		  "group_id":"2",
		  "sex":"1",
		  "country":"中国",
		  "province":"广东",
		  "city":"广州",
		  "client_platform_type":"2"
	  }
 }
 * 
 * 返回JSON：
 * {
 * errcode :"0",
 * errmsg :"",
 * }
 * </pre>
 * 
 * @author FrankGui 2015年12月12日
 */
public class _W_MENU_CREATE extends WeChatFunction {
	public static final String NAME = _W_MENU_CREATE.class.getSimpleName();

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
		final String menuStr = DataUtil.getStringValue(args[2]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		MenuAPI api = new MenuAPI(config);
		Menu menu = JSONUtil.toBean(menuStr, Menu.class);
		BaseResponse result = api.createMenu(menu);
		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}
		return JSON.toJSONString(result);
	}

}
