package com.meiqi.data.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceMappingDao;
import com.meiqi.data.entity.TServiceMapping;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午4:21:10 
 * 类说明 
 */
@Repository
public class TServiceMappingDaoImpl extends BaseDao implements ITServiceMappingDao{

	private final String STATEMENT = getClass().getCanonicalName();
	
	@Override
	public List<TServiceMapping> findAllTServiceMappingInfo() {
		return getSqlSessionTemplate().selectList(STATEMENT+".ServiceMappingList");
	}

}
