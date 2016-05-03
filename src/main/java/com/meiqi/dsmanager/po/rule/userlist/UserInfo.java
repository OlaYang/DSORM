package com.meiqi.dsmanager.po.rule.userlist;

/**
 * User: 
 * Date: 13-7-15
 * Time: 下午4:20
 */
public class UserInfo {
    private String userName;
    private String realName;
    private String jobType;

    @Override
    public String toString() {
        return "UserInfo{" +
                "jobType='" + jobType + '\'' +
                ", userName='" + userName + '\'' +
                ", realName='" + realName + '\'' +
                '}';
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
