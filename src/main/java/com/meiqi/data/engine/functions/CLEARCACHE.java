package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.action.ClearCacheAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.util.MyApplicationContextUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月7日 下午4:33:01 
 * 类说明  清楚规则缓存的函数
 */

public class CLEARCACHE extends Function{

	public static final String NAME = CLEARCACHE.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
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
		RepInfo repInfo = new RepInfo();
		repInfo.setAction("clearCacheAction");
		repInfo.setMethod("clear");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("serviceName", args[0]);
		repInfo.setParam(jsonObject.toJSONString());
		ClearCacheAction clearCacheAction = (ClearCacheAction) MyApplicationContextUtil.getBean("clearCacheAction");
		String clear = clearCacheAction.clear(null, null, repInfo);
		
		return clear;
	}

}
