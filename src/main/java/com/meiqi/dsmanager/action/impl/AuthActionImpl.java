package com.meiqi.dsmanager.action.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.EncodeAndDecodeUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.dsmanager.action.IAuthAction;
import com.meiqi.dsmanager.cache.CacheItem;
import com.meiqi.dsmanager.cache.CachePool;
import com.meiqi.dsmanager.po.dsmanager.AuthBean;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.config.SysConfig;

@Service
public class AuthActionImpl implements IAuthAction {

    /**
     * 
     * @Title: validateAuth
     * @Description:
     * @param @param auth
     * @param @return
     * @throws
     */
    @Override
    public AuthBean validateAuth(AppRepInfo appRepInfo, String authorization, String platName) {
    	AuthBean authBean=new AuthBean();
        if (!"true".equals(AppSysConfig.getValue("is_validate_auth"))) {
        	authBean.setState(true);
            return authBean;
        }
        String url = appRepInfo.getUrl();

        if (StringUtils.isBlank(url) || url.contains("clearAndReadProperties")) {
        	authBean.setState(true);
            return authBean;
        }

        if (StringUtils.isBlank(url)) {
            url = "";
        }
        try {
            // 获取授权证书
            if (!StringUtils.isBlank(authorization)) {
                authorization = EncodeAndDecodeUtils.decodeStrBase64(authorization);
                String[] authorizationArray = authorization.split(ContentUtils.UNDERLINE);
                if (!CollectionsUtils.isNull(authorizationArray) && authorizationArray.length == 3) {
                    // 明文
                    String accessToken = AppSysConfig.getValue(ContentUtils.REQUEST_HEADER_ACCESSTOKEN);
                    // 秘钥
                    String key = AppSysConfig.getValue(ContentUtils.REQUEST_HEADER_KEY);
                    String md5 = EncodeAndDecodeUtils.encodeStrMD5(accessToken + authorizationArray[1] + key
                            + authorizationArray[2]);
                    if (authorizationArray[0].equals(md5)) {
                        long userId = Long.parseLong(authorizationArray[1]);
                        // 获取匿名用户时不检查userid>0
                        if (url.contains("anonymous")) {
                            return checkRequestTime(platName + userId + url, "app_request_interval",authBean);
                        }
                        if (userId > 0) {
                            return checkRequestTime(platName + userId + url, "app_request_interval",authBean);
                        }else{
                        	authBean.setState(false);
                            authBean.setDescription("解析出的用户信息有误！");
                            return authBean;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("请求授权验证失败！");
            authBean.setState(false);
            authBean.setDescription(e.getMessage());
            return authBean;
        }
        authBean.setState(false);
        authBean.setDescription("没有找到auth参数！");
        return authBean;
    }



    /**
     * 
     * @Title: validateAuth
     * @Description:
     * @param @param auth
     * @param @return
     * @throws
     */
    @Override
    public AuthBean validateAuthForPc(HttpServletRequest request, String serviceName) {
    	AuthBean authBean=new AuthBean();
        if (!"true".equals(AppSysConfig.getValue("is_validate_auth"))) {
        	authBean.setState(true);
            return authBean;
        }
        String sessionId = request.getSession().getId();
        String ip = getIp(request);

        try {
        	authBean=checkRequestTime("pc" + sessionId + serviceName, "pc_request_interval",authBean);
        	if(true==authBean.isState()){
        		authBean=checkRequestTime("pc" + ip + serviceName, "pc_request_interval",authBean);
        	}
            return authBean;
        } catch (Exception e) {
        	authBean.setState(false);
        	authBean.setDescription(e.getMessage());
            LogUtil.error("请求授权验证失败！");
            return authBean;
        }
    }
    
    public AuthBean validateAuthForApp(RepInfo repInfo, String authorization, String platName) {
        AuthBean authBean=new AuthBean();
        if (!"true".equals(SysConfig.getValue("is_validate_auth"))) {
            authBean.setState(true);
            return authBean;
        }

        try {
            // 获取授权证书
            if (!StringUtils.isEmpty(authorization)) {
                authorization = EncodeAndDecodeUtils.decodeStrBase64(authorization);
                String[] authorizationArray = authorization.split(ContentUtils.UNDERLINE);
                if (!CollectionsUtils.isNull(authorizationArray) && authorizationArray.length == 3) {
                    // 明文
                    String accessToken = SysConfig.getValue(ContentUtils.REQUEST_HEADER_ACCESSTOKEN);
                    // 秘钥
                    String key = SysConfig.getValue(ContentUtils.REQUEST_HEADER_KEY);
                    String md5 = EncodeAndDecodeUtils.encodeStrMD5(accessToken + authorizationArray[1] + key
                            + authorizationArray[2]);
                    if (authorizationArray[0].equals(md5)) {
                        long userId = Long.parseLong(authorizationArray[1]);
                        // 获取匿名用户时不检查userid>0
                        if (repInfo.getAction().contains("anonymous") || userId > 0) {
                            return checkRequestTime(platName + userId + repInfo.getAction() + repInfo.getMethod(), "app_request_interval",authBean);
                        } else{
                            authBean.setState(false);
                            authBean.setDescription("解析出的用户信息有误！");
                            return authBean;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("请求授权验证失败！");
            authBean.setState(false);
            authBean.setDescription(e.getMessage());
            return authBean;
        }
        authBean.setState(false);
        authBean.setDescription("没有找到auth参数！");
        return authBean;
    }



    /**
     * 
     * 检查请求时间间隔
     *
     * @param cacheKey
     * @return
     */

    private AuthBean checkRequestTime(String cacheKey, String intervalKey,AuthBean authBean) {
        // 本次请求time
        long submitTime = DateUtils.getTime();
        Date nowDate = new Date(submitTime);
        // 从cache获取上次请求记录
        CacheItem cacheItem = (CacheItem) CachePool.getInstance().getAuthCacheItem(cacheKey);
        if (null == cacheItem) {
            cacheItem = new CacheItem(null, 1);
            cacheItem.setCreateOrUpdateTime(nowDate);
            CachePool.getInstance().putAuthCacheItem(cacheKey, cacheItem);
            authBean.setState(true);
            return authBean;
        }
        // 最后一次请求time
        long lastRequestTime = cacheItem.getCreateOrUpdateTime().getTime();
        // 请求time间隔
        long requestInterval = StringUtils.StringToLong(AppSysConfig.getValue(intervalKey));
        if (submitTime - lastRequestTime > requestInterval) {
            // 修改 cache time
            cacheItem.setCreateOrUpdateTime(nowDate);
            authBean.setState(true);
            return authBean;
        } else {
            // 修改 cache time
            cacheItem.setCreateOrUpdateTime(nowDate);
            authBean.setState(true);
            authBean.setDescription("请求间隔："+(submitTime - lastRequestTime));
            return authBean;
        }
//    	return true;
    }



    /**
     * 
     * @Title: getIp
     * @Description:获取访问者IP 
     *                      在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效
     *                      。
     *                      本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP
     *                      (用,分割)， 如果还不存在则调用Request .getRemoteAddr()。
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader(ContentUtils.X_REAL_IP);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader(ContentUtils.X_FORWARDED_FOR);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(ContentUtils.COMMA);
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }



	@Override
	public AuthBean validateAuth1(AppRepInfo appRepInfo, String authorization, String platName) {
		AuthBean authBean=new AuthBean();
        if (!"true".equals(AppSysConfig.getValue("is_validate_auth"))) {
        	authBean.setState(true);
            return authBean;
        }
        String url = appRepInfo.getUrl();

        if (StringUtils.isBlank(url)) {
            url = "";
        }
        try {
            // 获取授权证书
            if (!StringUtils.isBlank(authorization)) {
                authorization = EncodeAndDecodeUtils.decodeStrBase64(authorization);
                String[] authorizationArray = authorization.split(ContentUtils.UNDERLINE);
                if (!CollectionsUtils.isNull(authorizationArray) && authorizationArray.length == 3) {
                    // 明文
                    String accessToken = AppSysConfig.getValue(ContentUtils.REQUEST_HEADER_ACCESSTOKEN);
                    // 秘钥
                    String key = AppSysConfig.getValue(ContentUtils.REQUEST_HEADER_KEY);
                    String md5 = EncodeAndDecodeUtils.encodeStrMD5(accessToken + authorizationArray[1] + key
                            + authorizationArray[2]);
                    if (authorizationArray[0].equals(md5)) {
                        long userId = Long.parseLong(authorizationArray[1]);
                        // 获取匿名用户时不检查userid>0
                        if (url.contains("anonymous")) {
                            return checkRequestTime(platName + userId + url, "app_request_interval",authBean);
                        }
                        if (userId > 0) {
                            return checkRequestTime(platName + userId + url, "app_request_interval",authBean);
                        }else{
                        	authBean.setState(false);
                            authBean.setDescription("解析出的用户信息有误！");
                            return authBean;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("请求授权验证失败！");
            authBean.setState(false);
            authBean.setDescription(e.getMessage());
            return authBean;
        }
        authBean.setState(false);
        authBean.setDescription("没有找到auth参数！");
        return authBean;
	}
}
