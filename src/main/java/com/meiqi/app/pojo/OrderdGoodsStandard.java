package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: GoodsStandard
 * @Description:临时规格大类
 * @author 杨永川
 * @date 2015年5月4日 下午2:02:11
 *
 */
public class OrderdGoodsStandard {
    
    private String              standardName;
    
    private List<GoodsAttribute> attributes;



    public OrderdGoodsStandard(String standardName, List<GoodsAttribute> attributes) {
        this.standardName = standardName;
        this.attributes = attributes;
    }



    public OrderdGoodsStandard() {
    }



    public String getStandardName() {
        return standardName;
    }



    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }



    public List<GoodsAttribute> getAttributes() {
        return attributes;
    }



    public void setAttributes(List<GoodsAttribute> attributes) {
        this.attributes = attributes;
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
        OrderdGoodsStandard other = (OrderdGoodsStandard) obj;
        if (standardName == null) {
            if (other.standardName != null)
                return false;
        } else if (!standardName.equals(other.standardName))
            return false;
        return true;
    }

}
