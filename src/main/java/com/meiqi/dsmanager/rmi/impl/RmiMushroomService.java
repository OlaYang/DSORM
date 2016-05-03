package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.rmi.IRmiMushroomService;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.mushroom.dao.ITMushroomServiceDao;

@RemoteService(serviceInterface = IRmiMushroomService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiMushroomService implements IRmiMushroomService{

	@Autowired
	private IMushroomAction mushroomAction;
	@Autowired
	private ITMushroomServiceDao tMushroomServiceDao;
	
	@Override
	public String offer(String content) {
		String decodeContent = CommonUtil.getDecodeContent(content);
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.offer(dsReqInfo);
	}

	@Override
	public String start(String content) {
		String decodeContent = CommonUtil.getDecodeContent(content);
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.start(dsReqInfo);
	}

	@Override
	public String commit(String content) {
		String decodeContent = CommonUtil.getDecodeContent(content);
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.commit(dsReqInfo);
	}

	@Override
	public String rollback(String content) {
		String decodeContent = CommonUtil.getDecodeContent(content);
		DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
		return mushroomAction.rollback(dsReqInfo);
	}

}
