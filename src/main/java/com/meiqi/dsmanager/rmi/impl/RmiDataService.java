package com.meiqi.dsmanager.rmi.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.data.dao.ITServiceDao;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.rmi.IRmiDataService;
import com.meiqi.dsmanager.util.DataUtil;

@RemoteService(serviceInterface = IRmiDataService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiDataService implements IRmiDataService
{

	private static final Logger LOG               = Logger.getLogger(RmiSolrService.class);
	@Autowired
    private IDataAction dataAction;
    @Autowired
	private ITServiceDao tServiceDao;
	
    @Override
    public String getData(String content)
    {
        String decodeContent = CommonUtil.getDecodeContent(content);
        DsManageReqInfo dsReqInfo = DataUtil.parse(decodeContent, DsManageReqInfo.class);
        String resultData = dataAction.getData(dsReqInfo,decodeContent);
        //LOG.info("resultData:"+resultData);
        return resultData;
    }
}
