package com.meiqi.data.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceDBDao;
import com.meiqi.data.entity.TServiceDB;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午3:18:56 
 * 类说明 
 */
@Repository("tServiceDBDao")
public class TServiceDBDaoImpl extends BaseDao implements ITServiceDBDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public List<TServiceDB> getTServiceDBInfoById(Map<String,String> paramMap) {
		return getSqlSessionTemplate().selectList(STATEMENT+".SelectTServiceDB", paramMap);
	}

}
