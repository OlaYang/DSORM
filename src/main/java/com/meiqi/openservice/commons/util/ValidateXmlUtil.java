/**   
* @Title: ValidateXmlUtil.java 
* @Package com.lejj.passport.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author liujie@lejj.com  
* @date 2015年4月28日 下午12:02:28 
* @version V1.0   
*/
package com.meiqi.openservice.commons.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;

import com.alibaba.fastjson.JSONObject;

/** 
 * @ClassName: ValidateXmlUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author liujie@lejj.com 
 * @date 2015年4月28日 下午12:02:28 
 *  
 */
public class ValidateXmlUtil {
	
	public final static String  XML_PATH="/validate/";
	private final static String SUN_URL = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private final static String W3C_URL = "http://www.w3.org/2001/XMLSchema";
	private final static String SUN_SOURCE_URL = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	public final static String FLAG_TAG = "flag";
	private final static String CUT_STRING_AFTER = "element '";
	private final static String CUT_STRING_BEFORE = "' is";
	public final static String ERROR_MSG = "msg";
	public final static String XML_SUFFIX = ".xml";
    public final static String XSD_SUFFIX = "Validate.xsd";	
	/**
	 * 
	* @Title: viladate 
	* @Description: 
	* @param @param xmlDocument
	* @param @param xsdFileName
	* @param @return
	* @param @throws Exception    设定文件 
	* @author liujie@lejj.com
	* @return boolean    返回类型 
	* @throws
	 */
	public boolean viladate(Document xmlDocument, String xsdFileName,String formName)throws Exception {
		
		Element element = getErrorsElement(xmlDocument, xsdFileName);
		// 如果错误信息不为空，说明校验失败，打印错误信息
		if (element.hasContent()) {
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: basePath 
	* @Description: xsd 的节点信息
	* @param @param formName
	* @param @return    设定文件 
	* @author liujie@lejj.com
	* @return String    返回类型 
	* @throws
	 */
	public static String basePath(String formName){
		String basePath = "//" + XMLConstans.XSD_DEFAULT_NAMESPACE + ":"+XMLConstans.XSD_ELEMENT+"[@"+XMLConstans.XSD_ATTRIBUTE_NAME+"=\"" + formName + "\"]"; 
		return basePath;
	}
	/**
	 * 
	* @Title: currenXsPath 
	* @Description: 提示信息节点
	* @param @param tag
	* @param @return    设定文件 
	* @author liujie@lejj.com
	* @return String    返回类型 
	* @throws
	 */
	public static String currenXsPath(String tag){
		StringBuffer sb = new StringBuffer();
		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_COMPLEX_TYPE);
		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_SEQUENCE);
		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_ELEMENT+"[@"+XMLConstans.XSD_ATTRIBUTE_NAME+"=\""+tag+"\"]");
//		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_SIMPLE_TYPE);
		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_ANNOTATION);
		sb.append("/"+XMLConstans.XSD_DEFAULT_NAMESPACE+":"+XMLConstans.XSD_DOCUMENTATION);
		return sb.toString(); 
	}
	/**
	 * 
	* @Title: viladateMsg 
	* @Description: 验证xml信息返回具体错误
	* @param @param xmlDocument
	* @param @param xsdFileName
	* @param @param formName
	* @param @return
	* @param @throws Exception    设定文件 
	* @author liujie@lejj.com
	* @return Map<String,Object>    返回类型 
	* @throws
	 */
	public static Map<String,Object> viladateMsg(Document xmlDocument, String xsdFileName,String formName)throws Exception{
		SAXReader reader = new SAXReader();  
		Document  document = reader.read(new File(xsdFileName));
		Element xsdElment = document.getRootElement();
		Map<String,Object>  map = new HashMap<String,Object> ();
		Element xmlElement = getErrorsElement(xmlDocument, xsdFileName);
		boolean flag = true;
		if (xmlElement.hasContent()) {
			flag = false;
		  	for (int i = 0; i < xmlElement.nodeCount(); ++i) {
    			Node node = xmlElement.node(i);
    			String textInfo = node.getText();
    			String str [] = textInfo.split(CUT_STRING_AFTER);
    			if(str.length==1){continue;}
    			String arg [] = str[1].split(CUT_STRING_BEFORE);
    			Element dataElement = (Element) xsdElment.selectSingleNode(basePath(formName)+currenXsPath(arg[0]));
    			map.put(ERROR_MSG, dataElement.getText());
    			break;
		  	}
		}
		map.put(FLAG_TAG, flag);
		return map;
	}
	 /**
	  * 
	 * @Title: getErrorsElement 
	 * @Description: schema 校验 xml
	 * @param @param xmlDocument xml路径
	 * @param @param xsdFileName xsd路径
	 * @param @return 
	 * @param @throws Exception    设定文件 
	 * @author liujie@lejj.com
	 * @return Element    返回类型 
	 * @throws
	  */
	public static Element getErrorsElement(Document xmlDocument, String xsdFileName) throws Exception{
	        XMLErrorHandler errorHandler = new XMLErrorHandler(); 
	        SAXParserFactory factory = SAXParserFactory.newInstance(); 
	        factory.setValidating(true); 
	        factory.setNamespaceAware(true);
	        SAXParser parser = factory.newSAXParser();
	        parser.setProperty(SUN_URL, W3C_URL); 
	        parser.setProperty( SUN_SOURCE_URL, "file:" + xsdFileName);
	        SAXValidator validator = new SAXValidator(parser.getXMLReader()); 
	        //设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。 
	        validator.setErrorHandler(errorHandler); 
	        //校验 
	        validator.validate(xmlDocument); 
	 	    OutputFormat out = OutputFormat.createPrettyPrint();
		    XMLWriter writer = new XMLWriter(out); 
		    writer.write(errorHandler.getErrors());
	        return errorHandler.getErrors();
	  }
	public Document getCommonElementByJsonStr(String jsonStr,
			String xmlPath) throws DocumentException {
		SAXReader reader = new SAXReader();  
		Document document;
		document = reader.read(new File(xmlPath));
	    Element el = document.getRootElement();
	    JSONObject json = JSONObject.parseObject(jsonStr);
	    for(Object k : json.keySet()){  
            el.element(String.valueOf(k)).setText(String.valueOf(json.get(k)));
	 }
		return document;
	}
}
