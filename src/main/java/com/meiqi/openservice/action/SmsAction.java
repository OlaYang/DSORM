/**
 * @Title: RegisterAction.java
 * @Package com.meiqi.openservice.action.register
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * @version V1.0
 */
package com.meiqi.openservice.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.action.IAuthAction;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.AuthBean;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.po.rule.smsSend.SMSSend;
import com.meiqi.dsmanager.po.rule.smsSend.SMSSender;
import com.meiqi.dsmanager.po.rule.smsSend.ServiceSendReqInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.MD5Util;
import com.meiqi.openservice.action.javabin.ip.IpAction;
import com.meiqi.openservice.action.webservice.HomeLikeSoap;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.EnDecryptUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/**
 * @ClassName: RegisterAction
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08
 * 
 */
@Service
public class SmsAction extends BaseAction {

	private static final Log LOG =  LogFactory.getLog("sms");
	
    @Autowired
    private IMushroomAction mushroomAction;
    @Autowired
    private IMemcacheAction memcacheService;
    @Autowired
    private IDataAction     dataAction;
    @Autowired
    private IAuthAction authAction;
    
    
    //降价通知类型
    private static final String DEPRECIATE = "1";
    //订单确认通知类型
    private static final String ORDERAFFIRM = "2";
    //发货通知类型
    private static final String DELIVER = "3";
    
    //发短信ip黑名单
    static String path = BaseAction.basePath + File.separator + "lejj_resource" + File.separator + "ip"+ File.separator + "sms" + File.separator +"smsipblack.properties";
    static String whitePath = BaseAction.basePath + File.separator + "lejj_resource" + File.separator + "ip"+ File.separator + "sms" + File.separator +"smsipwhite.properties";
    
    static Properties properties = new Properties();
    static Properties  whiteProperties = new Properties();
    static{
        try {
            properties.load(new FileInputStream(new File(path)));
            whiteProperties.load(new FileInputStream(new File(whitePath)));
        } catch (Exception e) {
            LOG.info("smsAction load Properties error:"+e);
        }
    }

    
    public String reloadSmsipblackAndWhiteProperties(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        properties = new Properties();
        whiteProperties = new Properties();
        try {
            properties.load(new FileInputStream(new File(path)));
            whiteProperties.load(new FileInputStream(new File(whitePath)));
        } catch (Exception e) {
            LOG.info("smsAction load Properties error:"+e);
        }
        return "success";
    }

