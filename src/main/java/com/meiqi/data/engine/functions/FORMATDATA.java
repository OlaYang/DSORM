package com.meiqi.data.engine.functions;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月7日 上午10:18:09 
 * 类说明  将结果集按条件抽取出来以自定义符号拼接成字符串
 */

public class FORMATDATA extends Function{

	public static final String NAME = FORMATDATA.class.getSimpleName();
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {

		String str = "";
		
		if(1>args.length){
			throw new ArgsCountError(NAME);
		}
		String param=(String) args[0];
		if(null==param){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(param.equals("")){
		    throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		Map<String,String> map = new LinkedHashMap<String, String>();
		
		JSONArray results = JSONArray.parseArray(args[0].toString());
		String  conditionNmae = args[1].toString();//按conditionNmae抽取结果集
		String  conditionSigh = args[2].toString();//按conditionSigh拼接字符串
		String  distinct_flag = args[3].toString();//是否去重复的标识，0表示不去重，1表示去重
		
		
		for (int i = 0; i < results.size(); i++) {
			JSONObject jsonObject = results.getJSONObject(i);
			if(StringUtils.isEmpty(jsonObject.getString(conditionNmae))){
				continue;
			}
			if("0".equals(distinct_flag)){
				if(StringUtils.isEmpty(str)){
					str = jsonObject.getString(conditionNmae);
				}else{
					str += conditionSigh+jsonObject.getString(conditionNmae);
				}
			}else{
				if(!map.containsKey(jsonObject.getString(conditionNmae))){
					map.put(jsonObject.getString(conditionNmae), String.valueOf(i));
				}
			}
		}
		
		if(map.size() > 0){
			for(Map.Entry entry : map.entrySet()){
			    String key = entry.getKey().toString();
			    if(StringUtils.isEmpty(str)){
					str = key;
				}else{
					str += conditionSigh+key;
				}
			   }
		}
		
		return str;
	}

}
