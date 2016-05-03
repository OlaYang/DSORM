package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: payStatus
 * @Description:2.3.23 支付状态实体类
 * @author 杨永川
 * @date 2015年6月26日 下午4:00:06
 *
 */
public class PayStatus {

    // 0=成功， 1=失败，2=等待中
    private int    status;
    private String payDesc;
    private String payPrice;
    private long   orderId;



    public PayStatus() {
        super();
    }



    public PayStatus(int status, String payDesc, String payPrice, long orderId) {
        super();
        this.status = status;
        this.payDesc = payDesc;
        this.payPrice = payPrice;
        this.orderId = orderId;
    }



    public int getStatus() {
        return status;
    }



    public void setStatus(int status) {
        this.status = status;
    }



    public String getPayDesc() {
        return payDesc;
    }



    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }



    public String getPayPrice() {
        return payPrice;
    }



    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

}
