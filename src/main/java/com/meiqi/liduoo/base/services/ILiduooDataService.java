package com.meiqi.liduoo.base.services;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.rule.ServiceReqInfo;

public interface ILiduooDataService {
	String getJsonData(ServiceReqInfo reqInfo);

	List<Map> getListData(ServiceReqInfo reqInfo);

	Map<String,Object> getOneRow(ServiceReqInfo reqInfo);
}
