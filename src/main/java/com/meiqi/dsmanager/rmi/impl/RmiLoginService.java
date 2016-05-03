package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.alibaba.fastjson.JSON;
import com.meiqi.dsmanager.action.ILoginAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.rule.login.LoginReqInfo;
import com.meiqi.dsmanager.rmi.IRmiLoginService;
import com.meiqi.dsmanager.util.DataUtil;

@RemoteService(serviceInterface = IRmiLoginService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiLoginService implements IRmiLoginService {
	
	@Autowired
	private ILoginAction loginAction;

	@Override
	public String login(String reqStr) {
	    String decodeContent = CommonUtil.getDecodeContent(reqStr);
		LoginReqInfo reqInfo = DataUtil.parse(decodeContent, LoginReqInfo.class);
		return JSON.toJSONString(loginAction.login(reqInfo));
	}

}
