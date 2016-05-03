package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: OrderInfo
 * @Description:订单详细信息
 * @author 杨永川
 * @date 2015年5月7日 下午4:04:50
 *
 */
public class Order {
    private long                  orderId;
    private String                orderSn           = "";
    private long                  userId            = 0;

    // 订单状态 0=未确认 1=确认 2，3=交易关闭 4=退货 5=已发货 7=确认收货交易成功
    private byte                  orderStatus       = 0;

    // 支付状态 0=未支付 1=部分付款 2=已支付 3=已退款
    private byte                  payStatus         = 0;
    // 配送状态 0=未配送 1=已发货 2=已收货 3=配货中 5=配送完成
    private byte                  shippingStatus    = 0;

    // 收货人姓名
    private String                name              = "";
    private Long                  countryId         = 0L;
    private Long                  provinceId        = 0L;
    private Long                  cityId            = 0L;
    private Long                  districtId        = 0L;
    private Long                  extendId          = 0L;
    // 详细地址
    private String                detail            = "";
    private String                zipcode           = "";
    private String                tel               = "";
    private String                phone             = "";
    private String                email             = "";
    private String                bestTime          = "";
    private String                signBuilding      = "";
    private String                postscript        = "";
    private long                  shippingId        = 0;
    private String                shippingName      = "";
    private long                  payId             = 0;
    private String                payName           = "";
    private String                howOos            = "";
    private String                howSurplus        = "";
    private String                packName          = "";
    private String                cardName          = "";
    private String                cardMessage       = "";
    private String                invPayee          = "";
    private String                invContent        = "";
    private double                insureFee         = 0.00;
    private double                payFee            = 0.00;
    private double                packFee           = 0.00;
    private double                cardFee           = 0.00;
    private double                moneyPaid         = 0.00;
    private double                surplus           = 0.00;
    private int                   integral          = 0;
    private double                integralMoney     = 0.00;
    private double                bonus             = 0.00;
    // 订单总额
    private Double                amount            = 0.00;
    private long                  fromAd            = 0;
    private String                referer           = "";
    private int                   addTime           = 0;
    private int                   confirmTime       = 0;
    private int                   payTime           = 0;
    private int                   shippingTime      = 0;
    private long                  packId            = 0;
    private long                  cardId            = 0;
    private int                   bonusId           = 0;
    private String                invoiceNo         = "";
    private String                extensionCode     = "";
    private int                   extensionId       = 0;
    private String                toBuyer           = "";
    private String                payNote           = "";
    private short                 agencyId          = 0;
    private String                invType           = "";
    private double                tax;
    // 0，未分成或等待分成；1，已分成；2，取消分成；
    private byte                  isSeparate        = 0;
    private int                   parentId          = 0;
    //private Double                discount          = 0.00;
    private Double                preferent         = 0.00;

    // 0表示未被删除,1表示被删除
    private byte                  isDel             = 0;

    // RequestBody 用于接收数据
    private UserAddress           consignee;
    // 订单的配送方式
    private List<DeliveryGoods>   carts;
    private List<TransportMethod> transportMethods;
    // 0=POS机刷卡,1 << 1,=支付宝支付,1 << 2=微信支付,1 << 1 | 1 << 2在线支付
    private int                   paymentMethodType;
    // 所有商品价格
    private double                goodsPrice        = 0.00;
    // 折扣
    private double                discountPrice     = 0.00;
    // 所有运输费用
    private double                transportPrice    = 0.00;
    private List<OrderGoods>      goodsGroup;
    // 下单时间 字符串
    private String                addTimeStr        = "";
    private List<DeliveryOrder>   deliverys;
    // 订单所有配送单的综合状态
    // 0=只有一个配送单 已发货,并配送中
    // 1=该订单将会有多个配送单，并还有未发货商品
    // 2=配送单都已发货,有配送单还在配送中
    // 3=所有配送单都发货 并都配送完成(无论一个配送还是多个)
    private int                   allDeliveryStatus = 0;
    private int                   deliverySize      = 1;
    // 订单商品数量
    private int                   goodsAmount       = 0;
    // 支付宝回调url
    private String                alipayNotifyURL;
    // 快钱宝回调url
    private String                billpayNotifyURL;
    //微信支付回调url
    private String                wechatNotifyURL;
    //微信支付封装好的json支付单
    private String  			  wechatPayJson;
    
