package com.meiqi.dsmanager.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.meiqi.dsmanager.dao.IDataMappingDao;
import com.meiqi.dsmanager.entity.DataMapping;
import com.meiqi.util.BaseDao;

@Repository
public class DataMappingDaoImpl extends BaseDao implements IDataMappingDao{

	private final String STATEMENT = getClass().getCanonicalName();

	@Override
	public List<DataMapping> findAllbyDbNameAndTableNameAndMappingStatus(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".selectByDbNameAndTableNameAndMappingStatus",paramMap);
	}

	@Override
	public List<DataMapping> findAllByDsNameAndMappingStatus(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".selectByDsNameAndMappingStatus",paramMap);
	}

}
