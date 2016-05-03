package com.meiqi.mushroom.dao;

import java.util.List;

import com.meiqi.mushroom.entity.TMushroomNode;

public interface ITMushroomNodeDao {
	/**
	 * 查询mushroom所有node
	 * @return 查询到的所有node信息集合
	 */
	public List<TMushroomNode> findAll();
	
	public TMushroomNode findById(Integer id);
}
