package com.meiqi.openservice.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.HttpUtil;
import com.meiqi.app.service.PayService;
import com.meiqi.openservice.action.PayGiveRedPacketAction;
import com.meiqi.openservice.action.SmsAction;
import com.meiqi.openservice.action.pay.pc.alipay.util.AlipayNotify;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.Arith;
import com.meiqi.openservice.commons.util.StringUtils;
import com.unionpay.acp.sdk.SDKUtil;

@RequestMapping(value = "/pay/pc")
@Controller
public class PayPcController {

	private static final Log LOGSMS =  LogFactory.getLog("sms");
	
    private static final Log LOG =  LogFactory.getLog("pay");

    private static LRUMap<String, String> paying = new LRUMap<String, String>(10000);       // 支付通知记录

    @Autowired
    private PayService                    payService;//和美居（乐家购），因为APP之前已经有了实现，现在直接拿来用
    
    @Autowired
    private PayGiveRedPacketAction  payGiveRedPacketAction;
    
    @Autowired
    private SmsAction smsAction;

    /**
     * 支付测试代码
    * @Title: main 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param args  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public static void main(String[] args) {
        String str="buyer_id=2088612740732046, trade_no=2015091900001000040062967225, use_coupon=N, notify_time=2015-09-19 14:03:38, subject=订单编号：20150919010945579, sign_type=RSA, is_total_fee_adjust=N, notify_type=trade_status_sync, out_trade_no=20150919010945579, gmt_payment=2015-09-19 13:49:47, trade_status=TRADE_SUCCESS, sign=XkHPqw+pLgsJkcuPJmgpIYKeEsHo0JELgNf+OtUmwENVpLjELHndzAsqu92wpF4uzzD/uc1T7Uv18PdrsTsaE4QnWWTolcLcgbnLRtr4XDXCNTPjw3p5r9AGEMTip79DvNEvS7zAlcpNx9ThAhvIqtgfHshVZQ3mOjqVOiEC8TA=, buyer_email=duanran828@qq.com, gmt_create=2015-09-19 13:49:47, price=0.01, total_fee=0.01, quantity=1, seller_id=2088911500710372, notify_id=c0e5d457e3edc07fd90bb598a779799128, seller_email=707428854@qq.com, payment_type=1";
        Map<String, String> params = new HashMap<String, String>();
        String[] array=str.split(",");
        for(String str1:array){
            params.put(str1.split("=")[0].trim(), str1.split("=")[1].trim());
        }
        params.put("sign","XkHPqw+pLgsJkcuPJmgpIYKeEsHo0JELgNf+OtUmwENVpLjELHndzAsqu92wpF4uzzD/uc1T7Uv18PdrsTsaE4QnWWTolcLcgbnLRtr4XDXCNTPjw3p5r9AGEMTip79DvNEvS7zAlcpNx9ThAhvIqtgfHshVZQ3mOjqVOiEC8TA=");
        boolean verifyResult = AlipayNotify.verify(params);
        System.out.println(verifyResult);
    }
    
    
    @RequestMapping("/alipay/backnotify/yjg")
    @ResponseBody
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<?, ?> requestParams = request.getParameterMap();
        for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        LOG.info("=======alipay response params======:" + params);
        String trade_status = "", out_trade_no = null, trade_no = null, total_fee = null;
        String paidAccount="";
        String getherAccount="";
        try {
     // 交易状态
        trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
     // 商户订单号
        out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
     // 支付宝交易号
        trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 交易金额
        total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");
        //收款账户
        getherAccount=request.getParameter("seller_email")==null?"":request.getParameter("seller_email");
        //付款账户
        paidAccount=request.getParameter("buyer_email")==null?"":request.getParameter("buyer_email");
        
        } catch (UnsupportedEncodingException e) {
            LOG.error("alipayBackNotify error:" + e);
            response.getWriter().println("fail");
        }
        LOG.info("alipayBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                + trade_status + ",total_fee:" + total_fee);
        boolean verifyResult = AlipayNotify.verify(params);
        LOG.info("alipay AlipayNotify.verify=" + verifyResult);
        
        //验证支付宝返回信息合法成功
        if (verifyResult) {
            // 做业务
            String key = Constants.PAY_TYPE.ALIPAY + "_" + trade_no + "_" + trade_status;
            try {
                if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                    LOG.info("alipay to pay ordersn=" + out_trade_no);
                    LOG.info("alipay cache is exist? paying=" + paying.get(key));
                    if (paying.get(key) == null) {
                        paying.put(key, trade_no);
                        Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee),
                                trade_status,Constants.PAY_TYPE.ALIPAY,getherAccount,paidAccount,"","");
                        LOG.info("alipay to pay  result=" + result);
                        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                        if ("success".equals(payResult)) {
                            new PaySendMsgThread(request, response, out_trade_no).start();
                            new OrderConfirmThread(request, response, result).start();
                            response.getWriter().println("success");
                        } else {
                            response.getWriter().println("fail");
                        }
                    }
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } finally {
                paying.remove(key);
            }
        } else {
            response.getWriter().println("fail");
        }
    }
    
    
    @RequestMapping("/alipay/backnotify/app")
    @ResponseBody
    public void alipayNotifyForApp(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<?, ?> requestParams = request.getParameterMap();
        for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        LOG.info("=======alipay response params======:" + params);
        String trade_status = "", out_trade_no = null, trade_no = null, total_fee = null;
        String paidAccount="";
        String getherAccount="";
        try {
     // 交易状态
        trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
     // 商户订单号
        out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
     // 支付宝交易号
        trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 交易金额
        total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");
        //收款账户
        getherAccount=request.getParameter("seller_email")==null?"":request.getParameter("seller_email");
        //付款账户
        paidAccount=request.getParameter("buyer_email")==null?"":request.getParameter("buyer_email");
        
        } catch (UnsupportedEncodingException e) {
            LOG.error("alipayBackNotify error:" + e);
            response.getWriter().println("fail");
        }
        LOG.info("alipayBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                + trade_status + ",total_fee:" + total_fee);
        boolean verifyResult = AlipayNotify.verifyApp(params);
        LOG.info("alipay AlipayNotify.verify=" + verifyResult);
        
        //验证支付宝返回信息合法成功
        if (verifyResult) {
            // 做业务
            String key = Constants.PAY_TYPE.ALIPAY + "_" + trade_no + "_" + trade_status;
            try {
                if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                    LOG.info("alipay to pay ordersn=" + out_trade_no);
                    LOG.info("alipay cache is exist? paying=" + paying.get(key));
                    if (paying.get(key) == null) {
                        paying.put(key, trade_no);
                        Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee),
                                trade_status,Constants.PAY_TYPE.ALIPAY,getherAccount,paidAccount,"","");
                        LOG.info("alipay to pay  result=" + result);
                        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                        if ("success".equals(payResult)) {
                            response.getWriter().println("success");
                        } else {
                            response.getWriter().println("fail");
                        }
                    }
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } finally {
                paying.remove(key);
            }
        } else {
            response.getWriter().println("fail");
        }
        
    }
    
    @RequestMapping("/alipay/backnotify/yjgNew")
    @ResponseBody
    public void alipayNotifyNew(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<?, ?> requestParams = request.getParameterMap();
        for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        LOG.info("=======alipay response params======:" + params);
        String trade_status = "", out_trade_no = null, trade_no = null, total_fee = null;
        String paidAccount="";
        String getherAccount="";
        try {
        // 交易状态
        trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
        // 商户订单号
        out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 支付宝交易号
        trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 交易金额
        total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");
        //收款账户
        getherAccount=request.getParameter("seller_email")==null?"":request.getParameter("seller_email");
        //付款账户
        paidAccount=request.getParameter("buyer_email")==null?"":request.getParameter("buyer_email");
        } catch (UnsupportedEncodingException e) {
            LOG.error("alipayBackNotify error:" + e);
            response.getWriter().println("fail");
        }
        LOG.info("alipayBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                + trade_status + ",total_fee:" + total_fee);
        boolean verifyResult = AlipayNotify.verifyPC(params);
        LOG.info("alipay AlipayNotify.verify=" + verifyResult);
        
        //验证支付宝返回信息合法成功
        if (verifyResult) {
            // 做业务
            String key = Constants.PAY_TYPE.ALIPAY + "_" + trade_no + "_" + trade_status;
            try {
                if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                    LOG.info("alipay to pay ordersn=" + out_trade_no);
                    LOG.info("alipay cache is exist? paying=" + paying.get(key));
                    if (paying.get(key) == null) {
                        paying.put(key, trade_no);
                        Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee),
                                trade_status,Constants.PAY_TYPE.ALIPAY,getherAccount,paidAccount,"","");
                        LOG.info("alipay to pay  result=" + result);
                        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                        if ("success".equals(payResult)) {
                            new PaySendMsgThread(request, response, out_trade_no).start();
                            new OrderConfirmThread(request, response, result).start();
                            response.getWriter().println("success");
                        } else {
                            response.getWriter().println("fail");
                        }
                    }
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } finally {
                paying.remove(key);
            }
        } else {
            response.getWriter().println("fail");
        }
        
    }
    
    @RequestMapping("/alipay/backnotify/milly")
    @ResponseBody
    public void alipayNotifyForMilly(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<?, ?> requestParams = request.getParameterMap();
        for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        LOG.info("=======alipay response params======:" + params);
        String trade_status = "", out_trade_no = null, trade_no = null, total_fee = null;
        String paidAccount="";
        String getherAccount="";
        try {
        // 交易状态
        trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
        // 商户订单号
        out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 支付宝交易号
        trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 交易金额
        total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");
        //收款账户
        getherAccount=request.getParameter("seller_email")==null?"":request.getParameter("seller_email");
        //付款账户
        paidAccount=request.getParameter("buyer_email")==null?"":request.getParameter("buyer_email");
        } catch (UnsupportedEncodingException e) {
            LOG.error("alipayBackNotify error:" + e);
            response.getWriter().println("fail");
        }
        LOG.info("alipayBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                + trade_status + ",total_fee:" + total_fee);
        boolean verifyResult = AlipayNotify.verifyPCForMilly(params);
        LOG.info("alipay AlipayNotify.verify=" + verifyResult);
        
        //验证支付宝返回信息合法成功
        if (verifyResult) {
            // 做业务
            String key = Constants.PAY_TYPE.ALIPAY + "_" + trade_no + "_" + trade_status;
            try {
                if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                    LOG.info("alipay to pay ordersn=" + out_trade_no);
                    LOG.info("alipay cache is exist? paying=" + paying.get(key));
                    if (paying.get(key) == null) {
                        paying.put(key, trade_no);
                        Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee),
                                trade_status,Constants.PAY_TYPE.ALIPAY,getherAccount,paidAccount,"","");
                        LOG.info("alipay to pay  result=" + result);
                        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                        if ("success".equals(payResult)) {
                            new PaySendMsgThread(request, response, out_trade_no).start();
                            new OrderConfirmThread(request, response, result).start();
                            response.getWriter().println("success");
                        } else {
                            response.getWriter().println("fail");
                        }
                    }
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } finally {
                paying.remove(key);
            }
        } else {
            response.getWriter().println("fail");
        }
        
    }
    
    
    /*
     * 银联接收异步通知
     */
    @RequestMapping("/upmp/backnotify/hmj")
    @ResponseBody
    public void upmpBackNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取POST过来反馈信息
        Map<String, String> results = HttpUtil.packParamsFromRequest(request, "UTF-8");
        boolean verifyResult = SDKUtil.validate(results, "UTF-8");
        if (verifyResult) {
            // 交易状态
            String trade_status = new String(request.getParameter("respCode").getBytes("ISO-8859-1"), "UTF-8");
            // 商户订单号
            String out_trade_no = new String(request.getParameter("orderId").getBytes("ISO-8859-1"), "UTF-8");
            // 交易号
            String trade_no = new String(request.getParameter("queryId").getBytes("ISO-8859-1"), "UTF-8");
            // 交易金额(分)
            String txnAmt = new String(request.getParameter("txnAmt").getBytes("ISO-8859-1"), "UTF-8");
            //商户号
            String merId=new String(request.getParameter("merId").getBytes("ISO-8859-1"), "UTF-8");
            
            
            double amount=Arith.div(Double.valueOf(txnAmt),100);
            LOG.info("UPMPBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                    + trade_status + ",amount:" + amount);
            LOG.info("UPMP UPMPNotify.verify=" + verifyResult);
            // 做业务
            String key = Constants.PAY_TYPE.UPMPPAY + "_" + trade_no + "_" + trade_status;
            try {
                if ("00".equals(trade_status)) {
                    LOG.info("UPMP to pay ordersn=" + out_trade_no);
                    LOG.info("UPMP cache is exist? paying=" + paying.get(key));
                    if (paying.get(key) == null) {
                        paying.put(key, trade_no);
                        Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, amount,trade_status,Constants.PAY_TYPE.UPMPPAY,merId,"","","");
                        LOG.info("UPMP to pay  result=" + result);
                        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                        if ("success".equals(payResult)) {
                            new PaySendMsgThread(request, response, out_trade_no).start();
                            new OrderConfirmThread(request, response, result).start();
                            response.getWriter().println("success");
                        } else {
                            response.getWriter().println("fail");
                        }
                    }
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                response.getWriter().println("fail");
            } finally {
                paying.remove(key);
            }
        } 
    }
    
