package com.meiqi.data.engine.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
/**
 * 传入字符串进行utf8编码
 * @author duanran
 *
 */
public class UTF8 extends Function{
	static final String NAME = UTF8.class.getSimpleName();
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// TODO Auto-generated method stub
		if(0==args.length){
			throw new ArgsCountError(NAME);
		}
		String utfString="";
		try {
			utfString=URLEncoder.encode(args[0].toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new CalculateError(args[0]+" 编码失败！");
		}
		return utfString;
	}
	
}
