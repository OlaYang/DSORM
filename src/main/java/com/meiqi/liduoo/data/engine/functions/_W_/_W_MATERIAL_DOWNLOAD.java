/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MaterialAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.enums.MaterialType;
import com.meiqi.liduoo.fastweixin.api.response.DownloadMaterialResponse;

/**
 * 下载永久素材
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、素材ID
 * 4、素材类型：image、news、voice、video
 * 5、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * 不同的素材类型返回不同的内容
 * 1、news返回：{
	 * 	"errcode":"0",
	  	"errmsg":"",
	 	"news_item":
	 	[
	     {
		     "title":TITLE,
		     "thumb_media_id"::THUMB_MEDIA_ID,
		     "show_cover_pic":SHOW_COVER_PIC(0/1),
		     "author":AUTHOR,
		     "digest":DIGEST,
		     "content":CONTENT,
		     "url":URL,
		     "content_source_url":CONTENT_SOURCE_URL
	     },
	     //多图文消息有多篇文章
	  	]
	}
 * 2、视频返回：
 * {
 * 	"errcode":"0",
	"errmsg":"",
	"title":TITLE,
  	"description":DESCRIPTION,
  	"down_url":DOWN_URL,
 * }
 * 
 * 3、其他返回
 * {
 * 	"errcode":"0",
	"errmsg":"",
  	"down_url":DOWN_URL,
 * }
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MATERIAL_DOWNLOAD extends WeChatFunction {
	public static final String NAME = _W_MATERIAL_DOWNLOAD.class.getSimpleName();

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
		final String materialId = DataUtil.getStringValue(args[2]);
		final String typeStr = DataUtil.getStringValue(args[3]);
		MaterialType materialType = Enum.valueOf(MaterialType.class, typeStr.toUpperCase());

		String key = appId + "@" + appSecret + "@" + materialId + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		DownloadMaterialResponse mediaInfo = noCache ? null : (DownloadMaterialResponse) CacheUtils.getCache(key);

		if (mediaInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MaterialAPI api = new MaterialAPI(config);

			DownloadMaterialResponse result = api.downloadMaterial(materialId, materialType);
			if (!result.verifyWechatResponse( false,config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			if (materialType == MaterialType.IMAGE || materialType == MaterialType.VOICE) {
				try {
					String ext = materialType == MaterialType.IMAGE ? ".jpg" : ".amr";
					File downloadFile = File.createTempFile("down_material", ext);

					FileUtils.writeByteArrayToFile(downloadFile, result.getContent());

					JSONObject jsonObject = CommonUtils.uploadToFileServer(downloadFile.getAbsolutePath());
					if (jsonObject != null) {
						result.setDownUrl(jsonObject.getString("file"));
					}
				} catch (IOException e) {
					throw new RengineException(calInfo.getServiceName(), NAME + "调用出错" + e.getMessage());
				}
			}
			CacheUtils.putCache(key, result);
			mediaInfo = result;
		}

		return JSON.toJSONString(mediaInfo);
	}

}