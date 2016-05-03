package com.meiqi.app.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.meiqi.app.action.BaseAction;
import com.meiqi.app.common.config.AppSysConfig;

/**
 * 
 * @ClassName: XmlUtils
 * @Description:XML工具类
 * @author 杨永川
 * @date 2015年5月19日 上午11:33:34
 *
 */
public class XmlUtils {
    private static final String MEIQIREFERRER_SECTIONS = "/meiQiReferrer";

    private static final String SET_VALUE_SIGN1        = "${";
    private static final String SET_VALUE_SIGN2        = "}";
    private static final String ROOT_ELEMENT_NAME      = "meiQiReferrer";
    private static final Logger LOG                    = Logger.getLogger(XmlUtils.class);
    public final static String  SCHEMA_LANGUAGE        = "http://www.w3.org/2001/XMLSchema";
    private static final String XML_PATH               = AppSysConfig.getValue(ContentUtils.XML_PATH);
    private static final String MULTITEMS              = "multItems";



    /**
     * 
     * @Title: validateByXsd
     * @Description:通过Schema验证指定的xml字符串是否符合结构
     * @param @param xmlStr
     * @param @param xsdStr
     * @param @return
     * @return XmlValidateResult
     * @throws
     */
    public static XmlValidateResult validateByXsd(String xmlStr, String xsdStr) {
        // 查找支持指定模式语言的 SchemaFactory 的实现并返回它
        SchemaFactory factory = SchemaFactory.newInstance(XmlUtils.SCHEMA_LANGUAGE);
        // 包装待验证的xml字符串为Reader
        Reader xmlReader = new BufferedReader(new StringReader(xmlStr));
        // 保障Schema xsd字符串为Reader
        Reader xsdReader = new BufferedReader(new StringReader(xsdStr));
        // 创建返回值类，默认为失败
        XmlValidateResult vs = new XmlValidateResult();

        try {
            // 构造Schema Source
            Source xsdSource = new StreamSource(xsdReader);
            // 解析作为模式的指定源并以模式形式返回它
            Schema schema = factory.newSchema(xsdSource);

            // 根据Schema检查xml文档的处理器,创建此 Schema 的新 Validator
            Validator validator = schema.newValidator();

            // 构造待验证xml Source
            Source xmlSource = new StreamSource(xmlReader);

            // 执行验证
            validator.validate(xmlSource);
            // 设置验证通过
            vs.setValidated(true);
            return vs;
        } catch (SAXException ex) {
            LOG.error("xml验证失败,ErrorMessage:" + ex.getMessage());
            // 设置验证失败
            vs.setValidated(false);
            // 设置验证失败信息
            vs.setErrorMsg(ex.getMessage());
            return vs;
        } catch (IOException e) {
            LOG.error("xml验证失败,ErrorMessage:" + e.getMessage());
            // 设置验证失败
            vs.setValidated(false);
            // 设置验证失败信息
            vs.setErrorMsg(e.getMessage());
            return vs;
        }
    }



    /**
     * 
     * @Title: createDocument
     * @Description:获取 Document
     * @param @return
     * @return Document
     * @throws
     */
    public static Document createDocument() {
        DocumentFactory documentFactory = DOMDocumentFactory.getInstance();
        Document document = documentFactory.createDocument();
        Element rootElement = documentFactory.createElement(ROOT_ELEMENT_NAME);
        document.add(rootElement);
        return document;
    }



