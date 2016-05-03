package com.meiqi.openservice.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.openservice.action.login.LoginVerifyAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.UploadUtil;
import com.meiqi.util.DataUtil;
import com.meiqi.util.LogUtil;
import com.meiqi.util.SysConfig;

/**
 * 头像上传
 * 
 * @ClassName: HeadPicController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xiaokui.li
 * @date 2015年7月31日 上午11:05:03
 * 
 */
@Controller
public class HeadPicController {

    private static final String USER_ID = "uid";
    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    @Autowired
    private LoginVerifyAction   loginVerifyAction;



    @RequestMapping("/uploadHeadPic")
    public void uploadHeadPic(HttpServletRequest request, HttpServletResponse response, String type, String pic1,
            String pic2) {
        String userId = getUserId(request);
        // 验证是否已经登录
        RepInfo repInfo = new RepInfo();
        repInfo.setParam("{\"userId\":\"" + userId + "\",\"type\":\"" + type + "\"}");
        ResponseInfo verifyIsLogin = (ResponseInfo) loginVerifyAction.verifyIsLogin(request, response, repInfo);
        if (DsResponseCodeData.ERROR.code.equals(verifyIsLogin.getCode())) {
            outWrite(response, "result=fail&message=请先登录，然后再进行操作");
            return;
        }

        String fileUploadUrl;
        if("1".equals(type)){
            fileUploadUrl = SysConfig.getValue("file_upload_php1");
        }else{
            fileUploadUrl = SysConfig.getValue("file_upload_php2");
        }
        // 大图
        String pic1Result = UploadUtil.uploadFile(pic1, fileUploadUrl);
        // 中图
        String pic2Result = UploadUtil.uploadFile(pic2, fileUploadUrl);

        boolean success = true;
        Map<String, Object> pic1ResMap = DataUtil.parse(pic1Result);
        int pic1Status = Integer.parseInt(pic1ResMap.get("statusCode").toString());
        if (pic1Status == 1) {
            success = false;
        }
        Map<String, Object> pic2ResMap = DataUtil.parse(pic2Result);
        int pic2Status = Integer.parseInt(pic2ResMap.get("statusCode").toString());
        if (pic2Status == 1) {
            success = false;
        }
        String result = "";
        if (success) {
            String res = savePic(userId, pic1ResMap);
            ActionRespInfo actionRespInfo = DataUtil.parse(res, ActionRespInfo.class);
            if (DsResponseCodeData.ERROR.code.equals(actionRespInfo.getCode())) {
                result = "result=fail&message=图片上传失败！请重试！";
            } else {
                result = "result=success&avatar=" + pic1ResMap.get("file") + "&message=图片上传成功";
            }
        } else {
            result = "result=fail&message=图片上传失败！请重试！";
        }
        outWrite(response, result);
    }



    private String savePic(String userId, Map<String, Object> pic1ResMap) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");

        Action action = new Action();
        action.setType("U");
        action.setServiceName("test_ecshop_ecs_users");
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("avatar", pic1ResMap.get("path"));
        action.setSet(set);
        Where where = new Where();
        action.setWhere(where);
        where.setPrepend("and");
        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        where.setConditions(conditions);

        SqlCondition condition = new SqlCondition();
        condition.setKey("user_id");
        condition.setOp("=");
        condition.setValue(userId);
        conditions.add(condition);
        action.setWhere(where);

        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        actionReqInfo.setParam(param);
        String res = mushroomAction.offer(actionReqInfo);
        return res;
    }



    private String getUserId(HttpServletRequest request) {
        String userId = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), USER_ID)) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }
        return userId;
    }



    /**
     * 输出文本字符串
     * 
     * @param res
     * @param msg
     */
    protected void outWrite(HttpServletResponse res, String msg) {
        outWrite(res, msg, "text/xml");
    }



    /**
     * 返回流写入信息，并可以指定内容类型
     * 
     * @param res
     *            返回流
     * @param msg
     *            内容
     * @param contentType
     *            内容类型
     */
    protected void outWrite(HttpServletResponse res, String msg, String contentType) {
        res.setCharacterEncoding("utf-8");
        res.setContentType(contentType);
        PrintWriter out = null;
        try {
            out = res.getWriter();
            out.write(msg);
            out.flush();
        } catch (IOException e) {
            LogUtil.error(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
