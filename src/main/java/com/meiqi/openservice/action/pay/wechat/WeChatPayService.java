package com.meiqi.openservice.action.pay.wechat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.dsmanager.util.XmlUtil;
import com.meiqi.openservice.action.pay.PayService;
import com.meiqi.openservice.action.pay.pc.alipay.config.AliPayConfig;
import com.meiqi.openservice.action.pay.wechat.app.WeChatAppSignUtil;
import com.meiqi.openservice.commons.util.CollectionsUtils;

/**
 * 支付通知业务处理
 * @author duanran
 *
 */
@Service
public class WeChatPayService {
	@Autowired
	private PayService payService;
	@Autowired
	private WeChatAppSignUtil weChatAppSignUtil;
	@Autowired
	private IMushroomAction mushroomAction;
	
	@Autowired
	private IDataAction dataAction;
	
	private static final Log LOG =  LogFactory.getLog("pay");
	
	/**
	 * 支付成功后业务处理
	 */
	public JSONObject paySuccessDoService(String cash_fee,String out_trade_no,String transaction_id,String openId,String mchId,String orderType,String out_trade_no1){
		JSONObject resultJsonObject=new JSONObject();
		//使用wechat+微信订单号，保证支付中是唯一标识
		String key="wechat"+transaction_id;
		//不在支付业务队列中
		if(null==PayService.paying.get(key)){
			//立刻加入支付业务处理队列中，防止再次通知造成业务处理重复
			PayService.paying.put(key, transaction_id);
			try {
				//到规则中获取订单查询信息
				Map<String,String> orderMap=payService.getOrderInfoFromData(out_trade_no,orderType);
				//检测是否能取到该订单的信息
				if(null==orderMap||0==orderMap.size()){
					resultJsonObject.put("return_code", "FAIL");
					resultJsonObject.put("return_msg", "找不到指定的订单信息!");
					return resultJsonObject;
				}
				long orderId=Long.parseLong(orderMap.get("order_id"));
				long userId=Long.parseLong(orderMap.get("user_id"));
				String userName=orderMap.get("user_name");
				String pay_status=orderMap.get("pay_status"); //支付状态
				String previousOrderStatus=orderMap.get("order_status").toString();
				double order_amount=Double.parseDouble(orderMap.get("order_amount").toString()); //订单总金额
				double paid=Double.parseDouble(orderMap.get("paid").toString());//已付款
				double needPayMoney=order_amount-paid;//该订单应该支付的金额 订单总金额-已付款金额
				double cashFee=Double.parseDouble(payService.intString2DoubleStringFee(cash_fee)); //微信支付金额
				if(needPayMoney<=0){ //订单还需支付的金额小宇0，表示订单已经付完了指定的金额，只是可能状态没改变
					resultJsonObject.put("return_code", "FAIL");
					resultJsonObject.put("return_msg", "订单余款已经付完，不需再次付款!");
					return resultJsonObject;
				}
				if(cashFee<=0){ //支付的金额小于等于0
					resultJsonObject.put("return_code", "FAIL");
					resultJsonObject.put("return_msg", "订单付款金额必须大于0!");
					return resultJsonObject;
				}
				if("0".equals(pay_status)||"1".equals(pay_status)||"5".equals(pay_status)){//未付款,或者只付了部分，.
					String updateType=updateOrderInfo(cashFee,orderId,out_trade_no,transaction_id,paid,order_amount,userId,userName,openId,mchId,out_trade_no1);
					if(!"success".equals(updateType)){ //数据持久化失败
						resultJsonObject.put("return_code", "FAIL");
						resultJsonObject.put("return_msg", "订单状态更新失败!");
						return resultJsonObject;
					}
					resultJsonObject.put("return_code", "SUCCESS");
					resultJsonObject.put("order_sn", out_trade_no);
					resultJsonObject.put("previousOrderStatus", previousOrderStatus);
					resultJsonObject.put("order_status", Constants.ORDER_STATUS.CONFIRMED);
					
					return resultJsonObject;
				}else{ //订单状态不为可支付状态
					resultJsonObject.put("return_code", "FAIL");
					resultJsonObject.put("return_msg", "订单已经付款或者被关闭，不能在进行付款!");
					return resultJsonObject;
				}
			} catch (Exception e) {
				LOG.error(e.getMessage()+"订单状态更新失败");
				resultJsonObject.put("return_code", "FAIL");
				resultJsonObject.put("return_msg", "订单状态更新失败!");
				return resultJsonObject; 
			}finally{
				PayService.paying.remove(key);
			}
		}else{//已在支付业务中（支付结果重复通知）
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "已存在相同订单通知！");
			return resultJsonObject;
		}
	}
	
	/**
	 * 更新订单信息，包括添加流水号
	 *  cashFee支付金额
	 *  orderId 支付的订单id
	 *  orderSn支付的订单编号
	 *  transaction_id三方微信订单编号
	 *  return success 操作成功 fail操作失败
	 */
	private String updateOrderInfo(double cashFee,long orderId,String orderSn,String transaction_id,double paid,double order_amount,long userId,String userName,String openId,String mchId,String out_trade_no1){
		
		DsManageReqInfo serviceReqInfoTmp=new DsManageReqInfo();
		serviceReqInfoTmp.setServiceName("YJG_BUV1_order_pay");
		Map<String,Object> paramTmp=new HashMap<String, Object>();
		paramTmp.put("pay_type", Constants.PAY_TYPE.WECHAT_PAY);
		paramTmp.put("transaction_id", transaction_id);
		serviceReqInfoTmp.setParam(paramTmp);
		serviceReqInfoTmp.setNeedAll("1");
		RuleServiceResponseData responseDataTmp = null;
		String dataTmp =dataAction.getData(serviceReqInfoTmp,"");
		responseDataTmp = DataUtil.parse(dataTmp, RuleServiceResponseData.class);
		List<Map<String,String>> listTmp=responseDataTmp.getRows();
		if(!CollectionsUtils.isNull(listTmp)){
			LOG.info("WeChat pay param is: "+Constants.PAY_TYPE.WECHAT_PAY+","+transaction_id);
			LOG.info("WeChat pay result is: "+dataTmp);
			return "success";
		}
	    int currentTime=DateUtils.getSecond();
	    //定义即将调用的3个mushroom服务
		String serviceName = "ecs_pay_log_app"; //订单流水号
    	String serviceName1 = "ecs_order_info_app"; //订单详情
    	String serviceName2 = "test_ecshop_ecs_order_action"; //订单操作日志
    	
    	// 调用mushroom 
    	DsManageReqInfo actionReqInfo = new DsManageReqInfo();
    	actionReqInfo.setServiceName("MUSH_Offer");
        
    	//向ecs_pay_log插入支付流水记录
        Action action = new Action();
        action.setType("C");
        action.setServiceName(serviceName);
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("order_id", orderId);
        set.put("order_amount", cashFee);
        set.put("order_type", Constants.ORDER_TYPE.NORMAL);
        set.put("is_paid", Constants.PAY_STATUS.PAID);
        set.put("pay_type",  6); //微信支付 ，编号定义设置见数据库
        set.put("remark",  Constants.ORDER_TYPE.NORMAL);
        set.put("add_time", currentTime);
        set.put("transaction_id", transaction_id);
        set.put("third_order_sn", out_trade_no1);

        set.put("paid_account", openId);
        set.put("gether_account", mchId);
        set.put("creat_name", userName);
        action.setSet(set);
        
        //更新订单详情操作
        Action action1 = new Action();
        action1.setType("U");
        action1.setServiceName(serviceName1);
        Map<String, Object> set1 = new HashMap<String, Object>();
        double allPaidMoney=paid+cashFee;
        //set1.put("money_paid", allPaidMoney); //订单已付，本次付款+以前的付款
        if (order_amount <= allPaidMoney) { //如果当前支付加上历史支付大于订单总金额，设置支付状态为完成支付
                 set1.put("pay_status", Constants.PAY_STATUS.PAID);
        } else {//部分支付
                 set1.put("pay_status", Constants.PAY_STATUS.PART_PAID);
        }
        set1.put("order_status", Constants.ORDER_STATUS.CONFIRMED);
        set1.put("pay_time",currentTime);
        action1.setSet(set1);

        Where where1 = new Where();
        where1.setPrepend("and");

        List<SqlCondition> cons1 = new ArrayList<SqlCondition>();
        SqlCondition con1 = new SqlCondition();
        con1.setKey("order_id");
        con1.setOp("=");
        con1.setValue(orderId);
        cons1.add(con1);

        where1.setConditions(cons1);
        action1.setWhere(where1);
        
        //新增操作记录
        Action action2 = new Action();
        action2.setType("C");
        action2.setServiceName(serviceName2);
        Map<String, Object> set2 = new HashMap<String, Object>();
        set2.put("order_id", orderId);
        set2.put("action_user",userName);
        set2.put("order_status",1);
        set2.put("shipping_status",0);
        if (order_amount <= allPaidMoney) { //如果付款金额大于等于订单金额，设置状态全部付款
            set2.put("pay_status",2);
        }else{
            set2.put("pay_status",5); //如果付款金额小宇订单金额，设置状态部分
        }
        set2.put("action_note","您已付款【"+cashFee+"】元。");
        set2.put("log_time",currentTime);
        set2.put("is_show","1");
        action2.setSet(set2);
        
        
        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        actions.add(action1);
        actions.add(action2);
        
        Map<String,Object> param1=new HashMap<String, Object>();
        param1.put("actions", actions);
        param1.put("transaction", 1);
        actionReqInfo.setParam(param1);
        
        //update参数封装完成，调用mushroom持久化操作
        SetServiceResponseData actionResponse=null;
        String res1=mushroomAction.offer(actionReqInfo);
        LogUtil.info("wechat pay mushroom result="+res1);
        actionResponse= DataUtil.parse(res1, SetServiceResponseData.class);//解析返回报文为实体
        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){ //如果持久化成功
//        	eTagService.putEtagMarking("order/"+orderId,Long.toString(System.currentTimeMillis()));
        	return "success";
        	
        }
		return "fail";
	}
	
	
	/**
	 * 获取yjg支付签名信息 统一下单参数生成
	 * @param reqOrderSn
	 * @return
	 * @throws IOException
	 * reqOrderSn：支付的订单号
	 * ip：发起支付的设备ip
	 * userAgent：支付设备标识 app、js、native
	 * openID：如果是js支付的话，用户的openid
	 * currentPayMoney 不为null表示为分期支付，分期支付金额， 单位分 1000=10元
	 */
	public String yjgAppToPay(String reqOrderSn,String ip,String userAgent,String orderType,String openID,String currentPayMoney){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code", "1");
        if(null==reqOrderSn){
        	jsonObject.put("msg", "缺少订单号！");
        	return jsonObject.toJSONString();
        }
        if(null==orderType){
        	orderType="2";
        }
        Map<String, String> orderMap=payService.getOrderInfoFromData(reqOrderSn,orderType);
        if(null==orderMap){
        	jsonObject.put("msg", "订单编号错误！");
        	return jsonObject.toJSONString();
        }
        String orderSn=orderMap.get("order_sn").trim();
        if(!reqOrderSn.equals(orderSn)){
        	jsonObject.put("msg", "订单编号不一致！");
        	return jsonObject.toJSONString();
        }
        
        StringBuffer out_trade_no = new StringBuffer(orderSn);
        
		out_trade_no.append(AliPayConfig.up).append(System.currentTimeMillis()/1000).append(AliPayConfig.up).append(orderType);
	  //微信金额,获取金额
        String totalFeeString=orderMap.get("order_amount");
        if(null==totalFeeString||"".equals(totalFeeString)){
        	jsonObject.put("msg", "缺少订单金额！");
        	return jsonObject.toJSONString();
        }
        //获取支付状态
        String pay_status=orderMap.get("pay_status");
        if("0".equals(pay_status)){ //还未付款
        	//金额 double字符串转int字符串 
            totalFeeString=payService.doubleString2IntStringFee(totalFeeString);
        }else if("1".equals(pay_status)||"5".equals(pay_status)){ //部分付款
        	//金额 double字符串转int字符串 
        	String paid="0";
        	if(!"".equals(orderMap.get("paid"))){
        		paid=orderMap.get("paid");
        	}
        	double payMoney=Double.parseDouble(totalFeeString)-Double.parseDouble(paid);
        	if(0>=payMoney){
        		jsonObject.put("msg", "支付金额有误，不能下单！");
            	return jsonObject.toJSONString();
        	}
            totalFeeString=payService.doubleString2IntStringFee(String.valueOf(payMoney));
        }else{ //已付款、已退款情况
        	jsonObject.put("msg", "该订单状态不能支付（订单已完成支付或者订单已退款）");
        	return jsonObject.toJSONString();
        }
        
        long totalFee=Long.parseLong(totalFeeString);
        if(null==currentPayMoney){
            if(0>=totalFee){
            	jsonObject.put("msg", "订单金额小于等于0！");
            	return jsonObject.toJSONString();
            }
        }else{
        	long currentPayMoneyLong=Long.parseLong(currentPayMoney);
        	if(0>=currentPayMoneyLong){
            	jsonObject.put("msg", "支付金额小于等于0！");
            	return jsonObject.toJSONString();
            }
        	if(totalFee<currentPayMoneyLong){
        		jsonObject.put("msg", "支付的金额超过了订单金额！");
            	return jsonObject.toJSONString();
        	}
        	totalFeeString=currentPayMoney;
        }
        
        Map<String, String> urlParamMap=new HashMap<String, String>();
        urlParamMap.put("body", "订单号:"+orderSn);
        urlParamMap.put("out_trade_no", out_trade_no.toString());
        urlParamMap.put("total_fee", totalFeeString);
        urlParamMap.put("spbill_create_ip", ip);
        urlParamMap.put("userAgent", userAgent);
        urlParamMap.put("openid", openID);
        if("Native".equals(userAgent)){
        	urlParamMap.put("product_id", orderSn);
        }
        urlParamMap=weChatAppSignUtil.generateSignParam(urlParamMap);
        if(null==urlParamMap){
        	jsonObject.put("msg", "生成签名失败！");
        	return jsonObject.toJSONString();
        }
        String appKey= urlParamMap.get("appKey");
        urlParamMap.remove("appKey");
        JSONObject jObject=JSONObject.parseObject(JSONObject.toJSONString(urlParamMap));
        //json转换xml
        String xmlObject=XmlUtil.jsonToXml(jObject.toJSONString(), "xml",false);
      
        LogUtil.info("requset unifedorder xml="+xmlObject);
        //调用微信统一下单
        String resultXml=HttpUtil.httpPostData("https://api.mch.weixin.qq.com/pay/unifiedorder",xmlObject , 3000);
        JSONObject resultJson=null;
        try {
        	resultXml =new String(resultXml.getBytes("ISO8859_1"),"utf-8").trim();
        	LogUtil.info("requset unifedorder resultXml="+resultXml);
        	String tempJson=XmlUtil.xml2Json(resultXml);
        	resultJson=JSONObject.parseObject(tempJson);
		} catch (Exception e){
			jsonObject.put("msg", "统一订单生成发生异常！");
			return jsonObject.toJSONString();
		}
        if(null==resultJson){
        	jsonObject.put("msg", "统一订单生成发生异常！");
			return jsonObject.toJSONString();
        }
        if(!"SUCCESS".equals(resultJson.get("return_code"))){
        	jsonObject.put("msg", "统一订单生成失败，错误信息："+resultJson.get("return_msg"));
			return jsonObject.toJSONString();
        }
        jsonObject.remove("code");
        if("JSAPI".equals(userAgent)){
        	jsonObject.put("appId", urlParamMap.get("appid"));
//            jsonObject.put("partnerid", urlParamMap.get("mch_id"));
//            jsonObject.put("prepayid", resultJson.get("prepay_id"));
            jsonObject.put("nonceStr", urlParamMap.get("nonce_str"));
            jsonObject.put("timeStamp", String.valueOf(System.currentTimeMillis()/1000));
            jsonObject.put("package", "prepay_id="+resultJson.get("prepay_id"));
            jsonObject.put("signType", "MD5");
        }else if("Native".equals(userAgent)){
        	jsonObject.put("code_url", resultJson.getString("code_url"));
        	jsonObject.put("appid", urlParamMap.get("appid"));
            jsonObject.put("partnerid", urlParamMap.get("mch_id"));
            jsonObject.put("prepayid", resultJson.get("prepay_id"));
            jsonObject.put("noncestr", urlParamMap.get("nonce_str"));
            jsonObject.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        }else{
        	jsonObject.put("appid", urlParamMap.get("appid"));
            jsonObject.put("partnerid", urlParamMap.get("mch_id"));
            jsonObject.put("prepayid", resultJson.get("prepay_id"));
            jsonObject.put("noncestr", urlParamMap.get("nonce_str"));
            jsonObject.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
            if("APP".equals(resultJson.get("trade_type"))){
                jsonObject.put("package", "Sign=WXPay");
            }
        }
        
        Map<String,String> map=JSONObject.toJavaObject(jsonObject, Map.class);
        String sign=weChatAppSignUtil.sign(map,appKey);
        if("JSAPI".equals(userAgent)){
        	jsonObject.put("paySign", sign);
        }else{
        	jsonObject.put("sign", sign);
        }
        
//        jsonObject.put("code", 0);
//        jsonObject.put("trade_type", resultJson.get("trade_type"));
//        jsonObject.put("code_url", resultJson.get("code_url"));
        jsonObject.put("out_trade_no",out_trade_no);
        jsonObject.put("code","0");
        return jsonObject.toJSONString();
    }
	
}
