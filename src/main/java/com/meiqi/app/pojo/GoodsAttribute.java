package com.meiqi.app.pojo;

import com.meiqi.app.common.utils.StringUtils;

/**
 * 
 * @ClassName: GoodsStandard
 * @Description:商品规格 临时实体类
 * @author 杨永川
 * @date 2015年4月20日 下午2:26:50
 *
 */
public class GoodsAttribute {
    private long    attributeId;
    // 属性值 (命名奇怪)
    private String  attributeValue;
    private boolean selected;
    private boolean enable;
    private long    goodsId;



    public GoodsAttribute() {
        super();
    }



    public GoodsAttribute(long attributeId, String attributeValue, boolean selected, boolean enable) {
        super();
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
        this.selected = selected;
        this.enable = enable;
    }



    public long getAttributeId() {
        return attributeId;
    }



    public void setAttributeId(long attributeId) {
        this.attributeId = attributeId;
    }



    public String getAttributeValue() {
        return attributeValue;
    }



    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    public boolean isEnable() {
        return enable;
    }



    public void setEnable(boolean enable) {
        this.enable = enable;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    @Override
    public int hashCode() {

        return 0;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GoodsAttribute other = (GoodsAttribute) obj;
        // if (attributeId != other.attributeId) {
        // return false;
        // }
        if (StringUtils.isBlank(attributeValue)) {
            return false;
        }
        if (!attributeValue.equals(other.getAttributeValue())) {
            return false;
        }
        return true;
    }
    
}
