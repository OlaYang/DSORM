package com.meiqi.dsmanager.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.data.engine.RengineException;
import com.meiqi.dsmanager.action.IPreviewAction;
import com.meiqi.dsmanager.po.rule.preview.PreviewReqInfo;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;

@Controller
@RequestMapping("/service")
public class PreviewController {
	
	@Autowired
	private IPreviewAction previewAction;

	@ResponseBody
	@RequestMapping("/preview")
	public String getPreview(HttpServletRequest request) throws RengineException, Exception{
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		PreviewReqInfo reqInfo = DataUtil.parse(decodeContent, PreviewReqInfo.class);
		return previewAction.getPreview(reqInfo);
	}
}
