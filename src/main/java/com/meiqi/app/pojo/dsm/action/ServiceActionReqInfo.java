package com.meiqi.app.pojo.dsm.action;

import java.util.Map;



public class ServiceActionReqInfo {
	private String serviceName;
	private Map<String,Object> param;
	
    public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
