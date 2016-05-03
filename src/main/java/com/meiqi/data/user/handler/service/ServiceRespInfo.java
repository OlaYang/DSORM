package com.meiqi.data.user.handler.service;

import com.meiqi.data.handler.BaseRespInfo;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-7-1
 * Time: 下午4:00
 */
public class ServiceRespInfo extends BaseRespInfo {
    private List<Map<String, String>> rows;
    private Map<String, List<Map<String, String>>> rowsList;


    public Map<String, List<Map<String, String>>> getRowsList() {
        return rowsList;
    }

    public void setRowsList(Map<String, List<Map<String, String>>> rowsList) {
        this.rowsList = rowsList;
    }

    @Override
    public String toString() {
        return "ServiceRespInfo{" +
                "rows=" + rows +
                ", rowsList=" + rowsList +
                '}';
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }
}
