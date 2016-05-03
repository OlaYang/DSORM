package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.ApplyEntryLog;
import com.meiqi.app.pojo.InviteCode;
import com.meiqi.app.pojo.ResponseData;
import com.meiqi.app.pojo.SMSCode;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.VerificationCode;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CompanyService;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.InviteCodeService;
import com.meiqi.app.service.UsersService;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.commons.config.Constants;

/**
 * 
 * @ClassName: UsersController
 * @Description:
 * @author 杨永川
 * @date 2015年4月22日 下午5:32:56
 *
 */
@Service
public class UsersAction extends BaseAction {
    private static final Logger LOG                      = Logger.getLogger(UsersAction.class);
    private static final String USER_JSON_PROPERTY       = "accessToken,user,userId,phone,avatar,roleId,city,company,realName,sex,companyId,companyName,regionId,regionName,headChar,inviteCode,roleName";
    private static final String INVITECODE_LIST_PROPERTY = "inviteId,receivePhone,sendTime,sendDate";
    @Autowired
    private UsersService        usersService;
    @Autowired
    private EtagService         eTagService;
    @Autowired
    private CompanyService      companyService;

    @Autowired
    private InviteCodeService   inviteCodeService;
    
    @Autowired
    private SmsCodeAction smsCodeAction;



    /**
     * 
     * @Title: loginUser
     * @Description:用户登录
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String loginUser(Users loginUser, HttpServletRequest request) {
        LOG.info("Function:loginUser.Start.");
        String loginJson = JsonUtils.getErrorJson("登录失败，请重试.", null);
        String phone = loginUser.getPhone();
        String password = loginUser.getPassword();
        if (!StringUtils.isBlank(phone) && !StringUtils.isBlank(password)) {
            Users user = usersService.loginUsers(phone, password, getIp(request));
            if (null != user) {
                // 设置accessToken
                setAccessToken(user);
                // 赋值公司或门店信息
                usersService.setCompany(user);
                // 设置用户头像
                setDefaultAvatar(user);

                // 获取邀约码
                //String inviteCode = inviteCodeService.getInviteCode(user.getUserId());
                Users users = usersService.getUserByUserId(user.getUserId());
                if (null != users) {
                    if (!StringUtils.isBlank(users.getInviteCode())) {
                        user.setInviteCode(users.getInviteCode());
                    }
                    user.setRoleName(users.getRoleName());
                }
                

                loginJson = JsonUtils.objectFormatToString(user,
                        StringUtils.getStringList(USER_JSON_PROPERTY, ContentUtils.COMMA));
            } else {
                loginJson = JsonUtils.getErrorJson("手机号或密码不正确.", null);
            }
        } else {
            loginJson = JsonUtils.getErrorJson("请输入手机号和密码.", null);
        }
        LOG.info("Function:loginUser.End.");
        return loginJson;
    }



    /**
     * 如果用户没有头像则加入默认头像，如果有头像则不操作
     * 
     * @param user
     */
    // 设置默认头像
    private void setDefaultAvatar(Users user) {
        // 用户没有头像,设置默认头像
        String avatar = user.getAvatar();
        if (StringUtils.isBlank(avatar)) {
            user.setAvatar(AppSysConfig.getValue("defaultAvatar"));
        }
    }



    /**
     * 
     * @Title: setCompany
     * @Description: 为用户信息赋值公司或门店信息
     * @param @param user 参数说明
     * @return void 返回类型
     * @throws
     */
    /*
     * private void setCompany(Users user) { int roleId = user.getRoleId(); long
     * companyId = user.getCompanyId();
     * 
     * long shopId = user.getShopId(); Company company =
     * companyService.getCompanyById(2 == roleId ? companyId : shopId, 2 ==
     * roleId ? 1 : 2); user.setCompany(company);
     * 
     * }
     */

    /**
     * 
     * @Title: hasPhone
     * @Description:
     * @param @param phone
     * @param @return
     * @return boolean
     * @throws
     */
    // private boolean hasPhone(String phone) {
    // LOG.info("Function:hasPhone.Start.");
    // boolean result = false;
    // result = usersService.hasPhone(phone);
    // LOG.info("这个电话号码有效,phone=" + phone);
    // LOG.info("Function:hasPhone.End.");
    // return result;
    // }

