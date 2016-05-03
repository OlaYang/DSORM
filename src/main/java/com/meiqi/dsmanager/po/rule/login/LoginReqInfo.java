package com.meiqi.dsmanager.po.rule.login;

import java.io.Serializable;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午1:55:38
 * @discription
 */
public class LoginReqInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Override
    public String toString() {
        return "LoginReqInfo{" +
                "password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
