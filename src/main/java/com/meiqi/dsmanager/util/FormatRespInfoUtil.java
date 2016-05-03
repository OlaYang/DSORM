/**   
* @Title: FormatRespInfoActionImpl.java 
* @Package com.meiqi.dsmanager.service.impl 
* @Description: TODO(用一句话描述该文件做什么) 
* @author yangyong
* @date 2015年6月24日 下午2:55:37 
* @version V1.0   
*/
package com.meiqi.dsmanager.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.util.ConfigUtil;
import com.meiqi.dsmanager.common.config.DataFormatConfig;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.entity.DataSourcesStyle;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;

/** 
 * @ClassName: FormatRespInfoActionImpl 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author yangyong
 * @date 2015年6月24日 下午2:55:37 
 *  
 */
public class FormatRespInfoUtil
{
    
    
    /*
     * Title: format
     * Description: 
     * @param reqInfo
     * @param respInfo
     * @return 
     */
    public static String format(DsManageReqInfo reqInfo, RuleServiceResponseData respInfo, DataSources dataSource)
    {
        String resultData = JSON.toJSONString(respInfo);
        if (DataFormatConfig.XML.format.equalsIgnoreCase(reqInfo.getFormat())) {
            List<DataSourcesStyle> dsStyles = dataSource.getDsStyle();
            if (dsStyles == null || dsStyles.isEmpty()) {
                ResponseBaseData responseData = new ResponseBaseData(DsResponseCodeData.NOT_SET_STYLE.code, DsResponseCodeData.NOT_SET_STYLE.description);
                resultData = XmlUtil.jsonToXml(JSON.toJSONString(responseData));
            } else {
                resultData = getXmlByStyle(dsStyles.get(0), resultData);
            }
        }else if(DataFormatConfig.EXCEL.format.equalsIgnoreCase(reqInfo.getFormat())){
        	    try {
					resultData = new String(respInfo.getExcelByte(),"GBK");
				} catch (UnsupportedEncodingException e) {
				}
        }else if(DataFormatConfig.NOT_USE_XSL_XML.format.equalsIgnoreCase(reqInfo.getFormat())){
            resultData = data2Xml(respInfo.getRows());
        }
        return resultData;
    }
    
    
    private static String data2Xml(List<Map<String, String>> rowsList) {
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("result");
        if(ConfigUtil.getProject_center()==0){
            rootElement.addAttribute("code","0");
            rootElement.addAttribute("description","success");
        }

        Element rowE;

        for (Map<String, String> rows : rowsList) {
            rowE = rootElement.addElement("row");

            for (Map.Entry<String, String> entry : rows.entrySet()) {
                rowE.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        return document.asXML();
    }
    
    private static String getXmlByStyle(DataSourcesStyle dataSourcesStyle, String resultData) {
        String stytleContent = dataSourcesStyle.getStytleContent();
        try {
            resultData = XmlUtil.convertSourceJsontoTargetXMLByXslStyle(resultData, stytleContent);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            ResponseBaseData responseData = new ResponseBaseData();
            responseData.setCode(DsResponseCodeData.STYLE_CONVERT_ERROR.code);
            responseData.setDescription(DsResponseCodeData.STYLE_CONVERT_ERROR.description);
            resultData = XmlUtil.jsonToXml(JSON.toJSONString(responseData));
            try {
                resultData=URLDecoder.decode(resultData,"utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return resultData;
    }
    
}
