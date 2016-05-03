package com.meiqi.dsmanager.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.meiqi.dsmanager.dao.IDataSourcesDao;
import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月17日 下午3:38:17 
 * 类说明 
 */
@Repository
public class DataSourcesDaoImpl extends BaseDao implements IDataSourcesDao{
	
	private final String STATEMENT = getClass().getCanonicalName();

	@Override
	public DataSources findByName(String name) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".selectByName_dataSources", name);
	}

	@Override
	public DataSources findByNameAndStyleNumber(Map<String,String> paramMap) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".selectByNameAndStyleNumber_dataSources",paramMap);
	}

}
