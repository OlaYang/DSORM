package com.meiqi.data.user.handler.servicelist;

import com.meiqi.data.handler.BaseRespInfo;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-8-31
 * Time: 上午11:01
 */
public class ServiceListRespInfo extends BaseRespInfo {
    private List<List<Map<String, String>>> rowsList;


    @Override
    public String toString() {
        return "ServiceListRespInfo{" +
                "rowsList=" + rowsList +
                '}';
    }

    public List<List<Map<String, String>>> getRowsList() {
        return rowsList;
    }

    public void setRowsList(List<List<Map<String, String>>> rowsList) {
        this.rowsList = rowsList;
    }
}
