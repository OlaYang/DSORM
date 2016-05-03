/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.io.File;
import java.io.IOException;

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
import com.meiqi.liduoo.fastweixin.api.enums.MediaType;
import com.meiqi.liduoo.fastweixin.api.response.UploadMediaResponse;

/**
 * 上传多媒体资源到微信服务器
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、文件路径（必须是本地物理文件路径）
 * 4、媒体类型：图片（image）: 1M，支持JPG格式
			语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式
			视频（video）：10MB，支持MP4格式
			缩略图（thumb）：64KB，支持JPG格式
 * 5、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
	"type":"xxxx",
	"media_id":xxx,
	"create_at":
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MEDIA_UPLOADMEDIA extends WeChatFunction {
	public static final String NAME = _W_MEDIA_UPLOADMEDIA.class.getSimpleName();

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
		String fileName = DataUtil.getStringValue(args[2]);
		final String mediaTypeStr = DataUtil.getStringValue(args[3]);
		MediaType mediaType = Enum.valueOf(MediaType.class, mediaTypeStr.toUpperCase());

		String key = appId + "@" + appSecret + "@" + fileName + "@" + NAME + "@" + mediaType;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		UploadMediaResponse mediaInfo = noCache ? null : (UploadMediaResponse) CacheUtils.getCache(key);

		if (mediaInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MediaAPI api = new MediaAPI(config);

			File file = null;
			try {
				file = new File(fileName);
				if (!file.exists()) {
					fileName = CommonUtils.getWebImagePath(fileName);
					String ext = ".tmp";
					if (fileName.lastIndexOf(".") > 0) {
						ext = fileName.substring(fileName.lastIndexOf("."));
						if(ext.indexOf("?") >0) ext = ext.substring(0,ext.indexOf("?"));
						if(ext.indexOf("#") >0) ext = ext.substring(0,ext.indexOf("#"));
					} else if (mediaType == MediaType.IMAGE || mediaType == MediaType.THUMB) {
						ext = ".jpg";
					} else if (mediaType == MediaType.VOICE) {
						ext = ".amr";
					} else if (mediaType == MediaType.VIDEO) {
						ext = ".MP4";
					}
					file = File.createTempFile("upload_image", ext);
					CommonUtils.downloadToLocal(fileName, file);
				}
			} catch (IOException e) {
				throw new RengineException(calInfo.getServiceName(),
						NAME + "读取文件出错： fileName=" + fileName + " , " + e.getMessage());
			}
			if (!file.exists()) {
				throw new RengineException(calInfo.getServiceName(), NAME + "文件没找到： " + fileName);
			}
			UploadMediaResponse result = api.uploadMedia(mediaType, file);
			if (!result.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			mediaInfo = result;
		}

		return JSON.toJSONString(mediaInfo);
	}

}