/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MediaAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.DownloadMediaResponse;
import com.meiqi.openservice.commons.util.UploadUtil;

/**
 * 下载媒体
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、媒体ID
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回JSON：
 * {
		"errcode":"0",
	  	"errmsg":"",
	 	"down_url":可以直接访问的URL
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MEDIA_DOWNLOAD extends WeChatFunction {
	public static final String NAME = _W_MEDIA_DOWNLOAD.class.getSimpleName();

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
		final String mediaId = DataUtil.getStringValue(args[2]);

		String key = appId + "@" + appSecret + "@" + mediaId + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		DownloadMediaResponse mediaInfo = noCache ? null : (DownloadMediaResponse) CacheUtils.getCache(key);

		if (mediaInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MediaAPI api = new MediaAPI(config);

			DownloadMediaResponse result = api.downloadMedia(mediaId);
			if (!result.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			try {

				String strBase64 = new sun.misc.BASE64Encoder().encode(result.getContent());
				String ret = UploadUtil.uploadFile(strBase64, LdConfigUtil.getUpload_server());
				JSONObject jsonObject = JSON.parseObject(ret);
				boolean success = false;
				if (jsonObject != null) {
					if ("ok".equalsIgnoreCase(jsonObject.getString("errors"))) {
						result.setDownUrl(jsonObject.getString("file"));
						result.setPath(jsonObject.getString("path"));
						success = true;
					}
				}
				if (!success) {
					throw new RengineException(calInfo.getServiceName(), NAME + "上传文件失败:" + ret);// +
				}
			} catch (RengineException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RengineException(calInfo.getServiceName(), NAME + "调用出错" + e.getMessage());
			}
			CacheUtils.putCache(key, result);
			mediaInfo = result;
		}

		return JSON.toJSONString(mediaInfo);
	}

}