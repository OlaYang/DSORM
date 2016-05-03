package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: GoodsAttr
 * @Description:商品属性
 * @author 杨永川
 * @date 2015年4月8日 下午5:56:01
 *
 */
public class GoodsAttr {
    private long      goodsAttrId;
    private long      goodsId   = 0;
    private Attribute attribute;
    private String    attrName;
    private String    attrValue;
    private String    attrPrice;
    private int       sortOrder = 0;



    public GoodsAttr() {
    }



    public GoodsAttr(String attrName, String attrValue) {
        super();
        this.attrName = attrName;
        this.attrValue = attrValue;
    }



    public GoodsAttr(long goodsAttrId, Attribute attribute, long goodsId, String attrName, String attrValue) {
        super();
        this.goodsAttrId = goodsAttrId;
        this.attribute = attribute;
        this.goodsId = goodsId;
        this.attrName = attrName;
        this.attrValue = attrValue;
    }



    public GoodsAttr(long goodsAttrId, long goodsId, Attribute attribute, String attrValue, String attrPrice) {
        super();
        this.goodsAttrId = goodsAttrId;
        this.goodsId = goodsId;
        this.attribute = attribute;
        this.attrValue = attrValue;
        this.attrPrice = attrPrice;
    }



    public long getGoodsAttrId() {
        return goodsAttrId;
    }



    public void setGoodsAttrId(long goodsAttrId) {
        this.goodsAttrId = goodsAttrId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public Attribute getAttribute() {
        return attribute;
    }



    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }



    public String getAttrValue() {
        return attrValue;
    }



    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }



    public String getAttrPrice() {
        return attrPrice;
    }



    public void setAttrPrice(String attrPrice) {
        this.attrPrice = attrPrice;
    }



    public String getAttrName() {
        return attrName;
    }



    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}