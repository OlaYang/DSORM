/**
 * @Title: RegisterAction.java
 * @Package com.meiqi.openservice.action.register
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * @version V1.0
 */
package com.meiqi.openservice.action.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.action.UsersAction;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.ListUtil;
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
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.LADPEnDecryptUtil;
import com.meiqi.openservice.commons.util.LDAPAuthentication;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;
import com.meiqi.util.LogUtil;

/**
 * @ClassName: LoginVerifyAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * 
 */
@Service
public class LoginAction extends BaseAction {

    @Autowired
    private IMemcacheAction memcacheService;
    @Autowired
    private IDataAction     dataAction;
    @Autowired
    private UsersAction     usersAction;
    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private SmsAction       smsAction;



    public Object login(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        ResponseInfo respInfo = new ResponseInfo();

        DsManageReqInfo dsReqInfo = DataUtil.parse(repInfo.getParam(), DsManageReqInfo.class);
        String resultData = dataAction.getData(dsReqInfo, "");
        // LogUtil.info("LoginAction login rule result resultData:" +
        // resultData);
        RuleServiceResponseData resp = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
            Map<String, String> result = resp.getRows().get(0);
            if ("1".equals(result.get("login"))) {
                try {
                    Map<String, Object> param = dsReqInfo.getParam();
                    String isAutoLogin = param.get("isAutoLogin").toString();
                    String type = param.get("type").toString();
                    String userId = result.get("userID");
                    String userName = result.get("userName");
                    String key = "islogin_" + userId + "_" + type;
                    long time = 0;
                    Cookie uid = new Cookie("uid", userId);
                    Cookie name = new Cookie("userName",URLEncoder.encode(userName,"UTF-8"));
                    uid.setPath("/");
                    name.setPath("/");
                    if ("0".equals(isAutoLogin)) {
                        // 是非自动登录，缓存时间设置为30分钟
                        time = 60 * 60 * 1000;
                        uid.setMaxAge(3600);//1小时
                        name.setMaxAge(3600);//1小时
                    } else {
                        // 是自动登录，缓存时间设置为3天
                        time = 3 * 24 * 60 * 60 * 1000;
                        uid.setMaxAge(3 * 24 * 60 * 60);
                        name.setMaxAge(3 * 24 * 60 * 60);//3天
                    }
                    response.addCookie(uid);
                    response.addCookie(name);
                    String isAutoLoginKey = "isAutologin_" + userId + "_" + type;
                    boolean r1 = memcacheService.putCache(isAutoLoginKey, isAutoLogin);
                    String jsonResult=JSONObject.toJSONString(result);
                    boolean r2 = memcacheService.putCache(key, jsonResult, time);
    
                    String sessionId = request.getSession().getId();
                    String sessionIdLoginKey = "login_" + sessionId;
                    boolean r3 = memcacheService.putCache(sessionIdLoginKey, userId+"_"+type);
    
                    HttpSession session = request.getSession();
                    session.setAttribute(key, jsonResult);
                    if ((r1 && r2 && r3) || session.getAttribute(key) != null) {
                        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
                        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
                        respInfo.setObject(result);
                    } else {
                        respInfo.setCode(DsResponseCodeData.ERROR.code);
                        respInfo.setDescription(DsResponseCodeData.ERROR.description);
                    }
                    // 登录之后存放缓存
                    // putCacheAfterLogin(request,response,type,JSONObject.toJSONString(result),isAutoLogin);
    
                    String sourceFrom=dsReqInfo.getParam().get("sourceFrom")==null?"":dsReqInfo.getParam().get("sourceFrom").toString();
                    String from_user_id=dsReqInfo.getParam().get("from_user_id")==null?"":dsReqInfo.getParam().get("from_user_id").toString();
                    //合并购物车
                    mergeCart(userId, sessionId, from_user_id, type, sourceFrom);
                    
                } catch (UnsupportedEncodingException e) {
                    LogUtil.error("LoginAction-login:"+e.getMessage());
                }
            } else {
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(result.get("if_login"));
            }
        } else {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(resultData);
        }
        return respInfo;
    }


    public Object appLogin(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo respInfo = (ResponseInfo) login(request, response, repInfo);
        JSONObject loginJson=(JSONObject) JSONObject.toJSON(respInfo);
        if(loginJson.containsKey("object")){
        	com.alibaba.fastjson.JSONArray jsonArray=new com.alibaba.fastjson.JSONArray();
        	jsonArray.add(loginJson.get("object"));
        	loginJson.put("rows", jsonArray);
        	loginJson.remove("object");
        }
        return loginJson.toJSONString();
    }


    /**
     * 
    * @Title: putCacheAfterLogin 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param type
    * @param @param userInfo
    * @param @param isAutoLogin
    * @param @param isAutoLoginTime 自动登录的保持时间  单位秒
    * @param @return  参数说明 
    * @return int    返回类型 
    * @throws
     */
    public int putCacheAfterLogin(HttpServletRequest request, HttpServletResponse response, String type,
            Map<String, String> userInfo, String isAutoLogin,int isAutoLoginTime) {
        String userId = userInfo.get("userID").toString();
        String userName = userInfo.get("userName").toString();
        try {
            HttpSession session = request.getSession();
            type = !type.equals("")||type!=null?type:"1";//新增
            String key = "islogin_" + userId + "_" + type;
            session.setAttribute(key, JSONObject.toJSONString(userInfo));
            long time = 0;
            
            Cookie uid = new Cookie("uid", userId);
            uid.setPath("/");
            Cookie name = new Cookie("userName",URLEncoder.encode(userName,"UTF-8"));
            name.setPath("/");
            if ("0".equals(isAutoLogin)) {
                // 是非自动登录，缓存时间设置为30分钟
                time = 30 * 60 * 1000;
                uid.setMaxAge(1800);
                name.setMaxAge(1800);//半小时
            } else {
                // 是自动登录，缓存时间设置为3天
                time = isAutoLoginTime*1000;//毫秒
                uid.setMaxAge(isAutoLoginTime);
                name.setMaxAge(isAutoLoginTime);
            }
            response.addCookie(uid);
            response.addCookie(name);
            
            String isAutoLoginKey = "isAutologin_" + userId + "_" + type;
            memcacheService.putCache(isAutoLoginKey, isAutoLogin);
            memcacheService.putCache(key, JSONObject.toJSONString(userInfo), time);
    
            String sessionIdLoginKey = "login_" + request.getSession().getId();
            memcacheService.putCache(sessionIdLoginKey, userId+"_"+type);
        } catch (UnsupportedEncodingException e) {
            LogUtil.error("LoginAction-putCacheAfterLogin:"+e.getMessage());
        }
        return 1;
    }



    /**
     * 获取用户信息的方法
     * 
     * @Title: getUserInfo
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param userIdentifer
     * @param @param type
     * @param @return 参数说明
     * @return Object 返回类型
     * @throws
     */
    public Map<String, String> getUserInfo(String userIdentifer, String user_type) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userIdentifer);// userName可以是用户名，email,电话号码
        param.put("type", user_type);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("LJG_HSV2_orderpaylogin");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if(responseBaseData.getRows() == null){
            throw new RuntimeException("用户数据异常["+userIdentifer+"]");
        }
        if (responseBaseData.getRows().size() <= 0) {
            return null;
        }
        return responseBaseData.getRows().get(0);
    }



    
    public Object appSmsLogin(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        return appResultWraper(smsLogin(request, response, repInfo));
    }
    
    public Object smsLogin(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        String resultData = "";
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        Map<String, String> mapParam = DataUtil.parse(repInfo.getParam(), Map.class);
        Map<String, String> map = DataUtil.parse(JSON.toJSONString(mapParam.get("param")), Map.class);

        String userIdentifer = map.get("receive_phone") == null ? "" : map.get("receive_phone").toString();
        if(StringUtils.isEmpty(userIdentifer)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("receive_phone不能为空");
            return respInfo;
        }
        String user_type = map.get("user_type") == null ? "" : map.get("user_type").toString();//用户类型
        String sourceFrom = map.get("sourceFrom") == null ? "" : map.get("sourceFrom").toString();//请求来源
        String from = map.get("from") == null ? "0" : map.get("from").toString();
        
        if(StringUtils.isEmpty(user_type)){
            user_type="1";
        }
        String avatar = "";
        if(map.containsKey("param")){
            Map<String, String> avaMap = DataUtil.parse(JSON.toJSONString(map.get("param")), Map.class);
            avatar = avaMap.get("avatar") == null ? "" : avaMap.get("avatar").toString();//获取默认头像
        }
        String isAutoLogin = "0";
        // 验证短信验证码正确性
        BaseRespInfo baseInfo = (BaseRespInfo) smsAction.validateSmsCode(request, response, repInfo);
        if ("1".equals(baseInfo.getCode())) {
            respInfo.setCode(DsResponseCodeData.SMS_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.SMS_CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        } else {
            Map<String, String> userInfo = getUserInfo(userIdentifer, user_type);
            if (userInfo == null || StringUtils.isEmpty(userInfo.get("userID"))) {
                if(com.meiqi.openservice.commons.config.Constants.UserType.APP_MENDIANBAO_USER.getIndex()==Integer.parseInt(user_type) || 
                    (com.meiqi.openservice.commons.config.Constants.UserType.HEMEIJU_USER.getIndex()==Integer.parseInt(user_type) 
                     && com.meiqi.openservice.commons.config.Constants.SourceFrom.APP.equals(sourceFrom))){
                    //如果是APP门店宝用户类型或者是APP普通用户类型，如果没有相关的用户信息，那么返回登录失败
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setDescription("用户不存在");
                    return respInfo;
                }
                // 如果用户不存在那么注册一个用户
                // 保存用户
                DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                actionReqInfo.setServiceName("MUSH_Offer");

                String serviceName = "";
                if (StringUtils.isNotEmpty(user_type) && Integer.parseInt(user_type)>=0) {
                    serviceName = "test_ecshop_ecs_users";
                }
                if (StringUtils.isNotEmpty(serviceName)) {
                    Action action = new Action();
                    action.setType("C");
                    action.setServiceName(serviceName);
                    Map<String, Object> set = new HashMap<String, Object>();
                    set.put("user_name", userIdentifer);// 和美居是用手机号码作为用户名的
                    set.put("is_validated", 1);// 是否生效
                    set.put("visit_count", 1);// 访问次数
                    set.put("reg_time", DateUtils.getSecond());
                    set.put("last_ip", getIp(request));
                    set.put("mobile_phone", userIdentifer);
                    set.put("avatar", avatar);//头像
                    set.put("from", from);

                    action.setSet(set);
                    List<Action> actions = new ArrayList<Action>();
                    actions.add(action);

                    Map<String, Object> param1 = new HashMap<String, Object>();
                    param1.put("actions", actions);
                    param1.put("transaction", 1);
                    actionReqInfo.setParam(param1);
                    SetServiceResponseData actionResponse = null;
                    String res1 = mushroomAction.offer(actionReqInfo);
                    actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
                    if (Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
                        // 查询用户信息
                        userInfo = getUserInfo(userIdentifer, user_type);
                        userInfo.put("firstLogin", "1");//标记用户为新用户，且为第一次登陆
                        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
                        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
                        
                        //合并购物车
                        String from_user_id=map.get("from_user_id") == null ? "" : map.get("from_user_id").toString();//登录前的userid 
                        JSONObject jsonObject = JSONObject.parseObject(res1);
                        com.alibaba.fastjson.JSONArray parseArray = com.alibaba.fastjson.JSONArray.parseArray(jsonObject.getString("results"));
                        String userId = parseArray.getJSONObject(0).get("generateKey").toString();
                        String sessionId = request.getSession().getId();
                        mergeCart(userId, sessionId,from_user_id,user_type,sourceFrom);
                    } else {
                        respInfo.setCode(DsResponseCodeData.ERROR.code);
                        respInfo.setDescription(actionResponse.getDescription());
                    }
                }
            } else {
                //合并购物车
                String from_user_id=map.get("from_user_id") == null ? "" : map.get("from_user_id").toString();//登录前的userid 
                String userId = userInfo.get("userID");
                String sessionId = request.getSession().getId();
                mergeCart(userId, sessionId,from_user_id,user_type,sourceFrom);
            }
            if (StringUtils.isNotEmpty(userInfo.get("userID"))) {
                userInfo.put("login", "1");
                // 登录之后存放缓存
                int isAutoLoginTime=3 * 24 * 60 * 60;//3天
                putCacheAfterLogin(request, response, user_type, userInfo, isAutoLogin,isAutoLoginTime);
            }

            respInfo.setObject(userInfo);
        }
        return respInfo;
    }


   /**
    * 并购物车 
   * @Title: mergeCart 
   * @Description: TODO(这里用一句话描述这个方法的作用) 
   * @param @param userId
   * @param @param sessionId
   * @param @param from_user_id 登录前的userId
   * @param @param user_type 用户类型
   * @param @param sourceFrom 请求来源
   * @param @return  参数说明 
   * @return SetServiceResponseData    返回类型 
   * @throws
    */
    protected SetServiceResponseData mergeCart(String userId, String sessionId,String from_user_id,String user_type,String sourceFrom) {
        // 获取购物车信息
        List<Map<String, Object>> cartList = getCartInfo(userId, sessionId,from_user_id,user_type,sourceFrom);
        SetServiceResponseData actionResponse = null;
        if (!CollectionsUtils.isNull(cartList)) {
            // 获取更新cart actionReqInfo
            DsManageReqInfo actionReqInfo = getActionReqInfo(cartList, userId);
            if (null != actionReqInfo) {
                // 更新cart
                String res1 = mushroomAction.offer(actionReqInfo);
                actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
            }
        }
        return actionResponse;
    }



    /**
     * 
     * 获取购物车信息 根据userId,seesionId
     *
     * @param userId
     * @param sessionId
     * @return
     */
    private List<Map<String, Object>> getCartInfo(String userId, String sessionId,String from_user_id,String user_type,String sourceFrom) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);
        param.put("session_id", sessionId);
        if(StringUtils.isNotEmpty(from_user_id)){
            param.put("from_user_id", from_user_id);//登录前的userId
        }
        param.put("type", user_type);//用户类型
        if(StringUtils.isNotEmpty(sourceFrom)){
            param.put("sourceFrom", sourceFrom);//请求来源
        }else{
            param.put("sourceFrom", 1);//请求来源,默认为pc
        }

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("HMJ_HSV1_JoinCart");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo, "");
        LogUtil.info("login mergeCart getCartInfo:"+resultData);
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray shopJson = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) shopJson;
        return mapListJson;
    }



    /**
     * 
     * 获取更新购物车action info
     *
     * @param cartList
     * @param userId
     * @return
     */
    private DsManageReqInfo getActionReqInfo(List<Map<String, Object>> cartList, String userId) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        List<Action> actions = new ArrayList<Action>();
        for (Map<String, Object> cart : cartList) {
            String recId = (String) cart.get("rec_id");
            String goodsNumber = (String) cart.get("same_goods_num");
            String isDelate = (String) cart.get("is_delate");
            String isUpdate = (String) cart.get("is_update");
            String isUpdateUserid = (String) cart.get("is_update_userid");
            if (StringUtils.isEmpty(recId)) {
                continue;
            }
            if ("0".equals(isDelate) && "0".equals(isUpdate) && "0".equals(isUpdateUserid)) {
                continue;
            }
            Where where = new Where();
            where.setPrepend("and");

            List<SqlCondition> cons = new ArrayList<SqlCondition>();
            SqlCondition con = new SqlCondition();
            con.setKey("rec_id");
            con.setOp("=");
            con.setValue(recId);
            cons.add(con);
            where.setConditions(cons);

            if ("1".equals(isDelate)) {
                // 删除该条记录
                Action action = new Action();
                action.setType("D");
                String serviceName = "ecs_cart";
                action.setServiceName(serviceName);
                action.setWhere(where);
                actions.add(action);
            } else if ("1".equals(isUpdate)) {
                // 更新该记录商品的数量
                Action action = new Action();
                action.setType("U");
                String serviceName = "ecs_cart";
                action.setServiceName(serviceName);
                Map<String, Object> set = new HashMap<String, Object>();
                set.put("goods_number", goodsNumber);
                action.setSet(set);
                action.setWhere(where);
                actions.add(action);
            } else if ("1".equals(isUpdateUserid)) {
                // 更新该记录对应的user_id sessionId置为空
                Action action = new Action();
                action.setType("U");
                String serviceName = "ecs_cart";
                action.setServiceName(serviceName);
                Map<String, Object> set = new HashMap<String, Object>();
                set.put("session_id", "");
                set.put("user_id", userId);
                action.setSet(set);
                action.setWhere(where);
                actions.add(action);
            }
        }
        if (!CollectionsUtils.isNull(actions)) {
            Map<String, Object> param1 = new HashMap<String, Object>();
            param1.put("actions", actions);
            param1.put("transaction", 1);
            actionReqInfo.setParam(param1);
        } else {
            actionReqInfo = null;
        }
        return actionReqInfo;
    }
    
   /**
    * LADP认证登录
   * @Title: ldapLogin 
   * @Description: TODO(这里用一句话描述这个方法的作用) 
   * @param @param request
   * @param @param response
   * @param @param repInfo
   * @param @return  参数说明 
   * @return Object    返回类型 
   * @throws
    */
    @SuppressWarnings("unchecked")
	public Object ldapLogin(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
        ResponseInfo respInfo = new ResponseInfo();
        DsManageReqInfo dsReqInfo = DataUtil.parse(repInfo.getParam(), DsManageReqInfo.class);
        Map<String, Object> param = dsReqInfo.getParam();
        LogUtil.info("ldapLogin param:"+param);
        String userName=param.get("userName")==null?"":param.get("userName").toString();//ladp账户
        String third_user_name=param.get("third_user_name")==null?"":param.get("third_user_name").toString();//微信号或者是跟工作站账号保持一致（有可能不是微信登录业务，是完全的ladp认证登录）
        String isAutoLogin=param.get("isAutoLogin")==null?"":param.get("isAutoLogin").toString();//微信号
        String pwd=param.get("pwd")==null?"":param.get("pwd").toString();
        String userType=param.get("userType")==null?"":param.get("userType").toString();//用户类型
        String isAutoLoginTimeStr=param.get("isAutoLoginTime")==null?"":param.get("isAutoLoginTime").toString();//自动登录存放缓存的时间设置
        String open_id=param.get("open_id")==null?"":param.get("open_id").toString();
        int isAutoLoginTime=0;
        if(StringUtils.isEmpty(userName)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("userName不能为空");
            return respInfo;
        }
        if(StringUtils.isEmpty(third_user_name)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("third_user_name不能为空");
            return respInfo;
        }
        if(StringUtils.isEmpty(isAutoLogin)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("isAutoLogin不能为空");
            return respInfo;
        }else{
            if("1".equals(isAutoLogin) && StringUtils.isEmpty(isAutoLoginTimeStr)){
                if(StringUtils.isEmpty(isAutoLoginTimeStr)){
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setDescription("isAutoLoginTime不能为空");
                    return respInfo;
                }else{
                    isAutoLoginTime=Integer.parseInt(isAutoLoginTimeStr);
                }
            }
        }
        if(StringUtils.isEmpty(pwd)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("pwd不能为空");
            return respInfo;
        }
        if(StringUtils.isEmpty(userType)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("userType不能为空");
            return respInfo;
        }
        
        boolean login_success=false;
        Map<String,String> result=LDAPAuthentication.authenricate(userName, pwd);
        if(result!=null){
        	login_success=true;
        }else{
        	result = new HashMap<String, String>();
        }
        if(!login_success){
            Tool tool = new Tool();
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("admin_user", userName);
            paramMap.put("admin_pwd", pwd);
            Log log =  LogFactory.getLog("request");
            List<Map<String,String>> mapList2 = null;
            try {
            	mapList2 = (List<Map<String, String>>) tool.getRuleResult("SCM_HSV1_Admin_Login", paramMap, log, "ldapLogin SCM_HSV1_Admin_Login", "SCM_HSV1_Admin_Login");
    		} catch (Exception e) {
    			LogUtil.error("ldapLogin SCM_HSV1_Admin_Login param is: "+paramMap);
    			respInfo.setCode(DsResponseCodeData.ERROR.code);
    			respInfo.setDescription("后台登陆验证失败！");
    			return JSON.toJSONString(respInfo);
    		}
            String if_login = mapList2.get(0).get("if_login");
            String prompt = "";
            if("0".equals(if_login)){
            	prompt = mapList2.get(0).get("prompt");
            	respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(prompt);
                return respInfo;
            }else if("1".equals(if_login)){
            	login_success=true;
            }
        }
        if(login_success){
            //登录成功存缓存
            result.put("userID", third_user_name);
            result.put("userName", userName);
            try {
                result.put("pwd", LADPEnDecryptUtil.encrypt(pwd));
            } catch (Exception e) {
                LogUtil.error("ldapLogin error:"+e+",param:"+param);
            }
            
            String key = "islogin_" + third_user_name + "_" + userType;
            String resultJson=JSONObject.toJSONString(result);
            //存入session
            request.getSession().setAttribute(key, resultJson);
            //存入缓存
            putCacheAfterLogin(request, response, userType, result, isAutoLogin,isAutoLoginTime);
            
            //将账户和第三发账号的绑定关系存放到数据库
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName("YJG_BUV1_admin_info");
            Map<String,Object> params=new HashMap<String, Object>();
            params.put("name", userName);
            serviceReqInfo.setParam(params);
            serviceReqInfo.setNeedAll("1");
            RuleServiceResponseData responseData = null;
            String data =dataAction.getData(serviceReqInfo,"");
            responseData = DataUtil.parse(data, RuleServiceResponseData.class);
            
            if (Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())) {
                List<Map<String, String>> list=responseData.getRows();
                if(ListUtil.notEmpty(list)){
                    Map<String, String> map=list.get(0);
                    String micro_no=map.get("micro_no");
                    DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                    actionReqInfo.setServiceName("MUSH_Offer");
                    Action action = new Action();
                    action.setType("U");
                    action.setServiceName("test_ecshop_ecs_admin_user");
                    Map<String, Object> set = new HashMap<String, Object>();
                    set.put("micro_no", "");
                    set.put("open_id", open_id);
                    action.setSet(set);
    
                    Where where = new Where();
                    where.setPrepend("and");
    
                    List<SqlCondition> cons = new ArrayList<SqlCondition>();
                    SqlCondition con = new SqlCondition();
                    con.setKey("micro_no");
                    con.setOp("=");
                    con.setValue(third_user_name);
                    cons.add(con);
                    where.setConditions(cons);
                    action.setWhere(where);
                    
                    
                    Action action1 = new Action();
                    action1.setType("U");
                    action1.setServiceName("test_ecshop_ecs_admin_user");
                    Map<String, Object> set1 = new HashMap<String, Object>();
                    set1.put("micro_no", third_user_name);
                    set1.put("open_id", open_id);
                    action1.setSet(set1);
    
                    Where where1 = new Where();
                    where1.setPrepend("and");
    
                    List<SqlCondition> cons1 = new ArrayList<SqlCondition>();
                    SqlCondition con1 = new SqlCondition();
                    con1.setKey("user_name");
                    con1.setOp("=");
                    con1.setValue(userName);
                    cons1.add(con1);
                    where1.setConditions(cons1);
                    action1.setWhere(where1);
                    
                    
                    List<Action> actions = new ArrayList<Action>();
                    actions.add(action);
                    actions.add(action1);
                    
                    Map<String, Object> param1 = new HashMap<String, Object>();
                    param1.put("actions", actions);
                    param1.put("transaction", 1);
                    actionReqInfo.setParam(param1);
                    String res1 = mushroomAction.offer(actionReqInfo);
                    SetServiceResponseData actionResponse1 = DataUtil.parse(res1, SetServiceResponseData.class);
                    if (!Constants.SetResponseCode.SUCCESS.equals(actionResponse1.getCode())) {
                        respInfo.setCode(DsResponseCodeData.ERROR.code);
                        respInfo.setDescription(DsResponseCodeData.ERROR.description);
                        return respInfo;
                    }else{
                        if(StringUtils.isNotEmpty(micro_no) && !third_user_name.equals(micro_no)){
                            //将之前的账户退出登录
                            Map<String,Object> m=new HashMap<String,Object>();
                            m.put("userId", micro_no);
                            m.put("type", com.meiqi.openservice.commons.config.Constants.UserType.LADP_USER.getIndex());
                            LoginOutAction.loginOut(request, response, m, memcacheService);
                        }
                    }
                }
            }else{
                respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
                respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
                return respInfo;
            }
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
            respInfo.setObject(resultJson);
        }else{
            respInfo.setCode(DsResponseCodeData.USERNAME_OR_PWD_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.USERNAME_OR_PWD_NOT_RIGHT.description);
        }
        return respInfo;
    }
    
}
