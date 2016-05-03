package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.dsmanager.action.IPreviewAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.rule.preview.PreviewReqInfo;
import com.meiqi.dsmanager.rmi.IRmiPreviewService;
import com.meiqi.dsmanager.util.DataUtil;

@RemoteService(serviceInterface = IRmiPreviewService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiPreviewService implements IRmiPreviewService {

	@Autowired
	private IPreviewAction previewAction;
	
	@Override
	public String getPreview(String previewStr){
	    String decodeContent = CommonUtil.getDecodeContent(previewStr);
		PreviewReqInfo reqInfo = DataUtil.parse(decodeContent, PreviewReqInfo.class);
		return previewAction.getPreview(reqInfo);
	}

}
