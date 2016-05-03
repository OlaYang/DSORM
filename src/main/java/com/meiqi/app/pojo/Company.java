package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Company
 * @Description:合作公司
 * @author 杨永川
 * @date 2015年4月11日 下午4:04:47
 *
 */
public class Company {
    @ColumnKey(value = "id")
    private long   companyId;
    private long   regionId  = 0;
    private String companyCode;
    @ColumnKey(value = "name")
    private String companyName;
    private String companyDesc;
    private String companyAddress;
    private String companyLink;
    private String companyLogo;
    private int    addTime;
    private Region city;
    // 类型 1=公司 2=门店
    private int    type;
    @ColumnKey(value = "cityStr")
    private String cityStr;

    private int    pageIndex = 0;
    private int    pageSize  = 0;



    public Company() {
    }



    public Company(long companyId, long regionId, String companyCode, String companyName, String companyDesc,
            String companyAddress, String companyLink, String companyLogo, int addTime) {
        super();
        this.companyId = companyId;
        this.regionId = regionId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyDesc = companyDesc;
        this.companyAddress = companyAddress;
        this.companyLink = companyLink;
        this.companyLogo = companyLogo;
        this.addTime = addTime;
    }



    public long getCompanyId() {
        return companyId;
    }



    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }



    public long getRegionId() {
        return regionId;
    }



    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }



    public String getCompanyCode() {
        return companyCode;
    }



    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }



    public String getCompanyName() {
        return companyName;
    }



    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }



    public String getCompanyDesc() {
        return companyDesc;
    }



    public void setCompanyDesc(String companyDesc) {
        this.companyDesc = companyDesc;
    }



    public String getCompanyAddress() {
        return companyAddress;
    }



    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }



    public String getCompanyLink() {
        return companyLink;
    }



    public void setCompanyLink(String companyLink) {
        this.companyLink = companyLink;
    }



    public String getCompanyLogo() {
        return companyLogo;
    }



    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
    }



    public Region getCity() {
        return city;
    }



    public void setCity(Region city) {
        this.city = city;
    }



    public String getCityStr() {
        return cityStr;
    }



    public void setCityStr(String cityStr) {
        this.cityStr = cityStr;
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

}