package com.meiqi.mushroom.dao;

import org.apache.ibatis.session.SqlSession;

import com.meiqi.mushroom.entity.TMushroomTable;

public interface ITMushroomTableDao {
	public TMushroomTable findAllBySid(int sid);
	
	public int add(TMushroomTable tMushroomTable) throws Exception;
	
	public int add(SqlSession sqlSession,TMushroomTable tMushroomTable) throws Exception;
}
