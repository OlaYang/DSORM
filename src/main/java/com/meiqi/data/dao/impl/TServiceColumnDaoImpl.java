package com.meiqi.data.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceColumnDao;
import com.meiqi.data.entity.TServiceColumn;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午2:59:44 
 * 类说明 
 */
@Repository
public class TServiceColumnDaoImpl extends BaseDao implements ITServiceColumnDao{

	private final String STATEMENT = getClass().getCanonicalName();
	@Override
	public List<TServiceColumn> findTServiceColumnById(Integer serviceId) {

		return getSqlSessionTemplate().selectList(STATEMENT+".TServiceColumnListById", serviceId);
	}

}
