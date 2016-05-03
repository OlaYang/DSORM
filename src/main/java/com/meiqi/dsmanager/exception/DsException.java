package com.meiqi.dsmanager.exception;

import java.util.HashMap;
import java.util.Map;

public class DsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DsException() {
		super();
	}

	public DsException(String message) {
		super(message);
	}

	public DsException(String message, Throwable cause) {
		super(message, cause);
	}

	public DsException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	* @Title: getExceptionInfo 
	* @Description: TODO(返回对应数据源及异常信息) 
	* @param @param sname 数据源名称
	* @param @param msg 异常信息
	* @param @return  参数说明 
	* @return Map<String,String>    返回类型 
	* @throws
	 */
	public static Map<String,String> getExceptionInfo(String sname,String msg){
	    Map<String,String> exceptionMap = new HashMap<String, String>();//异常信息
	    exceptionMap.put("sname", sname);
        exceptionMap.put("msg", msg);
        return exceptionMap;
	}
}