    /**功能#12454 前台付款成功后，发送短信（调用买赠红包接口）
     * 支付金额=订单金额，异步发送短信通知
     * @author Administrator
     *
     */
    class PaySendMsgThread extends Thread {
        private String out_trade_no;
        private HttpServletRequest request;
        private HttpServletResponse response;
        public PaySendMsgThread(HttpServletRequest request,HttpServletResponse response,String out_trade_no) {
            super();    
            this.out_trade_no = out_trade_no;
            this.request = request;
            this.response = response;
        }
        public void run() {
            RepInfo  repInfo = new RepInfo();
            repInfo.setAction("payGiveRedPacketAction");
            repInfo.setMethod("payGiveRedPacket");
            String smsParam = "{\"order_sn\":\""+out_trade_no+"\"}";
            repInfo.setParam(smsParam);
            payGiveRedPacketAction.payGiveRedPacket(request, response, repInfo);
        }
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
				RepInfo repInfo1 = new RepInfo();
				repInfo1.setAction("smsAction");
				repInfo1.setMethod("sendMsg");
				String smsParam = "{\"website\":\"0\",\"page_link\":\"\",\"order_sn\":\"" + params.get("order_sn")
						+ "\",\"template_name_en\":\"dddxtzb\",\"smsType\":\"33\"}";
				repInfo1.setParam(smsParam);
				String sendMsg = smsAction.sendMsg(request, response, repInfo1);
				JSONObject jsonBaseRespInfo = (JSONObject) JSONObject.parseObject(sendMsg);
				if("1".equals(jsonBaseRespInfo.get("code"))){
					LOGSMS.info("PayGiveRedPacketAction send is fail and smsParam is: "+smsParam);
  			  	}
            }
        }
    }
    
}
