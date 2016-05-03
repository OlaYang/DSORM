package com.meiqi.dsmanager.action.impl;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.data.dao.ITServiceDBDao;
import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.cache.CachePool;

@Service("pushAction")
public class PushActionImpl implements IPushAction {

	public static final Logger         LOG = Logger.getLogger(Services.class);
	
	
	@Autowired
	private ITServiceDao tServiceDao;
	@Autowired
	private ITServiceDBDao tServiceDBDao;
	@Autowired
	private IMemcacheAction memcacheAction;

	@Override
	public void addService(String serName) throws Exception{
		// TODO Auto-generated method stub
		updateService(serName);
	}

	@Override
	public void updateService(String serName) throws Exception{
		// 根据服务名从数据库获取新的服务
		List<TService> pos = tServiceDao.getTServiceInfoByName(serName);
		// 将获取到的服务实体存入内存中
		// 因为是存放在map中因此不需要remove操作，put重复服务名会进行覆盖操作。
		for (TService service : pos) {
			//清除对应的memcache缓存
			removeMemCacheChooseServiceName(service);
		} 
		LOG.info("更新数据源memcache缓存："+serName);
	}
	

	@Override
	public void deleteService(TService tService) throws Exception{
		if (null != tService) {
			//删除datapage自身加载的初始信息
			Services.deleteService(tService);
			//清除memcache中的钙serviceName相关缓存
			removeMemCacheChooseServiceName(tService);
		}
		
	}
	
	/**
	 * 根据dataSourceName名字删除缓存
	 * 注:datasourcename与tserviceName相同
	 * @param dataSourceName
	 */
	private void removeDataSourceCacheMap(String dataSourceName){
		if(null!=dataSourceName){
			CachePool.getInstance().removeDataSourceCache(dataSourceName);
		}
	}
	
	/**
	 * 根据传入的service，选择需要清除缓存的数据源
	* @Title: removeMemCacheChooseServiceName 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param service  参数说明 
	* @return void    返回类型 
	* @throws
	 */
	private void removeMemCacheChooseServiceName(TService service){
		if(null != service){
		    TService baseService=null;
		    Integer baseServiceId=null;
			if("base".equals(service.getType()) || "weixin".equals(service.getType())){
			    baseServiceId=service.getServiceID();
			    baseService=service;
			}else{
			    baseServiceId=service.getBaseServiceID();
			    baseService=tServiceDao.getTServiceByServiceId(baseServiceId);
			}
			//先删除基础高级数据源的memcached的缓存和重载系统内部的缓存
            removeMemCacheServiceAndInsideCache(baseService);
            List<TService> tServiceList=tServiceDao.getTServiceInfoByBaseserviceid(baseServiceId);
            for(TService ts:tServiceList){
                //清除高级数据源的memcached的缓存和重载系统内部的缓存
                removeMemCacheServiceAndInsideCache(ts);
            }
		}
	}
	
	/**
	 * 根据Tservice清除memcache中与tservice相关的缓存
	* @Title: removeMemCacheServiceName 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param serviceName  参数说明 
	* @return void    返回类型 
	* @throws
	 */
	private void removeMemCacheServiceAndInsideCache(TService service){
		//同时清除该数据源对应的xml样式缓存
		removeDataSourceCacheMap(service.getName());
		String serviceNameKey=memcacheAction.createCacheKey(service.getName());
		Set<String> serviceKeySet=(Set<String>)memcacheAction.getCache(serviceNameKey);
		if(null!=serviceKeySet){
			for(String key:serviceKeySet){
				memcacheAction.removeCache(key);
			}
			memcacheAction.removeCache(serviceNameKey);
		}
		//删除系统内部的缓存
		Services.setService(service);
	}

    /*
    * Title: reloadRuleDbConfig
    * Description: 
    * @param dbId 
    * @see com.meiqi.dsmanager.action.IPushAction#reloadRuleDbConfig(java.lang.String) 
    */
    @Override
    public void reloadRuleDbConfig(String dbId) {
        Services.reloadDbConfig(dbId);
    }
    @Override
    public void reloadWxApiInfo() {
    	Services.reloadWxApiInfo();
    }

}
