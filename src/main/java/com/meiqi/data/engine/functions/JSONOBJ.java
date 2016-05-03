package com.meiqi.data.engine.functions;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * User: Date: 13-11-4 Time: 下午1:00
 */
public final class JSONOBJ extends Function {
	static final String NAME = JSONOBJ.class.getSimpleName();

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		if (args.length == 1) {
			String str = DataUtil.getStringValue(args[0]);
			return "{" + str + "}";
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
