package com.meiqi.mushroom.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomServiceTableDao;
import com.meiqi.mushroom.entity.TMushroomServiceTable;
import com.meiqi.util.BaseDao;
@Repository
public class TMushroomServiceTableDaoImpl extends BaseDao implements ITMushroomServiceTableDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public int add(TMushroomServiceTable tMushroomServiceTable) throws Exception {
		return getSqlSessionTemplate().insert(STATEMENT+".add",tMushroomServiceTable);
	}
	@Override
	public int add(SqlSession sqlSession,
			TMushroomServiceTable tMushroomServiceTable) throws Exception {
		return sqlSession.insert(STATEMENT+".add",tMushroomServiceTable);
	}

}
