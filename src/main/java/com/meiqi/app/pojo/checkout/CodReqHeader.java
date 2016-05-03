package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Description:COD请求报文 Author: jiawen.huang Date: 15/1/29 Time: 11:56 Version:
 * 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
// @XmlType
@XmlRootElement
public class CodReqHeader extends CodHeader {

    private static final long serialVersionUID = 8815298165654536011L;
    private String            ReqTime;                                 // 请求时间



    public String getReqTime() {
        return ReqTime;
    }



    public void setReqTime(String reqTime) {
        ReqTime = reqTime;
    }
}
