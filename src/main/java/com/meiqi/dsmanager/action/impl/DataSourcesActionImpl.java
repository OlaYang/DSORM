package com.meiqi.dsmanager.action.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IDataSourcesAction;
import com.meiqi.dsmanager.dao.IDataSourcesDao;
import com.meiqi.dsmanager.entity.DataSources;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月4日 下午1:21:47 
 * 类说明 
 */

@Service
public class DataSourcesActionImpl implements IDataSourcesAction{

	@Autowired
	private IDataSourcesDao dataSourcesImpl;
	
	@Override
	public DataSources findByName(String name) {
		return dataSourcesImpl.findByName(name);
	}

	@Override
	public DataSources findByNameAndStyleNumber(String dsName,String  styleSn) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", dsName);
		map.put("styleNumber", styleSn);
		
		return dataSourcesImpl.findByNameAndStyleNumber(map);
	}

}
