/*
 * File name: UserAction.java
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.SetServiceResponseData;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.SysConfig;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.bean.user.EmailSend;
import com.meiqi.openservice.bean.user.User;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.config.Constants.UserType;
import com.meiqi.openservice.commons.util.ContentUtils;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.DateUtil;
import com.meiqi.openservice.commons.util.EmailUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;
import com.meiqi.openservice.service.impl.UserService;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年7月21日
 */
@Service
public class UserAction extends BaseAction {

    private static final Logger LOG = Logger.getLogger(UserAction.class);
    
    private static final String VERIFY_EMAIL_TITLE = "验证邮箱";
    
    private static final String VERIFY_EMAIL_CONTENT = "尊敬的先生/女士：\n  您好！验证%s邮箱的申请已提交。\n" + "  请点击或把下面网页地址复制到浏览器地址栏中打开以完成验证：\n  %s\n\n此邮件由系统自动发送，请勿直接回复。";
    private static final String VERIFY_EMAIL_CONTENT_HTML = "尊敬的先生/女士：<br>&nbsp;&nbsp;您好！验证%s邮箱的申请已提交。<br>" 
    + "&nbsp;&nbsp;请点击或把下面网页地址复制到浏览器地址栏中打开以完成验证：<br>&nbsp;&nbsp;<a href=\"%s\">%s</a><br><br>此邮件由系统自动发送，请勿直接回复。";


    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;
    
    @Autowired
    private UserService        userService;


