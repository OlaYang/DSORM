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
import com.meiqi.liduoo.fastweixin.api.enums.MaterialType;
import com.meiqi.liduoo.fastweixin.api.response.GetMaterialListResponse;

/**
 * 获取永久素材的列表，也会包含公众号在公众平台官网素材管理模块中新建的图文消息、语音、视频等素材（但需要先通过获取素材列表来获知素材的media_id）
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、素材类型：image、news、voice、video
 * 4、offset:从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
 * 5、count：返回素材的数量，取值在1到20之间
 * 6、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：JSON字符串：
 * 1、永久图文消息素材列表的响应如下：
	{
	 	"errcode":"0",
	 	"errmsg":"",
	   "total_count": TOTAL_COUNT,
	   "item_count": ITEM_COUNT,
	   "item": [{
	       "media_id": MEDIA_ID,
	       "content": {
	           "news_item": [{
	               "title": TITLE,
	               "thumb_media_id": THUMB_MEDIA_ID,
	               "show_cover_pic": SHOW_COVER_PIC(0 / 1),
	               "author": AUTHOR,
	               "digest": DIGEST,
	               "content": CONTENT,
	               "url": URL,
	               "content_source_url": CONTETN_SOURCE_URL
	           },
	           //多图文消息会在此处有多篇文章
	           ]
	        },
	        "update_time": UPDATE_TIME
	    },
	    //可能有多个图文消息item结构
	  ]
	}
------------------------------------------
	2、其他类型（图片、语音、视频）的返回如下：
  {
 	"errcode":"0",
 	"errmsg":"",
 	"total_count": TOTAL_COUNT,
    "item_count": ITEM_COUNT,
    "item": [{
	       "media_id": MEDIA_ID,
	       "name": NAME,
	       "update_time": UPDATE_TIME,
	       "url":URL
   		},
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MATERIAL_BATCHGET extends WeChatFunction {
	public static final String NAME = _W_MATERIAL_BATCHGET.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 5) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String mediaTypeStr = DataUtil.getStringValue(args[2]);
		MaterialType mediaType = Enum.valueOf(MaterialType.class, mediaTypeStr.toUpperCase());
		final Integer offset = Integer.valueOf(DataUtil.getStringValue(args[3]));
		final Integer count = Integer.valueOf(DataUtil.getStringValue(args[4]));

		String key = appId + "@" + appSecret + "@" + offset + "@" + count + "@" + NAME + "@" + mediaType;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		GetMaterialListResponse mediaInfo = noCache ? null : (GetMaterialListResponse) CacheUtils.getCache(key);

		if (mediaInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MaterialAPI api = new MaterialAPI(config);

			GetMaterialListResponse result = api.batchGetMaterial(mediaType, offset, count);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			mediaInfo = result;
		}

		return JSON.toJSONString(mediaInfo);
	}

}