package com.meiqi.dsmanager.po.dsmanager;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午1:54:33
 * @discription
 */
public class BaseRespInfo {
    private String code = "0";//0,-1
    private String description = "Succeed";//Succeed,Failed



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
