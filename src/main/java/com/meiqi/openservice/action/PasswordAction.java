/*
 * File name: PasswordAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年7月21日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.BaseRespInfo;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.util.EmailUtil;
import com.meiqi.openservice.commons.util.Tool;

/**
 * <class description>
 * 
 * @author: luzicong
 * @version: 1.0, 2015年7月21日
 */
@Service
public class PasswordAction extends BaseAction {

    private static final Logger LOG                      = Logger.getLogger(PasswordAction.class);

    private static final String GETBACK_EMAIL_TITLE = "登录密码重置";
    
    private static final String GETBACK_EMAIL_CONTENT = "尊敬的先生/女士：\n  您好！修改%s登录密码的申请已提交。\n" + "  请点击或把下面网页地址复制到浏览器地址栏中打开：\n  %s&\n\n此邮件由系统自动发送，请勿直接回复。";

    @Autowired
    private IMushroomAction     mushroomAction;

    @Autowired
    private UserAction          userAction;
    
    @Autowired
    private IDataAction     dataAction;

    @Autowired
    private SmsAction smsAction;

    /**
     * 通过短信重置密码
     * @return
     */
    public String resetPasswordBySms(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
    	Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
    	
    	 // 获取site_id
        String site_id = "0";
        
        if(paramMap.containsKey("site_id")){
        	site_id=paramMap.get("site_id");
    	}
        
        ResponseInfo respInfo = new ResponseInfo();
        String phone="";
        if(paramMap.containsKey("phone")){
        	phone=paramMap.get("phone").trim();
        	if("".equals(phone)){
        		respInfo.setCode(DsResponseCodeData.PHONE_IS_EMPTY.code);
                respInfo.setDescription(DsResponseCodeData.PHONE_IS_EMPTY.description);
                return JSON.toJSONString(respInfo);
        	}
        }else{
        	respInfo.setCode(DsResponseCodeData.PHONE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.PHONE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
    	String type="1";
    	if(paramMap.containsKey("type")){
    		type=paramMap.get("type");
    	}
    	
    	String verifyCode="";
    	if(paramMap.containsKey("verifyCode")){
    		verifyCode=paramMap.get("verifyCode").trim();
    		if("".equals(verifyCode)){
    			respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
                respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
                return JSON.toJSONString(respInfo);
    		}
    	}else{
    		respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
    	}
    	
    	String id="";
    	//使用电话去查询用户信息
    	DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("YJG_HSV1_UpdatePassword");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userName", phone);
		 dsManageReqInfo.setParam(param);
		String resultData = dataAction.getInnerData(dsManageReqInfo);
		RuleServiceResponseData responseData = null;
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		List<Map<String,String>> mapList=responseData.getRows();
    	if(mapList.size()<1){
    		respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
            return JSON.toJSONString(respInfo);
    	}else{
    		Map<String,String> map=mapList.get(0);
    		id=map.get("user_id");
    	}
    	
    	if("".equals(id)){
    		respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
            return JSON.toJSONString(respInfo);
    	}
    	
    	
    	RepInfo req = new RepInfo();
        req.setAction("smsAction");
        req.setMethod("validateSmsCode");
        Map<String, Object> param1 = new HashMap<String, Object>();
        param1.put("serviceName", "SMS_Send");
        Map<String, String> param2 = new HashMap<String, String>();
        param2.put("receive_phone", phone);
        param2.put("code_value", verifyCode);
        if(paramMap.containsKey("template_id")){
        	param2.put("template_id", paramMap.get("template_id"));
        }
        param2.put("site_id", "0");
        param2.put("web_site", "0");
        param2.put("type", "" + type);
        param1.put("param", param2);
        req.setParam(JsonUtils.objectFormatToString(param1));
        com.meiqi.data.handler.BaseRespInfo baseRespInfo=(com.meiqi.data.handler.BaseRespInfo) smsAction.validateSmsCode(request, response, req);
        //验证成功
        if(baseRespInfo.getCode().equals("0")){
        	if(paramMap.containsKey("password")){
        		String password=paramMap.get("password").trim();
        		if("".equals(password)){
        			respInfo.setCode("1");
                    respInfo.setDescription("缺少新密码！");
                    return JSON.toJSONString(respInfo);
        		}
        		Action action = new Action();
                action.setServiceName("test_ecshop_ecs_users");
                action.setType("U");
                
                SqlCondition condition = new SqlCondition();
                condition.setKey("user_id");
                condition.setOp("=");
                condition.setValue(id);

                

                List<SqlCondition> conditions = new ArrayList<SqlCondition>();
                conditions.add(condition);

                Where where = new Where();
                where.setPrepend("and");
                where.setConditions(conditions);
                action.setWhere(where);

                Map<String, Object> set = new HashMap<String, Object>();
                set.put("password", MD5Util.MD5(password));
                action.setSet(set);
                
                DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                actionReqInfo.setServiceName("MUSH_Offer");

                List<Action> actions = new ArrayList<Action>();
                actions.add(action);

                Map<String, Object> mushparam = new HashMap<String, Object>();
                mushparam.put("actions", actions);
                mushparam.put("transaction", 1);
                actionReqInfo.setParam(mushparam);
                String res = mushroomAction.offer(actionReqInfo);

                ActionRespInfo actionRespInfo = DataUtil.parse(res, ActionRespInfo.class);
                if(actionRespInfo.getCode().equals("0")){
                	respInfo.setCode("0");
                    respInfo.setDescription("修改密码成功！");
                    return JSON.toJSONString(respInfo);
                }else{
                	respInfo.setCode("1");
                    respInfo.setDescription("修改密码失败！");
                    return JSON.toJSONString(respInfo);
                }
        	}else{
        		respInfo.setCode("1");
                respInfo.setDescription("缺少新密码！");
                return JSON.toJSONString(respInfo);
        	}
        }else{//验证失败
        	respInfo.setCode(DsResponseCodeData.SMS_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.SMS_CODE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
    }
    
    /**
     * 
     * 处理发送重置密码邮件请求
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String sendResetPasswordEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.debug("Function:sendResetEmail.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
        
        // 获取site_id
        String site_id = paramMap.get("site_id");
        if (StringUtils.isBlank(site_id)) {
            site_id = "0";
        }

        // 检查输入的邮件是否为空
        String email = paramMap.get("email");
        if (StringUtils.isBlank(email)) {
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        // 设置用户类型
        String userType = paramMap.get(Constants.USER_TYPE);
        if (StringUtils.isBlank(userType)) {
            userType = Constants.UserType.LEJJ_USER.getIndex() + ""; // 默认为爱有窝(乐家居)
        }
        
        int index = Integer.parseInt(userType);
        if (index < Constants.UserType.HEMEIJU_USER.getIndex() || index > Constants.UserType.LEJJ_USER.getIndex()) {
            respInfo.setCode(DsResponseCodeData.USER_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.USER_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        String fromEmail = SysConfig.getValue("mail.customer_service_userName_" + userType);
        String content = null;
        String url = SysConfig.getValue("reset_password_url_" + userType);
        String userIdentifer = email;
        Constants.UserType eUserType = null;
        if (userType.equals(Constants.UserType.LEJJ_USER.getIndex() + "")) { //爱有窝
            eUserType = Constants.UserType.LEJJ_USER;
            
            // 检查输入的验证码是否为空
            String verifyCode = paramMap.get("verifyCode");
            if (StringUtils.isBlank(verifyCode)) {
                respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
                respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
                return JSON.toJSONString(respInfo);
            }
            
            // 只有爱有窝检查校验码
            boolean r = Tool.verifyCode(request, verifyCode, Constants.CodeType.LEJJ_GETBACK_PWD,false);
            if (!r) {
                respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
                respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
                return JSON.toJSONString(respInfo);
            }
            
            url = String.format(url, email);
            content = String.format(GETBACK_EMAIL_CONTENT, eUserType.getName(), url);
        }
        else { //优家购
            eUserType = Constants.UserType.HEMEIJU_USER;
            
            // 检查输入的userName 是否为空
            userIdentifer = paramMap.get("userName");
            if (StringUtils.isBlank(userIdentifer)) {
                respInfo.setCode(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.code);
                respInfo.setDescription(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.description);
                return JSON.toJSONString(respInfo);
            }
            
            url = String.format(url, email, userIdentifer);
            content = String.format(GETBACK_EMAIL_CONTENT, eUserType.getName(), url);
        }

        if (!userAction.userIsExist(userIdentifer, userType, site_id)) {
            respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
            return JSON.toJSONString(respInfo);
        }

        // 向邮件地址发送带有重置密码链接的邮件
        String title = eUserType.getName() + GETBACK_EMAIL_TITLE;

        try {
            //EmailUtil.sendTextEmail(email, title, content);
            EmailUtil.sendMessage(null, email, null, fromEmail, title, content, null);
        } catch (MessagingException e) {
            e.printStackTrace();
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        LOG.debug("Function:sendResetEmail.End.");
        return JSON.toJSONString(respInfo);
    }



    /**
     * 
     * 重置指定邮件地址对应帐户的密码
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String reset(HttpServletRequest request, HttpServletResponse response, RepInfo reqInfo) {
        LOG.debug("Function:reset.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(reqInfo.getParam(), Map.class);

        // 获取site_id
        Integer site_id = 0;
        if (paramMap.containsKey("site_id")) {
            site_id = Integer.parseInt(paramMap.get("site_id"));
        }

        // 检查两次输入的密码是否有效且一致
        String pwd = paramMap.get("pwd");
        String confirmPwd = paramMap.get("confirmPwd");
        if (!isPassWord(pwd) || !isPassWord(confirmPwd) || !pwd.equals(confirmPwd)) {
            respInfo.setCode(DsResponseCodeData.PWD_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.PWD_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }

        // 检查输入的邮件是否为空
        String email = paramMap.get("email");
        if (StringUtils.isBlank(email)) {
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        // 根据 emailAddress 更新用户密码
        if (!updatePassWord(email, pwd, site_id)) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description);
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        LOG.debug("Function:reset.End.");
        return JSON.toJSONString(respInfo);
    }



    // 更新用户password
    private boolean updatePassWord(String email, String pwd, Integer site_id) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        Action action = buildResetPasswordAction(email, pwd, site_id);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String res = mushroomAction.offer(actionReqInfo);

        ActionRespInfo respInfo = DataUtil.parse(res, ActionRespInfo.class);
        if (null != respInfo && !CollectionsUtils.isNull(respInfo.getResults())) {
            ActionResult actionResult = respInfo.getResults().get(0);
            if (null != actionResult && "0".equals(actionResult.getCode())) {
                if (actionResult.getUpdateCount() == 1) {
                    return true;
                }
            }
        }
        
        return false;
    }



    private boolean isPassWord(String passWord1) {
        return !StringUtils.isBlank(passWord1);
    }
    
    /**
     * 根据旧密码重置密码
    * @Title: changePassword 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
    public Object changePassword(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
        
        ResponseInfo respInfo = new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String userName=map.get("userName")==null?"":map.get("userName").toString();
        String type=map.get("type")==null?"":map.get("type").toString();
        String oldPassword=map.get("oldPassword")==null?"":map.get("oldPassword").toString();
        String newPassword=map.get("newPassword")==null?"":map.get("newPassword").toString();
        
        if(StringUtils.isEmpty(userName)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("userName不能为空");
            return respInfo;
        }
        if(StringUtils.isEmpty(type)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("type不能为空");
            return respInfo;
        }
        if(StringUtils.isEmpty(oldPassword)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("oldPassword不能为空");
            return respInfo;
        }
        
        if(StringUtils.isEmpty(newPassword)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("newPassword不能为空");
            return respInfo;
        }
        if(oldPassword.equals(newPassword)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("新旧密码不能相同");
            return respInfo;
        }
        
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("LJG_HSV1_orderpaylogin");
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("userName", userName);//可以用户名，email，电话号码
        params.put("password", oldPassword);
        params.put("type", type);
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setParam(params);
        
        String resultData = dataAction.getData(dsReqInfo, "");
        
        RuleServiceResponseData resp = DataUtil.parse(resultData, RuleServiceResponseData.class);
        Map<String, String> result = resp.getRows().get(0);
        if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
            if ("1".equals(result.get("login"))) {
                 String userId = result.get("userID");
                 DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                 actionReqInfo.setServiceName("MUSH_Offer");
                 Action action = new Action();
                 action.setType("U");
                 action.setServiceName("test_ecshop_ecs_users");//用户表合并后都统一用这个
                 Map<String, Object> set = new HashMap<String, Object>();
                 set.put("password", MD5Util.MD5(newPassword));
                 action.setSet(set);
                 
                 Where where = new Where();
                 where.setPrepend("and");
 
                 List<SqlCondition> cons = new ArrayList<SqlCondition>();
                 SqlCondition con = new SqlCondition();
                 con.setKey("user_id");
                 con.setOp("=");
                 con.setValue(userId);
                 cons.add(con);
 
                 where.setConditions(cons);
                 action.setWhere(where);
                 
                 List<Action> actions = new ArrayList<Action>();
                 actions.add(action);

                 Map<String, Object> param = new HashMap<String, Object>();
                 param.put("actions", actions);
                 param.put("transaction", 1);
                 actionReqInfo.setParam(param);
                 String res = mushroomAction.offer(actionReqInfo);
                 SetServiceResponseData actionResponse= DataUtil.parse(res, SetServiceResponseData.class);
                 if(!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())){
                     respInfo.setCode(DsResponseCodeData.ERROR.code);
                     respInfo.setDescription(DsResponseCodeData.ERROR.description);
                     return respInfo;
                 }
        }else{
            respInfo.setCode(DsResponseCodeData.OLD_PWD_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.OLD_PWD_NOT_RIGHT.description);
            return respInfo;
        }
    }else{
        respInfo.setCode(DsResponseCodeData.ERROR.code);
        respInfo.setDescription(DsResponseCodeData.ERROR.description);
        return respInfo;
    }
   return respInfo;
  }
    
    private Action buildResetPasswordAction(String email, String pwd, Integer site_id) {
        Action action = new Action();
        action.setSite_id(site_id);
        action.setServiceName("test_ecshop_ecs_users");
        action.setType("U");

        // user_name 与邮件地址一致，能唯一查找到用户
        SqlCondition condition = new SqlCondition();
        condition.setKey("email");
        condition.setOp("=");
        condition.setValue(email);

        SqlCondition condition2 = new SqlCondition();
        condition2.setKey("user_name");
        condition2.setOp("=");
        condition2.setValue(email);

        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);
        conditions.add(condition2);

        Where where = new Where();
        where.setPrepend("or");
        where.setConditions(conditions);
        action.setWhere(where);

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("password", MD5Util.MD5(pwd));
        action.setSet(set);

        return action;
    }

}
