package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: DeliveryOrder
 * @Description:发货订单
 * @author 杨永川
 * @date 2015年5月7日 下午4:11:24
 *
 */
public class DeliveryOrder {
    private long                deliveryId;
    private String              deliverySn;
    private String              orderSn;
    private long                orderId     = 0;
    // 发货单号 发货时填写，可在订单查询查看
    private String              invoiceNo;
    private int                 addTime     = 0;
    private long                shippingId  = 0;
    private String              shippingName;
    private long                userId      = 0;
    private String              actionUser;
    private String              name;
    private String              detail;
    private long                countryId   = 0;
    private long                provinceId  = 0;
    private long                cityId      = 0;
    private long                districtId  = 0;
    private long                extendId    = 0;
    private String              signBuilding;
    private String              email;
    private String              zipcode;
    private String              tel;
    private String              phone;
    private String              bestTime;
    private String              postscript;
    private String              howOos;
    private double              insureFee   = 0.00;
    private double              shippingFee = 0.00;
    private int                 updateTime  = 0;
    // 发货单状态，0=配送中 1=配送完成(用户未确认收货 order shipping status=4) 2=生成发货单(没发货)
    private byte                status      = 1;
    private short               agencyId    = 0;

    private List<DeliveryGoods> carts;



    public DeliveryOrder() {
    }



    /**
     * 订单的运输方式
     * 
     * @param deliveryId
     * @param deliverySn
     * @param invoiceNo
     * @param status
     */
    public DeliveryOrder(long deliveryId, String deliverySn, String invoiceNo, byte status) {
        super();
        this.deliveryId = deliveryId;
        this.deliverySn = deliverySn;
        this.invoiceNo = invoiceNo;
        this.status = status;
    }



    public DeliveryOrder(long deliveryId, String deliverySn, String orderSn, long orderId, String invoiceNo,
            int addTime, long shippingId, String shippingName, long userId, String actionUser, String name,
            String detail, long countryId, long provinceId, long cityId, long districtId, long extendId,
            String signBuilding, String email, String zipcode, String tel, String phone, String bestTime,
            String postscript, String howOos, double insureFee, double shippingFee, int updateTime, byte status,
            short agencyId) {
        super();
        this.deliveryId = deliveryId;
        this.deliverySn = deliverySn;
        this.orderSn = orderSn;
        this.orderId = orderId;
        this.invoiceNo = invoiceNo;
        this.addTime = addTime;
        this.shippingId = shippingId;
        this.shippingName = shippingName;
        this.userId = userId;
        this.actionUser = actionUser;
        this.name = name;
        this.detail = detail;
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.extendId = extendId;
        this.signBuilding = signBuilding;
        this.email = email;
        this.zipcode = zipcode;
        this.tel = tel;
        this.phone = phone;
        this.bestTime = bestTime;
        this.postscript = postscript;
        this.howOos = howOos;
        this.insureFee = insureFee;
        this.shippingFee = shippingFee;
        this.updateTime = updateTime;
        this.status = status;
        this.agencyId = agencyId;
    }



    public DeliveryOrder(long deliveryId, String deliverySn, String orderSn, long orderId, String invoiceNo,
            int addTime, long shippingId, String shippingName, long userId, String actionUser, String name,
            String detail, long countryId, long provinceId, long cityId, long districtId, long extendId,
            String signBuilding, String email, String zipcode, String tel, String phone, String bestTime,
            String postscript, String howOos, double insureFee, double shippingFee, int updateTime, byte status,
            short agencyId, List<DeliveryGoods> carts) {
        super();
        this.deliveryId = deliveryId;
        this.deliverySn = deliverySn;
        this.orderSn = orderSn;
        this.orderId = orderId;
        this.invoiceNo = invoiceNo;
        this.addTime = addTime;
        this.shippingId = shippingId;
        this.shippingName = shippingName;
        this.userId = userId;
        this.actionUser = actionUser;
        this.name = name;
        this.detail = detail;
        this.countryId = countryId;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.extendId = extendId;
        this.signBuilding = signBuilding;
        this.email = email;
        this.zipcode = zipcode;
        this.tel = tel;
        this.phone = phone;
        this.bestTime = bestTime;
        this.postscript = postscript;
        this.howOos = howOos;
        this.insureFee = insureFee;
        this.shippingFee = shippingFee;
        this.updateTime = updateTime;
        this.status = status;
        this.agencyId = agencyId;
        this.carts = carts;
    }



    public long getDeliveryId() {
        return deliveryId;
    }



    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }



    public String getDeliverySn() {
        return deliverySn;
    }



    public void setDeliverySn(String deliverySn) {
        this.deliverySn = deliverySn;
    }



    public String getOrderSn() {
        return orderSn;
    }



    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public String getInvoiceNo() {
        return invoiceNo;
    }



    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



    public long getShippingId() {
        return shippingId;
    }



    public void setShippingId(long shippingId) {
        this.shippingId = shippingId;
    }



    public String getShippingName() {
        return shippingName;
    }



    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public String getActionUser() {
        return actionUser;
    }



    public void setActionUser(String actionUser) {
        this.actionUser = actionUser;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getDetail() {
        return detail;
    }



    public void setDetail(String detail) {
        this.detail = detail;
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



    public String getSignBuilding() {
        return signBuilding;
    }



    public void setSignBuilding(String signBuilding) {
        this.signBuilding = signBuilding;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
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



    public String getBestTime() {
        return bestTime;
    }



    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }



    public String getPostscript() {
        return postscript;
    }



    public void setPostscript(String postscript) {
        this.postscript = postscript;
    }



    public String getHowOos() {
        return howOos;
    }



    public void setHowOos(String howOos) {
        this.howOos = howOos;
    }



    public double getInsureFee() {
        return insureFee;
    }



    public void setInsureFee(double insureFee) {
        this.insureFee = insureFee;
    }



    public double getShippingFee() {
        return shippingFee;
    }



    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }



    public int getUpdateTime() {
        return updateTime;
    }



    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }



    public byte getStatus() {
        return status;
    }



    public void setStatus(byte status) {
        this.status = status;
    }



    public short getAgencyId() {
        return agencyId;
    }



    public void setAgencyId(short agencyId) {
        this.agencyId = agencyId;
    }



    public List<DeliveryGoods> getCarts() {
        return carts;
    }



    public void setCarts(List<DeliveryGoods> carts) {
        this.carts = carts;
    }

}
