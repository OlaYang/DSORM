package com.meiqi.data.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.data.entity.TService;
import com.meiqi.util.BaseDao;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月24日 下午4:15:23 
 * 类说明 
 */
@Repository("tServiceDao")
public class TServiceDaoImpl extends BaseDao implements ITServiceDao{

	private final String STATEMENT = getClass().getCanonicalName();
	
	@Override
	public List<TService> getTServiceInfoByName(String name) {
		return getSqlSessionTemplate().selectList(STATEMENT+".SelectTService", name);
	}
	
	@Override
    public List<TService> getTServiceNameByName(String name) {
        return getSqlSessionTemplate().selectList(STATEMENT+".SelectTServiceName", name);
    }
	
	@Override
	public String getTServiceSqlByName(String name) {
		return getSqlSessionTemplate().selectOne(STATEMENT+".SelectSqlByName", name);
	}

	@Override
	public List<TService> getTServiceInfoByBaseserviceid(int baseserviceId) {
		return getSqlSessionTemplate().selectList(STATEMENT+".SelectTServiceByBaseserviceid", baseserviceId);
	}

	@Override
    public TService getTServiceByServiceId(int serviceId) {
        return getSqlSessionTemplate().selectOne(STATEMENT+".selectTServiceByServiceId", serviceId);
    }

    @Override
    public List<TService> getAllBaseTServiceInfo() {
        return getSqlSessionTemplate().selectList(STATEMENT+".getAllBaseTServiceInfo");
    }
}
