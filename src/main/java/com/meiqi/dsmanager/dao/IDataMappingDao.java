package com.meiqi.dsmanager.dao;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.entity.DataMapping;

/**
 * 缓存映射dao层
 * 
 * @author DuanRan
 * @date 2015年6月13日
 */
public interface IDataMappingDao {

	/**
	 * 通过数据库名和表set集合以及状态查询缓存映射表
	 * 
	 * @param paramMap sql需要的参数的集合（包含数据库名，表明的set集合和缓存映射表的状态）
	 * @return 
	 */
	public List<DataMapping> findAllbyDbNameAndTableNameAndMappingStatus(Map<String,Object> paramMap);

	/**
	 * 通过数据源名和缓存状态查询缓存映射表
	 * 
	 * @param paramMap sql需要参数的集合（包含数据库名和缓存映射表的状态）
	 * @return
	 */
	public List<DataMapping> findAllByDsNameAndMappingStatus(Map<String,Object> paramMap);
}
