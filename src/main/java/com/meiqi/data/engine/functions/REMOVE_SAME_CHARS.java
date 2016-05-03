package com.meiqi.data.engine.functions;

import java.util.LinkedHashMap;
import java.util.Map;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月23日 下午1:58:28 
 * 类说明  去掉相重复的字符串，并保持顺序返回
 */

public class REMOVE_SAME_CHARS extends Function{

	public static final String NAME = REMOVE_SAME_CHARS.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
		if(1 != args.length){
			throw new ArgsCountError(NAME);
		}
		
		String param=String.valueOf(args[0]);
		if(null==param){
			throw new ArgsCountError(NAME+"参数不能为空!");
		}
		
		String result = "";
		String[] split = param.split(",");
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < split.length; i++) {
			String str1 = split[i];
			map.put(str1, str1);
		}
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if(StringUtils.isEmpty(result)){
				result = entry.getKey();
			}else{
				result += ","+entry.getKey();
			}
		}
		
		return result;
	}

}
