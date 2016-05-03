package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * 传入一段html 统计html中img标签数量
 * @author meiqidr
 *
 */
public class GETIMGCOUNT extends Function{
	static final String NAME = GETIMGCOUNT.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// TODO Auto-generated method stub
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		String str=(String) args[0];
		//使用正则表达式去匹配img标签内的连接
		//匹配以src="|src='开头 .jpg结尾的的字符串
		Pattern p = Pattern.compile("src=(\"|')(.*?)jpg");
		Matcher m = p.matcher(str);
		List<String> strList=new ArrayList<String>();
		//遍历满足的条件，并加入集合
		while(m.find()){
			strList.add(m.group());
		}
		//返回集合元素数
		int count=strList.size();
		return String.valueOf(count);
	}

}