    /**
     * 发送非校验的短信信息接口
     * 
     * @Title: sendNormalMsg
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param request
     * @param @param response
     * @param @param repInfo
     * @param @return 参数说明
     * @return Object 返回类型
     * @throws
     */
	public Object send(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
	    
	    if(!isFromApp(request)){
	        //来自于非APP的请求
	        return "无权限";
	    }
	    HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String content1 = "";
        if (HttpMethod.GET.equals(method)) {
            content1 = request.getParameter("param");
        } else {
            try {
				content1 = DataUtil.inputStream2String(request.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        // 处理编码 和特殊字符
        String decodeContent = com.meiqi.app.common.utils.StringUtils.decode(content1);
        AppRepInfo appRepInfo = DataUtil.parse(decodeContent, AppRepInfo.class);
        if (null == appRepInfo) {
            LogUtil.error("请求参数不能为空!");
            return null;
        }
        // 获取header 参数
        appRepInfo.setHeader(com.meiqi.app.action.BaseAction.getHeader(request));
        // 请求授权验证
        AuthBean validateAuth = authAction.validateAuth1(appRepInfo,
                appRepInfo.getHeader().get(ContentUtils.AUTHORIZATION).toString(), "app");
        if (!validateAuth.isState()) {
            return JsonUtils.getAuthorizationErrorJson(validateAuth.getDescription());
        }
	    
        
        String content = repInfo.getParam();
        String ip="";
        String blackip="";
        if(request!=null){
            ip=IpAction.getClientIP(request);
            if(StringUtils.isNotEmpty(ip)){
                try {
                    blackip=properties.getProperty("ip");
                    LOG.info("smsAction send current ip:"+ip +",blackip :"+blackip+",content:"+content);
                    if(StringUtils.isNotEmpty(blackip)){
                        String[] ipArray=ip.split(",");
                        for(String ipTmp:ipArray){
                            if(blackip.contains(ipTmp.trim())){
                                LOG.info("smsAction send current ip:"+ip +",blackip :"+blackip+",content:"+content);
                                return "无权限";
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.info("smsAction send current ip:"+ip +",blackip :"+blackip+",content:"+content+",error:"+e);
                }
            }
        }
        String whiteip="";
        try {
            whiteip=whiteProperties.getProperty("ip");
            LOG.info("smsAction send current ip:"+ip +",whiteip :"+whiteip+",content:"+content);
        } catch (Exception e) {
            LOG.info("smsAction send current ip:"+ip +",whiteip :"+whiteip+",content:"+content+",error:"+e);
        }
        boolean flag=false;//是否在白名单内,默认值为不在
        if(StringUtils.isNotEmpty(whiteip) && StringUtils.isNotEmpty(ip)){
            String[] ipArray=ip.split(",");
            for(String ipTmp:ipArray){
                if(whiteip.contains(ipTmp.trim())){
                    flag=true;//在白名单内
                    break;
                }
            }
        }
        
        BaseRespInfo respInfo = new BaseRespInfo();
        String date=DateUtils.formatDateToSimpleString(new Date());
        String smsSendIpKey="smsSendIpKey_"+date+"_"+ip;
        String smsSendIpKeyMd5=MD5Util.MD5(smsSendIpKey);
        Object currentCount=memcacheService.getCache(smsSendIpKeyMd5);
        Integer smsLimit=Integer.parseInt(SysConfig.getValue("smsLimit"));
        if(!flag){
            if(currentCount!=null){
                if(Integer.parseInt(currentCount.toString()) >= smsLimit){
                    respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                    respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                    return respInfo;
                }
            }
        }
        // 将传入的json字符串解析封装成SMSSend对象
        SMSSend smsSend;
        try {
            ServiceSendReqInfo serviceSendReqInfo = DataUtil.parse(content, ServiceSendReqInfo.class);
            smsSend = serviceSendReqInfo.getParam();
            //判断该电话号码24小时内发送次数是否超过20条
            DsManageReqInfo sendDs = new DsManageReqInfo();
            sendDs.setServiceName("YJG_HSV1_phonemessages_limt");
            Map<String,Object> sendMap = new HashMap<String, Object>();
            
            RuleServiceResponseData responseData = null;
            List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
            Map<String, Object> param = new HashMap<String, Object>();
            String result1 = "";
            //判断是否是发送降价通知这类型的短信业务
            if(DEPRECIATE.equals(smsSend.getOperation())){
            	DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
        		dsManageReqInfo.setNeedAll("1");
        		dsManageReqInfo.setServiceName("YJG_BUV1_SendMassage");
        		dsManageReqInfo.setParam(param);
        		result1 = dataAction.getData(dsManageReqInfo, "");
        		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
        		LOG.info("SendOperationMsg YJG_BUV1_SendMassage param is: "+param);
        		LOG.info("SendOperationMsg YJG_BUV1_SendMassage result is: "+result1);
        		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        			LOG.info("SendOperationMsg YJG_BUV1_SendMassage param is: "+param);
            		LOG.info("SendOperationMsg YJG_BUV1_SendMassage result is: "+result1);
        			respInfo.setCode(DsResponseCodeData.ERROR.code);
        			respInfo.setDescription("系统出错！");
        			return JSON.toJSONString(respInfo);
        	    }
        		mapList = responseData.getRows();
        		for (int i = 0; i < mapList.size(); i++) {
        			sendMap.put("phone_num", mapList.get(i).get("mobile_phone"));
        			sendMap.put("code", "maxinf001");
    	            sendDs.setParam(sendMap);
    	            ResponseInfo sendResp = DataUtil.parse(dataAction.getData(sendDs), ResponseInfo.class);
    	            if("1".equals(sendResp.getCode())){
    	                respInfo.setCode(DsResponseCodeData.ERROR.code);
    	                respInfo.setDescription(DsResponseCodeData.ERROR.description);
    	                LOG.info("SmsAction_phone_get_error:规则[YJG_HSV1_phonemessages_limt]获取数据失败，参数信息["+DataUtil.toJSONString(sendDs)+"],时间["+DateUtils.formatDateToString(new Date())+"]");
    	                return respInfo;
    	            }
    	            List<Map<String,String>> phoneList = sendResp.getRows();
    	            if(null!=phoneList&&phoneList.size()>0){
    	                Map<String,String> phoneMap = phoneList.get(0);
    	                //1=能发送，0=不能发送
    	                if("0".equals(phoneMap.get("is_send"))){
    	                    respInfo.setCode(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.code);
    	                    respInfo.setDescription(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description);
    	                    LOG.info("SmsAction_phone_send_limit:"+DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description+",时间["+DateUtils.formatDateToString(new Date())+"]");
    	                    return respInfo;
    	                }
    	            }
				}
            }else if(ORDERAFFIRM.equals(smsSend.getOperation())){
            	DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
        		dsManageReqInfo.setNeedAll("1");
        		dsManageReqInfo.setServiceName("HMJ_HSV1_MyOrderBackground");
        		param.put("order_sn", smsSend.getParam().get("order_sn"));
        		dsManageReqInfo.setParam(param);
        		result1 = dataAction.getData(dsManageReqInfo, "");
        		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
        		LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground param is: "+param);
        		LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground result is: "+result1);
        		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        			LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground param is: "+param);
            		LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground result is: "+result1);
        			respInfo.setCode(DsResponseCodeData.ERROR.code);
        			respInfo.setDescription("规则查询出错！");
        			return JSON.toJSONString(respInfo);
        	    }
        		mapList = responseData.getRows();
        		if(mapList != null && mapList.size() > 0){
        			smsSend.setPhoneNumber(mapList.get(0).get("mobile"));
        			smsSend.setTemplateId(smsSend.getTemplateId());
        		}else{
        			LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground param is: "+param);
            		LOG.info("SendOperationMsg HMJ_HSV1_MyOrderBackground result is: "+result1);
        			respInfo.setCode(DsResponseCodeData.ERROR.code);
        			respInfo.setDescription("规则查询无数据！");
        			return JSON.toJSONString(respInfo);
        		}
            }else if(DELIVER.equals(smsSend.getOperation())){
            	DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
        		dsManageReqInfo.setNeedAll("1");
        		dsManageReqInfo.setServiceName("YJG_HSV1_Deliver_MessageSend");
        		param.put("delivery_id", smsSend.getParam().get("delivery_id"));
        		dsManageReqInfo.setParam(param);
        		result1 = dataAction.getData(dsManageReqInfo, "");
        		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
        		LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend param is: "+param);
        		LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend result is: "+result1);
        		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        			LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend param is: "+param);
            		LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend result is: "+result1);
        			respInfo.setCode(DsResponseCodeData.ERROR.code);
        			respInfo.setDescription("规则查询出错！");
        			return JSON.toJSONString(respInfo);
        	    }
        		mapList = responseData.getRows();
        		if(mapList != null && mapList.size() > 0){
        			if(mapList.get(0).get("flag").equals("1")){
        				smsSend.setPhoneNumber(mapList.get(0).get("mobile"));
        				smsSend.setTemplateId(smsSend.getTemplateId());
        				Map<String,String> map1 = new HashMap<String, String>();
        				map1.put("goods_number", mapList.get(0).get("goods_number"));
        				map1.put("order_sn", mapList.get(0).get("order_sn"));
        				smsSend.setParam(map1);;
        			}else{
        				LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend param is: "+param);
                		LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend result is: "+result1);
        				respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            			respInfo.setDescription("提交成功，但不发送短信！");
            			return JSON.toJSONString(respInfo);
        			}
        		}else{
        			LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend param is: "+param);
            		LOG.info("SendOperationMsg YJG_HSV1_Deliver_MessageSend result is: "+result1);
        			respInfo.setCode(DsResponseCodeData.ERROR.code);
        			respInfo.setDescription("规则查询无数据！");
        			return JSON.toJSONString(respInfo);
        		}
            }
        	String phone_num = smsSend.getPhoneNumber();
        	isSend(phone_num,sendDs);
            // 检测发送auth_key是否正确
            String auth_key = EnDecryptUtil.decrypt(smsSend.getAuth_key());
            if (!SysConfig.getValue("sms_auth_key").equals(auth_key)) {
                throw new IllegalArgumentException("auth_key验证失败！ ");
            }

            if(DEPRECIATE.equals(smsSend.getOperation())){
            	String res = depreciate(smsSend,mapList,param,result1);
        		return res;
            }else{
            	param.put("id", smsSend.getTemplateId());
            	
            	TService po = Services.getService(Services.SERVICE_SENDMESSAGE_INFO);
            	D2Data data = com.meiqi.data.engine.DataUtil.getD2Data(po, param);
            	// 获取模板内容
            	Object smsTemplate = data.getValue("模板内容", 0);
            	if (smsTemplate == null) {
            		respInfo.setCode(DsResponseCodeData.SMS_TEMPLATE_NOT_EXIST.code);
            		respInfo.setDescription(DsResponseCodeData.SMS_TEMPLATE_NOT_EXIST.description);
            		return respInfo;
            	}
            	String msg = data.getValue("模板内容", 0).toString();
            	long templateId = smsSend.getTemplateId();
            	DsManageReqInfo dsInfo = new DsManageReqInfo();
            	dsInfo.setServiceName("YJG_BUV1_message");
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("template_id", templateId);
            	dsInfo.setParam(map);
            	ResponseInfo resp = DataUtil.parse(dataAction.getData(dsInfo), ResponseInfo.class);
            	List<Map<String, String>> list = resp.getRows();
            	String is_code = "";
            	if (null != list && list.size() > 0) {
            		for (Map<String, String> m : list) {
            			if (m.get("id").equals(String.valueOf(templateId))) {
            				is_code = m.get("is_code");// 判断是验证类型的还是普通类型的模板
            			}
            		}
            	} else {
            		respInfo.setCode("1");
            		respInfo.setDescription("模板信息为空！");
            		return respInfo;
            	}
            	// 转换模板内容变量
            	Map<String, String> params = smsSend.getParam();
            	String verifyCode = "";
            	long validTime = 0;
            	boolean isVerifyTemplate = false;
            	BaseRespInfo reInfo = null;
            	if (is_code.equals("1")) {// 判断该模板是否有验证码，1有 0无
            		isVerifyTemplate = true;
            		if (null != params) {
            			for (String key : params.keySet()) {
            				String replaceKey = "\\{" + key + "\\}";
            				String value1 = params.get(key);
            				msg = msg.replaceAll(replaceKey, value1);
            			}
            		}
            		if (templateId == 78 || templateId == 139  || templateId == 126) {// 短信验证码
            			verifyCode = getCode(SysConfig.getValue("verify_code_source"), 4);
            			validTime = DateUtils.getValidSecond(10);
            			msg = msg.replaceAll("\\{code\\}", verifyCode);
            		} else if (templateId == 116 || templateId == 141) {// 入驻邀约码
            			//verifyCode = getCode(SysConfig.getValue("verify_code_mode"), 6);
            			verifyCode = params.get("discount_code");
            			validTime = 0;
            			msg = msg.replaceAll("\\{invite_code\\}", verifyCode);
            		} else if (templateId == 115 || templateId == 140) {// 订单折扣码
            			verifyCode = params.get("discount_code");
            			validTime = 0;
            			msg = msg.replaceAll("\\{discount_code\\}", verifyCode);
            		}
            		reInfo = updatePreCode(smsSend.getPhoneNumber(), smsSend.getTemplateId(),
            				smsSend.getWebSite(), smsSend.getSmsType());
            	} else {
            		if (null != params) {
            			if(templateId == 110 && !isFromApp(request)){
            				//验证输入的注册码的正确性
            				String normalVerifyCode=params.get("simpleVerifyCode");
            				boolean r=Tool.verifyCode(request, normalVerifyCode,com.meiqi.openservice.commons.config.Constants.CodeType.SEND_EXPERIENCE_ADDRESS,true);
            				if(!r){
            					respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
            					respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
            					return respInfo;
            				}
            				if(!params.containsKey("store_id")||params.get("store_id").equals("")){
            					respInfo.setCode("1");
            					respInfo.setDescription("店铺id为空！");
            					return respInfo;
            				}
            				DsManageReqInfo ds = new DsManageReqInfo();
            				ds.setServiceName("YJG_BUV1_lejj_store");
            				Map<String, Object> dsMap = new HashMap<String, Object>();
            				dsMap.put("store_id", params.get("store_id"));
            				ds.setParam(dsMap);
            				ResponseInfo re = DataUtil.parse(dataAction.getData(ds), ResponseInfo.class);
            				List<Map<String, String>> row = re.getRows();
            				if(null==row||row.size()==0){
            					respInfo.setCode("1");
            					respInfo.setDescription("获取的店铺信息为空！");
            					return respInfo;
            				}
            				Map<String, String> storeMap = row.get(0);
            				msg = msg.replace("{experience_hall_name}", storeMap.get("simple_name"));//体验店名称
            				msg = msg.replace("{experience_hall_address}", storeMap.get("address"));//体验店地址
            				msg = msg.replace("{experience_hall_phone}", storeMap.get("tel"));//体验店电话
            			}else{
            				for (String key : params.keySet()) {
            					String replaceKey = "\\{" + key + "\\}";
            					msg = msg.replaceAll(replaceKey, params.get(key));
            				}
            			}
            		}
            		reInfo = new BaseRespInfo();
            	}
            	smsSend.setSendMsg(msg);
            	// long time = System.currentTimeMillis();
            	
            	// System.out.println("设置之前发送验证码无效耗时："+(System.currentTimeMillis()-time));
            	if (reInfo.getCode().equals("0")) {
            		String sendMessage = SMSSender.sendSms(data, smsSend);
            		respInfo = DataUtil.parse(sendMessage, BaseRespInfo.class);
            		// long sendTime = System.currentTimeMillis();
            		// System.out.println("发送短信耗时："+(sendTime-time));
            		// 短信发送成功后， 将发送记录写入数据库
            		if ("0".equals(respInfo.getCode())) {
            			DsManageReqInfo actionReqInfo = new DsManageReqInfo();
            			actionReqInfo.setServiceName("MUSH_Offer");
            			String serviceName = "MeiqiServer_ecs_messaging_send";
            			Action action = new Action();
            			action.setType("C");
            			action.setServiceName(serviceName);
            			Map<String, Object> set = new HashMap<String, Object>();
            			set.put("receive_phone", smsSend.getPhoneNumber());
            			set.put("messaging_content", smsSend.getSendMsg());
            			set.put("is_succeed", "1");
            			set.put("send_time", DateUtils.getSecond());
            			set.put("template_id", smsSend.getTemplateId());
            			set.put("web_site", smsSend.getWebSite());
            			set.put("type", smsSend.getSmsType());// 业务类型
            			if(templateId==110){
            				if (params.containsKey("district")) {
            					set.put("district", params.get("district"));
            				}
            				if (params.containsKey("store_id")) {
            					set.put("store_id", params.get("store_id"));
            				}
            			}else if(templateId==119){
            				if (params.containsKey("receiver_name")) {
            					set.put("receiver_name", params.get("receiver_name"));
            				}
            				if (params.containsKey("receiver_province")) {
            					set.put("receiver_province", params.get("receiver_province"));
            				}
            				if (params.containsKey("receiver_city")) {
            					set.put("receiver_city", params.get("receiver_city"));
            				}
            				if (params.containsKey("receiver_area")) {
            					set.put("receiver_area", params.get("receiver_area"));
            				}
            			}
            			if (isVerifyTemplate) {// 判断该模板是否有验证码，1有 0无
            				set.put("code_value", verifyCode);
            				if (validTime != 0) {
            					set.put("valid_time", validTime);// 验证码的有效时间
            				}
            				set.put("is_valid", "1");// 1代表未验证，0代表已验证
            			}
            			
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
            				respInfo.setCode(DsResponseCodeData.SUCCESS.code);
            				respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
            			} else {
            				respInfo.setCode(DsResponseCodeData.ERROR.code);
            				respInfo.setDescription(actionResponse.getDescription());
            			}
            			if (templateId == 115) {
            				// 更改折扣码发送状态
            				updateDiscountCode(verifyCode, 0, 1);
            			}
            			// System.out.println("短信内容入库耗时："+(System.currentTimeMillis()-sendTime));
            		}
            	}
            }
        } catch (Exception e) {
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description);
            e.printStackTrace();
            LOG.info("SmsAction send error msg:" + e.getMessage());
        }
        Integer count=0;
        if(currentCount!=null){
            count=Integer.parseInt(currentCount.toString())+1;
        }else{
            count=1;
        }
        boolean result=memcacheService.putCache(smsSendIpKeyMd5, count.toString());
        LOG.info("smsAction put memcache smsSendIpKey:"+smsSendIpKey+",smsSendIpKeyMd5:"+smsSendIpKeyMd5+",result:"+result);
        return respInfo;
    }


	
	public Object validateSmsCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
		return validateSmsCode(request, response, repInfo,true);
	}
	

