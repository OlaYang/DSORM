package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: GoodsFilterItem
 * @Description:商品筛选条件类--条件子类GoodsFilterItem
 * @author 杨永川
 * @date 2015年4月23日 下午9:58:13
 *
 */
public class GoodsFilterItem {
    // if fiterType =5,fiterObejctId = attrId, objectId = goodsAttrId
    private long    objectId;
    private byte    filterType;
    private String  objectName;
    private String  imageURL;
    private double  maxValue;
    private double  minValue;
    private boolean selected;



    public GoodsFilterItem() {
        super();
    }



    public GoodsFilterItem(long objectId, byte filterType, String objectName, String imageURL, double maxValue,
            double minValue) {
        super();
        this.objectId = objectId;
        this.filterType = filterType;
        this.objectName = objectName;
        this.imageURL = imageURL;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }



    public GoodsFilterItem(long objectId, byte filterType, String objectName, String imageURL, double maxValue,
            double minValue, boolean selected) {
        super();
        this.objectId = objectId;
        this.filterType = filterType;
        this.objectName = objectName;
        this.imageURL = imageURL;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.selected = selected;
    }



    public long getObjectId() {
        return objectId;
    }



    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }



    public byte getFilterType() {
        return filterType;
    }



    public void setFilterType(byte filterType) {
        this.filterType = filterType;
    }



    public String getObjectName() {
        return objectName;
    }



    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }



    public String getImageURL() {
        return imageURL;
    }



    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    public double getMaxValue() {
        return maxValue;
    }



    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }



    public double getMinValue() {
        return minValue;
    }



    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
