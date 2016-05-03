package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Attribute
 * @Description:商品属性
 * @author 杨永川
 * @date 2015年4月8日 下午5:50:53
 *
 */
public class Attribute {
    private long   attrId;
    private long   catId         = 0;
    private String attrName;
    private byte   attrInputType = 1;
    private byte   attrType      = 1;
    private String attrValues;
    private byte   attrIndex     = 0;
    private int    sortOrder     = 0;
    private byte   isLinked      = 0;
    private long   attrGroup     = 0;
    private byte   isSell        = 0;



    public Attribute() {
    }



    public Attribute(long attrId, long catId, String attrName, byte attrInputType, byte attrType, String attrValues,
            byte attrIndex, int sortOrder, byte isLinked, long attrGroup) {
        super();
        this.attrId = attrId;
        this.catId = catId;
        this.attrName = attrName;
        this.attrInputType = attrInputType;
        this.attrType = attrType;
        this.attrValues = attrValues;
        this.attrIndex = attrIndex;
        this.sortOrder = sortOrder;
        this.isLinked = isLinked;
        this.attrGroup = attrGroup;
    }



    public long getAttrId() {
        return attrId;
    }



    public void setAttrId(long attrId) {
        this.attrId = attrId;
    }



    public long getCatId() {
        return catId;
    }



    public void setCatId(long catId) {
        this.catId = catId;
    }



    public String getAttrName() {
        return attrName;
    }



    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }



    public byte getAttrInputType() {
        return attrInputType;
    }



    public void setAttrInputType(byte attrInputType) {
        this.attrInputType = attrInputType;
    }



    public byte getAttrType() {
        return attrType;
    }



    public void setAttrType(byte attrType) {
        this.attrType = attrType;
    }



    public String getAttrValues() {
        return attrValues;
    }



    public void setAttrValues(String attrValues) {
        this.attrValues = attrValues;
    }



    public byte getAttrIndex() {
        return attrIndex;
    }



    public void setAttrIndex(byte attrIndex) {
        this.attrIndex = attrIndex;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }



    public byte getIsLinked() {
        return isLinked;
    }



    public void setIsLinked(byte isLinked) {
        this.isLinked = isLinked;
    }



    public long getAttrGroup() {
        return attrGroup;
    }



    public void setAttrGroup(long attrGroup) {
        this.attrGroup = attrGroup;
    }



    public byte getIsSell() {
        return isSell;
    }



    public void setIsSell(byte isSell) {
        this.isSell = isSell;
    }

}