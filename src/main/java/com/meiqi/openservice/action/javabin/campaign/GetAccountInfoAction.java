/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: GetAccountInfoAction.java 
 * @author wanghuanwei
 * @since 2015.7.13
 * @Desciption 实现百度推广（搜索推广和网盟推广）相关API
 * 
 * */
package com.meiqi.openservice.action.javabin.campaign;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.baidu.drapi.autosdk.core.CommonService;
import com.baidu.drapi.autosdk.core.ServiceFactory;
import com.baidu.drapi.autosdk.exception.ApiException;
import com.baidu.drapi.autosdk.sms.service.AccountInfo;
import com.baidu.drapi.autosdk.sms.service.AccountService;
import com.meiqi.openservice.bean.RepInfo;

/**
 * 实现获取账户信息的功能
 * @author wanghuanwei
 * @date 2015/7/13
 * @version 1.0
 * */
@Service
public class GetAccountInfoAction {
    public String GetAccountInfo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
        String data = null;
        return data;
    }
    
    private AccountInfo innerGetAccountInfo(){
        AccountInfo accountInfo = null;
        
        try {
            CommonService factory = ServiceFactory.getInstance();
            AccountService accountService = factory.getService(AccountService.class);
            
            
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return accountInfo;
    }
}
