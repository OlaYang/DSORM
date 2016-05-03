package com.meiqi.dsmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.json.xml.XMLSerializer;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class XmlUtil {

	/**
	 * 根据xls样式将原xml字符串转换成目标xml字符串
	 * @param rourceXml xml原字符串
	 * @param xslStyle  样式
	 * @return 目标xml字符串
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	public static String convertSourceXMLtoTargetXMLByXslStyle(String rourceXml,String xslStyle) throws TransformerFactoryConfigurationError, TransformerException{
		 String targetXML="";
		 InputStream sourceIs = new ByteArrayInputStream(rourceXml.getBytes());
		 InputStream xlsIs = new ByteArrayInputStream(xslStyle.getBytes());
		 StreamSource xmlSource = new StreamSource(sourceIs);   
         StreamSource xsl = new StreamSource(xlsIs);
         ByteArrayOutputStream  os=new ByteArrayOutputStream();
         Result outputTarget = new StreamResult(os);   
         Transformer ts;
		 ts = TransformerFactory.newInstance().newTransformer(xsl);
		 ts.transform(xmlSource, outputTarget); 
		 targetXML=os.toString();
		return targetXML;
	}
	/**
	 * 根据xls样式将原json字符串转换成目标xml字符串
	 * @param json  json原串
	 * @param xslStyle 样式
	 * @return
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String convertSourceJsontoTargetXMLByXslStyle(String json,String xslStyle) throws TransformerFactoryConfigurationError, TransformerException, UnsupportedEncodingException{
		
	    json=json.replaceAll("<","%3C");
	    json=json.replaceAll(">","%3E");
		String rourceXml = jsonToXml(json);
		String targetXML="";
		InputStream sourceIs = new ByteArrayInputStream(rourceXml.getBytes());
		InputStream xlsIs = new ByteArrayInputStream(xslStyle.getBytes());
		StreamSource xmlSource = new StreamSource(sourceIs);   
        StreamSource xsl = new StreamSource(xlsIs);
        ByteArrayOutputStream  os=new ByteArrayOutputStream();
        Result outputTarget = new StreamResult(os);   
        Transformer ts;
		ts = TransformerFactory.newInstance().newTransformer(xsl);
		ts.transform(xmlSource, outputTarget); 
		targetXML=os.toString();
		targetXML=targetXML.replaceAll("%3C","<");
		targetXML=targetXML.replaceAll("%3E",">");
		targetXML = targetXML.replaceAll("%(?![0-9a-fA-F]{2})", "%25");//处理特殊字符，防止URLDecoder.decode错误
        try {
            targetXML = URLDecoder.decode(targetXML.toString(),"utf-8");
        } catch (Exception e) {
              e.printStackTrace();
        }
		return targetXML;
	}
	
	
	/**
	 * 将xml字符串转换成json串
	 * @param xml 原xml字符串
	 * @return 目标json串
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws JSONException 
	 */
    public static  String xmlToJson(String xml) throws JDOMException, IOException, JSONException {  
    	 JSONObject obj = new JSONObject();  
             InputStream is = new ByteArrayInputStream(xml.getBytes("utf-8"));  
             SAXBuilder sb = new SAXBuilder();  
             Document doc = sb.build(is);  
             Element root = doc.getRootElement();  
             obj.put(root.getName(), iterateElement(root));  
             return obj.toString();  
    }
    
    /**
	 * 将xml字符串转换成json串 不转吗
	 * @param xml 原xml字符串
	 * @return 目标json串 
	 */
    public static String xml2Json(String xml){
    	return new XMLSerializer().read(xml).toString();
    }
    /** 
     * 一个迭代方法 
     *  
     * @param element 
     *            : org.jdom.Element 
     * @return java.util.Map 实例 
     */  
    @SuppressWarnings("unchecked")  
    private static Map<String,Object>  iterateElement(Element element) {  
        List<Object> jiedian = element.getChildren();  
        Element et = null;  
        Map<String,Object> obj = new HashMap<String,Object>();  
        List<Object> list = null;  
        for (int i = 0; i < jiedian.size(); i++) {  
            list = new LinkedList<Object>();  
            et = (Element) jiedian.get(i);  
            if (et.getTextTrim().equals("")) {  
                if (et.getChildren().size() == 0)  
                    continue;  
                if (obj.containsKey(et.getName())) {  
                    list = (List<Object>) obj.get(et.getName());  
                }  
                list.add(iterateElement(et));  
                obj.put(et.getName(), list);  
            } else {  
                if (obj.containsKey(et.getName())) {  
                    list = (List<Object>) obj.get(et.getName());  
                }  
                list.add(et.getTextTrim());  
                obj.put(et.getName(), list);  
            }  
        }  
        return obj;  
    }  
    
    /**
     * 
     * @param targetXml 目标xml，不带根元素的,自动加了数组的节点标示<group></group>
     * @param objJson  原json串 isEncode是否编码/
     */
    @SuppressWarnings("unchecked")
	public static  void parseJsonToXml(StringBuilder targetXml,Object objJson,boolean isEncode) {
    	if(objJson instanceof net.sf.json.JSONArray){
    		net.sf.json.JSONArray objArray = (net.sf.json.JSONArray)objJson;
			for (int i = 0; i < objArray.size(); i++) {
				targetXml.append("<group>");
				parseJsonToXml(targetXml,objArray.get(i),isEncode);
				targetXml.append("</group>");
			}
    	}else if(objJson instanceof net.sf.json.JSONObject){
    		net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject)objJson;
			Iterator<Object> it = jsonObject.keys();
			while(it.hasNext()){
				String key = it.next().toString();
				Object object = jsonObject.get(key);
				if(object instanceof net.sf.json.JSONArray){
					targetXml.append("<").append(key).append(">");
					net.sf.json.JSONArray objArray = (net.sf.json.JSONArray)object;
					parseJsonToXml(targetXml,objArray,isEncode);
					targetXml.append("</").append(key).append(">");
				}
				else if(object instanceof net.sf.json.JSONObject){
					parseJsonToXml(targetXml,(net.sf.json.JSONObject)object,isEncode);
				}else{
					targetXml.append("<").append(key).append(">");
					String value;
                    try {
                        value = object.toString();
                        if(value.contains("/")&&isEncode){
                            value = URLEncoder.encode(object.toString(),"UTF-8");
                        }
                        targetXml.append(value);
                        targetXml.append("</").append(key).append(">");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
				}
			}
    	}
    	
    	
    }
    
    
    
    /**
     * 
     * @param targetXml 目标xml，不带根元素的,自动加了数组的节点标示<group></group>
     * @param objJson  原json串
     */
    @SuppressWarnings("unchecked")
	public static  void parseJsonToXml(StringBuilder targetXml,Object objJson) {
    	if(objJson instanceof net.sf.json.JSONArray){
    		net.sf.json.JSONArray objArray = (net.sf.json.JSONArray)objJson;
			for (int i = 0; i < objArray.size(); i++) {
				targetXml.append("<group>");
				parseJsonToXml(targetXml,objArray.get(i));
				targetXml.append("</group>");
			}
    	}else if(objJson instanceof net.sf.json.JSONObject){
    		net.sf.json.JSONObject jsonObject = (net.sf.json.JSONObject)objJson;
			Iterator<Object> it = jsonObject.keys();
			while(it.hasNext()){
				String key = it.next().toString();
				Object object = jsonObject.get(key);
				if(object instanceof net.sf.json.JSONArray){
					targetXml.append("<").append(key).append(">");
					net.sf.json.JSONArray objArray = (net.sf.json.JSONArray)object;
					parseJsonToXml(targetXml,objArray);
					targetXml.append("</").append(key).append(">");
				}
				else if(object instanceof net.sf.json.JSONObject){
					parseJsonToXml(targetXml,(net.sf.json.JSONObject)object);
				}else{
					targetXml.append("<").append(key).append(">");
					String value;
                    try {
                        value = object.toString();
                        //if(value.contains("/") || value.contains("%")){
                            value = URLEncoder.encode(object.toString(),"UTF-8");
                        //}
                        targetXml.append(value);
                        targetXml.append("</").append(key).append(">");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
				}
			}
    	}
    	
    	
    }
    
    /**
	 * 将json字符串转换成xml串  自动加了根元素<xmlroot></xmlroot>
	 * @param xml 原xml字符串
	 * @return 目标json串
	 */
    public static  String jsonToXml(String json) {
    	net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(json);
    	StringBuilder targetXml=new StringBuilder();
    	targetXml.append("<xmlroot>");
    	parseJsonToXml(targetXml,jsonObject);
    	targetXml.append("</xmlroot>");
//    	String result="";
//        try {
//            result = URLDecoder.decode(targetXml.toString(),"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    	return result;
    	return targetXml.toString();
    }
    
    
    /**
	 * 将json字符串转换成xml串  自动加了指定的根元素,不变吗
	 * @param xml 原xml字符串
	 * @return 目标json串
	 */
    public static  String jsonToXml(String json,String root,boolean isEncode) {
    	net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(json);
    	StringBuilder targetXml=new StringBuilder();
    	targetXml.append("<").append(root).append(">");
    	if(isEncode){
        	parseJsonToXml(targetXml,jsonObject);
    	}else{
    		parseJsonToXml(targetXml, jsonObject,false);
    	}
    	targetXml.append("</").append(root).append(">");
    	return targetXml.toString();
    }
//    
//    public static void main(String[] args) {
//		
//		StringBuilder rourceXml=new StringBuilder();
//		//rourceXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");  
//		rourceXml.append("<xmlroot>");  
//		rourceXml.append("<book_store>");  
//		rourceXml.append("<book>");  
//		rourceXml.append("<title>Everyday Italian</title>");  
//		rourceXml.append("<author>Giada De Laurentiis</author>");  
//		rourceXml.append("<year>2005</year>");  
//		rourceXml.append("<price>30.00</price>");  
//		rourceXml.append("</book>");  
//		rourceXml.append("<book>");  
//		rourceXml.append("<title>Harry Potter</title>");  
//		rourceXml.append("<author>J K. Rowling</author>");  
//		rourceXml.append("<year>2005</year>");  
//		rourceXml.append("<price>29.99</price>");  
//		rourceXml.append("</book>");  
//		rourceXml.append("<book category=\"WEB\">");  
//		rourceXml.append("<title>XQuery Kick Start</title>");  
//		rourceXml.append("<author>James McGovern</author>");  
//		rourceXml.append("<author>Per Bothner</author>");  
//		rourceXml.append("<author>Kurt Cagle</author>");  
//		rourceXml.append("<author>James Linn</author>");  
//		rourceXml.append("<author>Vaidyanathan Nagarajan</author>");  
//		rourceXml.append("<year>2003</year>");  
//		rourceXml.append("<price>49.99</price>");  
//		rourceXml.append("</book>");  
//		rourceXml.append("<book>");  
//		rourceXml.append("<title>Learning XML</title>");  
//		rourceXml.append("<author>Erik T. Ray</author>");  
//		rourceXml.append("<year>2003</year>");  
//		rourceXml.append("<price>39.95</price>");  
//		rourceXml.append("</book>");  
//		rourceXml.append("</book_store>");  
//		rourceXml.append("</xmlroot>"); 
//		
//		StringBuilder xslStyle=new StringBuilder();
//		xslStyle.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
//		xslStyle.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">");
//		xslStyle.append("<xsl:template match=\"/\">");
//		xslStyle.append("<html>");
//		xslStyle.append("<body>");
//		xslStyle.append("<h2>This is my books</h2>");
//		xslStyle.append("<table border=\"1\">");
//		xslStyle.append("<tr>");
//		xslStyle.append("<th>book title</th>");
//		xslStyle.append("<th>book author</th>");
//		xslStyle.append("</tr>");
//		xslStyle.append("<xsl:for-each select=\"xmlroot/book_store/book\">");
//		xslStyle.append("<tr>");
//		//xslStyle.append("<td><xsl:choose><xsl:when test=\"year='2003'\"><shoplist status=\"yes\"></shoplist></xsl:when><xsl:when test=\"year!='2003'\"><shoplist status=\"no\"></shoplist></xsl:when>\"></xsl:choose></td>");
//		xslStyle.append("<td><xsl:value-of select=\"title\"/></td>");
//		xslStyle.append("<td><xsl:value-of select=\"author\"/></td>");
//		xslStyle.append("</tr>");
//		xslStyle.append("</xsl:for-each>");
//		xslStyle.append("</table>");
//		xslStyle.append("</body>");
//		xslStyle.append("</html>");
//		xslStyle.append("</xsl:template>");
//		xslStyle.append("</xsl:stylesheet>");
//		
//		System.out.println("rourceXml:"+rourceXml);
//		String json="";
//		try {
//			json = xmlToJson(rourceXml.toString());
//		} catch (JDOMException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//		System.out.println("json:"+json);
//		String xml=jsonToXml(json);
//		System.out.println("xml:"+xml);
//		//根据xls样式将原xml字符串转换成目标xml字符串
//		try {
//			System.out.println("xmlToxml:"+convertSourceXMLtoTargetXMLByXslStyle(xml,xslStyle.toString()));
//		} catch (TransformerFactoryConfigurationError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			System.out.println("xmlToxml1:"+convertSourceXMLtoTargetXMLByXslStyle(rourceXml.toString(),xslStyle.toString()));
//		} catch (TransformerFactoryConfigurationError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
    /**
     *获取xml某节点的值
    * @Title: getValueFromXml 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param tag 节点名称
    * @param @param xml 原xml
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public  static String getValueFromXml(String tag,String xml){
    	String value="";
        try {
			org.dom4j.Document document = DocumentHelper.parseText(xml);
			org.dom4j.Element root= document.getRootElement();
			Node node=root.selectSingleNode(tag);
			value=node.getText();
		} catch (DocumentException e) {
			e.printStackTrace();
		}  
        return value;
    }
    
    public static Object convertXMLToObject(String xml,Class<?> cla){
    	JAXBContext context;
    	Object obj=null;
		try {
			context = JAXBContext.newInstance(cla);
			Unmarshaller unmarshaller = context.createUnmarshaller();  
		    obj = (Object)unmarshaller.unmarshal(new StringReader(xml)); 
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return obj;  
    }
    
    public static void main(String[] args) {
		
        String str="续重量/体积/件运费（元）</item><item type=\"text\" sort=\"1\" key=\"amount_percent\">按商品金额的百分比计算%</item>";
	    try {
            System.out.println(URLDecoder.decode(str, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    
    }
}
