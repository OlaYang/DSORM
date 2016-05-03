package com.meiqi.dsmanager.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月3日 下午5:46:57 
 * 类说明  数据源实体类
 */

public class DataSources implements Serializable{

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
	
	private String name;
	
	/**
	 * 数据源内容
	 */
	
	private String dsContent; 
	
	/**
	 * 数据源样式
	 */

	private List<DataSourcesStyle> dsStyle;
	
	/**
	 * 数据源备注
	 */
	private String dsRemark;
	
	/**
	 * 是否删除
	 */
	private boolean isDelete;
	
	/**
	 * 是否启用
	 */
	private boolean isEnabled;
	
	/**
	 * 创建人
	 */
	private String createName;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 最后修改人
	 */
	private String lastName;
	
	/**
	 * 最后修改时间
	 */
	private Date lastDate;
	
	private String version;

	/**
	 * 缓存等级 0=不缓存 1=memcache 2=dsmanager缓存
	 */
	private int cacheLevel;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCacheLevel() {
		return cacheLevel;
	}

	public void setCacheLevel(int cacheLevel) {
		this.cacheLevel = cacheLevel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDsContent() {
		return dsContent;
	}

	public void setDsContent(String dsContent) {
		this.dsContent = dsContent;
	}

	public List<DataSourcesStyle> getDsStyle() {
		return dsStyle;
	}

	public void setDsStyle(List<DataSourcesStyle> dsStyle) {
		this.dsStyle = dsStyle;
	}

	public String getDsRemark() {
		return dsRemark;
	}

	public void setDsRemark(String dsRemark) {
		this.dsRemark = dsRemark;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
