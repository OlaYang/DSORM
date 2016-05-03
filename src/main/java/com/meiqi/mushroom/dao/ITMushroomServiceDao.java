package com.meiqi.mushroom.dao;

import java.util.List;

import com.meiqi.mushroom.entity.TMushroomService;

public interface ITMushroomServiceDao {
	/**
	 * 查询mushroom所有service
	 * @return 查询到的所有service信息集合
	 */
	public List<TMushroomService> findAll();
	
	/**
	 * 通过服务名查询服务
	 * @param name
	 * @return
	 */
	public TMushroomService findByName(String name);
}
