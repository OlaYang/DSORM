package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @Description:订单折扣码
 * 
 * @author: luzicong
 * 
 * @time:2015年7月2日 下午6:54:21
 */
public class DiscountInfo {
    @ColumnKey(value = "discount_id")
    private long   discountId;
    @ColumnKey(value = "activity_id")
    private long   activityId;
    @ColumnKey(value = "discount_code")
    private String discountCode;
    @ColumnKey(value = "add_time")
    private long   addTime;
    @ColumnKey(value = "send_user_id")
    private long   sendUserId;
    @ColumnKey(value = "send_time")
    private long   time;
    private String sendTime;
    @ColumnKey(value = "relate_phone")
    private String receivePhone;
    @ColumnKey(value = "relate_order")
    private String relateOrder;
    @ColumnKey(value = "status")
    private int    status    = 0; // 折扣码状态 0未使用,1使用
    @ColumnKey(value = "")
    private String explains;
    // 临时属性
    // 商品总额
    private double goodsPrice;
    // 折扣码
    private String code;
    // 折扣金额
    private double discountPrice;
    // 折扣信息
    private String discountInfo;
    // 1-折扣码错误 2—折扣码正确，金额不满足 3-折扣码正确，金额满足
    private int    isFlag;

    private int    pageIndex = 0;
    private int    pageSize  = 0;



    public DiscountInfo() {
        super();
    }



    public long getDiscountId() {
        return discountId;
    }



    public void setDiscountId(long discountId) {
        this.discountId = discountId;
    }



    public long getActivityId() {
        return activityId;
    }



    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }



    public String getDiscountCode() {
        return discountCode;
    }



    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }



    public long getAddTime() {
        return addTime;
    }



    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }



    public long getSendUserId() {
        return sendUserId;
    }



    public void setSendUserId(long sendUserId) {
        this.sendUserId = sendUserId;
    }



    public long getTime() {
        return time;
    }



    public void setTime(long time) {
        this.time = time;
    }



    public String getSendTime() {
        return sendTime;
    }



    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }



    public String getReceivePhone() {
        return receivePhone;
    }



    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }



    public String getRelateOrder() {
        return relateOrder;
    }



    public void setRelateOrder(String relateOrder) {
        this.relateOrder = relateOrder;
    }



    public int getStatus() {
        return status;
    }



    public void setStatus(int status) {
        this.status = status;
    }



    public String getExplains() {
        return explains;
    }



    public void setExplains(String explains) {
        this.explains = explains;
    }



    public double getDiscountPrice() {
        return discountPrice;
    }



    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }



    public double getGoodsPrice() {
        return goodsPrice;
    }



    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }



    public String getDiscountInfo() {
        return discountInfo;
    }



    public void setDiscountInfo(String discountInfo) {
        this.discountInfo = discountInfo;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }



    public int getIsFlag() {
        return isFlag;
    }



    public void setIsFlag(int isFlag) {
        this.isFlag = isFlag;
    }

}
