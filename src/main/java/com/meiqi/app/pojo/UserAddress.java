package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: UserAddress
 * @Description:用户收货信息表
 * @author 杨永川
 * @date 2015年4月24日 下午6:41:54
 *
 */
public class UserAddress {
    private long    consigneeId;
    private String  addressName  = "";
    private long    userId       = 0;
    private String  name         = "";
    private String  email        = "";
    private long    countryId    = 1;
    private long    provinceId   = 0;
    private long    cityId       = 0;
    private long    districtId   = 0;
    private long    extendId     = 0;
    private long    regionId;
    private String  detail       = "";
    private String  zipcode      = "";
    private String  tel          = "";
    private String  phone        = "";
    private String  signBuilding = "";
    private String  bestTime     = "";
    // RequestBody
    private Region  region;
    private boolean selected;



    public UserAddress() {
    }



    /**
     * 查询订单列表使用
     * 
     * @param consigneeId
     * @param name
     * @param phone
     */
    public UserAddress(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }



    public UserAddress(long consigneeId, String name, long provinceId, long cityId, long districtId, long extendId,
            String detail, String phone) {
        super();
        this.consigneeId = consigneeId;
        this.name = name;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.extendId = extendId;
        this.detail = detail;
        this.phone = phone;
    }



    public UserAddress(long consigneeId, String addressName, long userId, String name, String email, long countryId,
            long provinceId, long cityId, long districtId, long extendId, String detail, String zipcode, String tel,
            String phone, String signBuilding, String bestTime) {
        super();
        this.consigneeId = consigneeId;
        this.addressName = addressName;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.extendId = extendId;
        this.detail = detail;
        this.zipcode = zipcode;
        this.tel = tel;
        this.phone = phone;
        this.signBuilding = signBuilding;
        this.bestTime = bestTime;
    }



    public long getConsigneeId() {
        return consigneeId;
    }



    public void setConsigneeId(long consigneeId) {
        this.consigneeId = consigneeId;
    }



    public String getAddressName() {
        return addressName;
    }



    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public long getCountryId() {
        return countryId;
    }



    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }



    public long getProvinceId() {
        return provinceId;
    }



    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }



    public long getCityId() {
        return cityId;
    }



    public void setCityId(long cityId) {
        this.cityId = cityId;
    }



    public long getDistrictId() {
        return districtId;
    }



    public void setDistrictId(long districtId) {
        this.districtId = districtId;
    }



    public long getExtendId() {
        return extendId;
    }



    public void setExtendId(long extendId) {
        this.extendId = extendId;
    }



    public String getDetail() {
        return detail;
    }



    public void setDetail(String detail) {
        this.detail = detail;
    }



    public String getZipcode() {
        return zipcode;
    }



    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }



    public String getTel() {
        return tel;
    }



    public void setTel(String tel) {
        this.tel = tel;
    }



    public String getPhone() {
        return phone;
    }



    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getSignBuilding() {
        return signBuilding;
    }



    public void setSignBuilding(String signBuilding) {
        this.signBuilding = signBuilding;
    }



    public String getBestTime() {
        return bestTime;
    }



    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }



    public long getRegionId() {
        return regionId;
    }



    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }



    public Region getRegion() {
        return region;
    }



    public void setRegion(Region region) {
        this.region = region;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    /**
     * 
     * @Title: assembleUsesAddress
     * @Description:拼装usersAddress 将region信息设置到usersAddress
     * @param @param userAddress
     * @param @param region2
     * @return void
     * @throws
     */
    public void assembleUsesAddress(UserAddress userAddress, Region region) {
        if (null != region) {
            int regionType = region.getRegionType();
            long regionId = region.getRegionId();
            if (3 == regionType) {
                long cityId = region.getParentRegion().getRegionId();
                long provinceId = region.getParentRegion().getParentRegion().getRegionId();
                userAddress.setDistrictId(regionId);
                userAddress.setCityId(cityId);
                userAddress.setProvinceId(provinceId);
            } else if (4 == regionType) {
                long districtId = region.getParentRegion().getRegionId();
                long cityId = region.getParentRegion().getParentRegion().getRegionId();
                long provinceId = region.getParentRegion().getParentRegion().getParentRegion().getRegionId();
                userAddress.setExtendId(regionId);
                userAddress.setDistrictId(districtId);
                userAddress.setCityId(cityId);
                userAddress.setProvinceId(provinceId);
            }
        }
    }

}