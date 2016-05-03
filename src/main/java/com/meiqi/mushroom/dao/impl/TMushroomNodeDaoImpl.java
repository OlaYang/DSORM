package com.meiqi.mushroom.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomNodeDao;
import com.meiqi.mushroom.entity.TMushroomNode;
import com.meiqi.util.BaseDao;
@Repository
public class TMushroomNodeDaoImpl extends BaseDao implements ITMushroomNodeDao{
	
	private final String STATEMENT = getClass().getCanonicalName();
	
	@Override
	public List<TMushroomNode> findAll() {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectList(STATEMENT+".TMushroomNodeList"); 
	}

	@Override
	public TMushroomNode findById(Integer id) {
		// TODO Auto-generated method stub
		return getSqlSessionTemplate().selectOne(STATEMENT+".TMushroomNodeById",id);	
	}

}
