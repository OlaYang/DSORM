package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.meiqi.app.pojo.checkout.CodTradeReqBody;

import java.io.Serializable;

/**
 * Description: Author: jiawen.huang Date: 15/1/29 Time: 14:32 Version: 1.0
 * Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlRootElement(name = "COD-MS")
public class CodTradeReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private CodReqHeader      reqHeader;

    private CodTradeReqBody   reqBody;



    @XmlElement(name = "SessionHead")
    public CodReqHeader getReqHeader() {
        return reqHeader;
    }



    public void setReqHeader(CodReqHeader reqHeader) {
        this.reqHeader = reqHeader;
    }



    @XmlElement(name = "SessionBody")
    public CodTradeReqBody getReqBody() {
        return reqBody;
    }



    public void setReqBody(CodTradeReqBody reqBody) {
        this.reqBody = reqBody;
    }
}
