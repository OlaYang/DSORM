/*
 * File name: AppResponseInfo.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年10月16日 ...
 * ... ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.bean;

import java.util.List;
import java.util.Map;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年10月16日
 */

public class AppResponseInfo {
    private String                    code;

    private String                    description;

    private Object                    object;

    private List<Map<String, Object>> rows;



    public AppResponseInfo() {

    }



    public AppResponseInfo(String code, String description) {
        this.code = code;
        this.description = description;
    }



    public List<Map<String, Object>> getRows() {
        return rows;
    }



    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
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



    public Object getObject() {
        return object;
    }



    public void setObject(Object object) {
        this.object = object;
    }
}
