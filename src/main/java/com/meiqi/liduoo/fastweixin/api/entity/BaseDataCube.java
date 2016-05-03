package com.meiqi.liduoo.fastweixin.api.entity;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author peiyu
 */
public class BaseDataCube extends BaseModel {

    @JSONField(name = "ref_date", format = "yyyy-MM-dd")
    private Date refDate;

    public Date getRefDate() {
        return refDate;
    }

    public void setRefDate(Date refDate) {
        this.refDate = refDate;
    }
}
