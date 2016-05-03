package com.meiqi.mushroom.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomTableFieldDao;
import com.meiqi.mushroom.entity.TMushroomTableField;
import com.meiqi.util.BaseDao;

@Repository
public class TMushroomTableFieldDaoImpl extends BaseDao implements ITMushroomTableFieldDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public List<TMushroomTableField> findAllByTid(int tid) {
		return getSqlSessionTemplate().selectList(STATEMENT+".TMushroomTableFieldList",tid); 

	}



}
