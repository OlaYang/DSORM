package com.meiqi.data.dao;

import java.util.List;

import com.meiqi.data.entity.TServiceNextDBConfig;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午1:08:21
 * @discription
 */
public interface ITServiceNextDBConfigDao {
	
	/**
	 * @description:获取所有的数据库表中的信息
	 * @param:
	 * @return:List<TServiceNextDBConfig>
	 */
	public List<TServiceNextDBConfig> findAllTServiceNextDBConfigInfo();
}
