package com.meiqi.dsmanager.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.entity.DataSourcesStyle;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.XmlUtil;

/**
 * @author 作者 xubao
 * @version 创建时间：2015年6月23日 下午3:19:29 类说明
 */

public class CommonUtil {

	/**
	 * 得到get请求的参数
	 * 
	 * @param request
	 * @param content
	 * @return
	 */
	public static String getNoKeyParamValue(HttpServletRequest request) {
		String content = "";
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Iterator<Entry<String, String[]>> iterator = parameterMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String[]> entry = iterator.next();
			if (entry.getValue().length == 1 && "".equalsIgnoreCase(entry.getValue()[0])) {
				content = entry.getKey();
				break;
			}
		}
		return content;
	}

	/**
	 * 得到返回的xml样式
	 * 
	 * @param dataSourcesStyle
	 * @param resultData
	 * @return
	 */
	public static String getXmlByStyle(DataSourcesStyle dataSourcesStyle, String resultData) {
		String stytleContent = dataSourcesStyle.getStytleContent();
		try {
			resultData = XmlUtil.convertSourceJsontoTargetXMLByXslStyle(resultData, stytleContent);
		} catch (Exception e) {
			LogUtil.error(e.getMessage());
			ResponseBaseData responseData = new ResponseBaseData();
			responseData.setCode(DsResponseCodeData.STYLE_CONVERT_ERROR.code);
			responseData.setDescription(DsResponseCodeData.STYLE_CONVERT_ERROR.description);
			resultData = XmlUtil.jsonToXml(JSON.toJSONString(responseData));
		}
		return resultData;
	}

	/**
	 * 得到请求的参数
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getContet(HttpServletRequest request, HttpMethod method) throws IOException {
		String content = null;
		if (HttpMethod.GET.equals(method)) {
			content = getNoKeyParamValue(request);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		return content;

	}

	public static String getDecodeContent(String content) {
		String decodeContent = "";
		try {
		    content = content.replaceAll("\\+", "%2B"); 
			decodeContent = URLDecoder.decode(content, "UTF-8");
			if (decodeContent.endsWith("=")) {
				decodeContent = decodeContent.substring(0, decodeContent.length() - 1);
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		return decodeContent;
	}
}
