package com.meiqi.dsmanager.po.dsmanager;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.mushroom.offer.Action;

/**
 * 数据源管理请求信息
 */
public class DsManageReqInfo { 
    private String accessFlag;//标示为是否要做权限控制2：不控制  默认值为2 不控制  非2的话就都要做控制
    private String serviceName;
	private String dsContent;
	private String format = "json";
	private String needAll = "0";
	//定义返回字段，如：returnFilter："id,name",且returnFilter和needAll同时使用，即:needAll=1时returnFilter必为空
	private String returnFilter;
	private String dbLang = "en";
	private Map<String, Object> param;
	private String orderColumnName = null;
	private String order = "asc";
	private String styleSn;
	private List<DsManageReqInfo> services;
	private Integer site_id;//站点ID
    private List<Map<String, Object>> params;
    //mushroom简单报文使用
	private List<Action> actions;
	private String uid;
	private long startTime;
	    
	private boolean needVerifyAndRebuildData;//是否需要根据规则配置的刷选条件过滤数据
	
	private boolean noDataFlag;//标示是否有没有数据标示
	// 记录操作日志相关参数，传送时，只需要请求开始时间和uid两个

    public boolean isNoDataFlag() {
        return noDataFlag;
    }

    public void setNoDataFlag(boolean noDataFlag) {
        this.noDataFlag = noDataFlag;
    }

    public boolean isNeedVerifyAndRebuildData() {
        return needVerifyAndRebuildData;
    }

    public void setNeedVerifyAndRebuildData(boolean needVerifyAndRebuildData) {
        this.needVerifyAndRebuildData = needVerifyAndRebuildData;
    }

	public String getAccessFlag() {
	        return accessFlag;
	}

	public void setAccessFlag(String accessFlag) {
	        this.accessFlag = accessFlag;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDsContent() {
		return dsContent;
	}

	public void setDsContent(String dsContent) {
		this.dsContent = dsContent;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getNeedAll() {
		return needAll;
	}

	public void setNeedAll(String needAll) {
		this.needAll = needAll;
	}

	public String getDbLang() {
		return dbLang;
	}

	public void setDbLang(String dbLang) {
		this.dbLang = dbLang;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public String getOrderColumnName() {
		return orderColumnName;
	}

	public void setOrderColumnName(String orderColumnName) {
		this.orderColumnName = orderColumnName;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getStyleSn() {
		return styleSn;
	}

	public void setStyleSn(String styleSn) {
		this.styleSn = styleSn;
	}

    public String getReturnFilter() {
        return returnFilter;
    }

    public void setReturnFilter(String returnFilter) {
        this.returnFilter = returnFilter;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

	public List<DsManageReqInfo> getServices() {
		return services;
	}

	public void setServices(List<DsManageReqInfo> services) {
		this.services = services;
	}
    public List<Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
        this.params = params;
    }
    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

}
