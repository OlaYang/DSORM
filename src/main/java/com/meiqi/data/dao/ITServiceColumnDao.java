package com.meiqi.data.dao;

import java.util.List;

import com.meiqi.data.entity.TServiceColumn;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午12:59:58
 * @discription
 */
public interface ITServiceColumnDao {
	
	/**
	 * @description:根据serviceId查询出对应的所有对应的列名信息
	 * @param:serviceId数据源的Id，具有唯一性
	 * @return:TServiceColumnPo
	 */
	public List<TServiceColumn> findTServiceColumnById(Integer serviceId); 
}
