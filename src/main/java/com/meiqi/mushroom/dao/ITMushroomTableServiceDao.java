package com.meiqi.mushroom.dao;

import java.util.List;
import com.meiqi.mushroom.entity.TMushroomTableService;

public interface ITMushroomTableServiceDao {
	public List<TMushroomTableService> findbyTid(Integer tid);

}