    /**
     * 
     * @Title: hasPhone
     * @Description:
     * @param @param phone
     * @param @return
     * @return boolean
     * @throws
     */
    private boolean hasUserName(String userName) {
        LOG.info("Function:hasPhone.Start.");
        boolean result = false;
        result = usersService.hasUserName(userName);
        LOG.info("这个用户名有效,userName=" + userName);
        LOG.info("Function:hasPhone.End.");
        return result;
    }



    /**
     * 
     * @Title: validUserInfo
     * @Description:用户注册，信息验证
     * @param @param users
     * @param @return
     * @return ResponseMessage
     * @throws
     */
    private ResponseData validUserInfo(Users users, boolean isRegister) {
        if (null == users) {
            return new ResponseData(1, "请正确输入信息!", null, null);
        }

        if (isRegister) {
            if (StringUtils.isBlank(users.getInviteCode())) {
                return new ResponseData(1, "请输入邀约码!", null, null);
            }
            if (StringUtils.isBlank(users.getPhone())) {
                return new ResponseData(1, "请输入手机号码!", null, null);
            }
            if (StringUtils.isBlank(users.getCode())) {
                return new ResponseData(1, "请输入验证!", null, null);
            }
            if (StringUtils.isBlank(users.getPassword())) {
                return new ResponseData(1, "请输入密码!", null, null);
            }
        }
        if (StringUtils.isBlank(users.getRealName())) {
            return new ResponseData(1, "请输入真实姓名!", null, null);
        }
        /*
         * if (0 != users.getSex() && 1 != users.getSex()) { return new
         * ResponseData(1, "请选择性别!", null, null); } if (0 ==
         * users.getRegionId()) { return new ResponseData(1, "请选择城市!", null,
         * null); } if (0 == users.getRoleId()) { return new ResponseData(1,
         * "请选择职业!", null, null); } if (0 == users.getCompanyId()) { return new
         * ResponseData(1, "请选择公司!", null, null); }
         */
        // if (StringUtils.isBlank(users.getAvatar())) {
        // return new ResponseData(1, "请上传头像!", null, null);
        // }
        return null;
    }



    /**
     * 
     * @Title: loginUser
     * @Description:用户注册 其实为设计师导购申请入驻
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String registerUser(Users regUser, long userId, HttpServletRequest request) {
        LOG.info("Function:registerUser.Start.");
        String registerJson = JsonUtils.getErrorJson("注册失败，请重试.", null);
        try {

            // 设配id
            ResponseData erroMessage = validUserInfo(regUser, true);
            if (null != erroMessage) {
                return JsonUtils.objectFormatToString(erroMessage);
            }

            // 检查邀约码 v2
            String inviteCode = regUser.getInviteCode();
            if (StringUtils.isBlank(inviteCode)) {
                return JsonUtils.getErrorJson("邀约码不能为空!", null);
            }

            InviteCode uCode = usersService.getInviteCode(inviteCode);
            if (null == uCode) {
                return JsonUtils.getErrorJson("邀约码有误，请重新输入", null);
            }
            uCode.setReceivePhone(regUser.getPhone());
            regUser.setUCode(uCode);

            String phone = regUser.getPhone();
            String code = regUser.getCode();
            // 验证电话号码是否被注册
            boolean hasUserName = hasUserName(phone);
            if (hasUserName) {
                // bug:1522
                return JsonUtils.getErrorJson("该手机号已成功入驻优家购，您可直接登录!", null);
            }
            // 检查验证码
            if (!usersService.isValidCode(phone, code, (byte) 0, true)) {
                return JsonUtils.getErrorJson("请输入正确的验证码", null);
            }

            // ip
            regUser.setLastIp(getIp(request));
            //regUser.setUserId(userId);
            // 注册
            Users user = usersService.registerUsers(regUser);
            if (null != user) {
                // 设置accessToken
                setAccessToken(user);
                // 赋值公司或门店信息
                usersService.setCompany(user);
                // 设置用户默认头像
                setDefaultAvatar(user);
                registerJson = JsonUtils.objectFormatToString(user,
                        StringUtils.getStringList(USER_JSON_PROPERTY, ContentUtils.COMMA));
            }else{
                return JsonUtils.getErrorJson("注册失败!", null);
            }
        } catch (Exception e) {
            LOG.error("registerUser fail error:"+e.getMessage());
        }

        LOG.info("Function:registerUser.End.");
        return registerJson;
    }



    /**
     * 
     * @Title: setAccessToken
     * @Description:设置accessToken
     * @param @param user
     * @return void
     * @throws
     */
    private void setAccessToken(Users user) {
        if (null != user) {
            // 设置accessToken
            user.setAccessToken(getShopConfig(ContentUtils.REQUEST_HEADER_ACCESSTOKEN));
        }
    }



