package com.meiqi.dsmanager.po.rule.userlist;


import java.util.List;

import com.meiqi.dsmanager.po.dsmanager.BaseRespInfo;

/**
 * User: 
 * Date: 13-7-15
 * Time: 下午4:19
 */
public class UserListRespInfo extends BaseRespInfo {
    private List<UserInfo> userInfoList;

    @Override
    public String toString() {
        return "UserListRespInfo{" +
                "userInfoList=" + userInfoList +
                '}';
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
