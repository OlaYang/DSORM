package com.meiqi.dsmanager.po.mushroom.offer;

import java.util.Map;

/**
 * offer请求参数 action实体
 * User: 
 * Date: 13-10-7
 * Time: 下午3:35
 */
public class Action {
    private Integer site_id;//站点ID
    private String key_type;//insert 语句中关键字 比如IGNORE
    private String type;
    private String serviceName;
    private Map<String, Object> set;
    private Where where;
    
    //函数用
    private String whereSql;
    
    private String requestType;//'来源 1 来自规则,2 来自mushroom接口'

	//mushroom简单报文使用的key
	private String key;
	
    public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Action(){
    	
    }
	@Override
    public String toString() {
        return "Action{" +
                "serviceName='" + serviceName + '\'' +
                ", type='" + type + '\'' +
                ", set=" + set +
                ", where=" + where +
                ", whereSql="+whereSql+
                '}';
    }


    public String getServiceName() {
        return serviceName;
    }

    public String getWhereSql() {
		return whereSql;
	}
	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
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
    public Integer getSite_id() {
        return site_id;
    }
    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }
    public String getKey_type() {
        return key_type;
    }
    public void setKey_type(String key_type) {
        this.key_type = key_type;
    }
    
    public String getRequestType() {
        return requestType;
    }
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
