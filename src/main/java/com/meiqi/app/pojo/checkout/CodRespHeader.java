package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Description:COD响应报文 Author: jiawen.huang Date: 15/1/29 Time: 11:58 Version:
 * 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlType
@XmlRootElement
public class CodRespHeader extends CodHeader {

    private static final long serialVersionUID = -2750312542842844L;

    private String            ResultCode;                           // 请求结果编码
                                                                     // 2=接收成功
                                                                     // 3=接收失败

    private String            ResultMsg;                            // 请求结果消息

    private String            RespTime;                             // 请求时间,格式为yyyyMMddHHmmss



    @XmlElement(name = "Result_code")
    public String getResultCode() {
        return ResultCode;
    }



    public void setResultCode(String resultCode) {
        ResultCode = resultCode;
    }



    @XmlElement(name = "Result_msg")
    public String getResultMsg() {
        return ResultMsg;
    }



    public void setResultMsg(String resultMsg) {
        ResultMsg = resultMsg;
    }



    @XmlElement(name = "Resp_time")
    public String getRespTime() {
        return RespTime;
    }



    public void setRespTime(String respTime) {
        RespTime = respTime;
    }
}
