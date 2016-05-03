package com.meiqi.mushroom.dao;

import java.util.List;
import com.meiqi.mushroom.entity.TMushroomTableField;

public interface ITMushroomTableFieldDao {
	/**
	 * 查询mushroom所有node
	 * @return 查询到的所有node信息集合
	 */
	public List<TMushroomTableField> findAllByTid(int tid);
}
