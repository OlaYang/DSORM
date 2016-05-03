/**
 * @Title: LoginWith3rdAction.java
 * @Package com.meiqi.openservice.action.login.thirdLogin
 * @Description: TODO(用一句话描述该文件做什么)
 * @author wanghuanwei
 * @date 2015年7月8日 下午19:52:08
 * @version V1.0
 */
package com.meiqi.openservice.bean;

public class LoginResponseInfo {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String            uid;

    private String            openId;

    private String            regionId;

    private String            email;

    private String            userName;

    private String            realName;

    private String            small_avatar;

    private String            login;

    private String            code;

    private String            description;



    public String getUid() {
        return uid;
    }



    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }



    public String getUserName() {
        return userName;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getRealName() {
        return realName;
    }



    public void setRealName(String realName) {
        this.realName = realName;
    }



    public String getSmall_avatar() {
        return small_avatar;
    }



    public void setSmall_avatar(String small_avatar) {
        this.small_avatar = small_avatar;
    }



    public String getOpenId() {
        return openId;
    }



    public void setOpenId(String openId) {
        this.openId = openId;
    }



    public String getRegionId() {
        return regionId;
    }



    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }



    public String getLogin() {
        return login;
    }



    public void setLogin(String login) {
        this.login = login;
    }



    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{").append(uid).append(",").append(email).append(",").append(userName).append(",").append(realName)
                .append(",").append(small_avatar).append("}");
        return sb.toString();
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }



    public String getDescription() {
        return description;
    }



    public void setDescription(String description) {
        this.description = description;
    }
}
