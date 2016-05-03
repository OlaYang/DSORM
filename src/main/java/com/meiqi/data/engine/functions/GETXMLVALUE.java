package com.meiqi.data.engine.functions;



import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月24日 下午4:16:06 
 * 类说明   微信功能函数：根据传入的name，抽取xml文本中property属性的值
 */

public class GETXMLVALUE extends Function{
	
	public static final String NAME = GETXMLVALUE.class.getSimpleName();

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		
		String str = "";
		
		if(1>args.length){
			throw new ArgsCountError(NAME);
		}
		String xml=(String) args[0];
		if(StringUtils.isEmpty(xml)){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		String name=(String) args[1];
		if(StringUtils.isEmpty(name)){
			throw new ArgsCountError(NAME+"第二个参数不能为空!");
		}
		
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//步骤1
			DocumentBuilder builder = factory.newDocumentBuilder();//步骤2
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			Document doc = builder.parse(is);
			NodeList nodeList = doc.getElementsByTagName("property");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				if(name.equals(element.getAttribute("name"))){
					str = element.getAttribute("value");
					break;
				}
			}
		} catch (Exception e) {
			throw new ArgsCountError(e.getMessage());
		}
        
		return str;
	}

}
