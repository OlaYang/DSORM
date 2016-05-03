package com.meiqi.app.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.SMSCode;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.DiscountCodeService;
import com.meiqi.app.service.StoreService;
import com.meiqi.app.service.UsersService;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.action.javabin.ip.IpAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.threads.CrmClientTableAndBrowseTrackThread;
import com.meiqi.thread.ThreadHelper;

@Service
public class SmsCodeAction extends BaseAction {
    @Autowired
    private DiscountCodeService discountCodeService;
    @Autowired
    private StoreService        storeService;
    @Autowired
    private UsersService        usersService;
    @Autowired
    private ThreadHelper indexTheadHelper;
    @Autowired
    private SmsAction     smsAction;
    @Autowired
    private IpAction ipAction;
    
    private static final String VERIFICATION_CODE_TEMPLATE_ID_ = "verification_code_template_id_";
    private static final String INVITEID_CODE_TEMPLATE_ID      = "inviteid_code_template_id";
    private static final String DISCOUNT_CODE_TEMPLATE_ID      = "discount_code_template_id";
    private static final String STOREINFO_TEMPLATE_ID          = "storeInfo_template_id";
//    private static final String PLACE_ORDER_TEMPLATE_ID        = "place_order_template_id";



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String param = appRepInfo.getParam();
        SMSCode smsCode = DataUtil.parse(param, SMSCode.class);
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        smsCode.getParam().put("userId", userId);
        return sendSMSCode(request, response, smsCode);
    }



    /**
     * 
     * @Title: sendSMSCode
     * @Description:
     * @param @param smsCode
     * @param @return
     * @return String
     * @throws
     */
    public String sendSMSCode(HttpServletRequest request, HttpServletResponse response, SMSCode smsCode) {
        // 验证参数
        String validate = validateSmsCode(smsCode);
        if (!StringUtils.isBlank(validate)) {
            return validate;
        }
        byte type = smsCode.getType();
        String phone = smsCode.getPhone();
        String templateId = null;
        Users user = null;

        if (type == Constants.VerificationCodeType.discountCode || type == Constants.VerificationCodeType.inviteCode) {
            // 获取U码
            if (null == smsCode.getParam().get("userId")) {
                return JsonUtils.getErrorJson("用户ID 为空", null);
            }
            long userId = Long.parseLong(smsCode.getParam().get("userId").toString());
            user = usersService.getUserByUserId(userId);
            if (null == user) {
                return JsonUtils.getErrorJson("用户为空", null);
            }

            String uCode = user.getInviteCode();
            if (StringUtils.isBlank(uCode)) {
                return JsonUtils.getErrorJson("用户U码为空", null);
            }
            smsCode.getParam().put("discount_code", uCode);
        }

        switch (type) {
        case Constants.VerificationCodeType.register:
            // 0,注册短信
            // 验证电话号码是否被注册
            if (hasUserName(phone)) {
                return JsonUtils.getErrorJson("该手机号已成功入驻优家购，您可直接登录!", null);
            }
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            // return getUserCode(smsCode);
            break;
        case Constants.VerificationCodeType.getBackPwd:
            // 1,找回密码短信
            // 验证电话号码是否被注册
            if (!hasUserName(phone)) {
                return JsonUtils.getErrorJson("该手机未注册!", null);
            }
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            // return getUserCode(smsCode);
            break;
        case Constants.VerificationCodeType.changeBindingPhone:
            // 2,更改手机绑定短信（验证旧手机号）
            // 验证电话号码是否被注册
            if (!hasUserName(phone)) {
                return JsonUtils.getErrorJson("该手机未注册!", null);
            }
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            // return getUserCode(smsCode);
            break;
        case Constants.VerificationCodeType.bankCard:
            // 3,银行卡号绑定短信
            // return getUserCode(smsCode);
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            break;
        case Constants.VerificationCodeType.modifyBindingPhone:
            // 4,更改手机绑定短信（验证新手机号）
            // 验证电话号码是否被注册
            if (hasAllTypeUserName(phone)) {
                return JsonUtils.getErrorJson("该手机已被注册!", null);
            }
            // return getUserCode(smsCode);
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            break;
        case Constants.VerificationCodeType.queryOrderPhone:
            // 5,订单查询短信
            // return getUserCode(smsCode);
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            break;
        case Constants.VerificationCodeType.discountCode:
            // 6, 折扣码
            templateId = AppSysConfig.getValue(DISCOUNT_CODE_TEMPLATE_ID);
            // return sendInviteCode(phone, userId,ym_discount_template_id);
            break;
        case Constants.VerificationCodeType.inviteCode:
            // 7, 邀约码
            int roleId = user.getRoleId();
            if (!(Constants.UserRoleId.COMPANY == roleId || Constants.UserRoleId.PUSHER == roleId)) {
                return JsonUtils.getErrorJson("只有装修公司或地推人员能发送邀约码", null);
            }
            // return sendInviteCode(phone, userId,ym_invite_template_id);
            templateId = AppSysConfig.getValue(INVITEID_CODE_TEMPLATE_ID);
            break;
        case Constants.VerificationCodeType.storeInfoType:
            // 8, 店铺信息
            // result = storeService.sendStoreInfo(userId, phone, msg, smsCode.getParam());
            if (CollectionsUtils.isNull(smsCode.getParam()) || smsCode.getParam().isEmpty()) {
                return JsonUtils.getErrorJson("店铺信息不能空!", null);
            }
            templateId = AppSysConfig.getValue(STOREINFO_TEMPLATE_ID);
            break;
        case Constants.VerificationCodeType.phoneLoginType:
            // 9, 手机验证登陆
            // return addCode(smsCode);
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            break;
        default:
            // 其他短信验证type
            // return addCode(smsCode);
            templateId = AppSysConfig.getValue(VERIFICATION_CODE_TEMPLATE_ID_ + type);
            break;
        }

        RepInfo req = new RepInfo();
        req.setAction("smsAction");
        req.setMethod("send");
        Map<String, Object> param1 = new HashMap();
        param1.put("serviceName", "SMS_Send");
        Map<String, Object> param2 = new HashMap();
        param2.put("phoneNumber", smsCode.getPhone());
        param2.put("auth_key", "5961A30CADB586921AE2364D90A69DFB");
        param2.put("templateId", templateId);
        param2.put("site_id", "0");
        param2.put("webSite", "0");
        param2.put("smsType", String.valueOf(smsCode.getType()));
        param2.put("param", smsCode.getParam());
        param1.put("param", param2);
        req.setParam(JsonUtils.objectFormatToString(param1));
        BaseRespInfo rep = (BaseRespInfo)smsAction.send(request, response, req);
        
        if (DsResponseCodeData.SUCCESS.code.equals(rep.getCode())) {
            //记录体验馆的
            if(type==Constants.VerificationCodeType.storeInfoType){
                //记录crm
                Map<String,Object> params=new HashMap<String, Object>();
                params.put("user_name",smsCode.getPhone());//手机号
                params.put("ADD_time",DateUtils.getSecond());//当前时间戳(10位)
                RepInfo r=new RepInfo();
                r.setParam("{}");
                Object ipMsg=ipAction.getCountryAndArea(request, response, r);
                params.put("ipMsg",ipMsg);//城市名称
                params.put("collect_event","发送体验馆");//采集事件
                params.put("collect_content",smsCode.getParam().get("experience_hall_name"));//采集内容
                params.put("type",2);//类型 1 着陆 2 转化
                params.put("cookie",smsCode.getParam().get("userId").toString());//记录user_id
                Map<String,Object> header=BaseAction.getHeader(request);
                params.put("header",header);//(来源平台 1 PC端 2 M站 3 APP-Android 4 iPad 5 APP-iOS 6微信)
                params.put("collect_place","商品详情页-到店体验");//(采集位置)：固定记为“商品详情页-到店体验 ”
                CrmClientTableAndBrowseTrackThread thread=new CrmClientTableAndBrowseTrackThread(params);
                indexTheadHelper.execute(thread);
            }
            return JsonUtils.getSuccessJson(null);
        }
        return JsonUtils.getErrorJson(rep.getDescription(), null);
    }



    /**
     * 
     * 发送短息 验证码 0,注册短信 1,找回密码短信 2,更改手机绑定短信（验证旧手机号） 3,银行卡号绑定短信 4,更改手机绑定短信（验证新手机号）
     * 5,订单查询短信
     *
     * @param smsCode
     * @return
     */
    /*private String getUserCode(SMSCode smsCode) {
        LOG.info("Function:getUserCode.Start.");
        String codeJson = JsonUtils.getErrorJson("获取验证码失败，请重试.", null);
        String phone = smsCode.getPhone();
        int type = smsCode.getType();
        if (StringUtils.isBlank(phone)) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        // type == 0为注册，需要验证电话是否已经被注册
        if (0 == type) {
            // 验证电话号码是否被注册
            boolean hasPhone = hasUserName(phone);
            if (hasPhone) {
                return JsonUtils.getErrorJson("该手机号已成功入驻优家购，您可直接登录!", null);
            }
        }

        if (4 == type) {
            // 验证电话号码是否被注册
            boolean hasPhone = hasAllTypeUserName(phone);
            if (hasPhone) {
                return JsonUtils.getErrorJson("该手机已被注册!", null);
            }
        }
        // type==1 找回密码 type == 2 修改绑定手机(旧手机验证)
        if (1 == type || 2 == type) {
            // 验证电话号码是否被注册
            boolean hasPhone = hasUserName(phone);
            if (!hasPhone) {
                return JsonUtils.getErrorJson("该手机未注册!", null);
            }
        }
        
        String verCode = usersService.addCode(phone, (byte) type, smsCode.getParam());
        if (null != verCode) {
            codeJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:getUserCode.End.");
        return codeJson;
    }*/



    /**
     * 
     * @Title: validateSmsCode
     * @Description: 验证smscode信息
     * @param @param smsCode
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    private String validateSmsCode(SMSCode smsCode) {
        if (null == smsCode) {
            return JsonUtils.getErrorJson("请正确输入参数!", null);
        }
        if (StringUtils.isBlank(smsCode.getPhone())) {
            return JsonUtils.getErrorJson("电话号码不能为空!", null);
        }

        return null;
    }



    /**
     * 
     * @Title: addInviteCode
     * @Description:发送邀约码短信
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    /*private String sendInviteCode(String receivePhone, long userId) {
        LOG.info("Function:sendInviteCode.Start.");
        String ret = null;
        boolean result = usersService.checkSendEnabled(userId, receivePhone);
        if (!result) {
            return JsonUtils.getErrorJson("发送过于频繁，请稍后再试!", null);
        }
        try {
            usersService.sendInviteCode(userId, receivePhone);
            ret = JsonUtils.getSuccessJson(null);
        } catch (Exception e) {
            e.printStackTrace();
            ret = JsonUtils.getErrorJson("发送邀约码短信失败." + e.getMessage(), null);
        }
        LOG.info("Function:sendInviteCode.End.");
        return ret;
    }*/



    /**
     * 
     * @Title: hasUserName
     * @Description:
     * @param @param userName
     * @param @return
     * @return boolean
     * @throws
     */
    private boolean hasUserName(String userName) {
        return usersService.hasUserName(userName);
    }



    /**
     * 
     * @Title: hasAllTypeUserName
     * @Description:
     * @param @param phone
     * @param @return
     * @return boolean
     * @throws
     */
    private boolean hasAllTypeUserName(String userName) {
        LOG.info("Function:hasPhone.Start.");
        boolean result = false;
        result = usersService.hasAllTypeUsersByUserName(userName);
        LOG.info("这个电话号码有效,phone=" + userName);
        LOG.info("Function:hasPhone.End.");
        return result;
    }



    /**
     * 
     * 发送短息 验证码 9,手机验证登陆
     *
     * @param smsCode
     * @return
     */
    /*private String addCode(SMSCode smsCode) {
        LOG.info("Function:addCode.Start.");
        String codeJson = JsonUtils.getErrorJson("获取验证码失败，请重试.", null);
        String phone = smsCode.getPhone();
        int type = smsCode.getType();
        if (StringUtils.isBlank(phone)) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        // type==9 手机验证登陆
        // if (9 == type) {
        // // 验证电话号码是否被注册
        // boolean hasPhone = hasPhone(phone);
        // if (!hasPhone) {
        // return JsonUtils.getErrorJson("该手机未注册!", null);
        // }
        // }

        String verCode = usersService.addCode(phone, (byte) type, smsCode.getParam());
        if (null != verCode) {
            codeJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:addCode.End.");
        return codeJson;
    }*/
    
    /**
     * 
    * @Title: sendInviteCode 
    * @Description: TODO(发送邀约、折扣码) 
    * @param @param receivePhone
    * @param @param userId
    * @param @param templateName
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    /*private String sendInviteCode(String receivePhone, long userId, String templateName) {
        LOG.info("Function:sendInviteCode.Start.");
        String ret = null;
        try {
            usersService.sendInviteCode(userId, receivePhone, templateName);
            ret = JsonUtils.getSuccessJson(null);
        } catch (Exception e) {
            e.printStackTrace();
            ret = JsonUtils.getErrorJson("发送邀约码短信失败." + e.getMessage(), null);
        }
        LOG.info("Function:sendInviteCode.End.");
        return ret;
    }*/
}
