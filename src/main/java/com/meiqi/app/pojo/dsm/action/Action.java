package com.meiqi.app.pojo.dsm.action;

import java.util.Map;

/**
 * User: 
 * Date: 13-10-7
 * Time: 下午3:35
 */
public class Action {
    private Integer site_id;//站点ID
    private String type;
    private String serviceName;
    private Map<String, Object> set;
    private Where where;
    
    public Action(){
    	
    }
    @Override
    public String toString() {
        return "Action{" +
                "serviceName='" + serviceName + '\'' +
                ", type='" + type + '\'' +
                ", set=" + set +
                ", where=" + where +
                '}';
    }
    
    public Integer getSite_id() {
        return site_id;
    }
    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, Object> getSet() {
        return set;
    }

    public void setSet(Map<String, Object> set) {
        this.set = set;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Where getWhere() {
        return where;
    }

    public void setWhere(Where where) {
        this.where = where;
    }
}
