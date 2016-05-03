package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Description:COD登陆请求对象
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 13:54
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlRootElement(name = "COD-MS")
public class CodLoginRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private CodReqHeader reqHeader;

	private CodLoginReqBody reqBody;

	@XmlElement(name = "SessionHead")
	public CodReqHeader getReqHeader() {
		return reqHeader;
	}

	public void setReqHeader(CodReqHeader reqHeader) {
		this.reqHeader = reqHeader;
	}

	@XmlElement(name = "SessionBody")
	public CodLoginReqBody getReqBody() {
		return reqBody;
	}

	public void setReqBody(CodLoginReqBody reqBody) {
		this.reqBody = reqBody;
	}
}
