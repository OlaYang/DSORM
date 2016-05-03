/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.meiqi.liduoo.fastweixin.api.MediaAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.Article;
import com.meiqi.liduoo.fastweixin.api.enums.MediaType;
import com.meiqi.liduoo.fastweixin.api.response.UploadMediaResponse;
import com.meiqi.liduoo.fastweixin.util.StrUtil;
import com.meiqi.util.LogUtil;

/**
 * 媒体上传图文消息
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、图文消息列表
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
 * "errcode":"0",
	"errmsg":"",
   	"media_id":MEDIA_ID
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MEDIA_UPLOADNEWS extends WeChatFunction {
	public static final String NAME = _W_MEDIA_UPLOADNEWS.class.getSimpleName();

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
		String key = appId + "@" + appSecret + "@" + articleList + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		UploadMediaResponse newsInfo = noCache ? null : (UploadMediaResponse) CacheUtils.getCache(key);

		if (newsInfo == null) {
			// 先检查Article内部
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MediaAPI api = new MediaAPI(config);
			for (Article article : articleList) {
				String thumbMediaId = article.getThumbMediaId();
				if (StrUtil.isBlank(thumbMediaId)) {
					continue;
				}
				if (thumbMediaId.toLowerCase().startsWith("http://")
						|| thumbMediaId.toLowerCase().startsWith("https://") || thumbMediaId.indexOf("/") >= 0
						|| thumbMediaId.indexOf("\\") >= 0) {
					String itemCacheKey = appId + "@" + appSecret + "@" + thumbMediaId;
					//String cacheMediaId = (String) CacheUtils.getCache(itemCacheKey);
					// 上传到微信服务器
//					if (StrUtil.isNotBlank(cacheMediaId)) {
//						article.setThumbMediaId(cacheMediaId);
//						continue;
//					}
					File file = null;
					try {
						file = getLocalFile(thumbMediaId);
					} catch (IOException e) {
						LogUtil.error(calInfo.getServiceName() + ", " + NAME + "读取文件出错： fileName=" + thumbMediaId
								+ " , " + e.getMessage());
						continue;
					}

					if (!file.exists()) {
						LogUtil.error(calInfo.getServiceName() + ", " + NAME + "文件没找到： " + file.getAbsolutePath());
						// throw new RengineException(calInfo.getServiceName(),
						// NAME + "文件没找到： " + thumbMediaId);
						continue;
					}
					UploadMediaResponse result = api.uploadMedia(MediaType.IMAGE, file);
					if (!result.verifyWechatResponse( false, config)) {
						throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
					}
					article.setThumbMediaId(result.getMediaId());
					//CacheUtils.putCache(itemCacheKey, result.getMediaId(), 3 * 24 * 60 * 60 - 600);// 微信缓存三天，我们预留10分钟伸缩时间
				}
			}
			UploadMediaResponse result = api.uploadNews(articleList);
			if (!result.verifyWechatResponse( false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result, 3 * 24 * 60 * 60*1000L - 600*1000L);// 微信缓存三天，我们预留10分钟伸缩时间
			newsInfo = result;
		}

		return JSON.toJSONString(newsInfo);
	}

	private File getLocalFile(final String fileName) throws RengineException, IOException {
		File file = null;

		if (fileName.toLowerCase().startsWith("http://") || fileName.toLowerCase().startsWith("https://")) {
			String ext = ".JPG";
			if (fileName.lastIndexOf(".") > 0) {
				ext = fileName.substring(fileName.lastIndexOf("."));
			}
			file = File.createTempFile("upload_image", ext);
			CommonUtils.downloadToLocal(fileName, file);
		} else {
			file = new File(fileName);
		}

		return file;
	}

}