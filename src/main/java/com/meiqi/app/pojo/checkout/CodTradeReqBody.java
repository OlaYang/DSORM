package com.meiqi.app.pojo.checkout;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Description:COD上行付款交易通知报文消息体 Author: jiawen.huang Date: 15/1/29 Time: 14:22
 * Version: 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlType
@XmlRootElement
public class CodTradeReqBody implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            EmployeeID;           // 操作员工号

    private String            PosSn;                // POS机编号

    private String            OrderNo;              // 订单号

    private String            Amount;               // 金额（格式为：77,777.77）

    private String            Split;                // 是否拆单付款

    private String            PayTypeCode;          // 支付方式标识1刷卡支付、2账户支付、3支票支付、4现金支付
                                                     // 5储值卡:7：月结 8：无款 9：代金券 6：
                                                     // 积分 10：组合支付

    private String            PayMethod;            // 支付类型（1整单支付 2拆单支付）

    private String            ChequeNo;             // 支票号

    private String            PosRequestID;         // 凭证号

    private String            ReferNo;              // 参考号

    private String            BankCardNo;           // 银行卡号(中间会有星号)

    private String            BankCardName;         // 银行卡名称

    private String            BankOrderNo;          // 银行订单号

    private String            YeepayOrderNo;        // 易宝订单号

    private String            BusinessType;         // 业务类型



    @XmlElement(name = "EmployeeID")
    public String getEmployeeID() {
        return EmployeeID;
    }



    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }



    @XmlElement(name = "PosSn")
    public String getPosSn() {
        return PosSn;
    }



    public void setPosSn(String posSn) {
        PosSn = posSn;
    }



    @XmlElement(name = "OrderNo")
    public String getOrderNo() {
        return OrderNo;
    }



    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }



    @XmlElement(name = "Amount")
    public String getAmount() {
        return Amount;
    }



    public void setAmount(String amount) {
        Amount = amount;
    }



    @XmlElement(name = "Split")
    public String getSplit() {
        return Split;
    }



    public void setSplit(String split) {
        Split = split;
    }



    @XmlElement(name = "PayTypeCode")
    public String getPayTypeCode() {
        return PayTypeCode;
    }



    public void setPayTypeCode(String payTypeCode) {
        PayTypeCode = payTypeCode;
    }



    @XmlElement(name = "PayMethod")
    public String getPayMethod() {
        return PayMethod;
    }



    public void setPayMethod(String payMethod) {
        PayMethod = payMethod;
    }



    @XmlElement(name = "ChequeNo")
    public String getChequeNo() {
        return ChequeNo;
    }



    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }



    @XmlElement(name = "PosRequestID")
    public String getPosRequestID() {
        return PosRequestID;
    }



    public void setPosRequestID(String posRequestID) {
        PosRequestID = posRequestID;
    }



    @XmlElement(name = "ReferNo")
    public String getReferNo() {
        return ReferNo;
    }



    public void setReferNo(String referNo) {
        ReferNo = referNo;
    }



    @XmlElement(name = "BankCardNo")
    public String getBankCardNo() {
        return BankCardNo;
    }



    public void setBankCardNo(String bankCardNo) {
        BankCardNo = bankCardNo;
    }



    @XmlElement(name = "BankCardName")
    public String getBankCardName() {
        return BankCardName;
    }



    public void setBankCardName(String bankCardName) {
        BankCardName = bankCardName;
    }



    @XmlElement(name = "BankOrderNo")
    public String getBankOrderNo() {
        return BankOrderNo;
    }



    public void setBankOrderNo(String bankOrderNo) {
        BankOrderNo = bankOrderNo;
    }



    @XmlElement(name = "YeepayOrderNo")
    public String getYeepayOrderNo() {
        return YeepayOrderNo;
    }



    public void setYeepayOrderNo(String yeepayOrderNo) {
        YeepayOrderNo = yeepayOrderNo;
    }



    @XmlElement(name = "BusinessType")
    public String getBusinessType() {
        return BusinessType;
    }



    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }
}
