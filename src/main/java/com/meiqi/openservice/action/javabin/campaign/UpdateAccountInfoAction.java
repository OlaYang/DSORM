/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: UpdateAccountInfoAction.java 
 * @author wanghuanwei
 * @since 2015.7.13
 * @Desciption 实现百度推广（搜索推广和网盟推广）相关API
 * 
 * */
package com.meiqi.openservice.action.javabin.campaign;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.meiqi.openservice.bean.RepInfo;

/**
 * 实现账户信息的更新功能
 * @author wanghuanwei
 * @date 2015/7/13
 * @version 1.0
 * */
@Service
public class UpdateAccountInfoAction {
    public String UpdateAccountInfo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
        String data = null;
        return data;
    }
}
