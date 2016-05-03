package com.meiqi.openservice.threads;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.util.MyApplicationContextUtil;

/**
 * 发送短信 订单确定
 * @author meiqidr
 *
 */
/**需求 #12653
 * 订单状态从未确认到确认状态发送短信
 * @author Administrator
 *
 */
public class SendMessageOrderConfirm implements Runnable{

	private static final Log LOGSMS =  LogFactory.getLog("sms");
	
	
    private SmsAction smsAction;
	
	private Map<String,Object> params;
    private HttpServletRequest request;
    private HttpServletResponse response;
	
    public SendMessageOrderConfirm(HttpServletRequest request,HttpServletResponse response, Map<String,Object> params){
    	this.params = params;
        this.request = request;
        this.response = response;
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 String previousOrderStatus=params.get("previousOrderStatus")==null?"":params.get("previousOrderStatus").toString();
         String order_status=params.get("order_status")==null?"":params.get("order_status").toString();
         if(StringUtils.isNotEmpty(previousOrderStatus) && StringUtils.isNotEmpty(order_status) && Constants.ORDER_STATUS.UN_CONFIRM == Integer.parseInt(previousOrderStatus) && Constants.ORDER_STATUS.CONFIRMED == Integer.parseInt(order_status)){
             //如果之前状态是未确认，现在的订单状态是已确认，那么发送短信
				RepInfo repInfo1 = new RepInfo();
				repInfo1.setAction("smsAction");
				repInfo1.setMethod("sendMsg");
				String smsParam = "{\"website\":\"0\",\"page_link\":\"\",\"order_sn\":\"" + params.get("order_sn")
						+ "\",\"template_name_en\":\"dddxtzb\",\"smsType\":\"33\"}";
				repInfo1.setParam(smsParam);
				
				if(null==smsAction){
					ApplicationContext applicationContext=MyApplicationContextUtil.getContext();
					smsAction =(SmsAction)applicationContext.getBean("smsAction");
				}
		        
				String sendMsg = smsAction.sendMsg(request, response, repInfo1);
				JSONObject jsonBaseRespInfo = (JSONObject) JSONObject.parseObject(sendMsg);
				if("1".equals(jsonBaseRespInfo.get("code"))){
					LOGSMS.info("PayGiveRedPacketAction send is fail and smsParam is: "+smsParam);
			  	}
         }
	}

}
