package com.meiqi.mushroom.dao;

import java.util.List;

import com.meiqi.mushroom.entity.TMushroomDB;

public interface ITMushroomDBDao {
	/**
	 * 查询mushroom所有db
	 * @return 查询到的所有db信息集合
	 */
	public List<TMushroomDB> findAll();
	/**
	 * 查询mushroom的db，根据db名
	 * @return
	 */
	public TMushroomDB find(String dbName);
	
	public TMushroomDB findByDid(Integer id);
}
