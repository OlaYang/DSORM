package com.meiqi.dsmanager.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.meiqi.dsmanager.entity.DataMapping;

/**
 * 缓存映射表ser类接口
 * @author DuanRan
 * @date 2015年6月12日
 */
public interface IDataMappingAction {
	/**
	 * 通过查找数据库名、数据表名将状态为true（不需更新）的数据改为false（需要更新）
	 * @param mapSet map集合，key为数据库名 value为set集合存放着表名
	 */
	public void updateMappingTOFalseStatusByDbNameAndTableName(Map<String,Set<String>> mapSet);
	
	/**
	 * 通过dsname修改缓存状态为true不需更新
	 * @param dataMappingList
	 */
	public void updateMappingToTrueStatusByDSName(List<DataMapping> dataMappingList);
	
	/**
	 * 通过dsname与缓存更新状态查找缓存映射
	 * @param dsName 
	 * @param mappingStatus
	 * @return
	 */
	public List<DataMapping> findDataMappingByDsNameAndMappingStatus(String dsName,boolean mappingStatus);
	
	/**
	 * 新增一个key到对应的dsName
	 * @param dsName
	 * @param key
	 */
	public void addOneInMappingMap(String dsName,String key);
	
	/**
	 * 移除一个
	 * @param dsName
	 */
	public void removeMappingMap(String dsName);
}
