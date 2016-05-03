
package com.meiqi.liduoo.data.engine.functions._W_LBS_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.BaiduMapAPI;

/**
 * 获取百度静态地图的URL
 * 
 * <pre>
 * 1、GPS经度，如果经纬度合在一起，则使用一个参数即可（经度在前、纬度在后，中间为逗号分隔）
 * 2、GPS纬度（如果1中有逗号，此参数不需要）
 * 3、宽度
 * 4、高度
 * 5、缩放比例zoom
 * 
 * 返回： URL字符串
 * http://api.map.baidu.com/staticimage/v2?ak=MfvZuKqHHDaX6BX8DxkbMrht&width=379&height=305&center=30.5444704711,104.0665646227&markers=30.5444704711,104.0665646227&zoom=18&copyright=1&random=1452073122
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月6日
 */
public class _W_LBS_STATICIMAGEURL extends WeChatFunction {
	public static final String NAME = _W_LBS_STATICIMAGEURL.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		String coords = null;
		int startPos = 0;
		final String longitude = DataUtil.getStringValue(args[0]);// 经度
		if (longitude.indexOf(',') > 0) {
			coords = longitude;
		} else {
			coords = longitude + "," + DataUtil.getStringValue(args[1]);
			startPos = 1;
		}
		final int width = Integer.valueOf(DataUtil.getStringValue(args[++startPos]));
		final int height = Integer.valueOf(DataUtil.getStringValue(args[++startPos]));
		final int zoom = Integer.valueOf(DataUtil.getStringValue(args[++startPos]));
		String result = BaiduMapAPI.getStaticImageUrl(coords, width, height, zoom);
		// if (BaiduMapAPI.verifyBaiduReturn(result)) {
		// //CacheUtils.putCache(key, result);
		// }
		return result;
	}

}
