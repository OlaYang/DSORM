package com.meiqi.mushroom.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomTableDao;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.util.BaseDao;
@Repository
public class TMushroomTableDaoImpl extends BaseDao implements ITMushroomTableDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public TMushroomTable findAllBySid(int sid) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".TMushroomTableList",sid);
	}
	@Override
	public int add(TMushroomTable tMushroomTable) throws Exception {
		// TODO Auto-generated method stub
		int insertCount= getSqlSessionTemplate().insert(STATEMENT+".add",tMushroomTable);
		if(insertCount>0){
			return tMushroomTable.getTid();
		}
		return 0;
	}
	@Override
	public int add(SqlSession sqlSession, TMushroomTable tMushroomTable)
			throws Exception {
		int insertCount= sqlSession.insert(STATEMENT+".add",tMushroomTable);
		if(insertCount>0){
			return tMushroomTable.getTid();
		}
		return 0;
	}

}
