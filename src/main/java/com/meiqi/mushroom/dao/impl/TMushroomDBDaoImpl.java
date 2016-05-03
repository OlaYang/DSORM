package com.meiqi.mushroom.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomDBDao;
import com.meiqi.mushroom.entity.TMushroomDB;
import com.meiqi.util.BaseDao;

@Repository
public class TMushroomDBDaoImpl extends BaseDao implements ITMushroomDBDao{

	private final String STATEMENT = getClass().getCanonicalName();
	
	@Override
	public List<TMushroomDB> findAll() {
		return getSqlSessionTemplate().selectList(STATEMENT+".TMushroomDBList"); 
	}


	@Override
	public TMushroomDB findByDid(Integer id) {
		return getSqlSessionTemplate().selectOne(STATEMENT+".TMushroomDBById",id); 
	}

}
