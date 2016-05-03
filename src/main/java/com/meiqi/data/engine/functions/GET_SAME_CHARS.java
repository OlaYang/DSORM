package com.meiqi.data.engine.functions;

import java.util.HashMap;
import java.util.Map;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月20日 下午3:22:17 
 * 类说明  取到传入字符串(以英文逗号拼接)中相同的字符串
 */

public class GET_SAME_CHARS extends Function{

	public static final String NAME = GET_SAME_CHARS.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if(args.length != 1){
			throw new ArgsCountError(NAME);
		}
		
		String str = String.valueOf(args[0]);
		if(StringUtils.isEmpty(str)){
			throw new ArgsCountError(NAME+"=====传入参数不能为空!");
		}
		String result = "";
		String[] split = str.split(",");
		Map<String,String> map = new HashMap<String,String> ();
		for (int i = 0; i < split.length; i++) {
			String str1 = split[i];
			if(map.containsKey(str1)){
				if(StringUtils.isEmpty(result)){
					result = str1;
				}else if(!result.contains(str1)){
					result += "," + str1;
				}
			}
			map.put(str1, str1);
			
		}
		return result;
	}
}
