package com.meiqi.openservice.service;

import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.vo.ApplyVo;


public interface IApplyService
{
    public ResponseInfo saveRegisterinfo(String xmlPath,String xsdPath, ApplyVo apply) throws Exception;
}
