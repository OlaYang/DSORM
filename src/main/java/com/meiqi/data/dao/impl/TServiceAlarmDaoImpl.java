package com.meiqi.data.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceAlarmDao;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午4:51:27 
 * 类说明 
 */
@Repository
public class TServiceAlarmDaoImpl extends BaseDao implements ITServiceAlarmDao{

	private final String STATEMENT =getClass().getCanonicalName();
	
	@Override
	public Integer addTServiceAlarmInfo(Map<String, String> paramMap) {
		return getSqlSessionTemplate().insert(STATEMENT+".insertTServiceAlarm", paramMap);
	}

}
