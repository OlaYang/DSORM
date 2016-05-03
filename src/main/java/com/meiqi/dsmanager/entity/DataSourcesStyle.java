package com.meiqi.dsmanager.entity;

import java.io.Serializable;
import java.util.List;


/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月3日 下午6:57:02 
 * 类说明  数据源样式
 */
public class DataSourcesStyle implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	/**
	 * 数据源
	 */
	private List<DataSources> dsList;
	
	/**
	 * 样式内容
	 */
	private String stytleContent;
	
	/**
	 * 样式编号
	 */
	private String styleNumber;
	
	/**
	 * 样式备注
	 */
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<DataSources> getDsList() {
		return dsList;
	}

	public void setDsList(List<DataSources> dsList) {
		this.dsList = dsList;
	}

	public String getStytleContent() {
		return stytleContent;
	}

	public void setStytleContent(String stytleContent) {
		this.stytleContent = stytleContent;
	}

	public String getStyleNumber() {
		return styleNumber;
	}

	public void setStyleNumber(String styleNumber) {
		this.styleNumber = styleNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
