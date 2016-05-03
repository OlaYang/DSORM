package com.meiqi.dsmanager.action.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.dao.ITServiceAlarmDao;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.data.handler.BaseRespInfo;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.action.ISendMessageAction;
import com.meiqi.dsmanager.exception.DsException;
import com.meiqi.dsmanager.po.rule.smsSend.SMSSend;
import com.meiqi.dsmanager.po.rule.smsSend.SMSSender;
import com.meiqi.dsmanager.po.rule.smsSend.ServiceSendReqInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.util.RuleExceptionUtil;
import com.meiqi.openservice.commons.util.StringUtils;

@Service
public class SendMessageActionImpl implements ISendMessageAction {

    @Autowired
    private RuleExceptionUtil ruleExceptionUtil;
    
	@Override
	public String sendMessage(String content) {
		boolean flag = false;
		BaseRespInfo respInfo = new BaseRespInfo();
		// 验证传入的字符串是否为空
		if (0 == content.trim().length()) {
			throw new IllegalArgumentException("接受到的参数不能为空值! ");
		}

		// 将传入的json字符串解析封装成SMSSend对象
		SMSSend smsSend;
		try {
			ServiceSendReqInfo serviceSendReqInfo = DataUtil.parse(content, ServiceSendReqInfo.class);
			smsSend = serviceSendReqInfo.getParam();
			// 检测发送auth_key是否正确
			if (!"lejjsms".equals(smsSend.getAuth_key())) {
				throw new IllegalArgumentException("auth_key验证失败！ ");
			}
			
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("id", smsSend.getTemplateId());

			TService po = Services.getService(Services.SERVICE_SENDMESSAGE_INFO);
			D2Data data = DataUtil.getD2Data(po, param);
	        

			//获取模板内容
			String msg=data.getValue("模板内容", 0).toString();
			//转换模板内容变量
			Map<String,String> params=smsSend.getParam();
			for(String key:smsSend.getParam().keySet()){
				String replaceKey="\\{"+key+"\\}";
				msg=msg.replaceAll(replaceKey, smsSend.getParam().get(key));
				
			}
			smsSend.setSendMsg(msg);
			
	        String sendMessage = SMSSender.sendSms(data,smsSend);
	        respInfo.setDescription(sendMessage);
	        flag = true;
	        
	        //将发送记录写入数据库
//	        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
//	        actionReqInfo.setServiceName("MUSH_Offer");
//	        String serviceName="MeiqiServer_ecs_messaging_send";
//	        Action action = new Action();
//	        action.setType("C");
//	        action.setServiceName(serviceName);
//	        Map<String, Object> set = new HashMap<String, Object>();
//	        set.put("receive_phone", smsSend.getPhoneNumber());
//	        set.put("messaging_content", smsSend.getSendMsg());
//	        set.put("is_succeed", "0");
//	        set.put("send_time", DateUtils.getSecond());
//	        set.put("district", params.get("district"));
//	        set.put("address",  params.get("address"));
//	        set.put("store_id", params.get("store_id"));
//	        set.put("template_id", smsSend.getTemplateId());
//	        action.setSet(set);
//	        List<Action> actions = new ArrayList<Action>();
//	        actions.add(action);
//	        
//	        Map<String,Object> param1=new HashMap<String, Object>();
//	        param1.put("actions", actions);
//	        param1.put("transaction", 1);
//	        actionReqInfo.setParam(param1);
//	        SetServiceResponseData actionResponse=null;
//	        String res1=mushroomAction.offer(actionReqInfo);
//	        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);
//	        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){
//	            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
//	            respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
//	        }else{
//	            respInfo.setCode(DsResponseCodeData.ERROR.code);
//	            respInfo.setDescription(actionResponse.getDescription());
//	        }
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.error(e.getMessage());
			ruleExceptionUtil.run(e);
		}
		if(!flag){
			respInfo.setCode("-1");
			respInfo.setDescription("Failed");
		}
		return JSON.toJSONString(respInfo);
	}

}
