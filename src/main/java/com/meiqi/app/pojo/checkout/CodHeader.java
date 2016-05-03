package com.meiqi.app.pojo.checkout;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Description:COD报文头 Author: jiawen.huang Date: 15/1/29 Time: 14:09 Version:
 * 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlType
@XmlRootElement
public class CodHeader implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            Version;              // 业务编码

    private String            ServiceCode;          // 业务编码

    private String            TransactionID;        // 业务编码

    private String            SrcSysID;             // 发起请求方编码

    private String            DstSysID;             // 应答方编码

    private String            ExtendAtt;            // 扩展属性

    private String            HMAC;



    @XmlElement(name = "Version")
    public String getVersion() {
        return Version;
    }



    public void setVersion(String version) {
        Version = version;
    }



    @XmlElement(name = "ServiceCode")
    public String getServiceCode() {
        return ServiceCode;
    }



    public void setServiceCode(String serviceCode) {
        ServiceCode = serviceCode;
    }



    @XmlElement(name = "TransactionID")
    public String getTransactionID() {
        return TransactionID;
    }



    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }



    @XmlElement(name = "SrcSysID")
    public String getSrcSysID() {
        return SrcSysID;
    }



    public void setSrcSysID(String srcSysID) {
        SrcSysID = srcSysID;
    }



    @XmlElement(name = "DstSysID")
    public String getDstSysID() {
        return DstSysID;
    }



    public void setDstSysID(String dstSysID) {
        DstSysID = dstSysID;
    }



    @XmlElement(name = "ExtendAtt")
    public String getExtendAtt() {
        return ExtendAtt;
    }



    public void setExtendAtt(String extendAtt) {
        ExtendAtt = extendAtt;
    }



    @XmlElement(name = "HMAC")
    public String getHMAC() {
        return HMAC;
    }



    public void setHMAC(String HMAC) {
        this.HMAC = HMAC;
    }
}
