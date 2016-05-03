/*
 * File name: WebChatAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年9月24日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action.javabin.webchat;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年9月24日
 */
@Service
public class WebChatAction extends BaseAction {
    @Autowired
    private IDataAction dataAction;

    private String      HTTP_BIND_CORS_ALLOW_ORIGIN = SysConfig.getValue("webchat.httpBindCrosAllowOrigin");



    /**
     * 
     * @Title: getLogo
     * @Description: 获取LOGO
     * @param @param request
     * @param @param response
     * @param @param repInfo
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String getLogo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        response.setHeader("Access-Control-Allow-Origin", HTTP_BIND_CORS_ALLOW_ORIGIN);
        Map<String, Object> reqMap = DataUtil.parse(repInfo.getParam());
        ResponseInfo resqInfo = new ResponseInfo();
        if (!reqMap.containsKey("workgroup")) {
            resqInfo.setCode("1");
            resqInfo.setDescription("workgroup 为空！");
            return null;
        }

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("YJG_HSV1_OnLineArticle");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setParam(reqMap);
        String result = dataAction.getData(dsReqInfo);
        LogUtil.info("WebChatAction:getLogo:" + result);

        return result;
    }



    /**
     * 
     * @Title: getAd
     * @Description: 获取广告
     * @param @param request
     * @param @param response
     * @param @param repInfo
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    public String getAd(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        response.setHeader("Access-Control-Allow-Origin", HTTP_BIND_CORS_ALLOW_ORIGIN);
        Map<String, Object> reqMap = DataUtil.parse(repInfo.getParam());
        ResponseInfo resqInfo = new ResponseInfo();
        if (!reqMap.containsKey("workgroup")) {
            resqInfo.setCode("1");
            resqInfo.setDescription("workgroup 为空！");
            return null;
        }

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("YJG_HSV1_OnLine");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setParam(reqMap);
        String result = dataAction.getData(dsReqInfo);
        LogUtil.info("WebChatAction:getAd:" + result);

        return result;
    }
}
