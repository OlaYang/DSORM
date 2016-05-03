/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;

/**
 * UTIL类型函数，将后续参数按照Key-Value方式转换成JSON格式
 * 
 * <pre>
 * 参数
 * 1、Key
 * 2、Value
 * .....
 * 
 * 返回格式化后的JSONString
 * </pre>
 * 
 * @author FrankGui 2015年12月27日
 */
public class _W_UTIL_TOMAP extends WeChatFunction {
	public static final String NAME = _W_UTIL_TOMAP.class.getSimpleName();

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		int index = 0;
		while (index + 2 <= args.length) {
			map.put(DataUtil.getStringValue(args[index]), args[index + 1]);
			index += 2;
		}
		return JSON.toJSONString(map);
	}

}