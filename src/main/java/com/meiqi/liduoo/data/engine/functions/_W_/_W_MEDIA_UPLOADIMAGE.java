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
import com.meiqi.liduoo.fastweixin.api.response.UploadImgResponse;

/**
 * 上传图片到微信服务器
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、图片文件路径（必须是本地物理文件路径）
 * 4、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":"",
	"url":"xxxx"
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MEDIA_UPLOADIMAGE extends WeChatFunction {
	public static final String NAME = _W_MEDIA_UPLOADIMAGE.class.getSimpleName();

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
		final String fileName = DataUtil.getStringValue(args[2]);//

		String key = appId + "@" + appSecret + "@" + fileName + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		UploadImgResponse imgInfo = noCache ? null : (UploadImgResponse) CacheUtils.getCache(key);

		if (imgInfo == null) {
			ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
			MediaAPI api = new MediaAPI(config);
			File file;
			try {
				file = getLocalFile(fileName);

			} catch (IOException e) {
				throw new RengineException(calInfo.getServiceName(),
						NAME + "读取文件出错： fileName=" + fileName + " , " + e.getMessage());
			}

			if (!file.exists()) {
				throw new RengineException(calInfo.getServiceName(), NAME + "文件没找到： " + fileName);
			}
			UploadImgResponse result = api.uploadImg(file);
			if (!result.verifyWechatResponse(false, config)) {
				throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
			}
			CacheUtils.putCache(key, result);
			imgInfo = result;
		}

		return JSON.toJSONString(imgInfo);
	}

	private File getLocalFile(String fileName) throws RengineException, IOException {
		File file = new File(fileName);
		if (file.exists()) {
			return file;
		}
		fileName = CommonUtils.getWebImagePath(fileName);
		if (fileName.toLowerCase().startsWith("http://") || fileName.toLowerCase().startsWith("https://")) {
			String ext = ".JPG";
			if (fileName.lastIndexOf(".") > 0) {
				ext = fileName.substring(fileName.lastIndexOf("."));
				if(ext.indexOf("?") >0) ext = ext.substring(0,ext.indexOf("?"));
				if(ext.indexOf("#") >0) ext = ext.substring(0,ext.indexOf("#"));
			}
			file = File.createTempFile("upload_image", ext);
			CommonUtils.downloadToLocal(fileName, file);
		} else {
			file = new File(fileName);
		}

		return file;
	}

}