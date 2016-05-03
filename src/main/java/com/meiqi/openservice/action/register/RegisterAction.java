/**   
* @Title: RegisterAction.java 
* @Package com.meiqi.openservice.action.register 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.app.action.UsersAction;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.action.UserAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * @ClassName: RegisterAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08 
 *  
 */
@Service
public class RegisterAction extends BaseAction{

    @Autowired
    private UsersAction usersAction;
    @Autowired
    private UserAction  userAction;
    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private IDataAction     dataAction;
    @Autowired
    private SmsAction       smsAction;
     
    public String hmjRegister(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        String resultData="";
        ResponseInfo respInfo=new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        
        String userName=map.get("userName")==null?"":map.get("userName").toString();
        String pwd=map.get("pwd")==null?"":map.get("pwd").toString();
        String confirmPwd=map.get("confirmPwd")==null?"":map.get("confirmPwd").toString();
        String phone=map.get("phone")==null?"":map.get("phone").toString();
        String smsCode=map.get("smsCode")==null?"":map.get("smsCode").toString();
        String normalVerifyCode=map.get("normalVerifyCode")==null?"":map.get("normalVerifyCode").toString();
        String avatar=map.get("avatar")==null?"":map.get("avatar").toString();
        String site_id=map.get("site_id")==null?"0":map.get("site_id").toString();
        String from=map.get("from")==null?"0":map.get("from").toString();
        
        if("".equals(userName)){
            respInfo.setCode(DsResponseCodeData.PHONE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.PHONE_IS_EMPTY.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
         //验证用户名的唯一性
         boolean isExist=userAction.userIsExist(userName, "1",site_id);
         if(isExist){
             respInfo.setCode(DsResponseCodeData.USER_IS_EXIST.code);
             respInfo.setDescription(DsResponseCodeData.USER_IS_EXIST.description);
             resultData = JSON.toJSONString(respInfo);
             return resultData;
         }
        
        //验证密码正确性
        if("".equals(pwd) || !pwd.equals(confirmPwd)){
                respInfo.setCode(DsResponseCodeData.PWD_NOT_RIGHT.code);
                respInfo.setDescription(DsResponseCodeData.PWD_NOT_RIGHT.description);
                resultData = JSON.toJSONString(respInfo);
                return resultData;
        }
        /*if("".equals(normalVerifyCode)){
            respInfo.setCode(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        //验证输入的注册码的正确性
        boolean r=Tool.verifyCode(request, normalVerifyCode,com.meiqi.openservice.commons.config.Constants.CodeType.HEMEIJU_REGISTER,false);
        if(!r){
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }*/
        if("".equals(smsCode)){
            respInfo.setCode(DsResponseCodeData.SMS_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.SMS_CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        //验证短信验证码正确性
//      VerificationCode code=new VerificationCode();
//      code.setType(VerificationCodeType.register);
//      code.setPhone(phone);
//        code.setCode(smsCode);
//      String result=usersAction.checkCode(code, request);
//      //LOG.info("sms code verify result result="+result);
//      Map<String,String> result1=DataUtil.parse(result,Map.class);
//      String statusCode=result1.get("message");
        RepInfo reInfo = new RepInfo();
        Map<String,Object> pa1 = new HashMap<String,Object>();
        pa1.put("receive_phone", phone);
        pa1.put("code_value", smsCode);
        pa1.put("template_id", map.get("template_id"));
        pa1.put("web_site", map.get("web_site"));
        pa1.put("site_id", site_id);
        pa1.put("type", map.get("type"));
        Map<String,Object> pa2 = new HashMap<String,Object>();
        pa2.put("param",pa1);
        reInfo.setParam(JSON.toJSONString(pa2));
        BaseRespInfo baseInfo = (BaseRespInfo) smsAction.validateSmsCode(request, response, reInfo);
        
        if("1".equals(baseInfo.getCode())){
            respInfo.setCode(DsResponseCodeData.SMS_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.SMS_CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        //保存用户
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        
        String serviceName="test_ecshop_ecs_users";
        Action action = new Action();
        action.setSite_id(Integer.parseInt(site_id));
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", phone);//和美居是用手机号码作为用户名的
        try {
            set.put("password", MD5Util.MD5(pwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        set.put("is_validated", 1);//是否生效
        set.put("visit_count", 1);//访问次数
        set.put("reg_time", DateUtils.getSecond());
        set.put("last_ip", getIp(request));
        set.put("mobile_phone", phone);
        set.put("avatar", avatar);
        set.put("from", from);
        
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse=null;
        String res1=mushroomAction.offer(actionReqInfo);
        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        }else{
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(actionResponse.getDescription());
        }
        resultData = JSON.toJSONString(respInfo);
        return resultData;
    }
    
    public String lejjRegister(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        String resultData="";
        ResponseInfo respInfo=new ResponseInfo();
        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        
        //LogUtil.info("lejjRegister param:"+repInfo.getParam());
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        
        String userName=map.get("userName")==null?"":map.get("userName").toString();
        String pwd=map.get("pwd")==null?"":map.get("pwd").toString();
        String confirmPwd=map.get("confirmPwd")==null?"":map.get("confirmPwd").toString();
        String email=map.get("email")==null?"":map.get("email").toString();
        String normalVerifyCode=map.get("normalVerifyCode")==null?"":map.get("normalVerifyCode").toString();
        String avatar=map.get("avatar")==null?"":map.get("avatar").toString();//头像
        String site_id=map.get("site_id")==null?"0":map.get("site_id").toString();
        String from=map.get("from")==null?"0":map.get("from").toString();
        
        if("".equals(userName)){
            respInfo.setCode(DsResponseCodeData.PHONE_IS_EMPTY.code);
            respInfo.setDescription(DsResponseCodeData.PHONE_IS_EMPTY.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        //验证用户名的唯一性
        boolean isExist=userAction.userIsExist(userName, "2",site_id);
        if(isExist){
            respInfo.setCode(DsResponseCodeData.USER_IS_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.USER_IS_EXIST.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        //验证密码正确性
        if("".equals(pwd) || !pwd.equals(confirmPwd)){
                respInfo.setCode(DsResponseCodeData.PWD_NOT_RIGHT.code);
                respInfo.setDescription(DsResponseCodeData.PWD_NOT_RIGHT.description);
                resultData = JSON.toJSONString(respInfo);
                return resultData;
        }
        //验证邮箱的唯一性
        boolean isExist1=userAction.userIsExist(email, "2",site_id);
        if(isExist1){
            respInfo.setCode(DsResponseCodeData.EMAIL_IS_EXIST.code);
            respInfo.setDescription(DsResponseCodeData.EMAIL_IS_EXIST.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        /*if("".equals(normalVerifyCode)){
            respInfo.setCode(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.REGISTER_CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }
        
        //验证输入的注册码的正确性
        boolean r=Tool.verifyCode(request, normalVerifyCode,com.meiqi.openservice.commons.config.Constants.CodeType.LEJJ_REGISTER,false);
        if(!r){
            respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            resultData = JSON.toJSONString(respInfo);
            return resultData;
        }*/
        //保存用户
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        
        String serviceName="test_ecshop_ecs_users";
        Action action = new Action();
        action.setSite_id(Integer.parseInt(site_id));
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_name", userName);//和美居是用手机号码作为用户名的
        try {
            set.put("password", MD5Util.MD5(pwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //是否生效
        set.put("is_validated", 1);//
        set.put("visit_count", 1);//访问次数
        set.put("reg_time", DateUtils.getSecond());
        set.put("last_ip", getIp(request));
        set.put("email", email);
        set.put("avatar", avatar);//头像
        set.put("from", from);
        
        action.setSet(set);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        SetServiceResponseData actionResponse=null;
        String res1=mushroomAction.offer(actionReqInfo);
        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        }else{
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(actionResponse.getDescription());
        }
        resultData = JSON.toJSONString(respInfo);
        return resultData;
    }
    
        }
