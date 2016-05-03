package com.meiqi.data.dao;

import java.util.List;
import java.util.Map;

import com.meiqi.data.entity.TServiceDB;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午12:05:14
 * @discription
 */
public interface ITServiceDBDao {

	/**
	 * @description:根据dbId查询对应的数据库信息，返回查询结果（如果dbId为空，则返回所有的数据库信息）
	 * @param:map存储dbId的键值对
	 * @return:List<TServiceDB>
	 */
	public List<TServiceDB> getTServiceDBInfoById(Map<String,String> paramMap);
}
