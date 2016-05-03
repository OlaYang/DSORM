package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Company;

public interface CompanyService {

    List<Company> getCompanyByregion(Company companyParam);



    Company getCompanyById(long id, int type);



    /**
     * 
     * 获取合作公司(Total)接口
     *
     * @param companyParam
     * @return
     */
    int getCompanyTotal(Company companyParam);
}
