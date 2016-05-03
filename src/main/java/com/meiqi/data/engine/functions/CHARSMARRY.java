package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月11日 上午9:21:22 
 * 类说明  字符串函数-匹配词库返回 匹配关键词
 */

public class CHARSMARRY extends Function{

	public static final String NAME = CHARSMARRY.class.getSimpleName();
	
	public static Map<String,Object> map = new HashMap<String, Object>();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		String result = "";
		if(1>args.length){
			throw new ArgsCountError(NAME);
		}
		String param=String.valueOf(args[0]);
		if(null==param){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(param.equals("")){
		    throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(param.length() > 192){
		    throw new ArgsCountError(NAME+"参数长度超出！");
		}
		
		Map<String,String> resultMap = new HashMap<String, String>();
		getStr(param,param,"",map,resultMap);
		if(!StringUtils.isEmpty(resultMap.get("result"))){
			result = resultMap.get("result").toString();
		}
		return result;
	}
	
	/**
	 * 初始化规则LDO_BUV1_CommonTag到map
	 * @throws ArgsCountError
	 */
	@SuppressWarnings("unchecked")
	public static void initData(){
		Log log =  LogFactory.getLog("request");
		String  ruleName = "LDO_BUV1_CommonTag";
		Map<String,Object> map1 = new HashMap<String, Object>();
		List<Map<String, String>> mapList = new ArrayList<Map<String,String>>();
		if(null == map || map.size() == 0){
			Tool tool = new Tool();
			try {
				mapList = (List<Map<String, String>>) tool.getRuleResult(ruleName, map1, log, "", ruleName);
			} catch (Exception e) {
				LogUtil.error("查询LDO_BUV1_CommonTag错误!   参数是: "+map1);
			}
			for (int i = 0; i < mapList.size(); i++) {
				map.put(mapList.get(i).get("ftags").toString(), i);
			}
		}
		
	}
	
	/**
	 * 递归得到匹配的关键词
	 * @param param  原字符串
	 * @param str 匹配的字符串
	 * @param result  拼接的值
	 * @param map  词库返回的值
	 * @param resultMap  匹配完后返回的结果
	 * @return
	 */
	public void getStr(String param,String str,String result,Map<String,Object> map,Map<String, String> resultMap){
		for (int i = 0; i < str.length(); i++) {
			boolean containsKey = map.containsKey(str.substring(0,i+1));
			if(containsKey){
				if(StringUtils.isEmpty(result)){
					result = str.substring(0,i+1);
				}else{
					result += ","+str.substring(0,i+1);
				}
				resultMap.put("result", result);
				str = str.substring(i+1, str.length());
				break;
			}
			if(i+1 == str.length()){
				str = str.substring(1, str.length());
			}
		}
		if(str.length() != 0){
			getStr(param,str,result,map,resultMap);
		}
	}

}
