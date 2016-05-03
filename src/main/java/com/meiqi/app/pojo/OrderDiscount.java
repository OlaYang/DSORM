/*
 * File name: OrderDiscount.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年10月29日 ...
 * ... ...
 * 
 * *************************************************
 */

package com.meiqi.app.pojo;

/**
 * 订单折扣
 *
 * @author: luzicong
 * @version: 1.0, 2015年10月29日
 */

public class OrderDiscount {

    private long   orderId;      // 订单id
    private int    bonusId;      // 订单活动id
    private double discount;     // 折扣码折扣
    private double orderDiscount; // 整单折扣
    private double bonusDiscount; // 红包折扣



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public int getBonusId() {
        return bonusId;
    }



    public void setBonusId(int bonusId) {
        this.bonusId = bonusId;
    }



    public double getDiscount() {
        return discount;
    }



    public void setDiscount(double discount) {
        this.discount = discount;
    }



    public double getOrderDiscount() {
        return orderDiscount;
    }



    public void setOrderDiscount(double orderDiscount) {
        this.orderDiscount = orderDiscount;
    }



    public double getBonusDiscount() {
        return bonusDiscount;
    }



    public void setBonusDiscount(double bonusDiscount) {
        this.bonusDiscount = bonusDiscount;
    }

}