    /**
     * 
     * @Title: createDocument
     * @Description:获取 Document
     * @param @return
     * @return Document
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static Element getSectionsEle(String xmlName) {
        SAXReader reader = new SAXReader();
        String xmlPath = BaseAction.basePath + xmlName;
        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            LOG.error("获取xml失败,errorMessage:" + xmlName + " 文件不存在!");
            return null;
        }
        Document document = null;
        try {
            document = reader.read(xmlFile);
        } catch (DocumentException e) {
            LOG.error("获取xml失败,error:" + e.getMessage());
            return null;
        }
        if (null == document) {
            LOG.error("获取xml失败,请检查xml文件是否为空!");
            return null;
        }
        List<Element> sectionsEleList = document.selectNodes(MEIQIREFERRER_SECTIONS);
        if (!CollectionsUtils.isNull(sectionsEleList)) {
            return sectionsEleList.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getMultItemsLevel
     * @Description:获取一个element mult items有多少层
     * @param @param ele
     * @param @return
     * @return int
     * @throws
     */
    @SuppressWarnings({ "unused", "unchecked" })
    private static int getMultItemsLevel(Element ele) {
        int multItemsLevel = 0;
        if (null == ele) {
            return multItemsLevel;
        }
        List<Element> childEleList = ele.elements();
        if (!CollectionsUtils.isNull(childEleList)) {
            for (Element childEle : childEleList) {
                if (MULTITEMS.endsWith(childEle.getName())) {
                    ++multItemsLevel;
                }
                multItemsLevel = multItemsLevel + getMultItemsLevel(childEle);
            }
        }

        return multItemsLevel;
    }



    /**
     * 
     * @Title: updateTextIndex
     * @Description:修改text 里面[0] 为 正确的下标,用于 multItems 配置了text的element
     * @param @param addElement
     * @param @param index
     * @return void
     * @throws
     */
    private static void updateTextIndex(Element element, int index,String paramName) {
        List<Element> updateIndexElementList = getNeedSetValueElement(element);
        if (CollectionsUtils.isNull(updateIndexElementList)) {
            return;
        }
        for (Element ele : updateIndexElementList) {
            String text = ele.getText();
            if (!StringUtils.isBlank(text) && text.contains("[0]")) {
                String[] textArray = text.split("\\$");
                for (int i = 0; i < textArray.length; i++) {
                    Pattern pattern = Pattern.compile("[0]");
                    Matcher matcher = pattern.matcher(textArray[i]);
                    if (matcher.find()) {
                        textArray[i] = textArray[i].replaceFirst(paramName + "\\[0\\]",paramName+ "[" + index + "]");
                    }
                }
                StringBuffer textStringBuffer = new StringBuffer();
                for (String str : textArray) {
                    if (str.startsWith("{")) {
                        textStringBuffer.append("$");
                    }
                    textStringBuffer.append(str);
                }
                ele.setText(textStringBuffer.toString());
            } else {
                ele.setText(text);
            }
        }

    }



    /**
     * 
     * @Title: getNeedSetValueElement
     * @Description:获取需要设置值的element
     * @param @param element
     * @param @return
     * @return List<Element>
     * @throws
     */
    @SuppressWarnings("unchecked")
    private static List<Element> getNeedSetValueElement(Element element) {
        if (null == element) {
            return null;
        }
        List<Element> needSetValueEleList = new ArrayList<Element>();
        String text = element.getText();
        if (!StringUtils.isBlank(text) && text.contains(SET_VALUE_SIGN1) && text.contains(SET_VALUE_SIGN2)) {
            needSetValueEleList.add(element);
        }
        List<Element> childElementList = element.elements();
        if (!CollectionsUtils.isNull(childElementList)) {
            List<Element> elementList = null;
            for (Element childElement : childElementList) {
                elementList = getNeedSetValueElement(childElement);
                needSetValueEleList.addAll(elementList);
            }
        }
        return needSetValueEleList;
    }



    /**
     * 
     * @Title: assembleEelement
     * @Description:装配Element 根据实体类 与 xml sectionsEle
     * @param @param sectionsEle
     * @param @param object
     * @param @return
     * @return Element
     * @throws
     */
    public static Element assembleElement(Element sectionsEle, Object object, String xsdName) {
        if (null == sectionsEle) {
            return null;
        }

        // 拼装xml element : add 含有 multItems
        updateElementByMultItems(sectionsEle, object);

        // 验证
        // XmlValidateResult xmlValidateResult =
        // validateByXsd(sectionsEle.getDocument(), xsdName);
        // if (null != xmlValidateResult && !xmlValidateResult.isValidated()) {
        // return getErrorMessageEle(xmlValidateResult);
        // }

        // 获取data element
        List<Element> needSetValueEleList = new ArrayList<Element>();

        needSetValueEleList = getNeedSetValueElement(sectionsEle);
        if (CollectionsUtils.isNull(needSetValueEleList)) {
            return sectionsEle;
        }

        for (Element needSetValueEle : needSetValueEleList) {
            // 获取text
            String text = needSetValueEle.getText();
            // 判断 是否需要设置值
            if (!StringUtils.isBlank(text) && text.contains(SET_VALUE_SIGN1) && text.contains(SET_VALUE_SIGN2)) {
                String nowText = getValueForDataElement(text, object);
                needSetValueEle.setText(nowText);
            }

        }
        return sectionsEle;
    }



