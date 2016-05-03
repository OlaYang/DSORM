package com.meiqi.dsmanager.action.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IDataMappingAction;
import com.meiqi.dsmanager.common.constants.Global;
import com.meiqi.dsmanager.dao.IDataMappingDao;
import com.meiqi.dsmanager.entity.DataMapping;
import com.meiqi.dsmanager.util.CacheUtil;

/**
 * 缓存映射服务类
 * @author DuanRan
 * @date 2015年6月13日
 */

@Service
public class DataMappingActionImpl implements IDataMappingAction{

	@Autowired
	private IDataMappingDao dataMappingDao;
	
	@Override
	public void updateMappingTOFalseStatusByDbNameAndTableName(Map<String,Set<String>> mapSet) {
		for (Entry<String,Set<String>> entry : mapSet.entrySet()) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("db_name", entry.getKey());
			map.put("table_name", entry.getValue());
			map.put("mapping_status", true);
			List<DataMapping> list = dataMappingDao.findAllbyDbNameAndTableNameAndMappingStatus(map);
			List<DataMapping> dataMappingList = new ArrayList<DataMapping>();
			for (DataMapping dataMapping : list) {
				dataMapping.setMapping_status(false);
				dataMappingList.add(dataMapping);
			}
			//dataMappingDao.save(dataMappingList);
		}
		
	}

	@Override
	public List<DataMapping> findDataMappingByDsNameAndMappingStatus(String dsName, boolean mappingStatus) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("db_name", dsName);
		map.put("mapping_status", mappingStatus);
		return dataMappingDao.findAllByDsNameAndMappingStatus(map);
	}

	@Override
	public void addOneInMappingMap(String dsName, String key) {
		// TODO Auto-generated method stub
		//检查是否已经存在内存
		if(Global.mappingMap.containsKey(dsName)){
			List<String> keys=Global.mappingMap.get(dsName);
			keys.add(key);
		}else{
			List<String> keys=new ArrayList<String>();
			keys.add(key);
			Global.mappingMap.put(dsName, keys);
		}
	}

	@Override
	public void removeMappingMap(String dsName) {
		// TODO Auto-generated method stub
		//检查是否存在内存map中
		if (Global.mappingMap.containsKey(dsName)) {
			//获取所有的key
			List<String> keys=Global.mappingMap.get(dsName);
			for(String key:keys){
				CacheUtil.removeCache(key); //到缓存中删除
			}
			Global.mappingMap.remove(dsName); //删除map
		}
	}

	@Override
	public void updateMappingToTrueStatusByDSName(List<DataMapping> dataMappingList){
		for(DataMapping dm:dataMappingList){
			dm.setMapping_status(true);
		}
		//dataMappingDao.save(dataMappingList);
	}

}
