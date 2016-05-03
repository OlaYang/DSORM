package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.dsmanager.action.ISendMessageAction;
import com.meiqi.dsmanager.rmi.IRmiSendMessageService;

@RemoteService(serviceInterface = IRmiSendMessageService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiSendMessageService implements IRmiSendMessageService {

	@Autowired
	private ISendMessageAction sendMessageAction;
	
	@Override
	public String sendMessage(String content) {
		return sendMessageAction.sendMessage(content);
	}

}