    /**
     * 
     * @Title: validateByXsd
     * @Description:验证xml是否合法
     * @param @param sectionsEle
     * @param @param xsdName
     * @param @return
     * @return XmlValidateResult
     * @throws
     */
    @SuppressWarnings("unused")
    private static XmlValidateResult validateByXsd(Document document, String xsdName) {
        XmlValidateResult xmlValidateResult = new XmlValidateResult();
        xmlValidateResult.setValidated(false);
        if (!StringUtils.isBlank(xsdName)) {
            SAXReader reader = new SAXReader();
            String xsdPath = BaseAction.basePath + XML_PATH + xsdName + ".xsd";
            File xsdFile = new File(xsdPath);
            if (!xsdFile.exists()) {
                LOG.error("获取xml失败,errorMessage:xml 文件不存在!");
            }
            Document xsdDocument = null;
            try {
                xsdDocument = reader.read(xsdFile);
            } catch (DocumentException e) {
                xmlValidateResult.setErrorMsg("获取xml失败,error:" + e.getMessage());
                LOG.error("获取xml失败,error:" + e.getMessage());
            }
            if (null == xsdDocument) {
                xmlValidateResult.setErrorMsg("获取xml失败,请检查xml文件是否为空!");
                LOG.error("获取xml失败,请检查xml文件是否为空!");
            }
            xmlValidateResult = validateByXsd(document.asXML(), xsdDocument.asXML());
        }
        return xmlValidateResult;
    }



    /**
     * 
     * @Title: getErrorMessageEle
     * @Description:获取xml验证失败error message
     * @param @param xmlValidateResult
     * @param @return
     * @return Element
     * @throws
     */
    @SuppressWarnings("unused")
    private static Element getErrorMessageEle(XmlValidateResult xmlValidateResult) {
        String xmlStr = "<sections><validated>false</validated><errorMsg>errorMessage</errorMsg></sections>";
        if (null != xmlValidateResult) {
            xmlStr = xmlStr.replaceAll("errorMessage", xmlValidateResult.getErrorMsg());
        }
        try {
            Document document = DocumentHelper.parseText(xmlStr);
            return document.getRootElement();
        } catch (DocumentException e) {
            LOG.error("Function:getErrorMessageEle.Error:" + e.getMessage());
        }
        return null;
    }



