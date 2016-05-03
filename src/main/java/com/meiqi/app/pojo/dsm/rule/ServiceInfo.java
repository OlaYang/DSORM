package com.meiqi.app.pojo.dsm.rule;

/**
 * 规则引擎规则信息
 */
public class ServiceInfo {
    private String serviceName;
    private String needAll = "0";
    private String dbLang = "en";
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

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "dbLang='" + dbLang + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", needAll='" + needAll + '\'' +
                ", orderColumnName='" + orderColumnName + '\'' +
                ", order='" + order + '\'' +
                '}';
    }

    public String getDbLang() {
        return dbLang;
    }

    public void setDbLang(String dbLang) {
        this.dbLang = dbLang;
    }

    public String getNeedAll() {
        return needAll;
    }

    public void setNeedAll(String needAll) {
        this.needAll = needAll;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