    /**
     * 
     * @Title: getCode
     * @Description:用户获取验证码，向后台数据库插入一条code数据，并返回
     * @param @param codeUser
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getCode(HttpServletRequest request, HttpServletResponse response, Users codeUser) {
        LOG.info("Function:getCode.Start.");
        String phone = codeUser.getPhone();
        byte type = codeUser.getType();
        if (StringUtils.isBlank(phone)) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }

        // if type == 0为注册，需要验证电话是否已经被注册
        // type == 4 修改绑定手机(新手机验证)
        if (0 == type || 4 == type) {
            boolean hasUserName = hasUserName(phone);
            // 验证电话号码是否被注册
            if (hasUserName) {
                // bug:1522
                return JsonUtils.getErrorJson(0 == type ? "该手机号已成功入驻优家购，您可直接登录!" : "该手机已被注册!", null);
            }
        }
        // type==1 找回密码 type == 2 修改绑定手机(旧手机验证)
        if (1 == type || 2 == type) {
            boolean hasUserName = hasUserName(phone);
            // 验证电话号码是否被注册
            if (!hasUserName) {
                return JsonUtils.getErrorJson("该手机未注册!", null);
            }
        }

        SMSCode smsCode = new SMSCode();
        smsCode.setType(type);
        smsCode.setPhone(phone);
        String res = smsCodeAction.sendSMSCode(request, response, smsCode);
        LOG.info("Function:getCode.End.");
        return res;
    }



    /**
     * 
     * @Title: checkCode
     * @Description:检查验证码传递数据是否合法
     * @param @param code
     * @param @return
     * @return String
     * @throws
     */
    private String checkCodeInfo(VerificationCode code) {
        if (null == code) {
            return JsonUtils.getErrorJson("请输入验证码!", null);
        }
        // bug:1512
        if (StringUtils.isBlank(code.getPhone())) {
            return JsonUtils.getErrorJson("请输入手机号!", null);
        }
        if (StringUtils.isBlank(code.getCode()) || code.getType() < 0) {
            return JsonUtils.getErrorJson("请输入验证码!", null);
        }
        return "";

    }



    /**
     * 
     * @Title: getCode
     * @Description:用户获取验证码，向后台数据库插入一条code数据，并返回
     * @param @param codeUser
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String checkCode(VerificationCode code, HttpServletRequest request) {
        LOG.info("Function:getCode.Start.");
        // 检查验证码传递数据是否合法
        String checkCodeJson = checkCodeInfo(code);
        if (!StringUtils.isBlank(checkCodeJson)) {
            LOG.info("验证失败!");
            return checkCodeJson;
        }
        boolean result = usersService.isValidCode(code.getPhone(), code.getCode(), code.getType(), false);
        if (result) {
            checkCodeJson = JsonUtils.getSuccessJson(null);
        } else {
            checkCodeJson = JsonUtils.getErrorJson("请输入正确的验证码", null);
        }
        LOG.info("Function:getCode.End.");
        return checkCodeJson;
    }



    /**
     * 
     * @Title: getCode
     * @Description:用户获取验证码，向后台数据库插入一条code数据，并返回
     * @param @param codeUser
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    /*public String checkCode(VerificationCode code, HttpServletRequest request, boolean isSetValidate) {
        LOG.info("Function:getCode.Start.");
        // 检查验证码传递数据是否合法
        String checkCodeJson = checkCodeInfo(code);
        if (!StringUtils.isBlank(checkCodeJson)) {
            LOG.info("验证失败!");
            return checkCodeJson;
        }
        boolean result = usersService.isValidCode(code.getPhone(), code.getCode(), code.getType(), isSetValidate);
        if (result) {
            checkCodeJson = JsonUtils.getSuccessJson(null);
        } else {
            checkCodeJson = JsonUtils.getErrorJson("请输入正确的验证码", null);
        }
        LOG.info("Function:getCode.End.");
        return checkCodeJson;
    }*/



