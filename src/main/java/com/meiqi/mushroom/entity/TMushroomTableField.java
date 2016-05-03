package com.meiqi.mushroom.entity;

/**
 * 表字段映射实体
 * User: 
 * Date: 13-10-10
 * Time: 上午10:03
 */
public class TMushroomTableField {
    private Integer tid;
    private String serviceField;
    private String tableField;

    @Override
    public String toString() {
        return "TMushroomTableField{" +
                "serviceField='" + serviceField + '\'' +
                ", tid=" + tid +
                ", tableField='" + tableField + '\'' +
                '}';
    }
    
    public String getServiceField() {
		return serviceField;
	}

	public void setServiceField(String serviceField) {
		this.serviceField = serviceField;
	}

	public String getTableField() {
		return tableField;
	}

	public void setTableField(String tableField) {
		this.tableField = tableField;
	}

	public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }
}
