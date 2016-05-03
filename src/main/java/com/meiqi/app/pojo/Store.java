package com.meiqi.app.pojo;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * @ClassName: Store
 * @Description:实体店
 * @author 杨永川
 * @date 2015年3月26日 下午2:16:13
 *
 */
public class Store {
    @ColumnKey(value = "shop_id")
    private long   storeId;
    @ColumnKey(value = "shop_name")
    private String storeName;
    // 体验店logo
    private String logo;

    private Region region;
    @ColumnKey(value = "shop_address")
    private String storeDetail;
    @ColumnKey(value = "tel_num")
    private String tel;
    @ColumnKey(value = "phone")
    private String phone;
    @ColumnKey(value = "experience")
    private String experience;   // 是否可以到店体验1：能，0：否。
    @ColumnKey(value = "latitude")
    private String lat;          // 纬度
    @ColumnKey(value = "longitude")
    private String lng;          // 经度
    private String addressDetail;
    // 备注“xx”负责发货并提供售后服务，“xx”读取店铺名
    private String remark;



    public Store(long storeId, String storeName, String logo, Region region, String storeDetail, String tel,
            String phone, String experience, String lat, String lng, String addressDetail, String remark) {
        super();
        this.storeId = storeId;
        this.storeName = storeName;
        this.logo = logo;
        this.region = region;
        this.storeDetail = storeDetail;
        this.tel = tel;
        this.phone = phone;
        this.experience = experience;
        this.lat = lat;
        this.lng = lng;
        this.addressDetail = addressDetail;
        this.remark = remark;
    }



    public Store() {
    }



    public Store(long storeId, String storeName, Region region, String storeDetail, String tel, String phone,
            String experience, String lat, String lng, String addressDetail) {
        super();
        this.storeId = storeId;
        this.storeName = storeName;
        this.region = region;
        this.storeDetail = storeDetail;
        this.tel = tel;
        this.phone = phone;
        this.experience = experience;
        this.lat = lat;
        this.lng = lng;
        this.addressDetail = addressDetail;
    }



    public long getStoreId() {
        return storeId;
    }



    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }



    public String getStoreName() {
        return storeName;
    }



    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }



    public Region getRegion() {
        return region;
    }



    public void setRegion(Region region) {
        this.region = region;
    }



    public String getStoreDetail() {
        return storeDetail;
    }



    public void setStoreDetail(String storeDetail) {
        this.storeDetail = storeDetail;
    }



    public String getPhone() {
        return phone;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getTel() {
        return tel;
    }



    public void setTel(String tel) {
        this.tel = tel;
    }



    public String getExperience() {
        return experience;
    }



    public void setExperience(String experience) {
        this.experience = experience;
    }



    public String getLat() {
        return lat;
    }



    public void setLat(String lat) {
        this.lat = lat;
    }



    public String getLng() {
        return lng;
    }



    public void setLng(String lng) {
        this.lng = lng;
    }



    public String getAddressDetail() {
        return addressDetail;
    }



    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }



    public String getLogo() {
        return logo;
    }



    public void setLogo(String logo) {
        this.logo = logo;
    }

}