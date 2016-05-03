package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Shipping
 * @Description:配送方式
 * @author 杨永川
 * @date 2015年5月7日 下午5:28:31
 *
 */
public class Shipping {
    private long   shippingId;
    private String shippingCode;
    private String shippingName;
    private String shippingDesc;
    // 保价费用，单位元，或者是百分数，该值直接输出为报价费用
    private String insure        = "0";
    // 是否支持货到付款，1，支持；0，不支持
    private byte   supportCod    = 0;
    // 该配送方式是否被禁用，1，可用；0，禁用
    private byte   enabled       = 0;
    private String shippingPrint;
    private String printBg;
    private String configLable;
    private long   printModel    = 0;
    private long   shippingOrder = 0;



    public Shipping() {
    }



    public Shipping(long shippingId, String shippingCode, String shippingName, String shippingDesc, String insure,
            byte supportCod, byte enabled, String shippingPrint, String printBg, String configLable, long printModel,
            long shippingOrder) {
        super();
        this.shippingId = shippingId;
        this.shippingCode = shippingCode;
        this.shippingName = shippingName;
        this.shippingDesc = shippingDesc;
        this.insure = insure;
        this.supportCod = supportCod;
        this.enabled = enabled;
        this.shippingPrint = shippingPrint;
        this.printBg = printBg;
        this.configLable = configLable;
        this.printModel = printModel;
        this.shippingOrder = shippingOrder;
    }



    public long getShippingId() {
        return shippingId;
    }



    public void setShippingId(long shippingId) {
        this.shippingId = shippingId;
    }



    public String getShippingCode() {
        return shippingCode;
    }



    public void setShippingCode(String shippingCode) {
        this.shippingCode = shippingCode;
    }



    public String getShippingName() {
        return shippingName;
    }



    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }



    public String getShippingDesc() {
        return shippingDesc;
    }



    public void setShippingDesc(String shippingDesc) {
        this.shippingDesc = shippingDesc;
    }



    public String getInsure() {
        return insure;
    }



    public void setInsure(String insure) {
        this.insure = insure;
    }



    public byte getSupportCod() {
        return supportCod;
    }



    public void setSupportCod(byte supportCod) {
        this.supportCod = supportCod;
    }



    public byte getEnabled() {
        return enabled;
    }



    public void setEnabled(byte enabled) {
        this.enabled = enabled;
    }



    public String getShippingPrint() {
        return shippingPrint;
    }



    public void setShippingPrint(String shippingPrint) {
        this.shippingPrint = shippingPrint;
    }



    public String getPrintBg() {
        return printBg;
    }



    public void setPrintBg(String printBg) {
        this.printBg = printBg;
    }



    public String getConfigLable() {
        return configLable;
    }



    public void setConfigLable(String configLable) {
        this.configLable = configLable;
    }



    public long getPrintModel() {
        return printModel;
    }



    public void setPrintModel(long printModel) {
        this.printModel = printModel;
    }



    public long getShippingOrder() {
        return shippingOrder;
    }



    public void setShippingOrder(long shippingOrder) {
        this.shippingOrder = shippingOrder;
    }

}