package com.meiqi.liduoo.fastweixin.company.api.response;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.company.api.entity.QYDepartment;

/**
 *  
 *  ====================================================================
 *  上海聚攒软件开发有限公司
 *  --------------------------------------------------------------------
 *  @author Nottyjay
 *  @version 1.0.beta
 *  ====================================================================
 */
public class GetDepartmentListResponse extends BaseResponse {

    @JSONField(name = "department")
    private List<QYDepartment> departments;

    public List<QYDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<QYDepartment> departments) {
        this.departments = departments;
    }
}
