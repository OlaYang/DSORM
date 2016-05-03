package com.meiqi.dsmanager.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;


public interface IMushroomAction {
	
//	public String setData(DsManageReqInfo reqInfo);

    public String offer(DsManageReqInfo reqInfo);
	
    public String offer(DsManageReqInfo reqInfo,HttpServletRequest request, HttpServletResponse response);
    
	public String start(DsManageReqInfo reqInfo);
	
	public String commit(DsManageReqInfo reqInfo);
	
	public String rollback(DsManageReqInfo reqInfo);
	
	public void insertRuleException2DB(String serviceName, String msg);
}
