
package com.meiqi.liduoo.data.engine.functions._W_LBS_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.BaiduMapAPI;

/**
 * 根据ID获取地图数据
 * 
 * <pre>
 * 1、poi主键
 * 
 * 返回：JSON 
 * {"status":0,"message":"","poi":{参见百度接口}}
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月6日
 */
public class _W_LBS_GETPOI extends WeChatFunction {
	public static final String NAME = _W_LBS_GETPOI.class.getSimpleName();

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
		final String id = DataUtil.getStringValue(args[0]);//
		String result = BaiduMapAPI.getPOI(id);
		// if (BaiduMapAPI.verifyBaiduReturn(result)) {
		// //CacheUtils.putCache(key, result);
		// }
		return result;
	}

}
