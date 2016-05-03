package com.meiqi.app.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.app.action.BaseAction;
import com.meiqi.app.common.utils.AppActionMappingUtil;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.dsmanager.action.IAuthAction;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;

/**
 *
 * @ClassName: AppServiceController
 * @Description:
 * @author 杨永川
 * @date 2015年6月30日 下午2:11:19
 *
 */
@RequestMapping("/service")
@Controller
public class AppServiceController {

    @Autowired
    private IAuthAction authAction;



    @RequestMapping(value = "/app", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String appService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long begin = System.currentTimeMillis();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String content = "";
        if (HttpMethod.GET.equals(method)) {
            content = request.getParameter("param");
        } else {
            content = DataUtil.inputStream2String(request.getInputStream());
        }
        // 处理编码 和特殊字符
        String decodeContent = StringUtils.decode(content);
        AppRepInfo appRepInfo = DataUtil.parse(decodeContent, AppRepInfo.class);
        if (null == appRepInfo) {
            LogUtil.error("请求参数不能为空!");
            return null;
        }
        // 获取header 参数
        appRepInfo.setHeader(BaseAction.getHeader(request));
        // 请求授权验证
        boolean validateAuth = authAction.validateAuth(appRepInfo,
                appRepInfo.getHeader().get(ContentUtils.AUTHORIZATION).toString(), "app");
        if (!validateAuth) {
            return JsonUtils.getAuthorizationErrorJson();
        }

        // 获取service实体类
        BaseAction executeAction = AppActionMappingUtil.getAppService(appRepInfo.getUrl());
        if (null == executeAction) {
            LogUtil.error("App service mapping error.");
            return JsonUtils.getErrorJson("url error,App service mapping error!", null);
        }
        // try error
        String resultData = "";
        try {
            //LogUtil.debug("appService reqInfo: - " + JsonUtils.objectFormatToString(appRepInfo));
            resultData = executeAction.execute(request, response, appRepInfo);
        } catch (Exception e) {
            StringBuffer sb = new StringBuffer();  
            StackTraceElement[] stackArray = e.getStackTrace();  
            for (int i = 0; i < stackArray.length; i++) {  
                StackTraceElement element = stackArray[i];  
                sb.append(element.toString() + "\n");
            }
            
            LogUtil.error("app server error,请重试! Error:" + sb.toString());
            resultData = JsonUtils.getErrorJson("app server error:" + e.getMessage(), null);
        }
        
        long end = System.currentTimeMillis();
        long time = end - begin;
        if (time > 500) {
            LogUtil.error("appService reqInfo slow:" + content + ",execute time:" + time);
        } else {
            LogUtil.info("appService reqInfo:" + content + ",execute time:" + time);
        }
        
        return resultData;

    }

}