    /**
     * 
     * @Title: updatePhone
     * @Description:更换绑定手机号
     * @param @param codeUser
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String updatePhone(VerificationCode code, long userId) {
        LOG.info("Function:getCode.Start.");
        //
        String checkCodeJson = checkCodeInfo(code);
        if (!StringUtils.isBlank(checkCodeJson)) {
            LOG.info("验证失败!");
            return checkCodeJson;
        }
        // 验证码是否无效
        boolean result = usersService.isValidCode(code.getPhone(), code.getCode(), code.getType(), true);
        if (!result) {
            return JsonUtils.getErrorJson("请输入正确的验证码", null);
        }
        // 验证电话号码是否被注册
        boolean hasUserName = hasUserName(code.getPhone());
        if (hasUserName) {
            return JsonUtils.getErrorJson("该手机已被注册.", null);
        }

        // 更换电话号码
        result = usersService.updatePhone(userId, code.getPhone());
        if (result) {
            LOG.info("更换手机号码成功,user:id=" + userId);
            checkCodeJson = JsonUtils.getSuccessJson(null);
        } else {
            checkCodeJson = JsonUtils.getErrorJson("更换手机号码失败,请重试!", null);
        }
        // eTagService.putEtagMarking("user/"+userId,
        // Long.toString(System.currentTimeMillis()));
        LOG.info("Function:getCode.End.");
        return checkCodeJson;
    }



    /**
     * 
     * @Title: updateDesignerInfo
     * @Description:更新设计师信息
     * @param @param designer
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String updateDesignerInfo(Users designer, long userId) {
        LOG.info("Function:updateDesignerInfo.Start.");
        String updateJson = JsonUtils.getErrorJson("更新失败，请重试.", null);
        designer.setUserId(userId);
        boolean result = usersService.updateDesignerInfo(designer);
        if (result) {
            updateJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:updateDesignerInfo.End.");
        return updateJson;

    }



    /**
     * 
     * @Title: updatePassword
     * @Description:修改密码
     * @param @param user
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String updatePassword(Users user, long userId) {
        LOG.info("Function:updatePassword.Start.");
        String updatePwdJson = JsonUtils.getErrorJson("密码修改失败，请重试.", null);
        user.setUserId(userId);
        Users oldUser = usersService.isValidPassword(user);
        if (null == oldUser) {
            return JsonUtils.getErrorJson("旧密码错误!", null);
        }

        // 重设密码时无短信验证码，
        String code = usersService.isValidVerificationCode(oldUser.getPhone(), (byte) 1); // 1,找回密码短信
        if (null != code) {
            // 只有忘记密码时需要短信验证码
            if (usersService.isValidCode(oldUser.getPhone(), code, (byte) 1, true)) {
                return JsonUtils.getErrorJson("验证码错误!", null);
            }
        }

        oldUser.setPassword(user.getPassword());
        boolean result = usersService.updatePassword(oldUser);
        if (result) {
            updatePwdJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("Function:updateDesignerInfo.End.");
        return updatePwdJson;
    }



    /**
     * 
     * @Title: retrievePassword
     * @Description:找回密码
     * @param @param user
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String retrievePassword(Users user) {
        LOG.info("Function:updatePassword.Start.");
        String retrievePwdJson = JsonUtils.getErrorJson("找回密码失败，请重试.", null);
        // 验证 code是否有效
        user.setType((byte) 1);
        Users oldUser = usersService.isValidCode(user);
        if (null == oldUser) {
            return JsonUtils.getErrorJson("请输入正确的验证码", null);
        }
        // 修改密码
        oldUser.setPassword(user.getPassword());
        boolean result = usersService.updatePassword(oldUser);
        if (result) {
            // 设置accessToken
            setAccessToken(user);
            // 设置用户头像
            setDefaultAvatar(user);
            // 修改成功 传回用户信息
            retrievePwdJson = JsonUtils.objectFormatToString(oldUser,
                    StringUtils.getStringList(USER_JSON_PROPERTY, ContentUtils.COMMA));
        }

        return retrievePwdJson;

    }



    /**
     * 
     * @Title: addAnonymousUser
     * @Description:客户（匿名用户） 根据deviceId 增加用户，如果已存在直接返回用户user id 与access token
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String addAnonymousUser(HttpServletRequest request, long userId) {
        LOG.info("Function:addAnonymousUser.Start.");
        String anonymousJson = "";
        String deviceId = request.getHeader(ContentUtils.REQUEST_HEADER_DEVICEID);
        if (StringUtils.isBlank(deviceId) || deviceId.length() > 32) {
            return JsonUtils.getAuthorizationErrorJson();
        }

        Users user = usersService.getUsers(deviceId);
        // 该设备没有user对应，创建一个
        if (null == user) {
            user = new Users();
            user.setLastIp(getIp(request));
            user.setDeviceId(deviceId);
            user.setRoleId(1);
            // 匿名用户id
            user.setRegionId(1);
            user = usersService.addAnonymousUser(user);
        }
        if (null != user) {
            // 设置accessToken
            setAccessToken(user);
            // 赋值公司或门店信息
            usersService.setCompany(user);
            anonymousJson = JsonUtils.objectFormatToString(user,
                    StringUtils.getStringList(USER_JSON_PROPERTY, ContentUtils.COMMA));
        } else {
            return JsonUtils.getAuthorizationErrorJson();
        }
        LOG.info("Function:addAnonymousUser.End.");
        return anonymousJson;
    }



    /**
     * 
     * @Title: getApplyEntryLog
     * @Description:获取用户申请入驻状态
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getApplyEntryLog(long userId, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Function:getApplyEntryLog.Start.");
        ApplyEntryLog applyEntryLog = usersService.getApplyEntryLog(userId);
        String applyEntryLogJson = JsonUtils.objectFormatToString(applyEntryLog);
        LOG.info("Function:getApplyEntryLog.End.");
        String key = "user/applyEntryLog/" + userId;
        boolean result = eTagService.toUpdatEtag1(request, response, key, applyEntryLogJson);
        if (result) {
            return null;
        } else {
            eTagService.putEtagMarking(request, key, applyEntryLogJson);
        }
        return applyEntryLogJson;
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
    private String sendInviteCode(HttpServletRequest request, HttpServletResponse response, String receivePhone, long userId) {
        LOG.info("Function:sendInviteCode.Start.");
        String ret = null;
        SMSCode smsCode = new SMSCode();
        smsCode.setType(Constants.VerificationCodeType.inviteCode);
        smsCode.setPhone(receivePhone);
        smsCode.getParam().put("userId", userId);
        ret = smsCodeAction.sendSMSCode(request, response, smsCode);
        LOG.info("Function:sendInviteCode.End.");
        return ret;
    }



    /**
     * 
     * @Title: getInviteCodeTotal
     * @Description:2.6.6.1 获取邀约码发送记录(Total)
     * @param @param userId
     * @param @return 参数说明
     * @return String 返回类型
     * @throws
     */
    private String getInviteCodeTotal(long userId) {
        LOG.info("Function:getInviteCodeTotal.Start.");
        String total = usersService.getInviteCodeTotal(userId);
        LOG.info("Function:getInviteCodeTotal.End.");
        return total;
    }



