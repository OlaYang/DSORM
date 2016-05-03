package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.mushroom.config.ServicesConfig;
import com.meiqi.util.MyApplicationContextUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月16日 上午11:15:38 
 * 类说明  通过服务名（serviceName）刷新MushRoom服务
 */

public class LOADSERVICE extends Function{

	public static final String NAME = LOADSERVICE.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
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
		
		ServicesConfig servicesConfig = (ServicesConfig) MyApplicationContextUtil.getBean("servicesConfig");
		try {
			servicesConfig.loadServiceByServiceName(args[0].toString());
		} catch (Exception e) {
			throw new ArgsCountError(NAME+","+e.getMessage());
		}
		return "成功";
	}

}
