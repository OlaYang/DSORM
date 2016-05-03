package com.meiqi.dsmanager.entity;

import java.io.Serializable;


/**
 * 缓存映射通知表
 * @author DuanRan
 * @date 2015年6月12日
 */

public class DataMapping implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 数据源id
	 */
	private Long id;
	
	/**
	 * 数据源名
	 */
	private String ds_name;
	
	/**
	 * 数据库名
	 */
	private String db_name;
	
	/**
	 * 数据表名
	 */
	private String table_name;
	
	/**
	 * 数据表是否发生更改的标识(缓存状态（0=false，1=true）false需要更新)
	 */
	private Boolean mapping_status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public Boolean getMapping_status() {
		return mapping_status;
	}

	public void setMapping_status(Boolean mapping_status) {
		this.mapping_status = mapping_status;
	}
	
	
}
