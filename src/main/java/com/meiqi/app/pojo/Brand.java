package com.meiqi.app.pojo;

public class Brand {
    private long   brandId;
    private String brandName;
    private String brandLogo;
    private String brandDesc;
    private String siteUrl;
    private long   sortOrder = 50;
    private byte   isShow    = 1;



    public Brand() {
    }



    public Brand(long brandId, String brandName, String brandLogo, String brandDesc, String siteUrl, long sortOrder,
            byte isShow) {
        super();
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandLogo = brandLogo;
        this.brandDesc = brandDesc;
        this.siteUrl = siteUrl;
        this.sortOrder = sortOrder;
        this.isShow = isShow;
    }



    public long getBrandId() {
        return brandId;
    }



    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }



    public String getBrandName() {
        return brandName;
    }



    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }



    public String getBrandLogo() {
        return brandLogo;
    }



    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }



    public String getBrandDesc() {
        return brandDesc;
    }



    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
    }



    public String getSiteUrl() {
        return siteUrl;
    }



    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }



    public long getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
    }



    public byte getIsShow() {
        return isShow;
    }



    public void setIsShow(byte isShow) {
        this.isShow = isShow;
    }

}