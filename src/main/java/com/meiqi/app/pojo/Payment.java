package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Payment
 * @Description:支付方式配置信息
 * @author 杨永川
 * @date 2015年5月7日 下午5:36:15
 *
 */
public class Payment {
    private long   payId;
    private String payCode;
    private String payName;
    private String payFee   = "0";
    private String payDesc;
    private long   payOrder = 0;
    private String payConfig;
    private byte   enabled  = 0;
    // 是否货到付款，0，否；1，是
    private byte   isCod    = 0;
    // 是否在线支付，0，否；1，是
    private byte   isOnline = 0;



    public Payment() {
    }



    public Payment(long payId, String payCode, String payName, String payFee, String payDesc, long payOrder,
            String payConfig, byte enabled, byte isCod, byte isOnline) {
        super();
        this.payId = payId;
        this.payCode = payCode;
        this.payName = payName;
        this.payFee = payFee;
        this.payDesc = payDesc;
        this.payOrder = payOrder;
        this.payConfig = payConfig;
        this.enabled = enabled;
        this.isCod = isCod;
        this.isOnline = isOnline;
    }



    public long getPayId() {
        return payId;
    }



    public void setPayId(long payId) {
        this.payId = payId;
    }



    public String getPayCode() {
        return payCode;
    }



    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }



    public String getPayName() {
        return payName;
    }



    public void setPayName(String payName) {
        this.payName = payName;
    }



    public String getPayFee() {
        return payFee;
    }



    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }



    public String getPayDesc() {
        return payDesc;
    }



    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }



    public long getPayOrder() {
        return payOrder;
    }



    public void setPayOrder(long payOrder) {
        this.payOrder = payOrder;
    }



    public String getPayConfig() {
        return payConfig;
    }



    public void setPayConfig(String payConfig) {
        this.payConfig = payConfig;
    }



    public byte getEnabled() {
        return enabled;
    }



    public void setEnabled(byte enabled) {
        this.enabled = enabled;
    }



    public byte getIsCod() {
        return isCod;
    }



    public void setIsCod(byte isCod) {
        this.isCod = isCod;
    }



    public byte getIsOnline() {
        return isOnline;
    }



    public void setIsOnline(byte isOnline) {
        this.isOnline = isOnline;
    }

}