    // 订单来源 , 0 PC端 1 M站扫码 2 IPAD 3 安卓APP 4 IOS APP
    private int orderSource = 0;
    
    // 订单折扣
    private OrderDiscount orderDiscount;
    
    // order_status_info（订单状态） 待付款  付款中  待发货  部分发货  已收货  已取消 待定 
    private String                orderStatusInfo;
    
    public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	private String 				  code;
    private String                discountCode;

    private int                   pageIndex         = 0;
    private int                   pageSize          = 0;
    // 商品数量
    private int                   goodsSize         = 0;
    // 下单时间
    private String                addDate;
    
    // 订单配送方式
    private String shippingStr;
    
    //红包折扣需求 支持 #9485邀约码、折扣码合并为U码，在确认订单页面生成红包序列需求
    private String getId;
    private String cellPhone;

    public String getGetId() {
        return getId;
    }



    public void setGetId(String getId) {
        this.getId = getId;
    }



    public String getCellPhone() {
        return cellPhone;
    }



    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public String getOrderSn() {
        return orderSn;
    }



    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public byte getOrderStatus() {
        return orderStatus;
    }



    public void setOrderStatus(byte orderStatus) {
        this.orderStatus = orderStatus;
    }



    public byte getShippingStatus() {
        return shippingStatus;
    }



    public void setShippingStatus(byte shippingStatus) {
        this.shippingStatus = shippingStatus;
    }



    public byte getPayStatus() {
        return payStatus;
    }



    public void setPayStatus(byte payStatus) {
        this.payStatus = payStatus;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public Long getCountryId() {
        return countryId;
    }



    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }



