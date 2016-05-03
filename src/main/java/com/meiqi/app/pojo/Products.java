package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Products
 * @Description:货品列表（当一种商品的某种属性不唯一时添加至此表，如一款手机的颜色==sku
 * @author 杨永川
 * @date 2015年4月13日 上午11:15:29
 *
 */
public class Products {
    private long   productId;
    // 同组goods id
    private long   groupId       = 0;
    private long   goodsId       = 0;
    private String goodsAttr;
    private String productSn;
    private short  productNumber = 0;
    // 与goodsAttr 使用'\r\n'隔开
    private String goodsAttrValue;
    //用户同组goods 排序
    private int    sortOrder     = 0;



    public Products() {
    }



    public Products(long goodsId) {
        this.goodsId = goodsId;
    }



    public Products(long productId, long goodsId, String goodsAttr, String goodsAttrValue) {
        super();
        this.productId = productId;
        this.goodsId = goodsId;
        this.goodsAttr = goodsAttr;
        this.goodsAttrValue = goodsAttrValue;
    }



    public Products(long productId, String goodsAttr, String goodsAttrValue) {
        super();
        this.productId = productId;
        this.goodsAttr = goodsAttr;
        this.goodsAttrValue = goodsAttrValue;
    }



    public Products(long productId, long groupId, long goodsId, String goodsAttr, String productSn,
            short productNumber, String goodsAttrValue, int sortOrder) {
        super();
        this.productId = productId;
        this.groupId = groupId;
        this.goodsId = goodsId;
        this.goodsAttr = goodsAttr;
        this.productSn = productSn;
        this.productNumber = productNumber;
        this.goodsAttrValue = goodsAttrValue;
        this.sortOrder = sortOrder;
    }



    public long getProductId() {
        return productId;
    }



    public void setProductId(long productId) {
        this.productId = productId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public String getGoodsAttr() {
        return goodsAttr;
    }



    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }



    public String getProductSn() {
        return productSn;
    }



    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }



    public short getProductNumber() {
        return productNumber;
    }



    public void setProductNumber(short productNumber) {
        this.productNumber = productNumber;
    }



    public String getGoodsAttrValue() {
        return goodsAttrValue;
    }



    public void setGoodsAttrValue(String goodsAttrValue) {
        this.goodsAttrValue = goodsAttrValue;
    }



    public long getGroupId() {
        return groupId;
    }



    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}