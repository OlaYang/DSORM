package com.meiqi.data.user.handler.service;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-6-26
 * Time: 上午10:34
 */
public class ServiceReqInfo {
    private Integer serviceID;
    private String serviceName;
    private List<Integer> serviceIDList;
    private List<String> serviceNameList;
    private String format = "json";
    private String needAll = "0";
    private String dbLang = "en";
    private Map<String, Object> param;
    private Map<String, Map<String, Object>> paramList;
    private String orderColumnName = null;
    private String order = "asc";


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderColumnName() {
        return orderColumnName;
    }

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public String getDbLang() {
        return dbLang;
    }

    public void setDbLang(String dbLang) {
        this.dbLang = dbLang;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getServiceNameList() {
        return serviceNameList;
    }

    public void setServiceNameList(List<String> serviceNameList) {
        this.serviceNameList = serviceNameList;
    }

    public Map<String, Map<String, Object>> getParamList() {
        return paramList;
    }

    public void setParamList(Map<String, Map<String, Object>> paramList) {
        this.paramList = paramList;
    }

    public List<Integer> getServiceIDList() {
        return serviceIDList;
    }

    public void setServiceIDList(List<Integer> serviceIDList) {
        this.serviceIDList = serviceIDList;
    }

    public String getNeedAll() {
        return needAll;
    }

    public void setNeedAll(String needAll) {
        this.needAll = needAll;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public Integer getServiceID() {
        return serviceID;
    }

    public void setServiceID(Integer serviceID) {
        this.serviceID = serviceID;
    }


    @Override
    public String toString() {
        return "ServiceReqInfo{" +
                "dbLang='" + dbLang + '\'' +
                ", serviceID=" + serviceID +
                ", serviceName='" + serviceName + '\'' +
                ", serviceIDList=" + serviceIDList +
                ", serviceNameList=" + serviceNameList +
                ", format='" + format + '\'' +
                ", needAll='" + needAll + '\'' +
                ", param=" + param +
                ", paramList=" + paramList +
                ", orderColumnName='" + orderColumnName + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
