package com.meiqi.data.po;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-5-22
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
public class TServiceCallLogPo {
    private Integer id;
    private String serviceName;
    private String param;
    private String ip;
    private Date time;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
