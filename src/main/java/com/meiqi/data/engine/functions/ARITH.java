package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.Arith;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月12日 下午3:40:19 
 * 类说明  加减乘除精确计算函数
 */

public class ARITH extends Function{

	public static final String NAME = ARITH.class.getSimpleName();
	
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
		
		Integer num = 0;
		if(4 == args.length){
			num = Integer.parseInt(args[3].toString());
		}
		Double result = 0.0;
		if("+".equals(param)){
			result = Arith.add(Double.valueOf(args[1].toString()), Double.valueOf(args[2].toString()),num);
		}else if("-".equals(param)){
			result = Arith.sub(Double.valueOf(args[1].toString()), Double.valueOf(args[2].toString()),num);
		}else if("*".equals(param)){
			result = Arith.mul(Double.valueOf(args[1].toString()), Double.valueOf(args[2].toString()),num);
		}else if("/".equals(param)){
			result = Arith.div(Double.valueOf(args[1].toString()), Double.valueOf(args[2].toString()),num);
		}
		
		return result;
	}

}
