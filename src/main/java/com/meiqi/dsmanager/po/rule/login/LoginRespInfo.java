package com.meiqi.dsmanager.po.rule.login;

import java.io.Serializable;

import com.meiqi.dsmanager.po.dsmanager.BaseRespInfo;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午1:55:47
 * @discription
 */
public class LoginRespInfo extends BaseRespInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupID;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    @Override
    public String toString() {
        return "LoginRespInfo{" +
                ", groupID='" + groupID + '\'' +
                '}';
    }
}
