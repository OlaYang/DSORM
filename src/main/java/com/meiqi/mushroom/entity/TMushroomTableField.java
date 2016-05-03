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
    //insert使用
    private String dbName;
    private String tableName;
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

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
