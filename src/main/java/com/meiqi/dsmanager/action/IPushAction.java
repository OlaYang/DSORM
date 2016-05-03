package com.meiqi.dsmanager.action;

import com.meiqi.data.entity.TService;

/*
 *推送action
 */
public interface IPushAction {
	/**
	 * 新增一个服务到内存中
	 */
	public void addService(String serName) throws Exception;
	
	/**
	 * 在内存中更新一个服务
	 */
	public void updateService(String serName) throws Exception;
	
	/**
	 * 从内存中删除一个服务
	 */
	public void deleteService(TService tService) throws Exception;

    /** 
    * @Title: reloadRuleDbConfig 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param dbId  参数说明 
    * @return void    返回类型 
    * @throws 
    */
    public void reloadRuleDbConfig(String dbId);
    
    /**
     * 更新微信API相关配置
     */
    public void reloadWxApiInfo();
}
