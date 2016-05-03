package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.config.AppSysConfig;
//import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.EncodeAndDecodeUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.LejjBeanUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.common.utils.XmlUtils;
import com.meiqi.app.dao.ApplyEntryLogDao;
import com.meiqi.app.dao.InviteCodeDao;
import com.meiqi.app.dao.RegionDao;
import com.meiqi.app.dao.UsersDao;
import com.meiqi.app.dao.VerificationCodeDao;
import com.meiqi.app.pojo.ApplyEntryLog;
import com.meiqi.app.pojo.Commission;
import com.meiqi.app.pojo.Company;
import com.meiqi.app.pojo.DesignerInfo;
import com.meiqi.app.pojo.InviteCode;
import com.meiqi.app.pojo.Region;
import com.meiqi.app.pojo.Users;
import com.meiqi.app.pojo.VerificationCode;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.app.service.CompanyService;
import com.meiqi.app.service.UsersService;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.action.UserAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.util.Tool;

/**
 * 
 * @ClassName: UsersServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年5月7日 上午9:35:01
 *
 */
@Service
public class UsersServiceImpl implements UsersService {
    private static final Logger LOG      = Logger.getLogger(UsersServiceImpl.class);
    private static final String XML_PATH = AppSysConfig.getValue(ContentUtils.XML_PATH);
    private static final String XML_TYPE = "userCenter";
    private static final int RETRY_COUNT = 5; // 注册用户失败时的重试次数
    Class<Users>                cls      = Users.class;
    @Autowired
    private UsersDao            usersDao;
    @Autowired
    private VerificationCodeDao verificationCodeDao;
    @Autowired
    private RegionDao           regionDao;
    @Autowired
    private ApplyEntryLogDao    applyEntryLogDao;
    @Autowired
    private InviteCodeDao       inviteCodeDao;

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    @Autowired
    private CompanyService      companyService;
    
    @Autowired
    private SmsAction     smsAction;
    
    @Autowired
    private UserAction     userAction;


    /**
     * 
     * @Title: loginUsers
     * @Description:用户登录
     * @param @param phone
     * @param @param password
     * @param @return
     * @throws
     */
    @Override
    public Users loginUsers(String userName, String password, String lastIp) {
        LOG.info("Function:loginUsers.Start.");
        Users result = null;
        Users users = usersDao.getUsersByUserName(cls, userName);
        // Users users = getUserByUserName(userName);
        password = EncodeAndDecodeUtils.encodeStrMD5(password);
        if (null != users && users.getPassword().equals(password)) {
            result = users;
            // 登录成功 修改登录ip 登录次数 登录时间
            users.setVisitCount(users.getVisitCount() + 1);
            users.setLastIp(lastIp);
            users.setLastLogin(DateUtils.getSecond());
            // usersDao.updateObejct(users);

            Action action = new Action();
            action.setServiceName("test_ecshop_ecs_users");
            Map<String, Object> set = new HashMap<String, Object>();
            set.put("visit_count", users.getVisitCount());
            set.put("last_ip", users.getLastIp());
            set.put("last_login", users.getLastLogin());

            action.setSet(set);

            action.setType("U");
            SqlCondition condition = new SqlCondition();
            condition.setKey("user_id");
            condition.setOp("=");
            condition.setValue(users.getUserId());

            List<SqlCondition> conditions = new ArrayList<SqlCondition>();
            conditions.add(condition);

            Where where = new Where();
            where.setPrepend("and");
            where.setConditions(conditions);

            action.setWhere(where);

            DsManageReqInfo actionReqInfo = new DsManageReqInfo();
            actionReqInfo.setServiceName("MUSH_Offer");
            List<Action> actions = new ArrayList<Action>();
            actions.add(action);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("actions", actions);
            param.put("transaction", 1);
            actionReqInfo.setParam(param);

            String res = mushroomAction.offer(actionReqInfo);
            SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
            if (!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
                LOG.error("Function:loginUsers updata user failed!");
            }
        }

        LOG.info("Function:loginUsers.End.");
        return result;
    }



