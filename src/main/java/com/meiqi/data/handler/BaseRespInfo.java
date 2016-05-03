package com.meiqi.data.handler;

/**
 * User: 
 * Date: 13-6-27
 * Time: 上午11:40
 */
public class BaseRespInfo {
    private String code = "0";
    private String description = "Succeed";



    public BaseRespInfo() {
    }

    public BaseRespInfo(String code, String description) {
        this.code = code;
        this.description = description;
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

    @Override
    public String toString() {
        return "BaseRespInfo{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
