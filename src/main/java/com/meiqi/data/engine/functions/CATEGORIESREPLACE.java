package com.meiqi.data.engine.functions;

import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

public class CATEGORIESREPLACE extends Function{
	static final String NAME = CATEGORIESREPLACE.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if(5>args.length){
			throw new ArgsCountError(NAME);
		}
		String str=(String) args[0];
		String beginTag=(String) args[1];
		String endTag=(String) args[2];
		String map=(String) args[3];
		boolean isSaveC=(Boolean) args[4];
		boolean isFirst=true;
		if(5<args.length){
			isFirst=(Boolean) args[5];
		}
		JSONArray jsonArray=JSONArray.parseArray(map);
		for(int i=0;i<jsonArray.size();i++){
			JSONObject jsonObject=jsonArray.getJSONObject(i);
			Set<String> setSet=jsonObject.keySet();
			for(String categories:setSet){
				String categoriesValue= (String) jsonObject.get(categories);
				
				String replacement=beginTag+(isSaveC?categories:"")+endTag;
				replacement=replacement.replaceAll("%tag%",categoriesValue);
				if(true==isFirst){
					str=str.replaceFirst(categories, replacement);
				}else{
					str=str.replaceAll(categories, replacement);
				}
			}
		}
		return str;
	}

}
