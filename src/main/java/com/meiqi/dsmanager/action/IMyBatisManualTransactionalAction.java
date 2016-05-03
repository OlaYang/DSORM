package com.meiqi.dsmanager.action;

import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;
import com.meiqi.mushroom.entity.TMushroomTableField;

/**
 * 需要进行数据库事务管理
 * @author Administrator
 *
 */
public interface IMyBatisManualTransactionalAction {
	public void saveMushroomConfig(TMushroomService tMushroomService,TMushroomTable tMushroomTable,TMushroomTableField tMushroomTableField) throws Exception;
}
