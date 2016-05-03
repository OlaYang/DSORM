/**   
* @Title: LoginType.java 
* @Package com.meiqi.openservice.action.bean 
* @Description: TODO(用一句话描述该文件做什么) 
* @author wanghuanwei
* @date 2015年7月8日  下午13:52:08 
* @version V1.0   
*/
package com.meiqi.openservice.bean;

import java.util.Map;

public class LoginType {

    private String type;
    
    private Map<String, Object> param;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
