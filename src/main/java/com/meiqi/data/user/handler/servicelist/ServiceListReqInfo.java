package com.meiqi.data.user.handler.servicelist;

import java.util.List;
import java.util.Map;

/**
 * User: 
 * Date: 13-8-31
 * Time: 上午10:53
 */
public class ServiceListReqInfo {
    private List<ServiceInfo> serviceInfos;
    private List<Map<String, Object>> params;
    private String format = "json";


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "ServiceListReqInfo{" +
                "format='" + format + '\'' +
                ", serviceInfos=" + serviceInfos +
                ", params=" + params +
                '}';
    }

    public List<Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
        this.params = params;
    }

    public List<ServiceInfo> getServiceInfos() {
        return serviceInfos;
    }

    public void setServiceInfos(List<ServiceInfo> serviceInfos) {
        this.serviceInfos = serviceInfos;
    }
}
