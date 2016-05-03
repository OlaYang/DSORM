package com.meiqi.dsmanager.action;

import javax.servlet.http.HttpServletRequest;

import com.meiqi.app.pojo.dsm.AppRepInfo;

public interface IAuthAction {
    boolean validateAuth(AppRepInfo appRepInfo, String auth, String platName);



    boolean validateAuthForPc(HttpServletRequest request, String serviceName);
}
