package com.meiqi.data.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceMonitorDao;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午5:04:54 
 * 类说明 
 */
@Repository
public class TServiceMonitorDaoImpl extends BaseDao implements ITServiceMonitorDao{

	private final String STATEMENT =getClass().getCanonicalName();
	
	@Override
	public Integer addTServiceMonitorInfo(Map<String, String> paramMap) {
		return getSqlSessionTemplate().insert(STATEMENT+".insertTServiceMonitor", paramMap);
	}

}
