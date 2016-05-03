package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: PayLog
 * @Description:支付记录
 * @author 杨永川
 * @date 2015年6月4日 下午3:58:57
 *
 */
public class PayLog {
    private long   payLogId;
    private long   orderId   = 0;
    private double orderAmount;
    private byte   orderType = 0;
    private byte   isPaid    = 0;
    private int    addTime   = 0;
    // 交易号
    private String transactionId;
    // 支付方式 1=易宝
    private int    payType   = 0;
    private String remark;



    public PayLog() {
    }



    public PayLog(long payLogId, long orderId, double orderAmount, byte orderType, byte isPaid, int addTime,
            String transactionId, int payType, String remark) {
        super();
        this.payLogId = payLogId;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.orderType = orderType;
        this.isPaid = isPaid;
        this.addTime = addTime;
        this.transactionId = transactionId;
        this.payType = payType;
        this.remark = remark;
    }



    public String getTransactionId() {
        return transactionId;
    }



    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }



    public int getPayType() {
        return payType;
    }



    public void setPayType(int payType) {
        this.payType = payType;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }



    public long getPayLogId() {
        return payLogId;
    }



    public void setPayLogId(long payLogId) {
        this.payLogId = payLogId;
    }



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public double getOrderAmount() {
        return orderAmount;
    }



    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }



    public byte getOrderType() {
        return orderType;
    }



    public void setOrderType(byte orderType) {
        this.orderType = orderType;
    }



    public byte getIsPaid() {
        return isPaid;
    }



    public void setIsPaid(byte isPaid) {
        this.isPaid = isPaid;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }

}