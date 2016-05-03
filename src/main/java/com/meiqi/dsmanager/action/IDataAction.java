package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.entity.DataSources;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;


public interface IDataAction {
	
	public String getDatas(DsManageReqInfo reqInfo,String keyContent);
	
	public String getData(DsManageReqInfo reqInfo);
	
	public String getData(DsManageReqInfo reqInfo,String keyContent);
	
	public RuleServiceResponseData getData(DsManageReqInfo reqInfo, DataSources dataSource);
	
	public DataSources getDataSource(String dsName, String styleSn);

    /** 
    * @Title: getInnerData 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param dsReqInfoTmp
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws 
    */
    public String getInnerData(DsManageReqInfo dsReqInfoTmp);
}