    /**
     * 
     * @Title: getInviteCodeList
     * @Description:邀约码发送记录
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getInviteCodeList(long userId, String param) {
        LOG.info("Function:getInviteCodeList.Start.");
        InviteCode inviteCode = DataUtil.parse(param, InviteCode.class);
        int pageIndex = 0;
        int pageSize = 0;
        if (null != inviteCode) {
            pageIndex = inviteCode.getPageIndex();
            pageSize = inviteCode.getPageSize();
        }
        List<InviteCode> inviteCodeList = usersService.getInviteCodeList(userId, pageIndex, pageSize);
        String inviteCodeListJson = JsonUtils.listFormatToString(inviteCodeList,
                StringUtils.getStringList(INVITECODE_LIST_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getInviteCodeList.End.");
        return inviteCodeListJson;
    }



    /**
     * 
     * @Title: checkInviteCode
     * @Description:检查邀约码
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    /*
     * private String checkInviteCode(InviteCode inviteCode) {
     * LOG.info("Function:checkInviteCode.Start."); if (null == inviteCode ||
     * StringUtils.isBlank(inviteCode.getCode())) { return
     * JsonUtils.getErrorJson("邀约码不能为空!", null); } String checkInviteCode =
     * null; // TODO 邀约码 不区分大小 统一转小写 2015-08-10 pm王厅提需求 InviteCode
     * inviteCodeForDb =
     * usersService.checkInviteCode(inviteCode.getCode().toLowerCase(), null);
     * if (null == inviteCodeForDb) { checkInviteCode =
     * JsonUtils.getErrorJson("邀约码有误，请重新输入", null); } else if (0 !=
     * inviteCodeForDb.getStatus()) { checkInviteCode =
     * JsonUtils.getErrorJson("该邀约码已被使用，请重新获取邀约码", null); } else {
     * checkInviteCode = JsonUtils.getSuccessJson(null); }
     * LOG.info("Function:checkInviteCode.End."); return checkInviteCode; }
     */
    public String checkInviteCode(InviteCode inviteCode) {
        LOG.info("Function:checkInviteCode.Start.");
        if (null == inviteCode || StringUtils.isBlank(inviteCode.getCode())) {
            return JsonUtils.getErrorJson("邀约码不能为空!", null);
        }

        if (null != usersService.getInviteCode(inviteCode.getCode())) {
            return JsonUtils.getSuccessJson(null);
        }

        LOG.info("Function:checkInviteCode.End.");
        return JsonUtils.getErrorJson("邀约码有误，请重新输入", null);
    }



