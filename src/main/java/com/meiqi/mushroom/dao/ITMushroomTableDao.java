package com.meiqi.mushroom.dao;

import com.meiqi.mushroom.entity.TMushroomTable;

public interface ITMushroomTableDao {
	public TMushroomTable findAllBySid(int sid);
}