    /**
     * 
     * <method description>
     *
     * @param request
     * @param response
     * @param repInfo
     * @return 用户若存在，返回：{"code":"0","description":"成功"}
     *         用户不存在，返回：{"code":"0","description":"无数据"}
     *         传入参数userIdentifer为空，返回：{
     *         "code":"1","description":"用户名、邮箱或电话号码不能为空"}
     *         传入参数type为空，返回：{"code":"1","description":"数据源类型未配置"}
     */
    public String userIsExist(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.debug("Function:userIsExist.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取site_id
        String site_id = paramMap.get("site_id");
        if (StringUtils.isBlank(site_id)) {
            site_id = "0";
        }
        
        // 检查输入参数是否为空
        String userIdentifer = paramMap.get("userIdentifer"); // userIdentifer可以是用户名，email,电话号码
        if (StringUtils.isBlank(userIdentifer)) {
            respInfo.setCode(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        String type = paramMap.get("type"); // type:2 表示系统为乐家居
        if (StringUtils.isBlank(type)) {
            respInfo.setCode(DsResponseCodeData.NOT_SET_DATASOURCE_TYPE.code);
            respInfo.setDescription(DsResponseCodeData.NOT_SET_DATASOURCE_TYPE.description);
            return JSON.toJSONString(respInfo);
        }

        if (!userIsExist(userIdentifer, type, site_id)) {
            respInfo.setCode(DsResponseCodeData.NO_DATA.code);
            respInfo.setDescription(DsResponseCodeData.NO_DATA.description);
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        LOG.debug("Function:userIsExist.End.");
        return JSON.toJSONString(respInfo);
    }



    /**
     * 
     * 验证用户就否存在
     *
     * @param userName
     *            电话/用户名/Email
     * @param type
     *            1=和美居用户 2=乐家居用户
     * @return
     */
    public boolean userIsExist(String userName, String type, String site_id) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();

        /*
         * 规则：USER_HSV1_Check 传入：user_name（电话/用户名/Email） type（1=和美居用户 2=乐家居用户）
         * 传出：judge_tag（0表示没有1代表存在）
         */
        serviceReqInfo.setServiceName("USER_HSV1_Check");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("site_id", site_id);
        queryParam.put("user_name", userName);// userName可以是用户名，email,电话号码
        queryParam.put("type", type);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");
        RuleServiceResponseData responseData = null;
        String data = dataAction.getData(serviceReqInfo,"");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if ("1".equals(jsonMap.get("judge_tag"))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * 处理发送验证邮件请求
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String verifyEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.debug("Function: verifyEmail.Start.");
        ResponseInfo respInfo = new ResponseInfo();

        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取site_id
        String site_id = paramMap.get("site_id");
        if (StringUtils.isBlank(site_id)) {
            site_id = "0";
        }

        // 检查输入的userName是否为空
        String userName = paramMap.get("userName");
        if (StringUtils.isBlank(userName)) {
            respInfo.setCode(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.USER_IDENTIFER_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查输入的邮件是否为空
        String email = paramMap.get("email");
        if (StringUtils.isBlank(email)) {
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查输入的验证码是否为空
        String verifyCode = paramMap.get("verifyCode");
        if (StringUtils.isBlank(verifyCode)) {
            respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        // 设置用户类型
        String userType = paramMap.get(Constants.USER_TYPE);
        if (StringUtils.isBlank(userType)) {
            respInfo.setCode(DsResponseCodeData.USER_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.USER_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        int index = Integer.parseInt(userType);
        if (index < Constants.UserType.HEMEIJU_USER.getIndex() || index > Constants.UserType.LEJJ_USER.getIndex()) {
            respInfo.setCode(DsResponseCodeData.USER_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.USER_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        String codeType = null;
        Constants.UserType eUserType = null;
        if (userType.equals(Constants.UserType.LEJJ_USER.getIndex() + "")) {
            eUserType = Constants.UserType.LEJJ_USER;
            codeType = Constants.CodeType.LEJJ_GETBACK_PWD;
        }
        else {
            eUserType = Constants.UserType.HEMEIJU_USER;
            codeType = Constants.CodeType.HEMEIJU_REGISTER;
        }
        
        // 检查校验码
        boolean r = Tool.verifyCode(request, verifyCode, codeType,false);
        if (!r) {
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }

        if (!userIsExist(userName, userType, site_id)) {
            respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
            return JSON.toJSONString(respInfo);
        }

        // 向邮件地址发送带有重置密码链接的邮件
        String title = eUserType.getName() + VERIFY_EMAIL_TITLE;
        String url = String.format(SysConfig.getValue("verify_email_url_" + userType), email, userName);
        String content = String.format(VERIFY_EMAIL_CONTENT, eUserType.getName(), url);

        try {
            EmailUtil.sendTextEmail(email, title, content);
        } catch (MessagingException e) {
            e.printStackTrace();
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);

        LOG.debug("Function: verifyEmail.End.");
        return JSON.toJSONString(respInfo);
    }
    


    /**
     * 
     * 发送绑定邮箱验证邮件
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String sendVerifyBindEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo respInfo = new ResponseInfo();
        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取web_site
        if (!paramMap.containsKey("web_site")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":web_site");
            return JSON.toJSONString(respInfo);
        }
        int web_site = Integer.parseInt(String.valueOf(paramMap.get("web_site")));

        // 检查输入的url是否为空
        String url = paramMap.get("url");
        if (StringUtils.isBlank(url)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description + " - URL 解码出错");
            return JSON.toJSONString(respInfo);
        }

        // 检查输入的userId是否为空
        if (!paramMap.containsKey("userId")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":userId");
            return JSON.toJSONString(respInfo);
        }
        long userId = Long.parseLong(String.valueOf(paramMap.get("userId")));

        // 检查输入的邮件是否为空
        String email = paramMap.get("email");
        if (StringUtils.isBlank(email)) {
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        // 检查输入的验证码是否为空
        String code = paramMap.get("code");
        if (StringUtils.isBlank(code)) {
            respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查验证码类型
        String codeType = paramMap.get("codeType");
        if (StringUtils.isBlank(codeType) || (!codeType.equals(
                userService.getBindEmailCodeType(Constants.BindEmailStep.VERIFY_BIND_EMAIL, web_site)))) {
            respInfo.setCode(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查校验码
        boolean r = Tool.verifyCode(request, code, codeType, true);
        if (!r) {
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }

        int type = getGlobalEmailType(web_site, Constants.BindEmailStep.VERIFY_BIND_EMAIL);
        int site_id = getSiteId(web_site);

        // 保存发送记录1，发送链接1到邮箱
        EmailSend emailSend = new EmailSend();
        emailSend.setEmail(email);
        emailSend.setUrl(url);
        emailSend.setType(type);
        emailSend.setUserId(userId);
        emailSend.setValid(true);
        long id = 0;
        
        try {
            emailSend = insertEmailSend(emailSend, site_id);
            id = emailSend.getId();
        } catch (Exception e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }
        
        try {
            StringBuffer sb = new StringBuffer(url).append("?type=").append(type).append("&id=").append(id);
            url = sb.toString();
            // 向邮件地址发送带有重置密码链接的邮件
            String title = getShopName(String.valueOf(web_site)) + "-"
                    + Constants.BindEmailStep.VERIFY_BIND_EMAIL_DESC;
            String content = String.format(VERIFY_EMAIL_CONTENT_HTML, getShopName(String.valueOf(web_site)), url, url);
            EmailUtil.sendHtmlEmail(email, title, content);
        } catch (Exception e) {
            // 发送失败，需要删除发送记录
            deleteEmailSend(id, site_id);
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return JSON.toJSONString(respInfo);
    }



    /**
     * 
     * 绑定邮箱
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String bindEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo respInfo = new ResponseInfo();
        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 检查输入的id是否为空
        if (!paramMap.containsKey("id")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }
        int id = Integer.parseInt(String.valueOf(paramMap.get("id")));
        
        // 获取web_site
        if (!paramMap.containsKey("web_site")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":web_site");
            return JSON.toJSONString(respInfo);
        }
        int web_site = Integer.parseInt(String.valueOf(paramMap.get("web_site")));
        int site_id = getSiteId(web_site);

        // 根据id 查找发送记录1，
        EmailSend emailSend = getEmailSend(id, site_id);
        if (!checkIsValid(emailSend)) {
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSON.toJSONString(respInfo);
        }
        // 如果发送记录1有效（24小时内），设为无效，绑定 email，返回成功
        emailSend.setValid(false);
        try {
            saveEmailSend(emailSend, site_id);
            
            bindEmail(emailSend, site_id);
        } catch (Exception e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return JSON.toJSONString(respInfo);
    }



    /**
     * 
     * 发送已验证邮箱验证邮件
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String sendVerifyBindedEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo respInfo = new ResponseInfo();
        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取web_site
        if (!paramMap.containsKey("web_site")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":web_site");
            return JSON.toJSONString(respInfo);
        }
        int web_site = Integer.parseInt(String.valueOf(paramMap.get("web_site")));
        int site_id = getSiteId(web_site);

        // 检查输入的url是否为空
        String url = paramMap.get("url");
        if (StringUtils.isBlank(url)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + "：url");
            return JSON.toJSONString(respInfo);
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description + " - URL 解码出错");
            return JSON.toJSONString(respInfo);
        }

     // 检查输入的userId是否为空
        if (!paramMap.containsKey("userId")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":userId");
            return JSON.toJSONString(respInfo);
        }
        long userId = Long.parseLong(String.valueOf(paramMap.get("userId")));

        // 检查输入的验证码是否为空
        String code = paramMap.get("code");
        if (StringUtils.isBlank(code)) {
            respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查验证码类型
        String codeType = paramMap.get("codeType");
        if (StringUtils.isBlank(codeType) || (!codeType.equals(
                userService.getBindEmailCodeType(Constants.BindEmailStep.VERIFY_BINDED_EMAIL, web_site)))) {
            respInfo.setCode(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查校验码
        boolean r = Tool.verifyCode(request, code, codeType, true);
        if (!r) {
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }

        // 根据userId 查找原邮箱，如果未绑定返回失败
        User user = userService.getUserByUserId(userId);
        if (null == user) {
            respInfo.setCode(DsResponseCodeData.USER_IS_NOT_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_NOT_EXIST.description);
            return JSON.toJSONString(respInfo);
        }

        if (StringUtils.isEmpty(user.getEmail())) {
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }

        int type = getGlobalEmailType(web_site, Constants.BindEmailStep.VERIFY_BINDED_EMAIL);

        // 保存发送记录2，发送链接2到原邮箱
        EmailSend emailSend = new EmailSend();
        emailSend.setEmail(user.getEmail());
        emailSend.setType(type);
        emailSend.setUrl(url);
        emailSend.setUserId(userId);
        emailSend.setValid(true);
        long id = 0;

        try {
            emailSend = insertEmailSend(emailSend, site_id);
            id = emailSend.getId();
        } catch (Exception e) {
            e.printStackTrace();
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        try {
            StringBuffer sb = new StringBuffer(url).append("?type=").append(type).append("&id=").append(id);
            url = sb.toString();
            String title = getShopName(String.valueOf(web_site)) + "-"
                    + Constants.BindEmailStep.VERIFY_BINDED_EMAIL_DESC;
            String content = String.format(VERIFY_EMAIL_CONTENT_HTML, getShopName(String.valueOf(web_site)), url, url);

            EmailUtil.sendHtmlEmail(user.getEmail(), title, content);
        } catch (Exception e) {
            e.printStackTrace();
            // 发送失败，需要删除发送记录
            deleteEmailSend(id, site_id);
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return JSON.toJSONString(respInfo);
    }



    /**
     * 
     * 发送修改邮箱验证邮件
     *
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    public String sendModifyEmail(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        ResponseInfo respInfo = new ResponseInfo();
        Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);

        // 获取web_site
        if (!paramMap.containsKey("web_site")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":web_site");
            return JSON.toJSONString(respInfo);
        }
        int web_site = Integer.parseInt(String.valueOf(paramMap.get("web_site")));
        int site_id = getSiteId(web_site);

        // 检查输入的url是否为空
        String url = paramMap.get("url");
        if (StringUtils.isBlank(url)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + "：url");
            return JSON.toJSONString(respInfo);
        }
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description + " - URL 解码出错");
            return JSON.toJSONString(respInfo);
        }

        // 检查输入的email是否为空
        String email = paramMap.get("email");
        if (StringUtils.isBlank(email)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":email");
            return JSON.toJSONString(respInfo);
        }
        
        // 如果父url为空，说明是用户直接输入url访问的
        String referer = request.getHeader("Referer");
        if(StringUtils.isBlank(referer)){
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description + "：未验证权限");
            return JSON.toJSONString(respInfo);
        }
        
     // 检查输入的验证码是否为空
        String code = paramMap.get("code");
        if (StringUtils.isBlank(code)) {
            respInfo.setCode(DsResponseCodeData.CODE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.CODE_IS_EMPTY.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查验证码类型
        String codeType = paramMap.get("codeType");
        if (StringUtils.isBlank(codeType) || (!codeType.equals(
                userService.getBindEmailCodeType(Constants.BindEmailStep.VERIFY_MODIFY_EMAIL, web_site)))) {
            respInfo.setCode(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_TYPE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }
        
        // 检查校验码
        boolean r = Tool.verifyCode(request, code, codeType, true);
        if (!r) {
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            return JSON.toJSONString(respInfo);
        }

        long id = -1;
        long userId = -1;

        // 检查输入的id是否为空
        if (paramMap.containsKey("id")) {
            id = Integer.parseInt(String.valueOf(paramMap.get("id")));

            // 根据id 查找发送记录，取得userId
            EmailSend emailSend = getEmailSend(id, site_id);
            if (!checkIsValid(emailSend)) {
                respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                return JSON.toJSONString(respInfo);
            }
            userId = emailSend.getUserId();

            // 设发送记录 为无效
            emailSend.setValid(false);
            try {
                saveEmailSend(emailSend, site_id);
            } catch (Exception e) {
                e.printStackTrace();
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(e.getMessage());
                return JSON.toJSONString(respInfo);
            }
        } else if (paramMap.containsKey("userId")) {
            userId = Long.parseLong(String.valueOf(paramMap.get("userId")));
        } else {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }

        int type = getGlobalEmailType(web_site, Constants.BindEmailStep.VERIFY_MODIFY_EMAIL);

        // 保存发送记录，发送链接到新的邮箱
        EmailSend emailSend = new EmailSend();
        emailSend.setEmail(email);
        emailSend.setUrl(url);
        emailSend.setType(type);
        emailSend.setUserId(userId);
        emailSend.setValid(true);

        // 保存发送记录3，发送链接3到新的邮箱
        try {
            emailSend = insertEmailSend(emailSend, site_id);
            id = emailSend.getId();
        } catch (Exception e) {
            e.printStackTrace();
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }
        try {
            StringBuffer sb = new StringBuffer(url).append("?type=").append(type).append("&id=").append(id);
            url = sb.toString();
            // 发送邮件
            String title = getShopName(String.valueOf(web_site)) + "-" + Constants.BindEmailStep.VERIFY_MODIFY_EMAIL_DESC;
            String content = String.format(VERIFY_EMAIL_CONTENT_HTML, getShopName(String.valueOf(web_site)), url, url);
            EmailUtil.sendHtmlEmail(email, title, content);
        } catch (Exception e) {
            e.printStackTrace();
            // 发送失败，需要删除发送记录
            deleteEmailSend(id, site_id);
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(e.getMessage());
            return JSON.toJSONString(respInfo);
        }

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return JSON.toJSONString(respInfo);
    }

    private EmailSend insertEmailSend(EmailSend emailSend, int site_id) throws Exception {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);
        List<Action> actions = new ArrayList<Action>();

        Action action = new Action();
        action.setType("C");
        action.setServiceName("test_ecshop_ecs_email_send");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("email", emailSend.getEmail());
        set.put("url", emailSend.getUrl());
        set.put("type", emailSend.getType());
        set.put("send_time", DateUtil.getSecond());
        set.put("user_id", emailSend.getUserId());
        set.put("valid", emailSend.isValid() ? "1" : "0");
        action.setSet(set);
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String result = mushroomAction.offer(actionReqInfo);

        JSONObject resultJson = JSONObject.parseObject(result);
        JSONArray jsonArray = (JSONArray)(resultJson.get("results"));
        
        if (!DsResponseCodeData.SUCCESS.code.equals(resultJson.get("code"))) {
            LOG.error("insertEmailSend fail - result:" + result);
            LOG.error("insertEmailSend - reqInfo:" + JSON.toJSONString(actionReqInfo));
            throw new Exception("insertEmailSend fail - reason:" + resultJson.getString("description"));
        }
        if (jsonArray == null || jsonArray.size() == 0) {
            LOG.error("insertEmailSend fail - result:" + result);
            LOG.error("insertEmailSend - reqInfo:" + JSON.toJSONString(actionReqInfo));
            throw new Exception("insertEmailSend fail - result is blank");
        }
        
        ActionResult ActionResult = DataUtil.parse(jsonArray.get(0).toString(), ActionResult.class);
        emailSend.setId(ActionResult.getGenerateKey());
        return emailSend;
    }

    private void deleteEmailSend(long id, int site_id) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);
        List<Action> actions = new ArrayList<Action>();

        Action action = new Action();
        action.setType("D");
        action.setServiceName("test_ecshop_ecs_email_send");
        SqlCondition condition = new SqlCondition();
        condition.setKey("id");
        condition.setOp("=");
        condition.setValue(id);
        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);
        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);
        action.setWhere(where);
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            LOG.error("deleteEmailSend fail - result:" + res);
            LOG.error("deleteEmailSend fail - reqInfo:" + JSON.toJSONString(actionReqInfo));
        }
    }



    private void saveEmailSend(EmailSend emailSend, int site_id) throws Exception {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);
        List<Action> actions = new ArrayList<Action>();

        Action action = new Action();
        action.setType("U");
        action.setServiceName("test_ecshop_ecs_email_send");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("valid", emailSend.isValid() ? "1" : "0");
        action.setSet(set);
        SqlCondition condition = new SqlCondition();
        condition.setKey("id");
        condition.setOp("=");
        condition.setValue(emailSend.getId());
        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);
        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);
        action.setWhere(where);
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            LOG.error("saveEmailSend fail - result:" + res);
            LOG.error("saveEmailSend fail - reqInfo:" + JSON.toJSONString(actionReqInfo));
            throw new Exception("saveEmailSend fail - reason:" + actionResponse.getDescription());
        }
    }



    /*
     * 获取用户类型
     */
    private UserType getUserType(int web_site) {
        switch (web_site){
            case Constants.WebSite.AUW:
                return Constants.UserType.LEJJ_USER;
            case Constants.WebSite.YJG:
                return Constants.UserType.HEMEIJU_USER;
            case Constants.WebSite.HL:
                return Constants.UserType.HL_USER;
            case Constants.WebSite.HLk:
                return Constants.UserType.HLK_USER;
            case Constants.WebSite.MBH:
                return Constants.UserType.MBH_USER;
            default:
                return null;
        }
    }
    
    private int getSiteId(int web_site) {
        return web_site == Constants.WebSite.AUW ? Constants.SiteId.LJJ : web_site;
    }



    /*
     * 获取全局邮件类型
     * 计算方式： web_site*10 + bindEmailStep ，爱有窝特殊处理 4*10 + bindEmailStep
     */
    private int getGlobalEmailType(int web_site, int bindEmailStep) {
        return web_site * 10 + bindEmailStep;
    }


    private boolean checkIsValid(EmailSend emailSend) {
        if (null == emailSend || !emailSend.isValid()) {
            return false;
        }

        // 发送记录24小时内为有效
        int sendTime = emailSend.getSendTime();
        if (DateUtil.getSecond() - sendTime > 24 * 60 * 60) {
            return false;
        }
        return true;
    }



    private void bindEmail(EmailSend emailSend, int site_id) throws Exception {
        long userId = emailSend.getUserId();
        String email = emailSend.getEmail();

        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);
        List<Action> actions = new ArrayList<Action>();

        // 绑定邮箱
        Action action = new Action();
        action.setType("U");
        action.setServiceName("test_ecshop_ecs_users");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("email", email);
        action.setSet(set);
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
        actions.add(action);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);
        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (!DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            LOG.error("bindEmail fail - reqInfo:" + JSON.toJSONString(actionReqInfo));
            LOG.error("bindEmail fail - result:" + res);
            throw new Exception("bindEmail failed - reason:" + actionResponse.getDescription());
        }
    }



    private EmailSend getEmailSend(long id, int site_id) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("YJG_BUV1_EmailSend");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("site_id", site_id);
        param.put("id", id);
        serviceReqInfo.setParam(param);
        serviceReqInfo.setNeedAll("1");

        String res = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(res, RuleServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(responseData.getCode()) && responseData.getRows() != null
                && responseData.getRows().size() != 0) {
            EmailSend emailSend = new EmailSend();
            Map<String, String> jsonMap = responseData.getRows().get(0);
            emailSend.setId(id);
            emailSend.setEmail(jsonMap.get("email"));
            emailSend.setSendTime(Integer.parseInt(jsonMap.get("send_time")));
            emailSend.setType(Integer.parseInt(jsonMap.get("type")));
            emailSend.setUrl(jsonMap.get("url"));
            emailSend.setUserId(Long.parseLong(jsonMap.get("user_id")));
            emailSend.setValid("1".equals(jsonMap.get("valid")) ? true : false);
            return emailSend;
        } else {
            LOG.error("getEmailSend fail - reqInfo:" + JSON.toJSONString(serviceReqInfo));
            LOG.error("getEmailSend fail - result:" + res);
        }

        return null;
    }
    


    /**
     * 
     * @Title: addAnonymousUser
     * @Description:客户（匿名用户） 根据deviceId 增加用户，如果已存在直接返回用户相关信息
     * @throws Exception
     */
    public Object addAnonymousUser(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo)
            throws Exception {
        ResponseInfo respInfo = new ResponseInfo();

        JSONObject param = JSONObject.parseObject(repInfo.getParam());
        if (!param.containsKey(ContentUtils.PARAM)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ContentUtils.PARAM);
            return respInfo;
        }
        JSONObject paramInnerObj = (JSONObject) param.get(ContentUtils.PARAM);
        int from = paramInnerObj.getIntValue(ContentUtils.FROM);
        int site_id = paramInnerObj.getIntValue(ContentUtils.SITE_ID);

        // 获取 deviceId
        String deviceId = paramInnerObj.getString(ContentUtils.DEVICEID);
        if (StringUtils.isBlank(deviceId)) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ContentUtils.DEVICEID);
            return respInfo;
        }

        DsManageReqInfo dsReqInfo = DataUtil.parse(repInfo.getParam(), DsManageReqInfo.class);

        Map<String, String> userMap = userService.getRow(dsReqInfo);

        // 该设备没有user对应，创建一个
        if (null == userMap) {
            userService.addAnonymousUser(deviceId, from, site_id);

            userMap = userService.getRow(dsReqInfo);
            if (null == userMap) {
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(DsResponseCodeData.ERROR.description);
                return respInfo;
            }
        }
        
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        respInfo.setObject(userMap);

        // app 设置accessToken
        if (Constants.UserFrom.YJG_ANDROID == from || Constants.UserFrom.YJG_IPAD == from
                || Constants.UserFrom.YJG_IPHONE == from) {
            String accessToken = userService.getAppAccessToken(site_id);
            if (!StringUtils.isEmpty(accessToken)) {
                userMap.put("accessToken", accessToken);
            }
            
            return appResultWraper(respInfo);
        }
        
        return respInfo;
    }
    
    /**
     * 通过站点取到商城名
     * @param web_site
     * @return
     */
    public String getShopName(String web_site){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		ResponseInfo respInfo = new ResponseInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_JudgeSiteName");
		param.put("site_id", web_site);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("sendVerifyBindEmail YJG_HSV1_JudgeSiteName param is: " + JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("sendVerifyBindEmail YJG_HSV1_JudgeSiteName result is: " + result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("sendVerifyBindEmail YJG_HSV1_JudgeSiteName param is: " + JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("sendVerifyBindEmail YJG_HSV1_JudgeSiteName result is: " + result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("获取商城名字失败！");
			return JSON.toJSONString(respInfo);
		}
		mapList = responseData.getRows();
		if (null == mapList || mapList.size() == 0) {
			LOG.error("sendVerifyBindEmail YJG_HSV1_JudgeSiteName param is: " + JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("sendVerifyBindEmail YJG_HSV1_JudgeSiteName result is: " + result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询商城无数据！");
			return JSON.toJSONString(respInfo);
		}
		return mapList.get(0).get("site_name").toString();
    	
    }
}
