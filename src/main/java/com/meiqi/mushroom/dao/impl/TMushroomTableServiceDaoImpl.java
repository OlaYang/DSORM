package com.meiqi.mushroom.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.mushroom.dao.ITMushroomTableServiceDao;
import com.meiqi.mushroom.entity.TMushroomTableService;
import com.meiqi.util.BaseDao;
@Repository
public class TMushroomTableServiceDaoImpl extends BaseDao implements ITMushroomTableServiceDao{
	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public List<TMushroomTableService> findbyTid(Integer tid) {
		return getSqlSessionTemplate().selectList(STATEMENT+".TMushroomTableServiceListByTid",tid);
	}

}
