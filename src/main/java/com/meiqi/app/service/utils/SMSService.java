package com.meiqi.app.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.dsm.RequestBaseData;
import com.meiqi.app.pojo.dsm.RequestParam;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * 
 * @ClassName: SmsService
 * @Description: 短信服务
 * @author 杨永川
 * @date 2015年6月4日 上午9:40:54
 *
 */
public class SMSService {
    private static final Logger LOG                            = Logger.getLogger(SMSService.class);
    private static final String AUTH_KEY                       = AppSysConfig.getValue(ContentUtils.AUTH_KEY);
    private static final String SMS_SEND_SERVICENAME           = AppSysConfig
                                                                       .getValue(ContentUtils.SMS_SEND_SERVICENAME);
    private static final String VERIFICATION_CODE_TEMPLATE_ID_ = "verification_code_template_id_";
    private static final String INVITEID_CODE_TEMPLATE_ID      = "inviteid_code_template_id";
    private static final String DISCOUNT_CODE_TEMPLATE_ID      = "discount_code_template_id";
    private static final String STOREINFO_TEMPLATE_ID          = "storeInfo_template_id";
    private static final String PLACE_ORDER_TEMPLATE_ID        = "place_order_template_id";

    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private IMemcacheAction memcacheService;
    @Autowired
    private IDataAction     dataAction;
    
    /**
     * 
     * @Title: sendVerificationCode
     * @Description:发送验证码
     * @param @param phone
     * @param @param code
     * @param @param userName
     * @param @param type
     * @param @return
     * @return boolean
     * @throws
     */
    /*public static void sendVerificationCode(String phone, Map<String, Object> templateParam, byte type) {
        LOG.info("Function:sendVerificationCode.Start.");
        String templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendVerificationCode.End.");
    }*/
    
    /**
     * 
     * @Title: updatePreCode
     * @Description: TODO(将以前发送的验证码都设定为失效)
     * @param @param phoneNum
     * @param @param templateId
     * @param @param webSite
     * @param @param smsType
     * @param @return 参数说明
     * @return BaseRespInfo 返回类型
     * @throws
     */
    private BaseRespInfo updatePreCode(String phoneNum, Long templateId, Integer webSite, Integer smsType) {
        BaseRespInfo response = new BaseRespInfo();
        // 再次发送前 获取之前发送的有效验证码
        DsManageReqInfo dsInfo = new DsManageReqInfo();
        dsInfo.setServiceName("YJG_BUV1_phone_code");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receive_phone", phoneNum);
        map.put("template_id", templateId);
        map.put("web_site", webSite);
        map.put("type", smsType);// 业务类型
        dsInfo.setParam(map);
        ResponseInfo resp = DataUtil.parse(dataAction.getData(dsInfo), ResponseInfo.class);
        List<Map<String, String>> list = resp.getRows();

        if (null != list && list.size() > 0) {
            // 修改所有有效的验证码为无效
            DsManageReqInfo actionReqInfo = new DsManageReqInfo();
            actionReqInfo.setServiceName("MUSH_Offer");
            String serviceName = "MeiqiServer_ecs_messaging_send";
            List<Action> actions = new ArrayList<Action>();
            for (Map<String, String> map0 : list) {
                Action action = new Action();
                action.setType("U");
                action.setServiceName(serviceName);
                Map<String, Object> set = new HashMap<String, Object>();
                set.put("is_valid", "0");
                action.setSet(set);
                Where where = new Where();
                where.setPrepend("and");
                List<SqlCondition> cons = new ArrayList<SqlCondition>();
                SqlCondition con = new SqlCondition();
                con.setKey("id");
                con.setOp("=");
                con.setValue(map0.get("id"));
                cons.add(con);
                where.setConditions(cons);
                action.setWhere(where);
                actions.add(action);
            }
            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("actions", actions);
            param1.put("transaction", 1);
            actionReqInfo.setParam(param1);
            SetServiceResponseData actionResponse = null;
            String res1 = mushroomAction.offer(actionReqInfo);
            actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
            response.setCode(actionResponse.getCode());
            response.setDescription(actionResponse.getDescription());
        }
        return response;
    }



    /**
     * 
     * @Title: sendInviteIdCode
     * @Description:发送邀约码
     * @param @param phone
     * @param @param code
     * @param @param userName
     * @return void
     * @throws
     */
    /*public static void sendInviteIdCode(String phone, String code, String userName) {
        LOG.info("Function:sendInviteIdCode.Start.");
        String templateId = AppSysConfig.getValue(INVITEID_CODE_TEMPLATE_ID);
        // 模板里面使用的参数
        Map<String, Object> templateParam = new HashMap<String, Object>();
        templateParam.put("user", userName);
        templateParam.put("code", code);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendInviteIdCode.End.");
    }*/
    
    /**
     * 
     * @Title: sendInviteIdCode
     * @Description:发送邀约码
     * @param @param phone
     * @param @param code
     * @param @param userName
     * @return void
     * @throws
     */
    /*public static void sendInviteIdCode(String phone, String code, String userName, String templateName) {
        LOG.info("Function:sendInviteIdCode.Start.");
        String templateId = AppSysConfig.getValue(templateName);
        // 模板里面使用的参数
        Map<String, Object> templateParam = new HashMap<String, Object>();
        templateParam.put("user", userName);
        templateParam.put("discount_code", code);
        templateParam.put("invite_code", code);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendInviteIdCode.End.");
    }*/


    /**
     * 
     * @Title: sendDiscountCode
     * @Description:发送折扣码
     * @param @param phone
     * @param @param code
     * @return void
     * @throws
     */
    /*public static void sendDiscountCode(String phone, Map<String, Object> templateParam) {
        LOG.info("Function:sendDiscountCode.Start.");
        String templateId = AppSysConfig.getValue(DISCOUNT_CODE_TEMPLATE_ID);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendDiscountCode.End.");
    }*/



    /**
     * 
     * @Title: sendStoreInfo
     * @Description: 发送店铺信息
     * @param 参数说明
     * @return void 返回类型
     * @throws
     */
    public static void sendStoreInfo(String phone, Map<String, Object> templateParam) {
        LOG.info("Function:sendStoreInfo.Start.");
        String templateId = AppSysConfig.getValue(STOREINFO_TEMPLATE_ID);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendStoreInfo.End.");
    }



    /**
     * 
     * 用户下单后 发送下单成功短信
     *
     * @param phone
     * @param templateParam
     */
    public static void sendPlaceOrder(String phone, Map<String, Object> templateParam) {
        LOG.info("Function:sendPlaceOrder.Start.");
        String templateId = AppSysConfig.getValue(PLACE_ORDER_TEMPLATE_ID);
        RequestParam param = new RequestParam(phone, AUTH_KEY, templateId, templateParam);
        RequestBaseData requestBaseData = new RequestBaseData(SMS_SEND_SERVICENAME, param);
        // 调用DSM
        new DSMService(JsonUtils.objectFormatToString(requestBaseData), "setData").start();
        LOG.info("Function:sendPlaceOrder.End.");
    }

}
