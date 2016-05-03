package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.entity.DataSources;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月4日 下午1:19:50 
 * 类说明 
 */

public interface IDataSourcesAction {

	/**
	 * 通过数据源名查找数据源
	 * @param name
	 * @return
	 */
	public DataSources findByName(String name);
	
	/**
	 * 通过数据源名和样式编码查找到有样式的数据源
	 * @param name
	 * @param styleNumber
	 * @return
	 */
	public DataSources findByNameAndStyleNumber(String dsName,String  styleSn);
}