    /**
     * 
     * @Title: validateSmsCode
     * @Description: TODO(检测验证码、邀约码、折扣码是否有效)
     * @param @param request
     * @param @param response
     * @param @param repInfo
     * @param @return 参数说明
     * @return Object
     * @throws
     */
    @SuppressWarnings("unchecked")
	public Object validateSmsCode(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo,boolean makeInvalid) {
        BaseRespInfo respInfo = new BaseRespInfo();
        respInfo.setCode("1");
        respInfo.setDescription("验证失败!");
        DsManageReqInfo dsInfo = DataUtil.parse(repInfo.getParam(), DsManageReqInfo.class);
        String site_id=dsInfo.getParam().get("site_id")==null?"0":dsInfo.getParam().get("site_id").toString();
        dsInfo.setServiceName("YJG_BUV1_code");
        // long time = System.currentTimeMillis();
        System.out.println(JSONObject.toJSONString(dsInfo));
        ResponseInfo resp = DataUtil.parse(dataAction.getData(dsInfo), ResponseInfo.class);
        // long getTime = System.currentTimeMillis();
        // System.out.println("获取验证信息耗时："+(getTime-time));
        if (CollectionsUtils.isNull(resp.getRows())) {
            respInfo.setDescription(resp.getDescription());
            return respInfo;
        }
        String id = resp.getRows().get(0).get("id");
        String is_valid = resp.getRows().get(0).get("is_valid");
        if (!StringUtils.isEmpty(id) && "1".equals(is_valid)) {
        	if(makeInvalid){ 
        		DsManageReqInfo actionReqInfo = new DsManageReqInfo();
                actionReqInfo.setServiceName("MUSH_Offer");
                Action action = new Action();
                action.setSite_id(Integer.parseInt(site_id));
                action.setType("U");
                action.setServiceName("MeiqiServer_ecs_messaging_send");
                Map<String, Object> set = new HashMap<String, Object>();
                set.put("is_valid", "0");
                action.setSet(set);

                Where where = new Where();
                where.setPrepend("and");
                List<SqlCondition> cons1 = new ArrayList<SqlCondition>();
                SqlCondition con1 = new SqlCondition();
                con1.setKey("id");
                con1.setOp("=");
                con1.setValue(id);
                cons1.add(con1);
                where.setConditions(cons1);
                action.setWhere(where);

                List<Action> actions = new ArrayList<Action>();
                actions.add(action);
                Map<String, Object> param1 = new HashMap<String, Object>();
                param1.put("actions", actions);
                param1.put("transaction", 1);
                actionReqInfo.setParam(param1);

                String str = mushroomAction.offer(actionReqInfo);
                Map<String, Object> resMap = DataUtil.parse(str, Map.class);
                if (String.valueOf(resMap.get("code")).equals("0")) {
                    respInfo.setCode("0");
                    respInfo.setDescription("验证成功!");
                }
            }else{//如果是不需要验证码设置为失效，直接判断验证码是否一样则可
            	respInfo.setCode("0");
                respInfo.setDescription("验证成功!");
            }
            
            // System.out.println("验证信息更新耗时："+(getTime-time));
        }
        return respInfo;
    }



