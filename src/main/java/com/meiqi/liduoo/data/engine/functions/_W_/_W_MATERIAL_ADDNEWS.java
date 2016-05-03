/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.List;

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
import com.meiqi.liduoo.fastweixin.api.entity.Article;
import com.meiqi.liduoo.fastweixin.api.response.UploadMaterialResponse;

/**
 * 新增图文消息类型的永久素材
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、图文消息内容JSON：
 * {
	  "articles": [{
		       "title": TITLE,
		       "thumb_media_id": THUMB_MEDIA_ID,
		       "author": AUTHOR,
		       "digest": DIGEST,
		       "show_cover_pic": SHOW_COVER_PIC(0 / 1),
		       "content": CONTENT,
		       "content_source_url": CONTENT_SOURCE_URL
	    	},
	    //若新增的是多图文素材，则此处应有几段articles结构，最多8段
	 	]
	}
 *  4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 *  
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
  	"media_id":MEDIA_ID,
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MATERIAL_ADDNEWS extends WeChatFunction {
	public static final String NAME = _W_MATERIAL_ADDNEWS.class.getSimpleName();

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
		final String articleStr = DataUtil.getStringValue(args[2]);

		List<Article> articleList = JSON.parseArray(articleStr, Article.class);

		String key = appId + "@" + appSecret + "@" + CacheUtils.createCacheKey(articleStr) + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		UploadMaterialResponse materialInfo = noCache ? null : (UploadMaterialResponse) CacheUtils.getCache(key);

		if (materialInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MaterialAPI api = new MaterialAPI(config);

			UploadMaterialResponse result = api.uploadMaterialNews(articleList);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			materialInfo = result;
		}

		return JSON.toJSONString(materialInfo);
	}

}