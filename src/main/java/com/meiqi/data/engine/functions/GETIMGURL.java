package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

public class GETIMGURL extends Function{
	static final String NAME = GETIMGURL.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// TODO Auto-generated method stub
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		int index=1; //设置默认取第一个img url
		String str=(String) args[0];
		if(2==args.length){
			index=Integer.valueOf(DataUtil.getStringValue(args[1]));
		}
		
		//使用正则表达式去匹配img标签内的连接
		//匹配以src="|src='开头 .jpg结尾的的字符串
		Pattern p = Pattern.compile("src=(\"|')(.*?)(jpg|png|gif)");
		Matcher m = p.matcher(str);
		List<String> strList=new ArrayList<String>();
		while(m.find()){
			strList.add(m.group());
		}
		if(0<strList.size()){
			if(index>strList.size()){
				index=strList.size();
			}else if(1>index){
				index=1;
			}
			return strList.get(index-1).substring(5);
		}else{
			return "";
		}
	}

}
