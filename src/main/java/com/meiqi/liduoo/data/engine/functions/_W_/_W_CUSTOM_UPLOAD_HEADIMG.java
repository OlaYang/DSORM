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
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.CustomAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * 设置客服头像
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、客服账号
 * 4、图片文件路径（必须是本地物理文件路径）
 * 
 * 返回：JSON字符串：
 * {
	"errcode":"0",
	"errmsg":""
  }
 * 
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_CUSTOM_UPLOAD_HEADIMG extends WeChatFunction {
	public static final String NAME = _W_CUSTOM_UPLOAD_HEADIMG.class.getSimpleName();

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
		final String accountName = DataUtil.getStringValue(args[2]);
		final String fileName = DataUtil.getStringValue(args[3]);

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		CustomAPI api = new CustomAPI(config);
		// 设置图像不考虑缓存处理

		File file = null;
		try {
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
		} catch (IOException e) {
			throw new RengineException(calInfo.getServiceName(),
					NAME + "读取文件出错： fileName=" + fileName + " , " + e.getMessage());
		}
		if (!file.exists()) {
			throw new RengineException(calInfo.getServiceName(), NAME + "文件没找到： " + fileName);
		}
		BaseResponse result = api.uploadHeadImg(accountName, file);
		if (!result.verifyWechatResponse( false, config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}