    /**
     * 
     * @Title: getValueForDataElement
     * @Description:
     * @param @param dataText
     * @param @param object
     * @param @return
     * @return String
     * @throws
     */
    private static String getValueForDataElement(String dataText, Object object) {
        // 可能会有多个值 拼接
        // :${OrderItem.consignee.phone1}+电话+${OrderItem.consignee.phone2}
        String[] dataTextArray = dataText.split("\\$\\{");
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < dataTextArray.length; i++) {
            String paramText = dataTextArray[i];
            // 排除 拼接 字符串：电话
            if (!paramText.contains(SET_VALUE_SIGN1) && !paramText.contains(SET_VALUE_SIGN2)) {
                result.append(paramText);
                continue;
            }
            int index = paramText.lastIndexOf("}");
            String notSetVallueStr = "";
            if (-1 != index) {
                notSetVallueStr = paramText.substring(index + 1);
                paramText = paramText.substring(0, index).trim().toLowerCase();

            } else {
                paramText = paramText.substring(0, paramText.length() - 1).trim().toLowerCase();
            }
            // 根据text 获取值
            Object valueObejct = getObjectByText(paramText, object);

            if (null != valueObejct) {
                result.append(valueObejct.toString());
            }
            result.append(notSetVallueStr);
        }
        return result.toString();
    }



    /**
     * 
     * @Title: getObjectByText
     * @Description:根据 OrderItem.carts[0].goods.cover 获取 值： cover
     * @param @param text
     * @param @param object
     * @param @return
     * @return Object
     * @throws
     */
    private static Object getObjectByText(String text, Object object) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Object nowParamObejct = object;
        
       List<String> list=new ArrayList<String>();
       int index=text.indexOf("(");
       if(index!=-1){
           String strTmp=text.substring(0,index);
           String[] array=strTmp.split("\\.");
           int length=array.length;
           for(int i=0;i<length;i++){
               if(i<length-1){
                   list.add(array[i]);
               }else{
                   String str1=array[i]+text.substring(index);
                   list.add(str1);
               }
           }
       }else{
           String[] array=text.split("\\.");
           int length=array.length;
           for(int i=0;i<length;i++){
               list.add(array[i]);
           }
       }
        
        if (list.size() > 1) {
            // 递归循环获取 属性值
            for (int j = 1; j < list.size(); j++) {
                nowParamObejct = getValueFormObject(nowParamObejct, list.get(j));
            }
        }

        return nowParamObejct;
    }



    /**
     * 
     * @Title: getValueFormO
     * @Description:获取实体类属性值
     * @param @param object
     * @param @param paramName
     * @param @return
     * @return Object
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static Object getValueFormObject(Object object, String paramName) {
        if (null == object || StringUtils.isBlank(paramName)) {
            return null;
        }
        Object o = null;
        boolean isCollection = false;
        String realParamName = paramName;
        int index = 0;
        // 是否是集合
        if (realParamName.contains("[") && realParamName.contains("]")
                && !(realParamName.contains("(") || realParamName.contains(")"))) {
            String[] paramArray = realParamName.split("\\[");
            String indexStr = paramArray[1].replaceAll("\\]", "");
            index = StringUtils.StringToInt(indexStr);
            realParamName = paramArray[0];
            isCollection = true;
        }
        // 获取下标
        if (object instanceof Collection<?> && realParamName.contains("[") && realParamName.contains("]")) {
            String[] paramArray = realParamName.split("\\[");
            String indexStr = paramArray[1].replaceAll("\\]", "");
            index = StringUtils.StringToInt(indexStr);
        }
        // 是否需要格式化
        String format = "";
        if (paramName.contains("(") && paramName.contains(")") && paramName.contains(ContentUtils.COMMA)) {
            format = paramName.substring(paramName.indexOf("(") + 1, paramName.length() - 1);
            realParamName = paramName.substring(0, paramName.indexOf("("));
        }

        // 获取f对象对应类中的所有属性域
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            String varName = fields[i].getName();
            if (!realParamName.equalsIgnoreCase(varName)) {
                continue;
            } else {
                try {
                    // 获取原来的访问控制权限
                    boolean accessFlag = fields[i].isAccessible();
                    // 修改访问控制权限
                    fields[i].setAccessible(true);
                    // 获取在对象f中属性fields[i]对应的对象中的变量
                    o = fields[i].get(object);
                    if (o instanceof Collection<?> && isCollection) {
                        Collection<Object> list = (Collection<Object>) o;
                        if (index < list.toArray().length) {
                            o = list.toArray()[index];
                        } else {
                            o = null;
                        }
                    }
                    // 恢复访问控制权限
                    fields[i].setAccessible(accessFlag);

                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }

        // 格式化数据
        if (!StringUtils.isBlank(format) && null != o) {
            o = StringUtils.formatStringByRegex(o.toString(), format);
        }

        return formartObjectValue(paramName, o);
    }



    /**
     * 
     * @Title: formartObjectValue
     * @Description:格式化数据
     * @param @param paramName
     * @param @param obejct
     * @param @return
     * @return Object
     * @throws
     */
    private static Object formartObjectValue(String paramName, Object obejct) {
        if (null == obejct || StringUtils.isBlank(paramName)) {
            return obejct;
        }
        String objectType = obejct.getClass().toString().toLowerCase();
        if (objectType.contains("double")) {
            obejct = StringUtils.savePriceTwoDecimal((Double) obejct);
        } else if (paramName.contains("time") && objectType.contains("int") && obejct.toString().length() > 9) {
            // 时间格式化 1746494941 -> yyyy-MM-dd HH:mm:ss
            obejct = DateUtils.timeToDate((Integer) obejct * 1000l);
        }
        return obejct;
    }



    /**
     * 
     * @Title: getElementListByEleName
     * @Description:根据element name 获取所有的element
     * @param @param element
     * @param @param eleName
     * @param @return
     * @return List<Element>
     * @throws
     */
    public static List<Element> getElementListByEleName(Element element) {
        List<Element> allLeafs = getAllLeafNode(element);
        if (CollectionsUtils.isNull(allLeafs)) {
            return null;
        }
        List<Element> elements = new ArrayList<Element>();
        for (Element ele : allLeafs) {
            if (MULTITEMS.equals(ele.getName())) {
                elements.add(ele);
            }
        }
        return elements;
    }



    /**
     * 
     * @Title: updateElementByMultItems
     * @Description:根据multItems 修改xml element 添加集合 element
     * @param @param element
     * @param @param eleName
     * @param @return
     * @return List<Element>
     * @throws
     */
    public static void updateElementByMultItems(Element element, Object object) {
        if (null == element) {
            return;
        }
        if (MULTITEMS.equals(element.getName())) {
            addElement(element, object);
        }

        // 所有子节点
        List<Element> childElementList = getLeafNodes(element);
        if (CollectionsUtils.isNull(childElementList)) {
            return;
        }
        for (int i = 0; i < childElementList.size(); i++) {
            Element childElement = childElementList.get(i);
            updateElementByMultItems(childElement, object);
        }
    }



    /**
     * 
     * @Title: addElement
     * @Description:
     * @param @param element
     * @param @param object 参数说明
     * @return void 返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static void addElement(Element element, Object object) {
        String text = element.getText();
        if (StringUtils.isBlank(text) || !text.contains(SET_VALUE_SIGN1) || !text.contains(SET_VALUE_SIGN2)) {
            return;
        }
        text = text.replaceAll("\\$\\{", "").replaceAll("\\}", "");
        // 设置text 标记该element已处理
        element.setText(text);
        // 根据text 获取 值
        Object nowParamObejct = getObjectByText(text, object);
        if (null != nowParamObejct && nowParamObejct instanceof Collection<?>) {
            Collection<Object> list = (Collection<Object>) nowParamObejct;
            // 获取父节点
            Element parentItemsElement = element.getParent();
            for (int j = 1; j < list.size(); j++) {
                // 获取父节点 grandparent
                Element grandparentEle = parentItemsElement.getParent();
                Element addElement = parentItemsElement.createCopy();
                // 修改下标
                String[] strs = text.split("\\.");
                updateTextIndex(addElement, j, strs[strs.length - 1]);
                List<Element> elements = grandparentEle.elements();
                elements.add(elements.indexOf(parentItemsElement) + j, addElement);
                updateElementByMultItems(addElement, object);
            }
        }
    }



    /**
     * 
     * @Title: getAllLeafNode
     * @Description:获取所有子节点
     * @param @param element
     * @param @return
     * @return List<Element>
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static List<Element> getAllLeafNode(Element element) {
        List<Element> allLeafs = new ArrayList<Element>();
        Iterator<Element> allSons = element.elementIterator();
        while (allSons.hasNext()) {
            List<Element> leafs = getLeafNodes(allSons.next());
            allLeafs.addAll(leafs);
        }
        return allLeafs;
    }



    /**
     * 
     * @Title: getLeafNodes
     * @Description:获取所有子节点
     * @param @param currentNode
     * @param @return
     * @return List<Element>
     * @throws
     */
    @SuppressWarnings("unchecked")
    private static List<Element> getLeafNodes(Element currentNode) {
        List<Element> allLeafs = new ArrayList<Element>();
        Element e = currentNode;
        if ((e.elements()).size() >= 0) {
            List<Element> el = e.elements();
            for (Element sonNode : el) {
                if (sonNode.elements().size() > 0)
                    allLeafs.addAll(getLeafNodes(sonNode));
                else
                    allLeafs.add(sonNode);
            }
        }
        return allLeafs;
    }
}
