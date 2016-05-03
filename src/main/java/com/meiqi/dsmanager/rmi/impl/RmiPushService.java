package com.meiqi.dsmanager.rmi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.annotation.RemoteService;
import org.springframework.remoting.annotation.RmiServiceProperty;
import org.springframework.remoting.annotation.ServiceType;

import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.dsmanager.common.CommonUtil;
import com.meiqi.dsmanager.rmi.IRmiPushService;
import com.meiqi.dsmanager.util.DataUtil;

@RemoteService(serviceInterface = IRmiPushService.class, serviceType = ServiceType.RMI)
@RmiServiceProperty(registryPort = 1022)
public class RmiPushService implements IRmiPushService {

	@Autowired
	private IPushAction pushAction;

	@Override
	public void addService(String serName) {
		try {
			serName = CommonUtil.getDecodeContent(serName);
			pushAction.addService(serName);
		} catch (Exception e) {
		}
	}

	@Override
	public void updateService(String serName) {
		try {
			serName = CommonUtil.getDecodeContent(serName);
			pushAction.updateService(serName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteService(String jsonTService) {
		try {
			TService tService = DataUtil.parse(CommonUtil.getDecodeContent(jsonTService), TService.class);
			pushAction.deleteService(tService);
		} catch (Exception e) {
		}

	}



    /*
     * 刷新对应的dbId的配置的数据库相关信息
     * Title: reloadRuleDbConfig Description: o
     * 
     * @param dbId
     * 
     * @return
     * 
     * @see
     * com.meiqi.dsmanager.rmi.IRmiPushService#reloadRuleDbConfig(java.lang.
     * String)
     */
    @Override
    public String reloadRuleDbConfig(String dbId) {
        pushAction.reloadRuleDbConfig(dbId);
        return "success";
    }
    @Override
    public String reloadWxApiInfo() {
        pushAction.reloadWxApiInfo();;
        return "success";
    }
}
