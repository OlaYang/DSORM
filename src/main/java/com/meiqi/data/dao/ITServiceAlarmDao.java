package com.meiqi.data.dao;

import java.util.Map;


/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午1:19:23
 * @discription
 */
public interface ITServiceAlarmDao {
	
	/**
	 * @description:往t_service_alarm表中新增数据
	 * paramMap 存储新增数据
	 * @return:void
	 */
	public Integer addTServiceAlarmInfo(Map<String,String> paramMap);
}
