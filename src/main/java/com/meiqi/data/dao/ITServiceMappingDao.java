package com.meiqi.data.dao;

import java.util.List;

import com.meiqi.data.entity.TServiceMapping;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午1:15:17
 * @discription
 */
public interface ITServiceMappingDao {
	
	/**
	 * @description:获取t_service_mapping表中的所有数据库信息
	 * @param:
	 * @return:List<TServiceMapping>
	 */
	public List<TServiceMapping> findAllTServiceMappingInfo();
}
