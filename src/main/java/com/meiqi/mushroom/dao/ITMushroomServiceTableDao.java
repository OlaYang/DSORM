package com.meiqi.mushroom.dao;

import org.apache.ibatis.session.SqlSession;

import com.meiqi.mushroom.entity.TMushroomServiceTable;


public interface ITMushroomServiceTableDao {
	public int add(TMushroomServiceTable tMushroomServiceTable) throws Exception;
	
	public int add(SqlSession sqlSession,TMushroomServiceTable tMushroomServiceTable) throws Exception;
}
