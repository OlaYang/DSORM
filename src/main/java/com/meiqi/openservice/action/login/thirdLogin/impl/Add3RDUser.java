package com.meiqi.openservice.action.login.thirdLogin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

public class Add3RDUser {
    @Autowired
    private IMushroomAction mushroomAction;
    
    private String userName;
    
    private String small_avatar;
    
    private String user_ip;
    
    
    public Add3RDUser(String userName, String avatar, String user_ip)
    {
        this.userName = userName;
        this.small_avatar = avatar;
        this.user_ip = user_ip;
    }
    
    public String addUserToDB()
    {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        
        ResponseInfo respInfo=new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        
        String serviceName="ecs_users_test_ecshop";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", userName);//和美居是用手机号码作为用户名的
        set.put("is_validated", 1);//
        set.put("visit_count", 1);//访问次数
        set.put("reg_time", DateUtils.getSecond());
        set.put("last_ip", user_ip);
        
        set.put("avatar", small_avatar);
        
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse=null;
        String res1=mushroomAction.offer(actionReqInfo);
        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        }else{
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(actionResponse.getDescription());
        }
        String resultData = JSON.toJSONString(respInfo);
        return resultData;
    }
}
