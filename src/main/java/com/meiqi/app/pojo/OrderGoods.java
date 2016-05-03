package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: OrderGoods
 * @Description:订单商品(订单ID,商品ID,商品名称,商品编号,商品数量,市场价,订单价,商品属性,配送数量,是否真实商品,扩展代码,父类ID,是否赠品)
 * @author 杨永川
 * @date 2015年4月17日 下午7:59:02
 *
 */
public class OrderGoods {
    private long   orderGoodsId;
    private long   orderId       = 0;
    private long   goodsId;
    private String name;
    private String goodsSn;
    private long   productId     = 0;
    private int    goodsNumber   = 1;
    private double originalPrice = 0.00;
    private double price         = 0.00;
    private String goodsAttr;
    // 当不是实物时，是否已发货，0，否；1，是
    private int    isSend        = 0;
    // 是否是实物，0，否；1，是；取值ecs_goods
    private byte   isReal        = 1;
    private String extensionCode;
    private int    parentId      = 0;
    private int    isGift        = 0;
    private String goodsAttrId;
    // 配送方式名称
    private String shippingName;

    // 商品 封面
    private String cover;



    public OrderGoods() {
    }



    /**
     * order list
     * 
     * @param goodsId
     * @param goodsName
     * @param goodsNumber
     * @param goodsPrice
     * @param goodsAttr
     */
    public OrderGoods(long goodsId, String name, int goodsNumber, double price, String goodsAttr) {
        this.goodsId = goodsId;
        this.name = name;
        this.goodsNumber = goodsNumber;
        this.price = price;
        this.goodsAttr = goodsAttr;
    }



    public OrderGoods(long orderGoodsId, long orderId, long goodsId, String name, String goodsSn, long productId,
            int goodsNumber, double originalPrice, double price, String goodsAttr, int isSend, byte isReal,
            String extensionCode, int parentId, int isGift, String goodsAttrId) {
        super();
        this.orderGoodsId = orderGoodsId;
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.name = name;
        this.goodsSn = goodsSn;
        this.productId = productId;
        this.goodsNumber = goodsNumber;
        this.originalPrice = originalPrice;
        this.price = price;
        this.goodsAttr = goodsAttr;
        this.isSend = isSend;
        this.isReal = isReal;
        this.extensionCode = extensionCode;
        this.parentId = parentId;
        this.isGift = isGift;
        this.goodsAttrId = goodsAttrId;
    }



    public long getOrderGoodsId() {
        return orderGoodsId;
    }



    public void setOrderGoodsId(long orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }



    public long getOrderId() {
        return orderId;
    }



    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getGoodsSn() {
        return goodsSn;
    }



    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }



    public long getProductId() {
        return productId;
    }



    public void setProductId(long productId) {
        this.productId = productId;
    }



    public int getGoodsNumber() {
        return goodsNumber;
    }



    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }



    public double getOriginalPrice() {
        return originalPrice;
    }



    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }



    public double getPrice() {
        return price;
    }



    public void setPrice(double price) {
        this.price = price;
    }



    public String getGoodsAttr() {
        return goodsAttr;
    }



    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }



    public byte getIsReal() {
        return isReal;
    }



    public void setIsReal(byte isReal) {
        this.isReal = isReal;
    }



    public String getExtensionCode() {
        return extensionCode;
    }



    public void setExtensionCode(String extensionCode) {
        this.extensionCode = extensionCode;
    }



    public int getParentId() {
        return parentId;
    }



    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    public int getIsGift() {
        return isGift;
    }



    public void setIsGift(int isGift) {
        this.isGift = isGift;
    }



    public String getGoodsAttrId() {
        return goodsAttrId;
    }



    public void setGoodsAttrId(String goodsAttrId) {
        this.goodsAttrId = goodsAttrId;
    }



    public int getIsSend() {
        return isSend;
    }



    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }



    public String getCover() {
        return cover;
    }



    public void setCover(String cover) {
        this.cover = cover;
    }



    public String getShippingName() {
        return shippingName;
    }



    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }



    @Override
    public int hashCode() {
        return 0;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderGoods other = (OrderGoods) obj;
        if (goodsId != other.goodsId)
            return false;
        return true;
    }

}