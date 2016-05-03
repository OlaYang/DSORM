package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.dsmanager.action.IUserListAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.rule.userlist.UserListReqInfo;
import com.meiqi.dsmanager.rmi.IRmiUserListService;
import com.meiqi.dsmanager.util.DataUtil;

@RemoteService(serviceInterface = IRmiUserListService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiUserListService implements IRmiUserListService {

	@Autowired
	private IUserListAction userListAction;
	
	@Override
	public String getUserList(String reqStr) {
	    String decodeContent = CommonUtil.getDecodeContent(reqStr);
		UserListReqInfo reqInfo = DataUtil.parse(decodeContent, UserListReqInfo.class);
		return userListAction.getUserList(reqInfo); 
	}

}
