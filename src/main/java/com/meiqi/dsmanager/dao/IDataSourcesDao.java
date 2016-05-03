package com.meiqi.dsmanager.dao;

import java.util.Map;

import com.meiqi.dsmanager.entity.DataSources;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月4日 上午10:09:54 
 * 类说明 
 */

public interface IDataSourcesDao{

	/**
	 * 通过数据源名查找数据源
	 * @param name
	 * @return DataSources
	 */
	public DataSources findByName(String name);
	
	/**
	 * 通过数据源名和样式编号查找到有样式的数据源
	 * @param paramMap
	 * @return
	 */
	public DataSources findByNameAndStyleNumber(Map<String,String> paramMap);
}
