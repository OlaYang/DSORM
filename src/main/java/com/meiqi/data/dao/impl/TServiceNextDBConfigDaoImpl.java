package com.meiqi.data.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceNextDBConfigDao;
import com.meiqi.data.entity.TServiceNextDBConfig;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午3:26:59 
 * 类说明 
 */
@Repository
public class TServiceNextDBConfigDaoImpl extends BaseDao implements ITServiceNextDBConfigDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public List<TServiceNextDBConfig> findAllTServiceNextDBConfigInfo() {
		return getSqlSessionTemplate().selectList(STATEMENT+".SelectTServiceNextDBConfig");
	}

}
