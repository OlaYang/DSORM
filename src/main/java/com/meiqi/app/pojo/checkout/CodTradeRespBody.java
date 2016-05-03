package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Description:
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 14:52
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlType
@XmlRootElement
public class CodTradeRespBody implements Serializable{
	private static final long serialVersionUID = 1L;

	private String orderNo;

	@XmlElement(name = "OrderNo")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}
