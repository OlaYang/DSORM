package com.meiqi.app.pojo;

import java.util.Set;

/**
 * 
 * @ClassName: GoodsStandard
 * @Description:临时规格大类
 * @author 杨永川
 * @date 2015年5月4日 下午2:02:11
 *
 */
public class GoodsStandard {
    private String              standardName;
    private Set<GoodsAttribute> attributes;



    public GoodsStandard(String standardName, Set<GoodsAttribute> attributes) {
        this.standardName = standardName;
        this.attributes = attributes;
    }



    public GoodsStandard() {
    }



    public String getStandardName() {
        return standardName;
    }



    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }



    public Set<GoodsAttribute> getAttributes() {
        return attributes;
    }



    public void setAttributes(Set<GoodsAttribute> attributes) {
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
        GoodsStandard other = (GoodsStandard) obj;
        if (standardName == null) {
            if (other.standardName != null)
                return false;
        } else if (!standardName.equals(other.standardName))
            return false;
        return true;
    }

}
