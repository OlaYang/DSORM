package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;


public interface ISolrAction {

	public static  final String DEF_RESULT_NAME="defResult";
	/**
	 * 索引搜索的服务接口
	 * @param reqInfo 请求参数
	 * @return
	 */
	public String query(DsManageReqInfo reqInfo);

}
