package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Description:COD下行响应登陆报文
 * Author: jiawen.huang
 * Date: 15/1/29
 * Time: 14:33
 * Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlRootElement(name = "COD-MS")
public class CodLoginResponseDTO implements Serializable{
	private static final long serialVersionUID = 1L;

	private CodRespHeader codRespHeader;

	private CodLoginRespBody codLoginRespBody;

	@XmlElement(name = "SessionHead")
	public CodRespHeader getCodRespHeader() {
		return codRespHeader;
	}

	public void setCodRespHeader(CodRespHeader codRespHeader) {
		this.codRespHeader = codRespHeader;
	}

	@XmlElement(name = "SessionBody")
	public CodLoginRespBody getCodLoginRespBody() {
		return codLoginRespBody;
	}

	public void setCodLoginRespBody(CodLoginRespBody codLoginRespBody) {
		this.codLoginRespBody = codLoginRespBody;
	}
}
