/*
* File name: UserService.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			luzicong		2015年11月18日
* ...			...			...
*
***************************************************/

package com.meiqi.openservice.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.user.User;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.util.ContentUtils;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.service.IUserService;

/**
 * <class description>
 *		
 * @author: luzicong
 * @version: 1.0, 2015年11月18日
 */
@Service
public class UserService implements IUserService {
    @Autowired
    private IDataAction         dataAction;
    
    @Autowired
    private IMushroomAction     mushroomAction;
    
    private static final Logger LOG      = Logger.getLogger(UserService.class);
    

    /**
     * @see com.meiqi.openservice.service.IUserService#getUserByUserId(long)
     */
    @Override
    public User getUserByUserId(long userId) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("user_id", userId);
        return getUser(queryParam);
    }

    
    private User getUser(Map<String, Object> queryParam) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_ecsusers");
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) 
                && responseData.getRows() != null
                && responseData.getRows().size() != 0) {
            User user = new User();
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("user_id"))) {
                user.setUserId(Integer.parseInt(jsonMap.get("user_id")));
            }
            user.setAvatar(jsonMap.get("avatar_1"));
            if (!StringUtils.isBlank(jsonMap.get("role_id"))) {
                user.setRoleId(Integer.parseInt(jsonMap.get("role_id")));
            }
            user.setRoleName(jsonMap.get("role_name"));
            if (!StringUtils.isBlank(jsonMap.get("sex"))) {
                user.setSex(Integer.parseInt(jsonMap.get("sex")));
            }
            // user.set(jsonMap.get("city_name"));
            user.setUserName(jsonMap.get("name"));
            user.setInviteCode(jsonMap.get("code"));
            user.setPassword(jsonMap.get("password"));
            user.setEmail(jsonMap.get("email"));

            return user;
        } else {
            LOG.error("getUser fail. data:" + data);
        }

        return null;
    }
    
    /*
     * 绑定邮件相关验证码codeType 定义： 
     * "bindEmail" + bindEmailStep + userType 示例：bindEmail11
     */
    @Override
    public String getBindEmailCodeType(byte bindEmailStep, int web_site) {
        return Constants.BIND_EMAIL_STEP + bindEmailStep + web_site;
    }

    
    public Map<String, String> getRow(DsManageReqInfo dsReqInfo) throws Exception {
        String serviceName = dsReqInfo.getServiceName();
        dsReqInfo.getParam().put("num", ContentUtils.ONE);
        String result = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(result, RuleServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
            LOG.error(serviceName + " - reqInfo: " + JSON.toJSONString(dsReqInfo) + ", result: " + result);
            throw new Exception(serviceName + " fail - " + responseData.getDescription());
        }
        if (responseData.getRows() == null || responseData.getRows().size() == 0) {
            return null;
        }
        
        return responseData.getRows().get(0);
    }

    public void addAnonymousUser(String deviceId, int from, int site_id) throws Exception {
        String serviceName = "test_ecshop_ecs_users";
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", deviceId);
        set.put("device_id", deviceId);
        set.put("sex", Constants.UserSex.male.ordinal());
        set.put("region_id", ContentUtils.ONE);
        set.put("role_id", Constants.UserRoleId.ANONYMOUS);
        // reg_time（注册时间）
        set.put("reg_time", "$UnixTime");
        set.put("last_login", "$UnixTime");
        // from（来源 0-优家购WEB 1-爱有窝WEB 2-优家购安卓 3-优家购IPAD 4-优家购IPHONE 5-优家购M站 6-爱有窝M站 ）
        set.put("from", from);
        action.setSet(set);
        
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);

        String res = mushroomAction.offer(actionReqInfo);
        LOG.info(serviceName + " - reqInfo: " + JSON.toJSONString(actionReqInfo) + " result: " + res);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            LOG.error(serviceName + " - reqInfo: " + JSON.toJSONString(actionReqInfo) + " result: " + res);
            throw new Exception("addAnonymousUser fail - " + actionResponse.getDescription());
        }
    }


    public String getAppAccessToken(int site_id) throws Exception {
        Map<String, Object> param = new HashMap();
        param.put("site_id", site_id);
        param.put("parent_code", ContentUtils.SHOPCONFIG_CODE_APPCONFIG);
        param.put("code", SysConfig.getValue(ContentUtils.REQUEST_HEADER_ACCESSTOKEN));
        
        return getShopConfig(param);
    }
    
    private String getShopConfig(Map<String, Object> queryParam) throws Exception {
        String serviceName = "YJG_BUV1_ShopCibfig";
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName(serviceName);
        dsReqInfo.setParam(queryParam);
        dsReqInfo.setNeedAll("1");
        Map<String, String> jsonMap = getRow(dsReqInfo);
        if (null == jsonMap) {
            return null;
        }
        String shopConfig = jsonMap.get("value");
        return shopConfig;
    }

}