    /**
     * 
     * @Title: getUserCenter
     * @Description:获取用户中心(申请入驻状态，导购佣金等 )
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getUserCenter(long userId, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Function:getUserCenter.Start.");
        String userCenterJson = "";
        int userRole = StringUtils.StringToInt(request.getHeader(ContentUtils.USERROLE));

        String userCenterXml = usersService.getUserCenter(userId, userRole, getPlatString(request));
        if (null != userCenterXml) {
            try {
                userCenterJson = XML.toJSONObject(userCenterXml).toString();
                userCenterJson = JsonUtils.formartJsonString(userCenterJson);
            } catch (JSONException e) {
                LOG.error("xml string转换json失败,error:" + e.getMessage());
            }

        }
        LOG.info("user center userCenterJson:" + userCenterJson);
        String key = "user/" + userId;
        boolean result = eTagService.toUpdatEtag1(request, response, key, userCenterJson);
        if (result) {
            return null;
        }
        LOG.info("Function:getUserCenter.End.");
        return userCenterJson;
    }



    /**
     * 
     * @Title: getApplyEntryStatus
     * @Description:查询行业专家入驻 状态 返回html页面url
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getApplyEntryStatus(long userId, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Function:getApplyEntryStatus.Start.");
        ApplyEntryLog applyEntryLog = usersService.getApplyEntryStatus(userId, getPlatString(request));
        String applyEntryLogJson = JsonUtils.objectFormatToString(applyEntryLog);
        LOG.info("Function:getApplyEntryStatus.End.");
        return applyEntryLogJson;
    }



    /**
     * 
     * @Title: checkPhone
     * @Description:检查电话号码是否注册
     * @param @param phone
     * @param @return
     * @return String
     * @throws
     */
    private String checkPhone(String phone) {
        LOG.info("FUnction:checkPhone.Start.");
        String checkPhoneJson = "";
        if (hasUserName(phone)) {
            checkPhoneJson = JsonUtils.getErrorJson("该手机已被注册.", null);
        } else {
            checkPhoneJson = JsonUtils.getSuccessJson(null);
        }
        LOG.info("FUnction:checkPhone.End.");
        return checkPhoneJson;
    }



    /**
     * 
     * @Title: checkPhone
     * @Description:检查电话号码是否注册 （android）
     * @param @param phone
     * @param @return
     * @return String
     * @throws
     */
    private String checkPhone1(String phone) {
        LOG.info("FUnction:checkPhone.Start.");
        String checkPhoneJson = "";
        if (hasUserName(phone)) {
            checkPhoneJson = JsonUtils.getSuccessJson(null);
        } else {
            checkPhoneJson = JsonUtils.getErrorJson("fail", null);
        }
        LOG.info("FUnction:checkPhone.End.");
        return checkPhoneJson;
    }



