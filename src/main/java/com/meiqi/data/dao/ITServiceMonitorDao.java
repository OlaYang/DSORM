package com.meiqi.data.dao;

import java.util.Map;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午1:21:47
 * @discription
 */
public interface ITServiceMonitorDao {

	/**
	 * @description:新增TServiceMonitor数据
	 * @param:monitor存储新增数据
	 * @return:void
	 */
	public Integer addTServiceMonitorInfo(Map<String, String> paramMap);
}
