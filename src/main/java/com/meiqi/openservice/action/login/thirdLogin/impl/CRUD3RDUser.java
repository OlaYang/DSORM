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

public class CRUD3RDUser {
    @Autowired
    private IMushroomAction mushroomAction;

    private String userName;

    private String small_avatar;

    private String user_ip;


    public CRUD3RDUser(String userName, String avatar, String user_ip) {
        this.userName = userName;
        this.small_avatar = avatar;
        this.user_ip = user_ip;
    }

    public String addUserToDB() {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        String serviceName = "ecs_users_test_ecshop";
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

        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse = null;
        String res1 = mushroomAction.offer(actionReqInfo);
        actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
        if (Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        } else {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(actionResponse.getDescription());
        }
        String resultData = JSON.toJSONString(respInfo);
        return resultData;
    }

    public void getDataFromSerivce(String serName,Map<String, Object> params,boolean needAll){

    }

    static class ThirdUserInfo {
        long user_id;
        String user_name;
        String alias;
        String avatar;
        String region_id;
        String source;
        long reg_time;
        long last_time;
        int visit_count;
        int role_id;
        String from;
        String open_id;

        public long getUser_id() {
            return user_id;
        }

        public void setUser_id(long user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public long getReg_time() {
            return reg_time;
        }

        public void setReg_time(long reg_time) {
            this.reg_time = reg_time;
        }

        public long getLast_time() {
            return last_time;
        }

        public void setLast_time(long last_time) {
            this.last_time = last_time;
        }

        public int getVisit_count() {
            return visit_count;
        }

        public void setVisit_count(int visit_count) {
            this.visit_count = visit_count;
        }

        public int getRole_id() {
            return role_id;
        }

        public void setRole_id(int role_id) {
            this.role_id = role_id;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }
    }

    public boolean existUser(){
        return false;
    }
}
