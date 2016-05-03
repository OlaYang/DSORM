/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.io.File;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.WeChatUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.MaterialAPI;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.UploadMaterialResponse;

/**
 * 新增永久素材文件
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、素材文件路径（必须是本地物理文件路径）
 * 4、素材标题：可选
 * 5、素材说明：可选
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
public class _W_MATERIAL_ADDFILE extends WeChatFunction {
	public static final String NAME = _W_MATERIAL_ADDFILE.class.getSimpleName();

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
		final String fileName = DataUtil.getStringValue(args[2]);
		final String title = args.length >= 4 ? DataUtil.getStringValue(args[3]) : null;
		final String intrduction = args.length >= 5 ? DataUtil.getStringValue(args[4]) : null;

		ApiConfig config = WeChatUtils.initApiConfig(appId, appSecret);
		MaterialAPI api = new MaterialAPI(config);

		File file = new File(fileName);// TODO：本地文件？是否需要支持URL文件
		if (!file.exists()) {
			throw new RengineException(calInfo.getServiceName(), NAME + "文件没找到： " + fileName);
		}
		UploadMaterialResponse result = api.uploadMaterialFile(file, title, intrduction);
		if (!result.verifyWechatResponse( false,config)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + result.toJsonString());
		}

		return JSON.toJSONString(result);
	}

}