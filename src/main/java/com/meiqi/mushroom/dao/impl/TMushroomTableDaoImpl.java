package com.meiqi.mushroom.dao.impl;

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

}
