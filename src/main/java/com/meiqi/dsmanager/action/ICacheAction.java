package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.po.ResponseBaseData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;





public interface ICacheAction {

	/**
	 * 缓存前处理
	 * @param String
	 * @return
	 */
	public boolean preCache(DsManageReqInfo reqInfo, DataSources dataSource, ResponseBaseData responseData);
	
	/**
     * 缓存后处理
     * @param String
     * @return 
     */
    public void postCache(DsManageReqInfo reqInfo,ResponseBaseData responseData, DataSources dataSource,String keyContent,String formatString);
	
	
}
