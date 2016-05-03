package com.meiqi.data.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.data.dao.IWxApiInfoDao;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.WxApiInfo;
import com.meiqi.util.BaseDao;

/**
 * @author guianzhou
 * @date 2016年2月18日 下午12:04:37
 */
@Repository("wxApiInfoDao")
public class WxApiInfoDaoImpl extends BaseDao implements IWxApiInfoDao {

	private final String STATEMENT = getClass().getCanonicalName();

	@Override
	public List<WxApiInfo> getAllApi() {
		return getSqlSessionTemplate().selectList(STATEMENT + ".SelectAllApi");
	}

	@Override
	public WxApiInfo getApiByName(String name) {
		return getSqlSessionTemplate().selectOne(STATEMENT + ".SelectApiByName", name);
	}

	@Override
	public WxApiInfo getApiById(Integer id) {
		return getSqlSessionTemplate().selectOne(STATEMENT + ".SelectApiById", id);
	}
}
