package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Description:COD下行操作员查询应答报文体
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 14:37
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlType
@XmlRootElement
public class CodLoginRespBody implements Serializable{
	private static final long serialVersionUID = 1L;

	private String employeeId;

	private String employeeName;

	private String companyCode;

	private String companyName;

	private String companyAddr;

	private String companyTel;

	@XmlElement(name = "Employee_ID")
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	@XmlElement(name = "Employee_Name")
	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@XmlElement(name = "Company_Code")
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	@XmlElement(name = "Company_Name")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@XmlElement(name = "Company_Addr")
	public String getCompanyAddr() {
		return companyAddr;
	}

	public void setCompanyAddr(String companyAddr) {
		this.companyAddr = companyAddr;
	}

	@XmlElement(name = "Company_Tel")
	public String getCompanyTel() {
		return companyTel;
	}

	public void setCompanyTel(String companyTel) {
		this.companyTel = companyTel;
	}
}