    /**
     * 
     * @Title: updateDiscountCode
     * @Description: TODO(更新折扣码、邀约码使用状态)
     * @param @param discountCode
     * @param @param orderId 参数说明
     * @return void 返回类型
     * @throws
     */
    private void updateDiscountCode(String discountCode, int status, int isSend) {
        String serviceName = "lejj_discount_info_app";

        // 调用mushroom 更新折扣码记录
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        Map<String, Object> set = new HashMap<String, Object>();
        if (status == 1 && isSend == 0) {
            // 已使用
            set.put("status", 1);
        }
        if (status == 0 && isSend == 1) {
            // 已发送
            set.put("is_send", 1);
        }

        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);
        action.setSet(set);

        Where where = new Where();
        where.setPrepend("and");
        List<SqlCondition> cons = new ArrayList<SqlCondition>();
        SqlCondition con = new SqlCondition();
        con.setKey("discount_code");
        con.setOp("=");
        con.setValue(discountCode);
        cons.add(con);
        where.setConditions(cons);
        action.setWhere(where);
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("transaction", 1);
        param.put("actions", actions);
        reqInfo.setServiceName("MUSH_Offer");
        reqInfo.setParam(param);
        // set Data
        String reslut = mushroomAction.offer(reqInfo);
        LOG.info("修改折扣码状态为已使用，结果：" + reslut);
    }



    /**
     * 
     * @Title: getCode
     * @Description:获取随机验证码
     * @param @param codeStr
     * @param @param count
     * @param @return
     * @return String
     * @throws
     */
    public static String getCode(String codeStr, int count) {
        String code = "";
        if (StringUtils.isBlank(codeStr)) {
            codeStr = ContentUtils.CODE_STRING;
        }
        String[] codeArray = codeStr.split(ContentUtils.COMMA);
        // 创建Random类的对象rand
        Random rand = new Random();

        for (int i = 0; i < count; ++i) {
            // 在0到str2.length-1生成一个伪随机数赋值给index
            int index = rand.nextInt(codeArray.length - 1);
            // 将对应索引的数组与randStr的变量值相连接
            code += codeArray[index];
        }
        return code;
    }



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
        String data1 = dataAction.getData(dsInfo);
        ResponseInfo resp = DataUtil.parse(data1, ResponseInfo.class);
        LOG.info("SmsAction YJG_BUV1_phone_code param is: " + map);
	    LOG.info("SmsAction YJG_BUV1_phone_code result is: " + data1);
	    if (!DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
	      LOG.info("SmsAction YJG_BUV1_phone_code param is: " + map);
	      LOG.info("SmsAction YJG_BUV1_phone_code result is: " + data1);
	      response.setCode(DsResponseCodeData.ERROR.code);
	      response.setDescription("查询短信验证码错误！");
	      return response;
	    }
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
     * 降价通知发送短信
     * @param smsSend
     * @return
     */
    private String depreciate(SMSSend smsSend,List<Map<String,String>> mapList,Map<String, Object> param,String result1){
    	BaseRespInfo respInfo = new BaseRespInfo();
    	
		if(mapList != null && mapList.size() > 0 ){
				JSONArray jsonArray = (JSONArray) JSONArray.toJSON(mapList);
				for (int j = 0; j < jsonArray.size(); j++) {
					Map<String, Object> param1 = new HashMap<String, Object>();
					param1.put("id", smsSend.getTemplateId());
					TService po = Services.getService(Services.SERVICE_SENDMESSAGE_INFO);
	            	D2Data data;
					try {
						data = com.meiqi.data.engine.DataUtil.getD2Data(po, param1);
						// 获取模板内容
		            	Object smsTemplate = data.getValue("模板内容", 0);
		            	if (smsTemplate == null) {
		            		respInfo.setCode(DsResponseCodeData.SMS_TEMPLATE_NOT_EXIST.code);
		            		respInfo.setDescription(DsResponseCodeData.SMS_TEMPLATE_NOT_EXIST.description);
		            		return JSON.toJSONString(respInfo);
		            	}
		            	String msg = data.getValue("模板内容", 0).toString();

		            	// 转换模板内容变量
		            	Map<String, String> params = new HashMap<String, String>();
		            	JSONObject jsonObject = jsonArray.getJSONObject(j);
						Iterator<String> iterator = jsonObject.keySet().iterator();
						while (iterator.hasNext()) {
							String key = iterator.next();
							String value = jsonObject.getString(key);
							params.put(key, value);
						}
		            	for (String key : params.keySet()) {
	    					String replaceKey = "\\{" + key + "\\}";
	    					msg = msg.replaceAll(replaceKey, params.get(key));
	    				}
		            	smsSend.setSendMsg(msg);
		        		smsSend.setPhoneNumber(jsonObject.get("mobile_phone").toString());
						String sendMessage = SMSSender.sendSms(data, smsSend);
						respInfo = DataUtil.parse(sendMessage, BaseRespInfo.class);
						if ("0".equals(respInfo.getCode())) {
							DsManageReqInfo actionReqInfo = new DsManageReqInfo();
							actionReqInfo.setServiceName("MUSH_Offer");
							Action action = new Action();
							action.setType("C");
							action.setServiceName("MeiqiServer_ecs_messaging_send");
							Map<String, Object> set = new HashMap<String, Object>();
							set.put("receive_phone", smsSend.getPhoneNumber());
							set.put("messaging_content", smsSend.getSendMsg());
							set.put("is_succeed", "1");
							set.put("send_time", DateUtils.getSecond());
							set.put("template_id", smsSend.getTemplateId());
							set.put("web_site", smsSend.getWebSite());
							set.put("type", smsSend.getSmsType());// 业务类型

							action.setSet(set);
							List<Action> actions = new ArrayList<Action>();
							actions.add(action);

							Action action1 = new Action();
							action1.setType("U");
							action1.setServiceName("test_ecshop_ecs_deprice_info");
							Map<String, Object> set1 = new HashMap<String, Object>();
							set1.put("status", 1);
							Where where = new Where();
							where.setPrepend("and");
							List<SqlCondition> conditions = new ArrayList<SqlCondition>();
							SqlCondition sqlCondition = new SqlCondition();
							sqlCondition.setKey("goods_id");
							sqlCondition.setOp("=");
							sqlCondition.setValue(jsonObject.get("goods_id"));
							
							SqlCondition sqlCondition1 = new SqlCondition();
							sqlCondition1.setKey("mobile_phone");
							sqlCondition1.setOp("=");
							sqlCondition1.setValue(jsonObject.get("mobile_phone"));
							
							conditions.add(sqlCondition);
							conditions.add(sqlCondition1);
							where.setConditions(conditions);
							action1.setSet(set1);
							action1.setWhere(where);
							actions.add(action1);
							
							Map<String, Object> param2 = new HashMap<String, Object>();
							param2.put("actions", actions);
							param2.put("transaction", 1);
							actionReqInfo.setParam(param2);
							SetServiceResponseData actionResponse = null;
							String res1 = mushroomAction.offer(actionReqInfo);
							actionResponse = DataUtil.parse(res1, SetServiceResponseData.class);
							if (Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())) {
								respInfo.setCode(DsResponseCodeData.SUCCESS.code);
								respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
							} else {
								LOG.info("SendOperationMsg depreciate param is: "+param2);
			            		LOG.info("SendOperationMsg depreciate result is: "+res1);
								respInfo.setCode(DsResponseCodeData.ERROR.code);
								respInfo.setDescription(actionResponse.getDescription());
								return JSON.toJSONString(respInfo);
							}
						}
					} catch (RengineException e) {
						respInfo.setCode(DsResponseCodeData.ERROR.code);
			            respInfo.setDescription(DsResponseCodeData.ERROR.description);
			            e.printStackTrace();
			            LOG.info("SmsAction send error msg:" + e.getMessage());
					}
	            	
				}
		}else{
			LOG.info("SendOperationMsg YJG_BUV1_SendMassage param is: "+param);
    		LOG.info("SendOperationMsg YJG_BUV1_SendMassage result is: "+result1);
			LOG.info("SendOperationMsg mapList length is: "+mapList.size());
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("系统出错！");
			return JSON.toJSONString(respInfo);
		}
		return JSON.toJSONString(respInfo);
    }
    
    /**
     * 判断是否可以发送短信
     * @param phone_num
     * @param sendDs
     * @return
     */
    private BaseRespInfo isSend(String phone_num,DsManageReqInfo sendDs){
    	BaseRespInfo respInfo = new BaseRespInfo();
    	Map<String,Object> sendMap = new HashMap<String, Object>();
    	sendMap.put("phone_num", phone_num);
        sendMap.put("code", "maxinf001");
        sendDs.setParam(sendMap);
        ResponseInfo sendResp = DataUtil.parse(dataAction.getData(sendDs), ResponseInfo.class);
        if("1".equals(sendResp.getCode())){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(DsResponseCodeData.ERROR.description);
            LOG.info("SmsAction_phone_get_error:规则[YJG_HSV1_phonemessages_limt]获取数据失败，参数信息["+DataUtil.toJSONString(sendDs)+"],时间["+DateUtils.formatDateToString(new Date())+"]");
            return respInfo;
        }
        List<Map<String,String>> phoneList = sendResp.getRows();
        if(null!=phoneList&&phoneList.size()>0){
            Map<String,String> phoneMap = phoneList.get(0);
            //1=能发送，0=不能发送
            if("0".equals(phoneMap.get("is_send"))){
                respInfo.setCode(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.code);
                respInfo.setDescription(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description);
                LOG.info("SmsAction_phone_send_limit:"+DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description+",时间["+DateUtils.formatDateToString(new Date())+"]");
                return respInfo;
            }
        }
		return null;
    }
    
    
    /**
     * 新的发送短信方法
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
	@SuppressWarnings("unchecked")
	public String sendMsg(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
		String result = "";
		Map<String,Object> param = DataUtil.parse(repInfo.getParam(), Map.class);
        String content = repInfo.getParam();
        String ip="";
        String blackip="";
        if(request!=null){
            ip=IpAction.getClientIP(request);
        }
        String whiteip="";
        try {
            whiteip=whiteProperties.getProperty("ip");
            LOG.info("smsAction send current ip:"+ip +",blackip :"+blackip+",content:"+content);
        } catch (Exception e) {
            LOG.info("smsAction send current ip:"+ip +",blackip :"+blackip+",content:"+content+",error:"+e);
        }
        boolean flag = false;//是否在白名单内,默认值为不在
        if(StringUtils.isNotEmpty(whiteip) && StringUtils.isNotEmpty(ip)){
            String[] ipArray = ip.split(",");
            for(String ipTmp:ipArray){
                if(whiteip.contains(ipTmp.trim())){
                    flag=true;//在白名单内
                    break;
                }
            }
        }
        BaseRespInfo respInfo = new BaseRespInfo();
        
        Object currentCount=0;
        String smsSendIpKeyMd5="";
        String smsSendIpKey="";
        if(!flag && com.meiqi.openservice.commons.util.StringUtils.isNotEmpty(SysConfig.getValue("smsLimit"))){
            String date=DateUtils.formatDateToSimpleString(new Date());
            smsSendIpKey="smsSendIpKey_"+date+"_"+ip;
            smsSendIpKeyMd5=MD5Util.MD5(smsSendIpKey);
            currentCount=memcacheService.getCache(smsSendIpKeyMd5);
            Integer smsLimit=Integer.parseInt(SysConfig.getValue("smsLimit"));
                if(currentCount!=null){
                    if(Integer.parseInt(currentCount.toString()) >= smsLimit){
                        respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
                        respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
                        return JSONObject.toJSONString(respInfo);
                    }
                }
        }
        //判断是来自什么地方的请求
        boolean fromApp = isFromApp(request);
        String isfromApp = "0";
        if(fromApp){
        	isfromApp = "1";
        }
        
        DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
        dsManageReqInfo1.setNeedAll("1");
	    dsManageReqInfo1.setServiceName("YJG_HSV1_MessageSend");
	    JSONObject jsonObject = JSONObject.parseObject(repInfo.getParam());
	    jsonObject.put("isfromApp", isfromApp);
	    dsManageReqInfo1.setParam(jsonObject);
	    String resultData = this.dataAction.getData(dsManageReqInfo1, "");
	    RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
	    LOG.info("sendMsg YJG_HSV1_MessageSend param is: " + jsonObject);
	    LOG.info("sendMsg YJG_HSV1_MessageSend result is: " + resultData);
	    if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
	      LOG.info("sendMsg YJG_HSV1_MessageSend param is: " + jsonObject);
	      LOG.info("sendMsg YJG_HSV1_MessageSend result is: " + resultData);
	      respInfo.setCode(DsResponseCodeData.ERROR.code);
	      respInfo.setDescription("查询短信模板错误！");
	      return JSON.toJSONString(respInfo);
	    }
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		mapList = responseData.getRows();
		if(mapList != null && mapList.size() > 0){
			String return_message = mapList.get(0).get("return_message");
			if(!StringUtils.isEmpty(return_message)){
				JSONArray resArray = JSONArray.parseArray(return_message);
				for (int i = 0; i < resArray.size(); i++) {
					//判断该电话号码24小时内发送次数是否超过20条
					DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
					dsManageReqInfo.setServiceName("YJG_HSV1_phonemessages_limt");
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("phone_num", resArray.getJSONObject(i).get("phone"));
					paramMap.put("code", "maxinf001");
					dsManageReqInfo.setParam(paramMap);
					String str1 = dataAction.getData(dsManageReqInfo);
					ResponseInfo sendResp = DataUtil.parse(str1, ResponseInfo.class);
					if("1".equals(sendResp.getCode())){
						respInfo.setCode(DsResponseCodeData.ERROR.code);
						respInfo.setDescription(DsResponseCodeData.ERROR.description);
						LOG.info("SmsAction_phone_get_error:规则[YJG_HSV1_phonemessages_limt]获取数据失败，参数信息["+DataUtil.toJSONString(dsManageReqInfo)+"],时间["+DateUtils.formatDateToString(new Date())+"]");
						return JSONObject.toJSONString(respInfo);
					}
					List<Map<String,String>> phoneList = sendResp.getRows();
					if(null!=phoneList && phoneList.size()>0){
						Map<String,String> phoneMap = phoneList.get(0);
						//1=能发送，0=不能发送
						if("0".equals(phoneMap.get("is_send"))){
							respInfo.setCode(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.code);
							respInfo.setDescription(DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description);
							LOG.info("SmsAction_phone_send_limit:"+DsResponseCodeData.SMS_SEND_PHONE_LIMIT.description+",时间["+DateUtils.formatDateToString(new Date())+"]");
							return JSONObject.toJSONString(respInfo);
						}
					}else{
						respInfo.setCode(DsResponseCodeData.ERROR.code);
						respInfo.setDescription(DsResponseCodeData.ERROR.description);
						LOG.info("sendMsg YJG_HSV1_phonemessages_limt result length is: "+phoneList.size());
						LOG.info("sendMsg YJG_HSV1_phonemessages_limt param is: "+paramMap);
						LOG.info("sendMsg YJG_HSV1_phonemessages_limt result is: "+str1);
						return JSONObject.toJSONString(respInfo);
					}
					
					//判断是否是图形验证码的业务类型(1表示是，0则是否)
					if(resArray.getJSONObject(i).get("isVerifyPictureCode").equals("1")){
						boolean r=Tool.verifyCode(request, JSONObject.parseObject(repInfo.getParam()).get("pictureCode").toString(),JSONObject.parseObject(repInfo.getParam()).get("pictureCodeType").toString(),true);
						if(!r){
							respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
							respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
							return JSONObject.toJSONString(respInfo);
						}
					}
					//判断短信模板是否是发送短信验证码的模板 1表示是 0则是否
					if(resArray.getJSONObject(i).get("is_code").equals("1")){
						respInfo = updatePreCode(resArray.getJSONObject(i).get("phone").toString(), Long.valueOf(resArray.getJSONObject(i).get("templateId").toString()),
								Integer.parseInt(param.get("website").toString()), Integer.parseInt(param.get("smsType").toString()));
					}
					if("0".equals(respInfo.getCode()) && "1".equals(resArray.getJSONObject(i).get("is_sendMsg").toString())){
						//发送短信
						String passWebSiteSendMsg = passWebSiteSendMsg(param.get("website").toString(), resArray.getJSONObject(i), respInfo,resultData,JSONObject.parseObject(repInfo.getParam()));
						String[] split = passWebSiteSendMsg.split(",");
						result = split[0];
						//发送完短信后的写入操作
						DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
						dsManageReqInfo2.setNeedAll("1");
						dsManageReqInfo2.setServiceName("YJG_HSV1_MessageSendWrite");
						JSONObject parseObject = JSONObject.parseObject(repInfo.getParam());
						parseObject.put("receive_phone", resArray.getJSONObject(i).get("phone").toString());
						parseObject.put("messaging_content", resArray.getJSONObject(i).get("content").toString());
						parseObject.put("is_succeed", split[1]);
						parseObject.put("template_id", resArray.getJSONObject(i).get("templateId").toString());
						parseObject.put("web_site", parseObject.get("website"));
						parseObject.put("type", parseObject.get("smsType"));
						String page_link = parseObject.get("page_link").toString().replaceAll("#.*", "");
						parseObject.put("page_link", page_link);
						parseObject.put("is_write", 1);
						if(!StringUtils.isEmpty(mapList.get(0).get("login_code"))){
							parseObject.put("code_value", mapList.get(0).get("login_code"));
							parseObject.put("valid_time", DateUtils.getValidSecond(10));
							parseObject.put("is_valid", 1);
						}
						dsManageReqInfo2.setParam(parseObject);
						String resultData1 = this.dataAction.getData(dsManageReqInfo2, "");
						RuleServiceResponseData responseData1 = DataUtil.parse(resultData1, RuleServiceResponseData.class);
						List<Map<String, String>> rows = responseData1.getRows();
						String write = rows.get(0).get("write");
						JSONObject parseObject2 = JSONObject.parseObject(write);
					    LOG.info("sendMsg YJG_HSV1_MessageSendWrite param is: " + parseObject);
					    LOG.info("sendMsg YJG_HSV1_MessageSendWrite result is: " + write);
					    if (!DsResponseCodeData.SUCCESS.code.equals(parseObject2.get("code"))) {
					      LOG.info("sendMsg YJG_HSV1_MessageSendWrite param is: " + parseObject);
					      LOG.info("sendMsg YJG_HSV1_MessageSendWrite result is: " + write);
					      LOG.info("SmsAction sendMsg 发送短信异常，标识为: "+split[0]);
					      respInfo.setCode(DsResponseCodeData.ERROR.code);
					      respInfo.setDescription("短信发送写入错误错误！");
					      return JSON.toJSONString(respInfo);
					    }
						
					}else{
						return JSONObject.toJSONString(respInfo);
					}
					
				}
			}else{
				LOG.info("sendMsg YJG_HSV1_MessageSend param is: " + JSONObject.parseObject(repInfo.getParam()));
				LOG.info("sendMsg YJG_HSV1_MessageSend result is: " + resultData);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("短信通道未找到！");
				return JSON.toJSONString(respInfo);
			}
			
		}else{
			LOG.info("sendMsg YJG_HSV1_MessageSend result length is: " + mapList.size());
			LOG.info("sendMsg YJG_HSV1_MessageSend param is: " + jsonObject);
			LOG.info("sendMsg YJG_HSV1_MessageSend result is: " + resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询短信模板无数据！");
			return JSON.toJSONString(respInfo);
		}
		respInfo.setDescription("Success,短信发送结果: "+result);
		return JSONObject.toJSONString(respInfo);
	}

	public String passWebSiteSendMsg(String webSite,JSONObject jsonObject,BaseRespInfo respInfo,String resultData,JSONObject jsonObject2){
		String result = "";
		String is_succeed = "1";
		if ("2".equals(webSite)) {
			JaxWsProxyFactoryBean  factory=new JaxWsProxyFactoryBean();           
	        factory.getInInterceptors().add(new LoggingInInterceptor());  
	        factory.getOutInterceptors().add(new LoggingOutInterceptor());            
	        factory.setServiceClass(HomeLikeSoap.class);  
	        factory.setAddress("http://ws.holike.com:9000/HomeLike.asmx?WSDL");  
	        factory.setServiceName(new QName("http://tempuri.org/", "HomeLikeSoap"));
	        HomeLikeSoap service=(HomeLikeSoap)factory.create(); 
	        boolean sendSms = service.sendSms(jsonObject.getString("phone").toString(), jsonObject.getString("content"),"HomeLike");
	        if(!sendSms){
	        	is_succeed = "0";
	        	result = "homelike send msg is false";
	        	LOG.info("SmsAction YJG_HSV1_MessageSend param is: " + jsonObject2);
				LOG.info("SmsAction YJG_HSV1_MessageSend result is: " + resultData);
	        }
	        result = "homelike send msg is success";
		}else {
			String str = jsonObject.get("sendUrl").toString();
			try {
				result = SMSSender.getUrl(str);
				String[] str2 = result.split(",");
				// 判断发送短信是否异常，异常则返回异常标识
				if (!"3".equals(webSite) && !"1".equals(str2[0]) && (str2[0].indexOf("-") == -1)) {
					is_succeed = "0";
					LOG.info("SmsAction sendMsg 发送短信异常，标识为: " + str2[0]);
					LOG.info("SmsAction YJG_HSV1_MessageSend param is: " + jsonObject2);
					LOG.info("SmsAction YJG_HSV1_MessageSend result is: " + resultData);
				}
				if ("3".equals(webSite) || "4".equals(webSite)) {
					String valueOf = String.valueOf(str2[1].charAt(0));
					if (!"0".equals(valueOf)) {
						is_succeed = "0";
						LOG.info("SmsAction sendMsg 发送短信异常，标识为: " + str2[0]);
						LOG.info("SmsAction YJG_HSV1_MessageSend param is: " + jsonObject2);
						LOG.info("SmsAction YJG_HSV1_MessageSend result is: " + resultData);
					}
				}
				result = str2[0];
			} catch (IOException e) {
				LOG.info("SmsAction sendMsg sendUrl is: "+jsonObject.get("sendUrl").toString());
				LOG.info("请求短信通道异常");
				respInfo.setCode("1");// 失败
				StringBuilder errMsg=new StringBuilder();
				errMsg.append("失败,请求路径：");
				errMsg.append(jsonObject.get("sendUrl").toString());
				errMsg.append(" Error Detail：");
				errMsg.append(""+e);
				respInfo.setDescription(errMsg.toString());
				LOG.info(errMsg.toString());
			}

		}
		return result+","+is_succeed;
		
	}
    
}
