package com.meiqi.openservice.controller.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.util.XmlUtil;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.action.pay.PayService;
import com.meiqi.openservice.action.pay.pc.alipay.config.AliPayConfig;
import com.meiqi.openservice.action.pay.wechat.WeChatPayService;
import com.meiqi.openservice.action.pay.wechat.app.WeChatAppPayConfig;
import com.meiqi.openservice.action.pay.wechat.app.WeChatAppSignUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.config.SysConfig;


/**
 * 微信支付接口
 * @author duanran
 *
 */
@RequestMapping(value = "/pay")
@Controller
public class WeChatPayController {
	
	@Autowired
	private PayService payService;
	@Autowired
	private WeChatAppSignUtil weChatAppSignUtil;
	@Autowired
	private WeChatPayService weChatPayService;
	@Autowired
	private SmsAction smsAction;
	/**
	 * 获取统一下单支付信息
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/yjg/getWeChatPay")
    @ResponseBody
	public String getWeChatPay(HttpServletRequest request, HttpServletResponse response){
		String orderSn=request.getParameter("orderSn");
		if(null==orderSn){
			return "{\"code\":\"1\",\"msg\":\"统一下单失败，缺少订单编号\"}";
		}
		String orderType=request.getParameter("orderType");
		String openID=request.getParameter("openID");
		if(null==openID){
			return "{\"code\":\"1\",\"msg\":\"统一下单失败，缺少openid\"}";
		}
		String ip=getIp(request);
		return weChatPayService.yjgAppToPay(orderSn, ip, "JSAPI", orderType,openID,null);
		
	}
	
	/**
	 * 安卓支付异步通知接口
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/yjg/wechat/app/android/notify")
    @ResponseBody
	public String yjgAndroidAppNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgappNotify(request, response);
	}
	
	
	//iphone普通版异步接口
	@RequestMapping("/yjg/wechat/app/iphone/notify")
    @ResponseBody
	public String yjgIphoneAppNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgappNotify(request, response);
	}
	
	//iphone企业版支付接口
	@RequestMapping("/yjg/wechat/app/iphoneEN/notify")
    @ResponseBody
	public String yjgIphoneENAppNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
			return yjgappNotify(request, response);
	}
	
	//APP支付通知接口
	@RequestMapping("/mdb/wechat/app/notify")
    @ResponseBody
	public String mdbappNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgAppNotify(request, response,WeChatAppPayConfig.MDB_APP_APP_ID,WeChatAppPayConfig.MDB_APP_MCH_ID,WeChatAppPayConfig.MDB_APP_APP_KEY);
	}
	
	
	//APP支付通知接口
	@RequestMapping("/yjg/wechat/app/notify")
    @ResponseBody
	public String yjgappNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgAppNotify(request, response,WeChatAppPayConfig.APP_APP_ID,WeChatAppPayConfig.APP_MCH_ID,WeChatAppPayConfig.APP_APP_KEY);
	}
	
	//js支付通知接口
	@RequestMapping("/yjg/wechat/js/notify")
    @ResponseBody
	public String yjgJSNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgAppNotify(request, response,WeChatAppPayConfig.JS_APP_ID,WeChatAppPayConfig.JS_MCH_ID,WeChatAppPayConfig.JS_APP_KEY);
	}
	
	//native支付通知接口
	@RequestMapping("/yjg/wechat/native/notify")
    @ResponseBody
	public String yjgNATIVENotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
		return yjgAppNotify(request, response,WeChatAppPayConfig.NATIVE_APP_ID,WeChatAppPayConfig.NATIVE_MCH_ID,WeChatAppPayConfig.NATIVE_APP_KEY);
	}
	
	
	private String yjgAppNotify(HttpServletRequest request, HttpServletResponse response,String appID,String mchID,String appKey) throws IOException{
		//接受参数
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        String content=sb.toString();
		LogUtil.error("wechat pay notify the param="+content);
		//声明一个json对象，存放通知返回结果
		JSONObject resultJsonObject=new JSONObject();
		//判断是否收到参数
		if(null==content||"".equals(content.trim())){
			LogUtil.error("wechat pay notify noParam the param="+content);
			//没有收到微信的支付参数，返回fail失败信息
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "未接收到支付参数！");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		LogUtil.info("=======wechat pay response params======:" + content);
		JSONObject payJson=null;
		//将xml转换为json对象
		try {
			payJson=JSONObject.parseObject(XmlUtil.xml2Json(content));
		} catch (Exception e) {
			LogUtil.error("wechat pay notify xml2jsonString"+e.getMessage());
		}
		//如果转换出错，那么该json对象为null
		if(null==payJson){
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "接收到的xml格式有误！");
			LogUtil.error("wechat pay notify="+resultJsonObject.toJSONString());
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//从支付参数中取出通信标识。看是否为成功
		if(!"SUCCESS".equals(payJson.get("return_code"))){
			LogUtil.error("wechat pay notify return_code="+payJson.get("return_code"));
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "return_code为FAIL");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//获取支付的状态
		String result_code=payJson.getString("result_code");
		if(!"SUCCESS".equals(result_code)){
			LogUtil.error("wechat pay notify result_code="+result_code);
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "支付result_code失败！");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//判断返回的支付应用是否为自己的
		String appid=payJson.getString("appid");
		if(!appID.equals(appid)){
			LogUtil.error("wechat pay notify appid="+appid);
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "支付appid错误，不为我方appid");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//判断支付的商户号是否为我方自己的
		String mch_id=payJson.getString("mch_id");
		if(!mchID.equals(mch_id)){
			LogUtil.error("wechat pay notify mch_id="+mch_id);
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "支付mch_id错误，不为我方mch_id");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//判断支付方式是否正确
//		String trade_type=payJson.getString("trade_type");
//		if(!WeChatAppPayConfig.TRADE_TYPE.equals(trade_type)){
//			LogUtil.error("wechat pay notify trade_type="+trade_type);
//			resultJsonObject.put("return_code", "FAIL");
//			resultJsonObject.put("return_msg", "支付trade_type错误，不为我方指定的trade_type");
//			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
//		}
		//获取签名
		String sign=payJson.getString("sign");
		//判断是否存在签名
		if("".equals(sign)){
			LogUtil.error("wechat pay notify no sign=");
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "支付通知不存在sign!");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		payJson.put("appKey", appKey);
		//在自己系统中，生成签名
		String gSign=weChatAppSignUtil.generateSignCheck(payJson);
		//签名不同
		if(!sign.equals(gSign)){
			LogUtil.error("wechat pay notify sign not equal wechat sign="+sign+" me sign="+gSign);
			resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "验证签名失败!");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
		}
		//获取支付的现金金额
		String cash_fee=payJson.getString("cash_fee");
		//获取支付的商户订单号
		String out_trade_no=payJson.getString("out_trade_no");
		String ourOrderSn="";//自己系统内部的订单编号
		String orderType="2";
        int index = out_trade_no.indexOf(AliPayConfig.up);
        if (index != -1) {
        	String[] out_trade_nos=out_trade_no.split(AliPayConfig.up);
        	ourOrderSn=out_trade_nos[0];
        	if(3==out_trade_nos.length){
        		orderType=out_trade_nos[2];
        	}
        }else{
            ourOrderSn=out_trade_no;
        }

		//获取支付的微信支付号
		String transaction_id=payJson.getString("transaction_id");
		//获取支付微信openid
		String openId=payJson.getString("openid");
		LogUtil.info("wechat notify pay: cash_fee="+cash_fee+",out_trade_no="+ourOrderSn+",transaction_id="+transaction_id);
        if("".equals(cash_fee)){
        	resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "缺少cash_fee支付金额!");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
        }
        if("".equals(ourOrderSn)){
        	resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "缺少out_trade_no商户订单号!");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
        }
        if("".equals(transaction_id)){
        	resultJsonObject.put("return_code", "FAIL");
			resultJsonObject.put("return_msg", "缺少transaction_id微信支付订单号!");
			return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
        }
        resultJsonObject = weChatPayService.paySuccessDoService(cash_fee, ourOrderSn, transaction_id,openId,mchID,orderType,out_trade_no);
        if("SUCCESS".equals(resultJsonObject.getString("return_code"))){
        	new OrderConfirmThread(request, response, resultJsonObject).start();
        }
       
		return XmlUtil.jsonToXml(resultJsonObject.toJSONString(), "xml",false);
    }
	
	/**需求 #12653
     * 订单状态从未确认到确认状态发送短信
     * @author Administrator
     *
     */
    class OrderConfirmThread extends Thread {
        private Map<String,Object> params;
        private HttpServletRequest request;
        private HttpServletResponse response;
        public OrderConfirmThread(HttpServletRequest request,HttpServletResponse response, Map<String,Object> params) {
            this.params = params;
            this.request = request;
            this.response = response;
        }
        public void run() {
            String previousOrderStatus=params.get("previousOrderStatus")==null?"":params.get("previousOrderStatus").toString();
            String order_status=params.get("order_status")==null?"":params.get("order_status").toString();
            if(StringUtils.isNotEmpty(previousOrderStatus) && StringUtils.isNotEmpty(order_status) && Constants.ORDER_STATUS.UN_CONFIRM == Integer.parseInt(previousOrderStatus) && Constants.ORDER_STATUS.CONFIRMED == Integer.parseInt(order_status)){
                //如果之前状态是未确认，现在的订单状态是已确认，那么发送短信
                RepInfo  repInfo = new RepInfo();
                repInfo.setAction("smsAction");
                repInfo.setMethod("send");
                String templateId=SysConfig.getValue("sms_order_unconfirm_to_confirm_templateId");
                String smsParam = "{\"serviceName\":\"SMS_Send\",\"param\":{\"auth_key\":\"5961A30CADB586921AE2364D90A69DFB\",\"operation\":"+
                                  "\"2\",\"templateId\":\""+templateId+"\",\"webSite\":\"0\",\"smsType\":\"33\",\"param\":{\"order_sn\":\""+params.get("order_sn")+"\"}}}";
                repInfo.setParam(smsParam);
                smsAction.send(request, response, repInfo);
            }
        }
    }
	
//	/**
//	 * 生成app微信预支付交易单
//	 * @param request
//	 * @param response
//	 * @return
//	 * @throws IOException
//	 */
//	@RequestMapping("/yjg/wechat/app/toPay")
//    @ResponseBody
//    public String yjgAppToPay(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        Map<String, ?> paramMap=request.getParameterMap();
//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("code", "1");
//        if(!paramMap.containsKey("orderSn")){
//        	jsonObject.put("msg", "缺少订单参数！");
//        	return jsonObject.toJSONString();
//        }
//        //获取要支付的订单号
//        String[] orderSns=(String[]) paramMap.get("orderSn");
//        if(0==orderSns.length){
//        	jsonObject.put("msg", "缺少订单参数！");
//        	return jsonObject.toJSONString();
//        }
//        String reqOrderSn=orderSns[0].trim();
//        return weChatPayService.yjgAppToPay(reqOrderSn);
//    }
	
	private String getIp(HttpServletRequest request) {
        String ip = request.getHeader(ContentUtils.X_REAL_IP);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader(ContentUtils.X_FORWARDED_FOR);
        if (!StringUtils.isBlank(ip) && !ContentUtils.UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(ContentUtils.COMMA);
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
	
}
