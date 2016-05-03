package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: DeliveryGoods
 * @Description:发货清单商品
 * @author 杨永川
 * @date 2015年5月7日 下午4:16:06
 *
 */
public class DeliveryGoods {
    private long    deliveryGoodsId;
    private long    deliveryId  = 0;

    private Goods   goods;
    private long    productId   = 0;
    private String  productSn;
    // 商品名
    private String  name;
    private String  brandName;
    private String  goodsSn;
    private byte    isReal      = 0;
    private String  extensionCode;
    private int     parentId    = 0;
    // 发货数量
    private int     goodsAmount = 0;
    private String  goodsAttr;
    // RequestBody
    private long    goodsId     = 0;
    private boolean editing;
    private double  amount;
    private boolean selected;
    private boolean invalid;
    private long    cartId;



    public DeliveryGoods() {
    }



    /**
     * 获取一个配送单 配送的商品
     * 
     * @param goods
     * @param goodsAmount
     * @param goodsAttr
     */
    public DeliveryGoods(Goods goods, int goodsAmount, String goodsAttr) {
        super();
        this.goods = goods;
        this.goodsAmount = goodsAmount;
        this.goodsAttr = goodsAttr;
    }



    public DeliveryGoods(long deliveryGoodsId, long deliveryId, Goods goods, long productId, String productSn,
            String name, String brandName, String goodsSn, byte isReal, String extensionCode, int parentId,
            int goodsAmount, String goodsAttr) {
        super();
        this.deliveryGoodsId = deliveryGoodsId;
        this.deliveryId = deliveryId;
        this.goods = goods;
        this.productId = productId;
        this.productSn = productSn;
        this.name = name;
        this.brandName = brandName;
        this.goodsSn = goodsSn;
        this.isReal = isReal;
        this.extensionCode = extensionCode;
        this.parentId = parentId;
        this.goodsAmount = goodsAmount;
        this.goodsAttr = goodsAttr;
    }



    public long getDeliveryGoodsId() {
        return deliveryGoodsId;
    }



    public void setDeliveryGoodsId(long deliveryGoodsId) {
        this.deliveryGoodsId = deliveryGoodsId;
    }



    public long getDeliveryId() {
        return deliveryId;
    }



    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public long getProductId() {
        return productId;
    }



    public void setProductId(long productId) {
        this.productId = productId;
    }



    public String getProductSn() {
        return productSn;
    }



    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getBrandName() {
        return brandName;
    }



    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }



    public String getGoodsSn() {
        return goodsSn;
    }



    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
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



    public int getGoodsAmount() {
        return goodsAmount;
    }



    public void setGoodsAmount(int goodsAmount) {
        this.goodsAmount = goodsAmount;
    }



    public String getGoodsAttr() {
        return goodsAttr;
    }



    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }



    public Goods getGoods() {
        return goods;
    }



    public void setGoods(Goods goods) {
        this.goods = goods;
    }



    public boolean isEditing() {
        return editing;
    }



    public void setEditing(boolean editing) {
        this.editing = editing;
    }



    public double getAmount() {
        return amount;
    }



    public void setAmount(double amount) {
        this.amount = amount;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    public boolean isInvalid() {
        return invalid;
    }



    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }



    public long getCartId() {
        return cartId;
    }



    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

}