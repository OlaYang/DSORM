package com.meiqi.dsmanager.action;

import javax.servlet.http.HttpServletRequest;

import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.dsmanager.po.dsmanager.AuthBean;
import com.meiqi.openservice.bean.RepInfo;

public interface IAuthAction {
    AuthBean validateAuth(AppRepInfo appRepInfo, String auth, String platName);

    /**
     * 用于发送短信的老方法对于APP的校验
     * @param appRepInfo
     * @param auth
     * @param platName
     * @return
     */
    AuthBean validateAuth1(AppRepInfo appRepInfo, String auth, String platName);

    AuthBean validateAuthForPc(HttpServletRequest request, String serviceName);

    AuthBean validateAuthForApp(RepInfo repInfo, String authorization, String string2);
}