    @Override
    public Users getUserByUserName(String userName) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("user_name", userName);
        return getUser(queryParam);
    }



    @Override
    public Users getUserByUserId(long userId) {
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("user_id", userId);
        return getUser(queryParam);
    }



    private Users getUser(Map<String, Object> queryParam) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_ecsusers");
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Users user = new Users();
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

            return user;
        } else {
            LOG.error("getUser fail. data:" + data);
        }

        return null;
    }



    /**
     * 
     * @see com.meiqi.app.service.UsersService#deleteAbandonUser(java.lang.String,
     *      long)
     */
    @Override
    public void deleteAbandonUser(String userName, long userId) {
        LOG.info("Function:deleteAbandonUser.Start.");
        usersDao.deleteAbandonUser(userName, userId);
        LOG.info("Function:deleteAbandonUser.End.");
    }



    @Override
    public Users addAnonymousUser(Users users) {
        LOG.info("Function:addAnonymousUser.Start.");
        // 添加匿名用户将deviceid作为用户名，解除数据库唯一性
        users.setUserName(users.getDeviceId());
        // 申请入驻的初始状态为0 是否有效
        users.setIsValidated(0);
        users.setRegTime(DateUtils.getSecond());
        users.setLastLogin(DateUtils.getSecond());
        // 性别设置默认值
        if (null == users.getSex()) {
            users.setSex(1);
        }
        // 区域
        Region region = (Region) regionDao.getObjectById(Region.class, users.getRegionId());
        users.setCity(region);

        // 添加匿名用户（匿名）
        // usersDao.addObejct(users);
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        List<Action> actions = new ArrayList<Action>();
        actions.add(buildAddOrUpdateUserAction(users, "C"));
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String reqInfo = JSON.toJSONString(actionReqInfo);
        LOG.info("addAnonymousUser: " + reqInfo);

        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            Users dbUser = getUserByUserName(users.getUserName());
            if (null == dbUser) {
                LOG.error("addAnonymousUser error: " + reqInfo);
            } else {
                users.setUserId(dbUser.getUserId());
                users.setAvatar(dbUser.getAvatar());
            }
        }

        LOG.info("Function:addAnonymousUser.End.");
        return users;
    }



    /**
     * 
     * @Title: registerUsers
     * @Description:注册用户
     * @param @param phone
     * @param @param password
     * @param @param code
     * @param @param lastIp
     * @param @return
     * @throws
     */
    @Override
    public Users registerUsers(Users users) {
        LOG.info("Function:registerUsers.Start.");
        Users regUser = new Users();
        // 注册
        try {
            String phone = users.getPhone();
            String deviceId = users.getDeviceId();
            LejjBeanUtils.copyProperties(regUser, users);
            regUser.setDeviceId(deviceId);
            regUser.setUserName(phone);
            // 密码
            regUser.setPassword(EncodeAndDecodeUtils.encodeStrMD5(users.getPassword()));
            // 申请入驻的初始状态为1 有效
            regUser.setIsValidated(1);
            regUser.setRegTime(DateUtils.getSecond());
            regUser.setLastLogin(DateUtils.getSecond());

            // 区域
            long regionId = users.getRegionId();
            if (0 == regionId) {
                regionId = 322;
                regUser.setRegionId(regionId);
            }
            Region region = (Region) regionDao.getObjectById(Region.class, regionId);
            regUser.setCity(region);

            int roleId = users.getRoleId();
            Integer belongsId = null;
            Long companyId = users.getCompanyId();
            if (Constants.UserRoleId.SHOPPER == roleId) {
                regUser.setRoleId(roleId);
                // 门店
                regUser.setShopId(companyId);
                regUser.setCompanyId(0l);

                // 所属公司的user_id
                if (null != companyId) {
                    belongsId = getUserIdByShopperId(companyId);
                }
            } else {
                roleId = Constants.UserRoleId.DESIGNER; // 默认为2（设计师）
                regUser.setRoleId(roleId);
                // 公司
                regUser.setShopId(0l);
                regUser.setCompanyId(companyId);

                // 所属公司的user_id
                if (null != companyId) {
                    belongsId = getUserIdByCompanyId(companyId);
                }
            }
            registerUser(regUser, belongsId, false, "C", 0);
        } catch (Exception e) {
            LOG.error("register User failed.", e);
            e.printStackTrace();
            regUser = null;
        }

        LOG.info("Function:registerUsers.End.");
        return regUser;
    }


    /**
     * getUserIdByCompanyId
     * 
     * @param type
     * @param phone
     * @return code_id
     */
    private Integer getUserIdByCompanyId(Long companId) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("LJG_BUV1_company");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("companid", companId);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("user_id"))) {
                return Integer.parseInt(jsonMap.get("user_id"));
            }
        } else {
            LOG.error("getUserIdByCompanyId fail. data:" + data);
        }
        return null;
    }



    /**
     * getUserIdByShopperId
     * 
     * @param type
     * @param phone
     * @return code_id
     */
    private Integer getUserIdByShopperId(Long shoppersId) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("COM_BUV1_shoppersinfo");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("shoppers_id", shoppersId);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");
        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("user_id"))) {
                return Integer.parseInt(jsonMap.get("user_id"));
            }
        } else {
            LOG.error("getUserIdByShopperId fail. data:" + data);
        }
        return null;
    }



    /**
     * 申请入驻 <method description>
     *
     * @param user
     * @param deleteUserFlag
     * @param userActionType
     * @param retryCount 失败时重试次数
     * @throws Exception
     */
    private void registerUser(Users user, Integer belongsId, boolean deleteUserFlag, String userActionType, int retryCount)
            throws Exception {
        if (retryCount >= RETRY_COUNT) {
            throw new Exception("register user fail! retry count is " + retryCount);
        }
        retryCount ++;
        
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        List<Action> actions = new ArrayList<Action>();
        int roleId = user.getRoleId();

        if (deleteUserFlag) {
            actions.add(buildDeleteUserAction(user.getUserId()));
        }

        user.setInviteCode(CodeUtils.getInviteCode());

        // 写入用户表
        actions.add(buildAddOrUpdateUserAction(user, userActionType));

        if (Constants.UserRoleId.DESIGNER == roleId) {
            // 写入设计师表
            actions.add(buildAddDesignerAction(user, belongsId, userActionType));
        } else if (Constants.UserRoleId.SHOPPER == roleId) {
            // 写入导购表
            actions.add(buildAddShopperAction(user, belongsId, userActionType));
        } else {
            throw new Exception("不支持角色注册。roleId=" + roleId);
        }

        // 写入审核表
        Action action = new Action();
        action.setType("C");
        action.setServiceName("test_ecshop_ecs_aplayaduit_info");

        Map<String, Object> set = new HashMap<String, Object>();
        if ("C".equals(userActionType)) {
            set.put("user_id", "$-2.generateKey");
        } else {
            set.put("user_id", user.getUserId());
        }
        set.put("role", user.getRoleId());
        set.put("add_time", "$UnixTime");
        set.put("enter_id", "$-1.generateKey");
        set.put("status", 2);// 审核状态 0待审核 1审核拒绝 2 审核通过
        set.put("check_time", "$UnixTime");
        action.setSet(set);
        actions.add(action);

        // 写入 ucode
        action = buildAddOrUpdateUCode(user, userActionType);
        if (null != action) {
            actions.add(action);
        }
        
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);

        String reqInfo = JSON.toJSONString(actionReqInfo);
        LOG.info("registerUser: " + reqInfo);

        String res = mushroomAction.offer(actionReqInfo);
        LOG.info("register user result="+res);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            Users dbUser = getUserByUserName(user.getUserName());
            if (null == dbUser) {
                LOG.info("register user 获取用户信息失败 userName="+user.getUserName());
                actionResponse.setCode(DsResponseCodeData.ERROR.description);
                throw new Exception("获取用户信息失败！");
            }
            user.setUserId(dbUser.getUserId());
            user.setAvatar(dbUser.getAvatar());
            return;
        } else {
            LOG.error("register user fail 自动再次尝试注册. errMsg:" + actionResponse.getDescription());
            /*
             * if (actionResponse.getDescription().contains(
             * "test_ecshop_lejj_invite_code-C 失败, Duplicate entry")) { // 如果
             * uCode 写入冲突则重新写入 //registerUser(user, belongsId, deleteUserFlag,
             * userActionType); }
             */
            //throw new Exception(actionResponse.getDescription());
            registerUser(user, belongsId, deleteUserFlag,userActionType, retryCount);
        }
    }



    private Action buildAddOrUpdateUCode(Users user, String userActionType) {
        String inviteCode=user.getInviteCode();
        LogUtil.info("register user inviteCode="+inviteCode);
        
        Action action = new Action();
        action.setServiceName("test_ecshop_lejj_invite_code");

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("receive_phone", user.getUCode().getReceivePhone());
        set.put("status", 1);
        action.setType("C");
        set.put("code", inviteCode);
        set.put("send_user_id", user.getUCode().getUseUserId());
        set.put("use_time", "$UnixTime");
        if ("C".equals(userActionType)) {
            set.put("use_user_id", "$-3.generateKey");
        } else {
            //如果当前用户存在，那么需要判断用户是否拥有invite_code，如果没有那么就新增
            long use_user_id=user.getUserId();//当前用户id
            String serviceName="YJG_BUV1_invite_code";
            DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
            serviceReqInfo.setServiceName(serviceName);
            Map<String, Object> queryParam = new HashMap<String, Object>();
            queryParam.put("use_user_id", use_user_id);
            serviceReqInfo.setParam(queryParam);
            serviceReqInfo.setNeedAll("1");
            String data = dataAction.getData(serviceReqInfo, "");
            RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
            if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
                //已经存在
                return null;
            }
            //如果不存在，那么新增
            set.put("use_user_id", use_user_id);
        }
        action.setSet(set);
        return action;
    }



    /*
     * 规则：HMJ_HSV1_ecsusers 传入：user_name（用户名） 传出：user_id（用户id），avatar_1（头像）
     */
    /*
     * private void getUserByUserName(String userName, Users user) { if (user ==
     * null) { user = new Users(); } user.setUserName(userName);
     * 
     * DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
     * serviceReqInfo.setServiceName("HMJ_HSV1_ecsusers"); Map<String, Object>
     * queryParam = new HashMap<String, Object>(); queryParam.put("user_name",
     * userName); serviceReqInfo.setParam(queryParam);
     * serviceReqInfo.setNeedAll("1");
     * 
     * String data = dataAction.getData(serviceReqInfo, "");
     * RuleServiceResponseData responseData = DataUtil.parse(data,
     * RuleServiceResponseData.class); if (responseData.getRows().size() != 0) {
     * Map<String, String> jsonMap = responseData.getRows().get(0); if
     * (!StringUtils.isBlank(jsonMap.get("user_id"))) {
     * user.setUserId(Integer.parseInt(jsonMap.get("user_id"))); } if
     * (!StringUtils.isBlank(jsonMap.get("avatar_1"))) {
     * user.setAvatar(jsonMap.get("avatar_1")); } } }
     */

    /**
     * 
     * <method description>
     *
     * @param userId
     * @return
     */
    private Action buildDeleteUserAction(long userId) {
        Action action = new Action();
        action.setServiceName("test_ecshop_ecs_users");
        action.setType("D");

        SqlCondition condition = new SqlCondition();
        condition.setKey("user_id");
        condition.setOp("=");
        condition.setValue(userId);

        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);

        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);

        action.setWhere(where);

        return action;
    }



    /**
     * 申请入驻——写入用户表——设计师 <method description>
     *
     * @param user
     * @param userActionType
     * @return
     */
    private Action buildAddOrUpdateUserAction(Users user, String userActionType) {
        Action action = new Action();
        action.setServiceName("test_ecshop_ecs_users");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("real_name", user.getRealName());
        set.put("user_name", user.getUserName());
        set.put("mobile_phone", user.getPhone());
        set.put("password", user.getPassword());
        set.put("is_validated", user.getIsValidated());
        if (null != user.getSex()) {
            set.put("sex", user.getSex());
        }
        set.put("region_id", user.getRegionId());
        if (null != user.getRoleId()) {
            set.put("role_id", user.getRoleId());
        }

        // reg_time（注册时间）
        set.put("reg_time", "$UnixTime");
        set.put("last_login", "$UnixTime");
        // avatar（头像）
        if (!StringUtils.isBlank(user.getAvatar())) {
            set.put("avatar", user.getAvatar());
        }
        // company_id（公司Id,设计师才有）
        if (null != user.getCompanyId()) {
            set.put("company_id", user.getCompanyId());
        }

        // shop_id
        if (null != user.getShopId()) {
            set.put("shop_id", user.getShopId());
        }

        // from（来源 0-优家购WEB 1-爱有窝WEB 2-优家购安卓 3-优家购IPAD 4-优家购IPHONE 5-优家购M站
        // 6-爱有窝M站 ）
        if (null != user.getFrom()) {
            set.put("from", user.getFrom());
        }

        if (null != user.getDeviceId()) {
            set.put("device_id", user.getDeviceId());
        }

        action.setSet(set);

        if ("C".equals(userActionType)) {
            action.setType("C");
        } else {
            action.setType("U");

            SqlCondition condition = new SqlCondition();
            condition.setKey("user_id");
            condition.setOp("=");
            condition.setValue(user.getUserId());

            List<SqlCondition> conditions = new ArrayList<SqlCondition>();
            conditions.add(condition);

            Where where = new Where();
            where.setPrepend("and");
            where.setConditions(conditions);

            action.setWhere(where);
        }

        return action;
    }



    /**
     * 申请入驻——写入设计师表 <method description>
     *
     * @param user
     * @return
     */
    private Action buildAddDesignerAction(Users user, Integer belongsId, String userActionType) {
        Action action = new Action();
        action.setType("C");
        action.setServiceName("test_ecshop_ecs_designer_info");

        Map<String, Object> set = new HashMap<String, Object>();
        if ("C".equals(userActionType)) {
            set.put("user_id", "$-1.generateKey");
        } else {
            set.put("user_id", user.getUserId());
        }
        set.put("real_name", user.getRealName());
        set.put("contact_phone", user.getPhone());
        if (null != user.getSex()) {
            set.put("sex", user.getSex());
        }
        if (user.getCity() != null) {
            set.put("city_id", user.getCity().getAgencyId());
        }
        if (belongsId != null) {
            set.put("belongs_id", belongsId);
        }
        // set.put("code", user.getUCode().getCode());
        set.put("c_status", 1);
        set.put("country_id", 1);
        set.put("status", 2);
        set.put("settle_source", getDesignerSettleSource(user.getFrom()));
        set.put("creat_time", "$UnixTime");

        action.setSet(set);

        return action;
    }



    private int getDesignerSettleSource(Integer userfrom) {
        if (null == userfrom) {
            return DesignerSettleSource.DEFAULT;
        }

        if (userfrom == UserFrom.YJG_ANDROID) {
            return DesignerSettleSource.ANDROID;
        } else if (userfrom == UserFrom.YJG_IPAD) {
            return DesignerSettleSource.IPAD;
        } else if (userfrom == UserFrom.YJG_IPHONE) {
            return DesignerSettleSource.IOS;
        } else {
            return DesignerSettleSource.DEFAULT;
        }
    }



    @Override
    public int getUserFrom(int platInt) {
        if (platInt == ContentUtils.PLAT_INT_ANDROID) {
            return UserFrom.YJG_ANDROID;
        } else if (platInt == ContentUtils.PLAT_INT_IPHONE) {
            return UserFrom.YJG_IPHONE;
        } else if (platInt == ContentUtils.PLAT_INT_IPAD) {
            return UserFrom.YJG_IPAD;
        } else {
            return UserFrom.DEFAULT;
        }
    }

    /*
     * 来源
     */
    interface UserFrom {
        int DEFAULT     = 0;
        // 0-优家购WEB
        int YJG_WEB     = 0;
        // 1-爱有窝WEB
        // 2-优家购安卓
        int YJG_ANDROID = 2;
        // 3-优家购IPAD
        int YJG_IPAD    = 3;
        // 4-优家购IPHONE
        int YJG_IPHONE  = 4;
        // 5-优家购M站 6-爱有窝M站
    }

    /*
     * 设计师入驻来源
     */
    interface DesignerSettleSource {
        // 0未知
        int DEFAULT = 0;
        // 1 IPAD
        int IPAD    = 1;
        // 2 IOS APP（优家购）
        int IOS     = 2;
        // 3 安卓 APP（优家购）
        int ANDROID = 3;
        // 4 微信入驻（会员卡）
        // 5 PC端-优家购
        // 6 PC端-爱有窝
        // 7 后台添加',
    }



    private Action buildAddShopperAction(Users user, Integer belongsId, String userActionType) {
        Action action = new Action();
        action.setType("C");
        action.setServiceName("test_ecshop_ecs_shoppers_info");

        Map<String, Object> set = new HashMap<String, Object>();
        if ("C".equals(userActionType)) {
            set.put("user_id", "$-1.generateKey");
        } else {
            set.put("user_id", user.getUserId());
        }
        set.put("real_name", user.getRealName());
        set.put("contact_phone", user.getPhone());
        if (null != user.getSex()) {
            set.put("sex", user.getSex());
        }
        if (user.getCity() != null) {
            set.put("city_id", user.getCity().getAgencyId());
        }
        if (belongsId != null) {
            set.put("belongs_id", belongsId);
        }
        set.put("c_status", 1);
        set.put("country_id", 1);
        set.put("status", 2);
        set.put("creat_time", "$UnixTime");

        action.setSet(set);

        return action;
    }



    /**
     * 
     * @Title: hasUserName
     * @Description:验证userName是否有效
     * @param @param userName
     * @param @return
     * @throws
     */
    @Override
    public boolean hasUserName(String userName) {
        return userAction.userIsExist(userName, 
                ""+Constants.UserType.HEMEIJU_USER.getIndex(), 
                ""+Constants.SiteId.YJG);
    }



    /**
     * 
     * @Title: hasAllTypeUsersByUserName
     * @Description:验证userName是否有效
     * @param @param userName
     * @param @return
     * @throws
     */
    @Override
    public boolean hasAllTypeUsersByUserName(String userName) {
        LOG.info("Function:hasPhone.Start.");
        Users users = usersDao.getAllTypeUsersByUserName(cls, userName);
        LOG.info("Function:hasPhone.End.");
        return null != users;
    }



    /**
     * 
     * @Title: isValidCode
     * @Description:验证码是否有效
     * @param @param phone
     * @param @param code
     * @param @return
     * @throws
     */
    @Override
    public boolean isValidCode(String phone, String code, byte type, boolean setInvalid,boolean makeInvalid) {
        if (setInvalid) {
            return validateSmsCode(phone, code, type,makeInvalid);
        } else {
            return isValidCode(phone, code, type);
        }
    }
    
    //新增makeInvalid 是否让验证码失效 true失效 fasle不失效
    private boolean validateSmsCode(String phone, String code, byte type,boolean makeInvalid) {
        RepInfo req = new RepInfo();
        req.setAction("smsAction");
        req.setMethod("validateSmsCode");
        Map<String, Object> param1 = new HashMap();
        param1.put("serviceName", "SMS_Send");
        Map<String, String> param2 = new HashMap();
        param2.put("receive_phone", phone);
        param2.put("code_value", code);
        param2.put("template_id", "126");
        param2.put("site_id", "0");
        param2.put("web_site", "0");
        param2.put("type", "" + type);
        param1.put("param", param2);
        req.setParam(JsonUtils.objectFormatToString(param1));
        
        Object obj = smsAction.validateSmsCode(null, null, req,makeInvalid);
        BaseRespInfo resp = JSONObject.parseObject(JSONObject.toJSONString(obj), BaseRespInfo.class);
        if ("0".equals(resp.getCode())) {
            return true;
        }
        
        return false;
    }
    
    private boolean isValidCode(String phone, String code, byte type) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_code");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("type", "" + type);
        queryParam.put("phone", phone);
        queryParam.put("code", code);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("is_flag"))) {
                String isFlag = jsonMap.get("is_flag");
                if ("1".equals(isFlag)) {
                    return true;
                }
            }
        } else {
            LOG.error("isValidCode fail. data:" + data);
        }
        return false;
    }



    /**
     * 
     * @Title: isValidCode
     * @Description:验证码是否有效通过phone
     * @param @param phone
     * @param @param code
     * @param @return
     * @throws
     */
    @SuppressWarnings("unchecked")
	@Override
    public Users isValidCode(Users user) {
        boolean result = false;
        //以前是直接写的hql查询，因业务变化，门店宝新增子账户，所以改为同意由规则查询
       /* Users userForDB = usersDao.getUsersByUserName(cls, user.getPhone());
        if (null != userForDB) {
            result = isValidCode(userForDB.getPhone(), user.getCode(), user.getType(), true,user.isMakeInvalid());
            if (result) {
                return userForDB;
            }
        }*/
        Users userForDB = new Users();
        Tool tool = new Tool();
        Log log =  LogFactory.getLog("request");
        Map<String,Object> map1 = new HashMap<String, Object>();
        List<Map<String, String>> mapList = new ArrayList<Map<String,String>>();
        map1.put("user_name", user.getPhone());
        try {
        	mapList = (List<Map<String, String>>) tool.getRuleResult("IPad_HSV1_ModifiyPassword", map1, log, "查询", "IPad_HSV1_ModifiyPassword");
		} catch (Exception e) {
			log.error("查询 IPad_HSV1_ModifiyPassword param is: "+com.meiqi.openservice.commons.util.DataUtil.toJSON(map1));
			return null;
		}
        result = isValidCode(mapList.get(0).get("user_name"), user.getCode(), user.getType(), true,user.isMakeInvalid());
        if (result) {
        	userForDB.setUserId(Long.valueOf(mapList.get(0).get("user_id")));
        	userForDB.setAvatar(mapList.get(0).get("avatar").toString());
        	userForDB.setCompanyId(Long.valueOf(mapList.get(0).get("companyId")));
        	userForDB.setPhone(mapList.get(0).get("mobile_phone").toString());
        	userForDB.setRealName(mapList.get(0).get("real_name").toString());
        	userForDB.setRegionId(Long.valueOf(mapList.get(0).get("region_id")));
        	userForDB.setRoleId(Integer.parseInt(mapList.get(0).get("role_id").toString()));
        }
        return userForDB;
    }



    /**
     * 
     * 通过电话号码和验证码类型拿取有效的验证码id
     *
     * @param type
     * @param phone
     * @return code_id
     */
    public String isValidVerificationCode(String phone, byte type) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_code");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("type", "" + type);
        queryParam.put("phone", phone);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("code_id"))) {
                return jsonMap.get("code_id");
            }
        } else {
            LOG.error("isValidVerificationCode fail. data:" + data);
        }
        return null;
    }



    /**
     * 更新密码
     * 
     */
    public boolean updatePassword(Users user) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        List<Action> actions = new ArrayList<Action>();

        actions.add(buildUpdatePasswordAction(user));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);

        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            return true;
        } else {
            return false;
        }
    }



    /*private Action buildSetVerificationCodeInvalidAction(String codeId) {
        String serviceName = "test_ecshop_lejj_verification_code";
        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("is_valid", 0);
        action.setSet(set);

        SqlCondition condition = new SqlCondition();
        condition.setKey("code_id");
        condition.setOp("=");
        condition.setValue(codeId);
        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);

        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);
        action.setWhere(where);

        return action;
    }*/



    private Action buildUpdatePasswordAction(Users user) {
        String serviceName = "test_ecshop_ecs_users";
        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("password", EncodeAndDecodeUtils.encodeStrMD5(user.getPassword()));
        action.setSet(set);

        SqlCondition condition = new SqlCondition();
        condition.setKey("user_id");
        condition.setOp("=");
        condition.setValue(user.getUserId());
        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);

        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);
        action.setWhere(where);

        return action;
    }



    /**
     * 
     * @Title: updateValidCode
     * @Description:验证成功，设置该验证码失效
     * @param @param verCode
     * @return void
     * @throws
     */
    public void updateValidCode(VerificationCode verCode) {
        // 验证成功，设置该验证码失效
        verCode.setIsValid((byte) 0);
        verificationCodeDao.updateObejct(verCode);
    }



    /**
     * 
     * @Title: getCode
     * @Description:用户获取验证码，向后台数据库插入一条code数据，并返回
     * @param @param phone
     * @param @param code
     * @param @param type
     * @param @return
     * @throws
     */
    /*@Override
    public VerificationCode addCode(String phone, byte type, Map<String, Object> param) {
        LOG.info("Function:addCode.Start.");
        // 设置该手机号的其他同类型验证码无效
        verificationCodeDao.invalidCode(phone, type);
        String code = CodeUtils.getVerificationCode();
        int validTime = DateUtils.getSecond()
                + StringUtils.StringToInt(AppSysConfig.getValue(ContentUtils.VERIFICATION_CODE_VALID_TIME));
        VerificationCode verCode = new VerificationCode(0, type, phone, code, validTime, (byte) 1);
        verificationCodeDao.addObejct(verCode);
        Users user = usersDao.getUsersByUserName(cls, phone);
        String userName = phone;
        if (null != user) {
            userName = user.getRealName();
            // 发送短息
        }
        // invite_code_mock = true 不发送短息 验证码为 invite_code_mock_value
        if ("false".equals(AppSysConfig.getValue("verification_code_mock"))) {
            param.put("code", code);
            param.put("user", userName);
            SMSService.sendVerificationCode(phone, param, type);
        }
        LOG.info("Function:addCode.End.");
        return verCode;
    }*/



    /**
     * 
     * @Title: updateDesignerInfo
     * @Description:更新设计师信息
     * @param @param designer
     * @param @return
     * @throws
     */
    @Override
    public boolean updateDesignerInfo(Users designer) {
        LOG.info("Function:updateDesignerInfo.Start.");
        boolean result = false;
        Users user = (Users) usersDao.getObjectById(cls, designer.getUserId());
        if (null != user) {
            if (!StringUtils.isBlank(designer.getAvatar())) {
                user.setAvatar(designer.getAvatar());
            }
            if (!StringUtils.isBlank(designer.getRealName())) {
                user.setRealName(designer.getRealName().trim());
            }
            if (null != designer.getSex()) {
                user.setSex(designer.getSex());
            }
            if (0 != designer.getRegionId()) {
                Region region = new Region();
                region.setRegionId(designer.getRegionId());
                user.setCity(region);
            }

            int roleId = designer.getRoleId();
            if (2 == roleId) {
                user.setRoleId(roleId);
                // 公司
                if (0 != designer.getCompanyId()) {
                    user.setCompanyId(designer.getCompanyId());
                }
                user.setShopId(0l);
            } else if (3 == roleId) {
                user.setRoleId(roleId);
                // 门店
                if (0 != designer.getCompanyId()) {
                    user.setShopId(designer.getCompanyId());
                }
                user.setCompanyId(0l);
            }

            usersDao.updateObejct(user);
            result = true;
        }
        LOG.info("Function:updateDesignerInfo.End.");
        return result;
    }



    /**
     * 
     * @Title: isValidPassword
     * @Description:
     * @param @param user
     * @param @return
     * @throws
     */
    @Override
    public Users isValidPassword(Users user) {
        LOG.info("Function:isValidPassword.Start.");
        Users result = null;
        Users oldUser = (Users) usersDao.getObjectById(cls, user.getUserId());
        
        String tempPassword=user.getOldPassword();
        if(null==tempPassword||"".equals(tempPassword)){
        	if (null != oldUser) {
                LOG.info("验证用户密码通过.");
                result = oldUser;
            }
        }else{
        	String password = EncodeAndDecodeUtils.encodeStrMD5(user.getOldPassword());
            
            if (null != oldUser && oldUser.getPassword().equals(password)) {
                LOG.info("验证用户密码通过.");
                result = oldUser;
            }
        }
        
        
        LOG.info("Function:isValidPassword.End.");
        return result;
    }



    /**
     * 
     * @Title: updatePassword
     * @Description:修改密码
     * @param @param user
     * @param @return
     * @throws
     */
    /*@Override
    public boolean updatePassword(Users user) {
        LOG.info("Function:updatePassword.Start.");
        boolean result = false;
        user.setPassword(EncodeAndDecodeUtils.encodeStrMD5(user.getPassword()));
        usersDao.updateObejct(user);
        result = true;
        LOG.info("Function:updatePassword.End.");
        return result;
    }*/



    /**
     * 
     * @Title: getUsers
     * @Description:根据id 获取用户
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public Users getUsers(long userId) {
        LOG.info("Function:getUsers.Satrt.");
        Users users = (Users) usersDao.getObjectById(cls, userId);
        LOG.info("Function:getUsers.End.");
        return users;
    }



    /**
     * 
     * @Title: updatePhone
     * @Description:更换绑定手机号
     * @param @param userId
     * @param @param phone
     * @param @return
     * @throws
     */
    @Override
    public boolean updatePhone(long userId, String phone) {
        LOG.info("Function:updatePhone.Start.");
        boolean result = false;
        Users users = (Users) usersDao.getObjectById(cls, userId);
        if (null != users) {
            // 删除该phone的废弃的用户数据 （没有入驻成功的）
            deleteAbandonUser(phone, userId);
            users.setUserName(phone);
            users.setPhone(phone);
            usersDao.updateObejct(users);
            result = true;
        }
        LOG.info("Function:updatePhone.End.");
        return result;
    }



    /**
     * 
     * @Title: getUsers
     * @Description:根据设备id获取用户
     * @param @param deviceId
     * @param @return
     * @throws
     */
    @Override
    public Users getUsers(String deviceId) {
        LOG.info("Function:getUsers.Start.");
        LOG.info("根据设备id获取用户,设备id=" + deviceId);
        Users user = usersDao.getUsersByDeviceId(cls, deviceId);
        LOG.info("Function:getUsers.End.");
        return user;
    }



    /**
     * 
     * @Title: getApplyEntryLog
     * @Description:获取用户申请入驻状态
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public ApplyEntryLog getApplyEntryLog(long userId) {
        LOG.info("Function:getApplyEntryLog.Start.");
        ApplyEntryLog applyEntryLog = applyEntryLogDao.getApplyEntryLog(userId);
        LOG.info("Function:getApplyEntryLog.End.");
        return applyEntryLog;
    }



    /**
     * 
     * @Title: checkSendEnabled
     * @Description:检查用户是否可以发送邀约码
     * @param @param userId
     * @param @param receivePhone
     * @param @return
     * @throws
     */
    /*
     * @Override public boolean checkSendEnabled(long userId, String
     * receivePhone) { LOG.info("Function:checkSendEnabled.Start."); boolean
     * result = false; Users user = usersDao.getObjectById(userId); if (null ==
     * user) { return result; } // 同一设计师一天发送邀约码最多10个 List<InviteCode>
     * inviteCodeList = inviteCodeDao.getTodaySendNumber(userId); if
     * (!CollectionsUtils.isNull(inviteCodeList) && inviteCodeList.size() > 9) {
     * return result; } // 同一目标手机号，每天可发送邀约码最多3个 inviteCodeList =
     * inviteCodeDao.getTodaySendNumber(receivePhone); if
     * (!CollectionsUtils.isNull(inviteCodeList) && inviteCodeList.size() > 2) {
     * return result; } result = true;
     * LOG.info("Function:checkSendEnabled.End."); return result; }
     */

    /**
     * @throws Exception
     * 
     * @Title: sendInviteCode
     * @Description:发送邀约码
     * @param @param userId
     * @param @param receivePhone
     * @param @return
     * @throws
     */
    /*@Override
    public void sendInviteCode(long userId, String receivePhone) throws Exception {
        // 获取邀约码
        Users user = getUserByUserId(userId);
        if (null == user) {
            LOG.error("Function:sendInviteCode user is null");
            throw new Exception("user is null");
        }
        if (StringUtils.isBlank(user.getInviteCode())) {
            LOG.error("Function:sendInviteCode user inviteCode is null");
            throw new Exception("user inviteCode is null");
        }
        // 短信发送
        SMSService.sendInviteIdCode(receivePhone, user.getInviteCode(), user.getUserName());
    }*/
    
    /**
     * @throws Exception
     * 
     * @Title: sendInviteCode
     * @Description:发送邀约码
     * @param @param userId
     * @param @param receivePhone
     * @param @return
     * @throws
     */
    /*@Override
    public void sendInviteCode(long userId, String receivePhone, String templateName) throws Exception {
        // 获取邀约码
        Users user = getUserByUserId(userId);
        if (null == user) {
            LOG.error("Function:sendInviteCode user is null");
            throw new Exception("user is null");
        }
        if (StringUtils.isBlank(user.getInviteCode())) {
            LOG.error("Function:sendInviteCode user inviteCode is null");
            throw new Exception("user inviteCode is null");
        }
        // 短信发送
        SMSService.sendInviteIdCode(receivePhone, user.getInviteCode(), user.getUserName(),templateName);
    }*/



    /**
     * 
     * @Title: getInviteCodeList
     * @Description:获取邀约码发送记录
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public List<InviteCode> getInviteCodeList(long userId, int pageIndex, int pageSize) {
        LOG.info("Function:getInviteCodeList.Start.");
        List<InviteCode> inviteCodeList = inviteCodeDao.getInvideCodeByUserId(userId, pageIndex * pageSize,
                pageSize > 0 ? pageSize : 10);
        LOG.info("Function:getInviteCodeList.End.");
        return inviteCodeList;
    }



    /**
     * 
     * @Title: getInviteCodeTotal
     * @Description:2.6.6.1 获取邀约码发送记录(Total)
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public String getInviteCodeTotal(long userId) {
        LOG.info("Function:getInviteCodeTotal.Start.");
        int total = inviteCodeDao.getInviteCodeTotal(userId);
        Map<String, Object> totalMap = new HashMap<String, Object>();
        totalMap.put("total", total);
        LOG.info("Function:getInviteCodeTotal.End.");
        return JsonUtils.objectFormatToString(totalMap);
    }



    /**
     * 
     * @Title: checkInviteCode
     * @Description:检查邀约码是否可能
     * @param @param inviteCode
     * @param @return
     * @throws
     */
    /*@Override
    public InviteCode checkInviteCode(String inviteCode, Byte status) {
        LOG.info("Fuction:checkInviteCode.Start.");
        InviteCode inviteCodet = inviteCodeDao.getInvideCodeByCode(InviteCode.class, inviteCode, status);
        LOG.info("Fuction:checkInviteCode.End.");
        return inviteCodet;
    }*/



    /**
     * 
     * @Title: getUserCenter
     * @Description:获取用户中心(申请入驻状态，导购佣金等 )
     * @param @param userId
     * @param @param userRole
     * @param @param platString
     * @param @return
     * @throws
     */
    @Override
    public String getUserCenter(long userId, int userRole, String plat) {
        LOG.info("Function:getUserCenter.Start.");
        Users users = usersDao.getObjectById(userId);
        String userCenter = "";

        if (userRole <= 1 || null == users) {
            // userRole <=1 该用户没登录或者角色不是导购 获取用户中心（包含申请入驻状态）
            userCenter = getApplyStatus(userId, plat);
        } else if (userRole > 1) {
            // userRole>1 该用户角色为导购 已登录 获取用户中心（包含佣金信息）
            userCenter = getDesigenerHomePage(userId, userRole, plat);
        }

        LOG.info("Function:getUserCenter.End.");
        return userCenter;
    }



    /**
     * 
     * @Title: getDesigenerHomePage
     * @Description:获取用户中心 ：用户角色为导购 已登录 获取用户中心（包含佣金信息）
     * @param @param userId
     * @param @param userRole
     * @param @param plat
     * @param @return
     * @return String
     * @throws
     */
    private String getDesigenerHomePage(long userId, int userRole, String plat) {
        LOG.info("Function:getDesigenerHomePage.Start.");
        String xmlPath = getXmlPath(userRole, null, plat);

        DesignerInfo designerInfo = new DesignerInfo();

        // 用户信息
        Users user = (Users) usersDao.getObjectById(Users.class, userId);
        if (null == user) {
            return null;
        }
        setCompany(user);
        designerInfo.setName(user.getRealName());
        designerInfo.setImagv(user.getAvatar());// 头像
        designerInfo.setCompany(user.getCompany().getCompanyName());

        // 佣金信息
        Commission commission = getCommission(1);
        designerInfo.setCommission(commission);

        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlPath);
        // 设置值
        sectionsEle = XmlUtils.assembleElement(sectionsEle, designerInfo, XML_TYPE);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        LOG.info("Function:getDesigenerHomePage.End.");
        return document.asXML();
    }



    /*
     * 调用规则查询 规则：COMM_HSV1_CommissionInfo 传入格式：{"user_id":"1"} 返回 C-user_id
     * I-NotCheckoutPrice J-HistoryIncome K-NewIncome M-newIncomeTime
     */
    private Commission getCommission(long userId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("COMM_HSV1_CommissionInfo");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(responseBaseData.getCode())
                || CollectionsUtils.isNull(responseBaseData.getRows())) {
            LOG.error("getCommission fail. data:" + resultData);
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) json;

        Commission commission = null;
        try {
            commission = (Commission) BeanBuilder.buildBean(Commission.class.newInstance(), mapListJson.get(0));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return commission;
    }



    /**
     * 
     * @Title: getApplyStatus
     * @Description: userRole <=1 该用户没登录或者角色不是导购 获取用户中心（包含申请入驻状态）
     * @param @param userId
     * @param @param plat
     * @param @return
     * @return String
     * @throws
     */
    private String getApplyStatus(long userId, String plat) {
        LOG.info("Function:getApplyStatus.Start.");

        ApplyEntryLog applyEntryLog = applyEntryLogDao.getApplyEntryLog(userId);
        String xmlPath = getXmlPath(0, applyEntryLog, plat);
        if (null != applyEntryLog) {
            applyEntryLog.setUrl(AppSysConfig.getValue(ContentUtils.APPLYENTRY_STATUS_URL) + userId + "&plat=" + plat);
        }

        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlPath);
        // 设置值
        sectionsEle = XmlUtils.assembleElement(sectionsEle, applyEntryLog, XML_TYPE);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        LOG.info("Function:getApplyStatus.End.");
        return document.asXML();

    }



    /**
     * 
     * @Title: getXmlPath
     * @Description:获取user center xml path
     * @param @param userRole
     * @param @param applyEntryLog
     * @param @param plat
     * @param @return
     * @return String
     * @throws
     */
    private String getXmlPath(int userRole, ApplyEntryLog applyEntryLog, String plat) {
        StringBuffer xmlName = new StringBuffer();
        xmlName.append(XML_PATH).append(XML_TYPE).append("/").append(XML_TYPE + "_");
        if (userRole > 1) {
            xmlName.append("designer_homePage_").append(plat).append(".xml");
            return xmlName.toString();
        }
        String applyStatusXmlName = "";
        // 未申请入驻
        if (null == applyEntryLog) {
            applyStatusXmlName = "notApply_";
        } else {
            // 申请入驻
            // 申请入驻状态 0=待审核 1=通过 2=驳回3=作废
            int applyStatus = applyEntryLog.getApplyStatus();
            if (0 == applyStatus) {
                applyStatusXmlName = "applyProcess_";
            } else if (1 == applyStatus) {
                applyStatusXmlName = "applySucceed_";
            } else if (2 == applyStatus) {
                applyStatusXmlName = "applyFailed_";
            }
        }
        xmlName.append(applyStatusXmlName).append(plat).append(".xml");
        return xmlName.toString();
    }



    /**
     * 
     * @Title: getApplyEntryStatus
     * @Description:查询行业专家入驻 状态 返回html页面url
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public ApplyEntryLog getApplyEntryStatus(long userId, String plat) {
        LOG.info("Function:getApplyEntryStatus.Start.");
        ApplyEntryLog applyEntryLog = new ApplyEntryLog();
        applyEntryLog.setUrl(AppSysConfig.getValue(ContentUtils.APPLYENTRY_STATUS_URL) + userId + "&plat=" + plat);
        LOG.info("Function:getApplyEntryStatus.End.");
        return applyEntryLog;
    }



    /**
     * 
     * @Title: setCompany
     * @Description: 为用户信息赋值公司或门店信息
     * @param @param user 参数说明
     * @return void 返回类型
     * @throws
     */
    public void setCompany(Users user) {
        Long companyId = user.getCompanyId();
        Long shopId = user.getShopId();

        int type = 0;
        long id = 0;
        int roleId = user.getRoleId();
        if (Constants.UserRoleId.DESIGNER == roleId) {
            id = companyId == null ? 0 : companyId;
            type = 1;
        } else if (Constants.UserRoleId.SHOPPER == roleId) {
            id = shopId == null ? 0 : shopId;
            type = 2;
        } else if (Constants.UserRoleId.COMPANY == roleId) {
            id = user.getUserId();
            type = 3;
        } else if (Constants.UserRoleId.PUSHER == roleId) {
            id = user.getUserId();
            type = 4;
        }

        Company company = companyService.getCompanyById(id, type);
        user.setCompany(company);
    }



    @Override
    public boolean checkInviteCode(String code) {
        return null != getInviteCode(code);
    }

    @Override
    public InviteCode getInviteCode(String code) {
        InviteCode inviteCode = null;
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_code_validated");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("code", code);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if ("1".equals(jsonMap.get("is_validated"))) {
                inviteCode = new InviteCode();
                inviteCode.setCode(jsonMap.get("code"));
                inviteCode.setUseUserId(Long.parseLong(jsonMap.get("use_user_id")));
            }
        } else {
            LOG.error("getInviteCode fail. data:" + data);
        }
        return inviteCode;
    }

}
