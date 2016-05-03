package com.meiqi.data.dao;

import java.util.List;

import com.meiqi.data.entity.TService;

/**
 * 
 * @author fangqi
 * @date 2015年6月23日 下午12:04:37
 * @discription
 */
public interface ITServiceDao {

	
	/**
	 * @description:通过数据源名称(完整名称)查找对应数据,并返回结果(name=null则查询所有)
	 * @param:name代表数据源名称
	 * @return:TService
	 */
	public List<TService> getTServiceInfoByName(String name);
	
	/**
     * @description:通过数据源名称(完整名称)查找对应数据,并返回结果(name=null则查询所有)
     * @param:name代表数据源名称
     * @return:TServiceName
     */
    public List<TService> getTServiceNameByName(String name);
    
	/**
	 * @description:通过数据源名称获取对应的sql语句，并返回sql
	 * @param:name代表数据源名称
	 * @return:String
	 */
	public String getTServiceSqlByName(String name);
	
	
	public List<TService> getTServiceInfoByBaseserviceid(int baseserviceId);
	
	
	public TService getTServiceByServiceId(int serviceId);
	
	public List<TService> getAllBaseTServiceInfo();
}
