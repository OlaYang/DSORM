package com.meiqi.app.controller;

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

import com.meiqi.app.common.config.Constants;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.HttpUtil;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pay.alipay.AlipayNotify;
import com.meiqi.app.pay.billpay.CerEncode;
import com.meiqi.app.pay.upmp.UpmpConfig;
import com.meiqi.app.pay.upmp.UpmpService;
import com.meiqi.app.service.PayService;
import com.meiqi.openservice.commons.util.Arith;
import com.unionpay.acp.sdk.SDKUtil;

@RequestMapping(value = ContentUtils.VERSION + "/pay")
@Controller
public class PayController {

    private static final Log LOG =  LogFactory.getLog("pay");

    private static LRUMap<String, String> paying = new LRUMap<String, String>(10000);     // 支付通知记录

    @Autowired
    private PayService                    payService;



    /*
     * 支付宝接收异步通知
     */
    @RequestMapping("/alipay/backnotify")
    @ResponseBody
    public void getData(HttpServletRequest request, HttpServletResponse response) throws IOException {
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


    /*
     * 银联接收异步通知
     */
    @RequestMapping("/upmp/backnotify")
    @ResponseBody
    public void upmpBackNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取POST过来反馈信息
        // 获取请求参数中所有的信息
        Map<String, String> results = HttpUtil.packParamsFromRequest(request, "UTF-8");
        boolean verifyResult=SDKUtil.validate(results, "UTF-8");
        LOG.info("upmpBackNotify the request params: " + results);
        LOG.info("upmp verify:" + verifyResult);
        if (!verifyResult) {
            LOG.error("upmpBackNotify verifySignature faild!");
            return;
        }

        String trade_status = null, out_trade_no = null, trade_no = null, total_fee = null;
        String merId="";
        LOG.info("=======upmp response params======:" + results);
        try {
            // 交易状态
            trade_status = request.getParameter("respCode");
            // 商户订单号
            out_trade_no = request.getParameter("orderId");
            // 交易号
            trade_no = request.getParameter("queryId");
            // 交易金额
            total_fee = request.getParameter("txnAmt");//单位分
            double amount=Arith.div(Double.valueOf(total_fee),100);//转换成元
            //商户号
            merId = request.getParameter("merId")==null?"":request.getParameter("merId");
            
            LOG.info("upmpBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"+ trade_status + ",total_fee:" + total_fee);
            
            if (!"00".equals(trade_status)) {
                LOG.error("upmpBackNotify the request params is invalide!");
                response.getWriter().println("fail");
                return;
            }
            String key = Constants.PAY_TYPE.UPMPPAY + "_" + trade_no + "_" + trade_status;
            if (paying.get(key) != null) {
                LOG.warn("upmpBackNotify, receive repeated message");
                return;
            }
            paying.put(key, trade_no);
            // 做业务
            try {
                Map<String,Object> result = payService.paySuccess(out_trade_no, trade_no, amount, trade_status,Constants.PAY_TYPE.UPMPPAY,merId,"","","");
                String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
                if ("success".equals(payResult)) {
                    response.getWriter().println("success");
                } else {
                    response.getWriter().println("fail");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                paying.remove(key);
            }
        } catch (Exception e) {
            LOG.error("upmpBackNotify error:" + e);
            response.getWriter().println("fail");
        }
    }



    /*
     * 快钱接收异步通知
     */
    @RequestMapping("/billpay/backnotify")
    @ResponseBody
    public void billPayBackNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取快钱POST过来反馈信息
        Map<String, String> results = HttpUtil.packParamsFromRequest(request, "gbk");
        LOG.info("billPayBackNotify the request params: " + results);
        // 获取快钱发送的通知参数
        String signature = results.get("signature");
        StringBuffer sb = new StringBuffer();
        sb.append(results.get("processFlag")).append(results.get("txnType")).append(results.get("orgTxnType"));
        sb.append(results.get("amt")).append(results.get("externalTraceNo")).append(results.get("orgExternalTraceNo"))
                .append(results.get("terminalOperId"));
        sb.append(results.get("authCode")).append(results.get("RRN")).append(results.get("txnTime"))
                .append(results.get("shortPAN")).append(results.get("responseCode"));
        sb.append(results.get("cardType")).append(results.get("issuerId"));
        LOG.warn("sb:" + sb.toString());
        LOG.info("billPayBackNotify signature:" + signature);
        boolean verifyResult = CerEncode.enCodeByCer(sb.toString(), signature);
        LOG.info("billPayBackNotify verifyResult:" + verifyResult);
        if (!verifyResult) {
            LOG.error("billPayBackNotify verifySignature faild!");
            return;
        }

        String trade_status = null, out_trade_no = null, total_fee = null, trade_no = null;
        String merchantId="";
        try {
            // 交易状态
            trade_status = new String(request.getParameter("processFlag").getBytes("ISO-8859-1"), "gbk");
            // 商户订单号
            out_trade_no = new String(request.getParameter("externalTraceNo").getBytes("ISO-8859-1"), "gbk");
            // 交易号
            trade_no = new String(request.getParameter("RRN").getBytes("ISO-8859-1"), "gbk");
            // 交易金额
            total_fee = new String(request.getParameter("amt").getBytes("ISO-8859-1"), "gbk");
            //商户号
            merchantId=request.getParameter("merchantId")==null?"":request.getParameter("merchantId");
            
            LOG.info("billPayBackNotify, out_trade_no:" + out_trade_no + ",trade_no:" + trade_no + ",trade_status:"
                    + trade_status + ",total_fee:" + total_fee);

            if (!trade_status.equals("0")) {
                response.getWriter().println("fail");
                return;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            response.getWriter().println("fail");
            return;
        }

        String key = Constants.PAY_TYPE.BILLPAY + "_" + trade_no + "_" + trade_status;
        if (paying.get(key) != null) {
            LOG.warn("billPayBackNotify, receive repeated message");
            return;
        }
        paying.put(key, trade_no);
        // 做业务
        Map<String,Object> result = null;
        try {
            result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee), trade_status,Constants.PAY_TYPE.BILLPAY,merchantId,"","","");
        } catch (NumberFormatException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            paying.remove(key);
        }

        String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
        if ("success".equals(payResult)) {
            // 成功接收到快钱返回的参数，验签成功后需返回输出"0",给快钱个响应
            response.getWriter().println("0");
        } else {
            response.getWriter().println("fail");
        }
    }

}
