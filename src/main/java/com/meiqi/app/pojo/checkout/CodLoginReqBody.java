package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Description:COD上行登陆请求报文消息体
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 14:19
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
//@XmlType
@XmlRootElement
public class CodLoginReqBody implements Serializable{
	private static final long serialVersionUID = 1L;

	private String employeeId;//工号，要求此工号必须在接入方公司系统中可以唯一标识一个操作员

	private String password;//工号，要求此工号必须在接入方公司系统中可以唯一标识一个操作员

	@XmlElement(name = "Employee_ID")
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	@XmlElement(name = "Password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