    public Long getProvinceId() {
        return provinceId;
    }



    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }



    public Long getCityId() {
        return cityId;
    }



    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }



    public Long getDistrictId() {
        return districtId;
    }



    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }



    public Long getExtendId() {
        return extendId;
    }



    public void setExtendId(Long extendId) {
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



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public String getBestTime() {
        return bestTime;
    }



    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }



    public String getSignBuilding() {
        return signBuilding;
    }



    public void setSignBuilding(String signBuilding) {
        this.signBuilding = signBuilding;
    }



    public String getPostscript() {
        return postscript;
    }



    public void setPostscript(String postscript) {
        this.postscript = postscript;
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



    public long getPayId() {
        return payId;
    }



    public void setPayId(long payId) {
        this.payId = payId;
    }



    public String getPayName() {
        return payName;
    }



    public void setPayName(String payName) {
        this.payName = payName;
    }



    public String getHowOos() {
        return howOos;
    }



    public void setHowOos(String howOos) {
        this.howOos = howOos;
    }



    public String getHowSurplus() {
        return howSurplus;
    }



    public void setHowSurplus(String howSurplus) {
        this.howSurplus = howSurplus;
    }



    public String getPackName() {
        return packName;
    }



    public void setPackName(String packName) {
        this.packName = packName;
    }



    public String getCardName() {
        return cardName;
    }



    public void setCardName(String cardName) {
        this.cardName = cardName;
    }



    public String getCardMessage() {
        return cardMessage;
    }



    public void setCardMessage(String cardMessage) {
        this.cardMessage = cardMessage;
    }



    public String getInvPayee() {
        return invPayee;
    }



    public void setInvPayee(String invPayee) {
        this.invPayee = invPayee;
    }



    public String getInvContent() {
        return invContent;
    }



    public void setInvContent(String invContent) {
        this.invContent = invContent;
    }



    public double getInsureFee() {
        return insureFee;
    }



    public void setInsureFee(double insureFee) {
        this.insureFee = insureFee;
    }



    public double getPayFee() {
        return payFee;
    }



    public void setPayFee(double payFee) {
        this.payFee = payFee;
    }



    public double getPackFee() {
        return packFee;
    }



    public void setPackFee(double packFee) {
        this.packFee = packFee;
    }



    public double getCardFee() {
        return cardFee;
    }



    public void setCardFee(double cardFee) {
        this.cardFee = cardFee;
    }



    public double getMoneyPaid() {
        return moneyPaid;
    }



    public void setMoneyPaid(double moneyPaid) {
        this.moneyPaid = moneyPaid;
    }



    public double getSurplus() {
        return surplus;
    }



    public void setSurplus(double surplus) {
        this.surplus = surplus;
    }



    public int getIntegral() {
        return integral;
    }



    public void setIntegral(int integral) {
        this.integral = integral;
    }



    public double getIntegralMoney() {
        return integralMoney;
    }



    public void setIntegralMoney(double integralMoney) {
        this.integralMoney = integralMoney;
    }



    public double getBonus() {
        return bonus;
    }



    public void setBonus(double bonus) {
        this.bonus = bonus;
    }



    public long getFromAd() {
        return fromAd;
    }



    public void setFromAd(long fromAd) {
        this.fromAd = fromAd;
    }



    public String getReferer() {
        return referer;
    }



    public void setReferer(String referer) {
        this.referer = referer;
    }



    public int getAddTime() {
        return addTime;
    }



    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



    public int getConfirmTime() {
        return confirmTime;
    }



    public void setConfirmTime(int confirmTime) {
        this.confirmTime = confirmTime;
    }



    public int getPayTime() {
        return payTime;
    }



    public void setPayTime(int payTime) {
        this.payTime = payTime;
    }



    public int getShippingTime() {
        return shippingTime;
    }



    public void setShippingTime(int shippingTime) {
        this.shippingTime = shippingTime;
    }



    public long getPackId() {
        return packId;
    }



    public void setPackId(long packId) {
        this.packId = packId;
    }



    public long getCardId() {
        return cardId;
    }



    public void setCardId(long cardId) {
        this.cardId = cardId;
    }



    public int getBonusId() {
        return bonusId;
    }



    public void setBonusId(int bonusId) {
        this.bonusId = bonusId;
    }



    public String getInvoiceNo() {
        return invoiceNo;
    }



    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }



    public String getExtensionCode() {
        return extensionCode;
    }



    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }



    public int getExtensionId() {
        return extensionId;
    }



    public void setExtensionId(int extensionId) {
        this.extensionId = extensionId;
    }



    public String getToBuyer() {
        return toBuyer;
    }



    public void setToBuyer(String toBuyer) {
        this.toBuyer = toBuyer;
    }



    public String getPayNote() {
        return payNote;
    }



    public void setPayNote(String payNote) {
        this.payNote = payNote;
    }



    public short getAgencyId() {
        return agencyId;
    }



    public void setAgencyId(short agencyId) {
        this.agencyId = agencyId;
    }



    public String getInvType() {
        return invType;
    }



    public void setInvType(String invType) {
        this.invType = invType;
    }



    public double getTax() {
        return tax;
    }



    public void setTax(double tax) {
        this.tax = tax;
    }



    public byte getIsSeparate() {
        return isSeparate;
    }



    public void setIsSeparate(byte isSeparate) {
        this.isSeparate = isSeparate;
    }



    public int getParentId() {
        return parentId;
    }



    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    public Double getPreferent() {
        return preferent;
    }



    public void setPreferent(Double preferent) {
        this.preferent = preferent;
    }



    public UserAddress getConsignee() {
        return consignee;
    }



    public void setConsignee(UserAddress consignee) {
        this.consignee = consignee;
    }



    public List<DeliveryGoods> getCarts() {
        return carts;
    }



    public void setCarts(List<DeliveryGoods> carts) {
        this.carts = carts;
    }



    public List<TransportMethod> getTransportMethods() {
        return transportMethods;
    }



    public void setTransportMethods(List<TransportMethod> transportMethods) {
        this.transportMethods = transportMethods;
    }



    public int getPaymentMethodType() {
        return paymentMethodType;
    }



    public void setPaymentMethodType(int paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }



    public double getGoodsPrice() {
        return goodsPrice;
    }



    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }



    public double getTransportPrice() {
        return transportPrice;
    }



    public void setTransportPrice(double transportPrice) {
        this.transportPrice = transportPrice;
    }



    public Double getAmount() {
        return amount;
    }



    public void setAmount(Double amount) {
        this.amount = amount;
    }



    public List<OrderGoods> getGoodsGroup() {
        return goodsGroup;
    }



    public void setGoodsGroup(List<OrderGoods> goodsGroup) {
        this.goodsGroup = goodsGroup;
    }



    public double getDiscountPrice() {
        return discountPrice;
    }



    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }



    public String getAddTimeStr() {
        return addTimeStr;
    }



    public void setAddTimeStr(String addTimeStr) {
        this.addTimeStr = addTimeStr;
    }



    public int getAllDeliveryStatus() {
        return allDeliveryStatus;
    }



    public void setAllDeliveryStatus(int allDeliveryStatus) {
        this.allDeliveryStatus = allDeliveryStatus;
    }



    public int getDeliverySize() {
        return deliverySize;
    }



    public void setDeliverySize(int deliverySize) {
        this.deliverySize = deliverySize;
    }



    public List<DeliveryOrder> getDeliverys() {
        return deliverys;
    }



    public void setDeliverys(List<DeliveryOrder> deliverys) {
        this.deliverys = deliverys;
    }



    public byte getIsDel() {
        return isDel;
    }



    public void setIsDel(byte isDel) {
        this.isDel = isDel;
    }



    public int getGoodsAmount() {
        return goodsAmount;
    }



    public void setGoodsAmount(int goodsAmount) {
        this.goodsAmount = goodsAmount;
    }



    public String getAlipayNotifyURL() {
        return alipayNotifyURL;
    }



    public void setAlipayNotifyURL(String alipayNotifyURL) {
        this.alipayNotifyURL = alipayNotifyURL;
    }



    public String getBillpayNotifyURL() {
        return billpayNotifyURL;
    }



    public void setBillpayNotifyURL(String billpayNotifyURL) {
        this.billpayNotifyURL = billpayNotifyURL;
    }



    public String getDiscountCode() {
        return discountCode;
    }



    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
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



    public int getGoodsSize() {
        return goodsSize;
    }



    public void setGoodsSize(int goodsSize) {
        this.goodsSize = goodsSize;
    }



    public String getAddDate() {
        return addDate;
    }



    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }



	public String getWechatNotifyURL() {
		return wechatNotifyURL;
	}

	
	public String getWechatPayJson() {
		return wechatPayJson;
	}



	public void setWechatPayJson(String wechatPayJson) {
		this.wechatPayJson = wechatPayJson;
	}



	public void setWechatNotifyURL(String wechatNotifyURL) {
		this.wechatNotifyURL = wechatNotifyURL;
	}



    public String getShippingStr() {
        return shippingStr;
    }



    public void setShippingStr(String shippingStr) {
        this.shippingStr = shippingStr;
    }



    public int getOrderSource() {
        return orderSource;
    }



    public void setOrderSource(int orderSource) {
        this.orderSource = orderSource;
    }



    public OrderDiscount getOrderDiscount() {
        return orderDiscount;
    }



    public void setOrderDiscount(OrderDiscount orderDiscount) {
        this.orderDiscount = orderDiscount;
    }



    public String getOrderStatusInfo() {
        return orderStatusInfo;
    }



    public void setOrderStatusInfo(String orderStatusInfo) {
        this.orderStatusInfo = orderStatusInfo;
    }

}
