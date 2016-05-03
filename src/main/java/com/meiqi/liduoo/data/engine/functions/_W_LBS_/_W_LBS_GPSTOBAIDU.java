/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_LBS_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.BaiduMapAPI;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 将GPS经纬度转换成百度坐标
 * 
 * <pre>
 * 1、GPS经度，如果经纬度合在一起，则使用一个参数即可（经度在前、纬度在后，中间为逗号分隔）
 * 2、GPS纬度
 * 
 * 返回：JSON 
 * {"status":0,"result":[{"x":121.35389864479,"y":30.745896679727}]}
 * {"status":24,"message":"param error:coords format error","result":[]}
 * {"status":4,"message": "convert failed:point index:0	x:1218.34285701195 y:30.742014308326","result":[]}
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月6日
 */
public class _W_LBS_GPSTOBAIDU extends WeChatFunction {
	public static final String NAME = _W_LBS_GPSTOBAIDU.class.getSimpleName();

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
		final String longitude = DataUtil.getStringValue(args[0]);// 经度
		if (args.length > 1) {
			coords = longitude + "," + DataUtil.getStringValue(args[1]); // 纬度
		} else if (longitude.indexOf(',') > 0) {
			coords = longitude;
		} else {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： 经纬度参数不全" + longitude);
		}
		String key = coords + "@" + NAME;
		String result = (String) CacheUtils.getCache(key);
		if (StrUtil.isBlank(result)) {
			result = BaiduMapAPI.gpsToBaidu(coords);
			if (BaiduMapAPI.verifyBaiduReturn(result)) {
				CacheUtils.putCache(key, result);
			}
		}
		return result;
	}

}