    /*
     * Title: execute Description:
     * 
     * @param request
     * 
     * @param appRepInfo
     * 
     * @return
     * 
     * @see com.meiqi.app.action.IBaseAction#execute(javax.servlet.http.
     * HttpServletRequest, com.meiqi.app.pojo.dsm.AppRepInfo)
     */
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String[] urlParams = url.split("/");
        String param = appRepInfo.getParam();
        int platInt = StringUtils.StringToInt(appRepInfo.getHeader().get(ContentUtils.PLAT_INT).toString());
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        String content = "";
        if (StringUtils.matchByRegex(url, "^user$") && "post".equals(method)) { // /user
                                                                                // POST
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            String data = loginUser(users, request);
            return data;
        } else if (StringUtils.matchByRegex(url, "^user$") && "put".equals(method)) { // /user
                                                                                      // PUT
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            // 设备id
            users.setDeviceId((String) appRepInfo.getHeader().get("deviceId"));
            users.setFrom(usersService.getUserFrom(platInt));

            return registerUser(users, userId, request);

        } else if (StringUtils.matchByRegex(url, "^user\\/code") && "post".equals(method)) { // /user/codePOST
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            return getCode(request, response, users);

        } else if (StringUtils.matchByRegex(url, "^user\\/code\\/check$")) { // /user/code/checkPOST
            content = appRepInfo.getParam();
            VerificationCode code = (VerificationCode) DataUtil.parse(content, VerificationCode.class);
            return checkCode(code, request);

        } else if (url.contains("phone") && "patch".equals(method)) { // /user/phone
                                                                      // PATCH
            content = appRepInfo.getParam();
            VerificationCode code = (VerificationCode) DataUtil.parse(content, VerificationCode.class);
            return updatePhone(code, userId);
            // bug :1553
        } else if (StringUtils.matchByRegex(url, "^user$") && "patch".equals(method)) { // /user
            // PATCH
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            // eTagService.putEtagMarking("user/"+users.getUserId(),
            // Long.toString(System.currentTimeMillis()));
            return updateDesignerInfo(users, userId);
        } else if (StringUtils.matchByRegex(url, "^user\\/password$") && "patch".equals(method)) { // /user/password
            // PATCH
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            return updatePassword(users, userId);
        } else if (StringUtils.matchByRegex(url, "^user\\/password$") && "post".equals(method)) { // /user/password
            // POST
            content = appRepInfo.getParam();
            Users users = (Users) DataUtil.parse(content, Users.class);
            return retrievePassword(users);

        } else if (url.contains("anonymous") && "get".equals(method)) { // /user/anonymous
                                                                        // GET
            return addAnonymousUser(request, userId);

        } else if (url.contains("applyEntryLog")) { // /user/applyEntryLog GET

            return getApplyEntryLog(userId, request, response);

        } else if (StringUtils.matchByRegex(url, "^inviteCode\\/\\d+$") && "post".equals(method)) { // /inviteCode/{receivePhone}
                                                                                                    // POST
            String receivePhone = urlParams[1];
            return sendInviteCode(request, response, receivePhone, userId);

        } else if (StringUtils.matchByRegex(url, "^inviteCodeTotal$") && "get".equals(method)) {
            // 此接口已废弃，为防止老app报错暂保留
            // 2.6.6.1 获取邀约码发送记录(Total) // redmine:1834
            String data = getInviteCodeTotal(userId);
            String key = "inviteCodeTotal/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if (StringUtils.matchByRegex(url, "^inviteCodeList$") && "get".equals(method)) {
            // 此接口已废弃，为防止老app报错暂保留
            // 2.6.6.2 获取邀约码发送记录(List)
            // 304 缓存
            String data = getInviteCodeList(userId, param);
            String key = "inviteCodeList/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            } // GET
            return data;

        } else if (url.equals("user/inviteCode/check") && "post".equals(method)) { // /user/inviteCode/check
                                                                                   // POST
            content = appRepInfo.getParam();
            InviteCode inviteCode = (InviteCode) DataUtil.parse(content, InviteCode.class);
            return checkInviteCode(inviteCode);

        } else if (url.contains("userCenter") && "get".equals(method)) { // /user/userCenter
                                                                         // GET
            return getUserCenter(userId, request, response);

        } else if (url.contains("user/ApplyEntryStatus") && "get".equals(method)) { // /user/ApplyEntryStatus
                                                                                    // GET
            String data = getApplyEntryStatus(userId, request, response);
            String key = "user/ApplyEntryStatus/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (StringUtils.matchByRegex(url, "^user\\/\\d+\\/check$") && "get".equals(method)) { // user/{phone}/check
                                                                                                     // GET
            String phone = urlParams[1];
            return checkPhone(phone);

        } else if (StringUtils.matchByRegex(url, "^user\\/\\d+\\/check1$") && "get".equals(method)) { // user/{phone}/check
            // GET
            String phone = urlParams[1];
            return checkPhone1(phone);

        }
        return null;
    }
}
