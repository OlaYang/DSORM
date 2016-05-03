/**   
* @Title: RegisterAction.java 
* @Package com.meiqi.openservice.action.register 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月8日 上午11:02:08 
* @version V1.0   
*/
package com.meiqi.openservice.action.pay;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.service.PayService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.ConfigFileUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.pay.pc.alipay.config.AliPayConfig;
import com.meiqi.openservice.action.pay.pc.alipay.util.AlipaySubmit;
import com.meiqi.openservice.action.pay.pc.upmp.UpmpConfig;
import com.meiqi.openservice.action.pay.pc.upmp.UpmpUtil;
import com.meiqi.openservice.action.pay.wechat.WeChatPayService;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.SysConfig;
import com.meiqi.openservice.commons.util.Arith;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.StringUtils;
import com.unionpay.acp.sdk.SDKConfig;

/** 
 * @ClassName: PayAction 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月8日 上午11:02:08 
 *  
 */
@Service
public class PayAction extends BaseAction{
	
	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private WeChatPayService weChatPayService;
	
    @Autowired
    private PayService                    payService;
    
    @Autowired
    private com.meiqi.openservice.action.pay.PayService payService2;
    
    @Autowired
    private IMushroomAction mushroomAction;
    
    private static final Log LOG =  LogFactory.getLog("pay");
    
    private static Properties  properties1 = new Properties();
	
	private final static int zhifubao = 1;//支付宝
	private final static int weixin = 2;//微信
	private final static int kuaishua = 3;//快钱
	private final static int upmp=4;//银联
	private final static int kuaiqian_oqs=Constants.PAY_TYPE.KUAIQIAN_OQS;
    
    static{
        Properties properties = ConfigFileUtil.propertiesReader("upmp_acp_sdk.properties");
        SDKConfig.getConfig().loadProperties(properties);
    }
	
    /**
     * 优家购  支付宝wap支付
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    @SuppressWarnings("unchecked")
	public String toPayAlipayWapYjg(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
 
    	Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
       	String ordersn=map.get("ordersn");
       	String amount=map.get("amount");
       	String money_type=map.get("money_type")==null?"":map.get("money_type");
       	
    	Map<String, String> sParaTemp = new HashMap<String, String>();
    	sParaTemp.put("service","alipay.wap.create.direct.pay.by.user");
    	sParaTemp.put("partner",AliPayConfig.PARTNER);
    	sParaTemp.put("_input_charset", AliPayConfig.INPUT_CHARSET);
    	//同步通知地址
    	
    	StringBuffer out_trade_no = new StringBuffer(ordersn);
        out_trade_no.append(AliPayConfig.up).append(System.currentTimeMillis()/1000).append(AliPayConfig.up).append(money_type);
         
    	String return_url=SysConfig.getValue("wap_alipay_return_url");
//    	String return_url="http://192.168.1.205:8080/DSORM/pay/pc/alipay/backnotify/yjg.do";
    	//异步通知地址
    	String notify_url=SysConfig.getValue("wap_alipay_notify_url");
    	sParaTemp.put("notify_url",notify_url);
    	sParaTemp.put("return_url",return_url);
    	sParaTemp.put("out_trade_no",out_trade_no.toString());
    	sParaTemp.put("subject","订单编号："+ordersn);
    	sParaTemp.put("total_fee",amount);
    	sParaTemp.put("seller_id",AliPayConfig.SELLER_EMAIL);
    	sParaTemp.put("payment_type", "1");
    	try {
			String result=AlipaySubmit.buildRequest(sParaTemp,"","");
			response.setContentType("text/html; charset=utf-8"); 
			PrintWriter out = response.getWriter(); 
			out.println(result); 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    /**
     * PC端优家购支付宝支付
    * @Title: toPayAlipayPcHmj 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return String    返回类型 
    * @throws
     */
	@SuppressWarnings("unchecked")
	public String toPayAlipayPc(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		

	    Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
	    Integer webSiteId=Integer.parseInt(map.get("siteId"));
	    String ordersn=map.get("ordersn");
	    String money_type=map.get("money_type")==null?"":map.get("money_type");
	    
	    Map<String, String> sParaTemp = new HashMap<String, String>();
        //支付类型
        String payment_type = "1";

        String return_url = "";//同步通知
        String notify_url = "";//异步通知

        //需http://格式的完整路径，不能加?id=123这类自定义参数
        //订单名称
        String subject = "订单编号：" + ordersn;
        //订单描述
        String body = subject;
        //卖家支付宝帐户
        String seller_email = AliPayConfig.SELLER_EMAIL_NEW;
        //必填

        //商户订单号
        StringBuffer out_trade_no = new StringBuffer(ordersn);
        out_trade_no.append(AliPayConfig.up).append(System.currentTimeMillis()/1000).append(AliPayConfig.up).append(money_type);
        
        //防钓鱼时间戳
        String anti_phishing_key = "";
        //若要使用请调用类文件submit中的query_timestamp函数

        //付款金额
        String currentPayMoney=map.get("amount");//单位元
        
        String exter_invoke_ip = "";
        
        switch (webSiteId) {
        case 0:
             //优家购
             return_url = SysConfig.getValue("pc_alipay_return_url_yjg");//同步通知
             notify_url = SysConfig.getValue("pc_alipay_notify_url_yjg");
             sParaTemp.put("partner", AliPayConfig.PARTNER_NEW);
             sParaTemp.put("seller_email", seller_email);
             break;
        case 1:
             //韩丽
             return_url = SysConfig.getValue("pc_alipay_return_url_hanli");//同步通知
             notify_url = SysConfig.getValue("pc_alipay_notify_url_hanli");
             sParaTemp.put("partner", AliPayConfig.PARTNER_NEW);
             sParaTemp.put("seller_email", seller_email);
             break;
        case 2:
             //好来客
             return_url = SysConfig.getValue("pc_alipay_return_url_haolk");//同步通知
             notify_url = SysConfig.getValue("pc_alipay_notify_url_haolk");
             sParaTemp.put("partner", AliPayConfig.PARTNER_NEW);
             sParaTemp.put("seller_email", seller_email);
             break;
        case 3:
            //梦百合
            return_url = SysConfig.getValue("pc_alipay_return_url_mlily");//同步通知
            notify_url = SysConfig.getValue("pc_alipay_notify_url_mlily");
            sParaTemp.put("partner", AliPayConfig.PARTNER_MILLY);
            sParaTemp.put("seller_email", AliPayConfig.SELLER_EMAIL_MILLY);
            break;
        case 4:
            //海尔
            return_url = SysConfig.getValue("pc_alipay_return_url_haier");//同步通知
            notify_url = SysConfig.getValue("pc_alipay_notify_url_haier");
            sParaTemp.put("partner", AliPayConfig.PARTNER_HAIER);
            sParaTemp.put("seller_email", AliPayConfig.SELLER_EMAIL_HAIER);
            break;
        case 5:
            //顾家
            return_url = SysConfig.getValue("pc_alipay_return_url_gujia");//同步通知
            notify_url = SysConfig.getValue("pc_alipay_notify_url_gujia");
            sParaTemp.put("partner", AliPayConfig.PARTNER_NEW);
            sParaTemp.put("seller_email", seller_email);
            break;
        default:
            break;
        }
        
        //////////////////////////////////////////////////////////////////////////////////
        
        LOG.info("alipay ordersn:"+ordersn+",out_trade_no:"+out_trade_no+",currentPayMoney:"+currentPayMoney);
        //把请求参数打包成数组
        sParaTemp.put("service", "create_direct_pay_by_user");
        sParaTemp.put("_input_charset", AliPayConfig.INPUT_CHARSET);
        sParaTemp.put("payment_type", payment_type);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", return_url);
        sParaTemp.put("out_trade_no", out_trade_no.toString());
        sParaTemp.put("subject", subject);
        sParaTemp.put("total_fee", currentPayMoney);
        sParaTemp.put("body", body);
        sParaTemp.put("anti_phishing_key", anti_phishing_key);
        sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
	    
        String html="";
        try {
             switch (webSiteId) {
             case 0:
                  //优家购
                  html=AlipaySubmit.createHtml(sParaTemp, "get", "confirm");
                  break;
             case 1:
                  //韩丽
                  html=AlipaySubmit.createHtml(sParaTemp, "get", "confirm");
                  break;
             case 2:
                  //好来客
                 html=AlipaySubmit.createHtml(sParaTemp, "get", "confirm");
                  break;
             case 3:
                 //梦百合
                 html=AlipaySubmit.createHtmlForMilly(sParaTemp, "get", "confirm");
                 break;
             case 4:
                 //海尔
                 html=AlipaySubmit.createHtmlForHaier(sParaTemp, "get", "confirm");
                 break;
             case 5:
                 //顾家
                 html=AlipaySubmit.createHtml(sParaTemp, "get", "confirm");
                 break;
             default:
                 break;
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return html;
	}
	
	@SuppressWarnings("unchecked")
    public String toPayUpmpForPcHmj(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
	    Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
	    
        Integer site_id=map.get("site_id")==null?0:Integer.parseInt(map.get("site_id"));
        
	    String return_url = "";//同步通知
        String notify_url = "";//异步通知
        switch (site_id) {
        case 0:
             //优家购
             return_url = SysConfig.getValue("pc_upmp_return_url_yjg");//同步通知
             notify_url = SysConfig.getValue("pc_upmp_notify_url_yjg");
             break;
        case 1:
             //韩丽
             return_url = SysConfig.getValue("pc_upmp_return_url_hanli");//同步通知
             notify_url = SysConfig.getValue("pc_upmp_notify_url_hanli");
             break;
        case 2:
             //好来客
             return_url = SysConfig.getValue("pc_upmp_return_url_haolk");//同步通知
             notify_url = SysConfig.getValue("pc_upmp_notify_url_haolk");
             break;
        case 3:
            //梦百合
            return_url = SysConfig.getValue("pc_upmp_return_url_mlily");//同步通知
            notify_url = SysConfig.getValue("pc_upmp_notify_url_mlily");
            break;
        case 4:
            //海尔
            return_url = SysConfig.getValue("pc_upmp_return_url_haier");//同步通知
            notify_url = SysConfig.getValue("pc_upmp_notify_url_haier");
            break;
        case 5:
            //顾家
            return_url = SysConfig.getValue("pc_upmp_return_url_gujia");//同步通知
            notify_url = SysConfig.getValue("pc_upmp_notify_url_gujia");
            break;
        default:
            break;
        }
        return toPayUpmpForPc(request,response,repInfo,return_url,notify_url);
	}
	
	/**
	 * 
	* @Title: toPayUpmpForPc 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param response
	* @param @param repInfo
	* @param @param reutrnUrl 同步通知地址
	* @param @param notifyUrl 异步通知地址
	* @param @return  参数说明 
	* @return String    返回类型 
	* @throws
	 */
    @SuppressWarnings("unchecked")
	public String toPayUpmpForPc(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo,String reutrnUrl,String notifyUrl){
          
        Map<String,String> map=DataUtil.parse(repInfo.getParam(),Map.class);
        String money_type=map.get("money_type")==null?"":map.get("money_type");
         /** 
          * 组装请求报文 
          */
         Map<String, String> contentData = new HashMap<String, String>(); 
           
         //固定填写 
         contentData.put("version", UpmpConfig.version);//M 
         //默认取值：UTF-8 
         contentData.put("encoding", UpmpConfig.encoding);//M 
         //取值：01（表示采用的是RSA） 
         contentData.put("signMethod",UpmpConfig.SIGN_METHOD);//M 
         //取值：01  
         contentData.put("txnType", UpmpConfig.txnType);//M 
         //01：自助消费，通过地址的方式区分前台消费和后台消费（含无跳转支付）03：分期付款 
         contentData.put("txnSubType", UpmpConfig.txnSubType);//M 
         //000201 业务类型 000201 B2C网关支付 
         contentData.put("bizType", UpmpConfig.bizType);//M 
         //07 
         contentData.put("channelType", UpmpConfig.channelType);//M 
         //前台返回商户结果时使用，前台类交易需上送 
         contentData.put("frontUrl", reutrnUrl);//C 
         //后台返回商户结果时使用，如上送，则发送商户后台交易结果通知 
         contentData.put("backUrl",notifyUrl);//M 
         //0：普通商户直连接入2：平台类商户接入 
         contentData.put("accessType",UpmpConfig.accessType);//M 
         //　 
         contentData.put("merId", UpmpConfig.merId);//M 
         //商户端生成 
         String orderId=map.get("orderId");
         contentData.put("orderId", orderId+UpmpConfig.up+System.currentTimeMillis()+UpmpConfig.up+money_type);//M 
         //商户发送交易时间 
         contentData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//M 
         //交易单位为分 
         String amount=Arith.mulToString(Double.valueOf(map.get("amount")), 100);
         int index=amount.indexOf(".");
         if(index!=-1){
             amount=amount.substring(0,index);//此值换算成分后是没有小数位的
         }
         contentData.put("txnAmt",amount);//M 
         //默认为156  
         contentData.put("currencyCode", UpmpConfig.currencyCode);//M 
        /** 
         * 创建表单 
         */
        String html = UpmpUtil.createHtml(UpmpConfig.UPMP_GATEWAY_NEW, UpmpUtil.signData(contentData)); 

        return html;
    }
    
    public String upmpPayNotifyForPc(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        
        return "";
    }
    
    /**
     * 线下转账付款
    * @Title: paySuccessOffline  
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return Object    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
	public Object payOffline(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
        
        // RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return respInfo;
        }
        
        ResponseInfo respInfo = new ResponseInfo();
        Map<String, String> param = DataUtil.parse(repInfo.getParam(), Map.class);
        String out_trade_no=param.get("out_trade_no")==null?"":param.get("out_trade_no").toString();//订单编号
        if(StringUtils.isEmpty(out_trade_no)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("订单编号out_trade_no不能为空");
            return respInfo;
        }
        
        String trade_no=param.get("trade_no")==null?"":param.get("trade_no").toString();//交易号
        
        String total_fee=param.get("total_fee")==null?"":param.get("total_fee").toString();//支付金额
        if(StringUtils.isEmpty(total_fee)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("支付金额total_fee不能为空");
            return respInfo;
        }
        String pay_type=param.get("pay_type")==null?"":param.get("pay_type").toString();//支付方式
        if(StringUtils.isEmpty(pay_type)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("支付方式pay_type不能为空");
            return respInfo;
        }
        String paid_account=param.get("paid_account")==null?"":param.get("paid_account").toString();//付款账户
        if(StringUtils.isEmpty(paid_account)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("付款账户paid_account不能为空");
            return respInfo;
        }
        String paid_card=param.get("paid_card")==null?"":param.get("paid_card").toString();//付款卡号
        
        String remark=param.get("remark")==null?"":param.get("remark").toString();//备注
        Map<String,Object> result;
        try {
            result = payService.paySuccess(out_trade_no, trade_no, Double.valueOf(total_fee),null,Integer.parseInt(pay_type),"",paid_account,paid_card,remark);
            String payResult=result.get("payResult")==null?"":result.get("payResult").toString();
            if ("success".equals(payResult)) {
                respInfo.setCode(DsResponseCodeData.SUCCESS.code);
                respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
            } else {
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(result.get("errorMsg")==null?DsResponseCodeData.ERROR.description:result.get("errorMsg").toString());
            }
        } catch (Exception e) {
            String msg=""+e;
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription(msg);
            LOG.info("payAction paySuccess error:"+e+",req:"+param);
        }
        return respInfo;
    }
    
    /**
     * 移动端订单支付新接口(将订单信息和支付信息拼接成一个字符串)
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
    @SuppressWarnings("unchecked")
	public String pay(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
    	
    	// RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSON.toJSONString(respInfo);
        }
    	
    	ResponseInfo respInfo = new ResponseInfo();
		String result = "";
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		String pay_type = (String) paramMap.get("pay_type");
		//String platform_type = (String) paramMap.get("platform_type");
		String order_id = (String) paramMap.get("order_id");
		String order_type = (String) paramMap.get("order_type");//order_type表示订单的金额类型
		String pay_money_tmp = (String) paramMap.get("pay_money");
		Double pay_money=0d;
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("IPAD_HSV2_Myorder");
		param.put("order_id", order_id);
		param.put("type", order_type);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		LOG.info("pay IPAD_HSV1_MyorderGoodsInfo param is: "+ param);
		LOG.info("pay IPAD_HSV1_MyorderGoodsInfo result is: "+ resultData);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("pay IPAD_HSV1_MyorderGoodsInfo param is: "+ param);
			LOG.error("pay IPAD_HSV1_MyorderGoodsInfo result is: "+ resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		JSONObject jsonObject = new JSONObject();
        JSONArray rows = new JSONArray();
        JSONObject obj = new JSONObject();
        
		if(mapList != null && mapList.size() > 0){
			String site_id = mapList.get(0).get("site_id");
			String order_source = mapList.get(0).get("order_source");
			String paid = mapList.get(0).get("paid");
			String order_money = mapList.get(0).get("order_money");//订单总金额
			if(StringUtils.isEmpty(pay_money_tmp)){
				/*LOG.error("pay  pay_money is: "+ pay_money);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("付款金额不能为空！");
				return JSONObject.toJSONString(respInfo);*/
				pay_money = Arith.sub(Double.valueOf(order_money), Double.valueOf(paid));
			}else{
				pay_money=Double.valueOf(pay_money_tmp);
			}
			if( Arith.add(Double.valueOf(pay_money), Double.valueOf(paid)) <= Double.valueOf(order_money)){
				String order_sn = mapList.get(0).get("order_sn");
				StringBuffer out_trade_no = new StringBuffer(order_sn);
				out_trade_no.append(AliPayConfig.up).append(System.currentTimeMillis()/1000).append(AliPayConfig.up).append(order_type);
				JSONArray parseArray = JSONArray.parseArray(mapList.get(0).get("goods_info"));
				String goods_name = parseArray.getJSONObject(0).get("goods_name").toString();
				switch (Integer.parseInt(pay_type)) {
				case zhifubao:
					properties1 = ConfigFileUtil.propertiesReader("sysConfig.properties");
		            String alipay_notifyURL = properties1.getProperty("alipay_notifyURL");
					result = "partner=\""+AliPayConfig.PARTNER_NEW+"\"&seller_id=\""+AliPayConfig.SELLER_EMAIL_NEW+"\"&out_trade_no=\""+
							 out_trade_no+"\"&subject=\""+goods_name+"\"&body=\""+goods_name+"\"&total_fee=\""+pay_money+"\"&notify_url=\""+
							 alipay_notifyURL+"\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\""+
							 "&return_url=\"m.alipay.com\"";
					String sign = AlipaySubmit.buildRequestMysignForApp(result);
					result += "&sign=\""+sign+"\"&sign_type=\"RSA\"";
					obj.put("out_trade_no", out_trade_no);
				break;
				case weixin:
					String reqOrderSn = order_sn;
					String ip=getIp(request);
					Map<String, Object> header = repInfo.getHeader();
					String userAgent=(String) header.get("User-Agent");

					result = weChatPayService.yjgAppToPay(reqOrderSn,ip,userAgent,order_type,null,payService2.doubleString2IntStringFee(pay_money.toString()));
					break;
				case kuaishua:
					result = getKuaiQianParams(out_trade_no.toString(),pay_money.toString(),goods_name);
					break;
				case upmp:
	                result = getUpmpParams(out_trade_no.toString(),Double.valueOf(pay_money),goods_name);
	                break; 
				case kuaiqian_oqs:
					List<Action> newActions=new ArrayList<Action>();
					Map<String,Object> paramMap1 = new HashMap<String, Object>();
					Action action = new Action();
					action.setServiceName("test_ecshop_ecs_tmp_paylog");
					Map<String, Object> setMap = new HashMap<String, Object>();
					action.setType("C");
					setMap.put("order_id", order_id);
					setMap.put("order_amount", pay_money.toString());
					setMap.put("pay_type", Integer.parseInt(pay_type));
					setMap.put("order_source", order_source);
					setMap.put("add_time", "$UnixTime");
					setMap.put("site_id", site_id);
					setMap.put("third_order_sn", out_trade_no.toString());
					action.setSet(setMap);
					newActions.add(action);
					
					paramMap1.put("actions", newActions);
					paramMap1.put("transaction",1);
					DsManageReqInfo dsReqInfo = new DsManageReqInfo();
					dsReqInfo.setServiceName("MUSH_Offer");
					dsReqInfo.setParam(paramMap1);
					String str = mushroomAction.offer(dsReqInfo);
					JSONObject job = JSONObject.parseObject(str);
					LOG.info("PayAction pay kuaiqian_oqs set param is: "+JSONObject.toJSONString(dsReqInfo));
					LOG.info("PayAction pay kuaiqian_oqs set result is: "+str);
					if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
						LOG.error("PayAction pay kuaiqian_oqs set param is: "+JSONObject.toJSONString(dsReqInfo));
						LOG.error("PayAction pay kuaiqian_oqs set result is: "+str);
						respInfo.setCode(DsResponseCodeData.ERROR.code);
		                respInfo.setDescription("快钱pos机支付写入订单流水号失败！");
		                return JSON.toJSONString(respInfo);
				    }
					
					result = out_trade_no.toString();
					break;
				default:
					return "";
				}
			}else{
				LOG.error("pay IPAD_HSV1_MyorderGoodsInfo param is: "+ param);
				LOG.error("pay IPAD_HSV1_MyorderGoodsInfo result is: "+ resultData);
				LOG.error("pay IPAD_HSV1_MyorderGoodsInfo paid is: "+ paid+" order_money is: "+order_money+" pay_money is: "+pay_money);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("支付金额多于未付金额，请重新支付！");
				return JSONObject.toJSONString(respInfo);
			}
		}else{
			LOG.error("pay IPAD_HSV1_MyorderGoodsInfo param is: "+ param);
			LOG.error("pay IPAD_HSV1_MyorderGoodsInfo result is: "+ resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("传入的订单ID"+order_id+"查询无数据！");
			return JSON.toJSONString(respInfo);
		}
		obj.put("result", result);
		rows.add(obj);
		jsonObject.put("rows", rows);
		jsonObject.put("code", DsResponseCodeData.SUCCESS.code);
		jsonObject.put("description", DsResponseCodeData.SUCCESS.description);
		return jsonObject.toJSONString();
	}
	
    
    /** 
* @Title: getUpmpParams 
* @Description: TODO(这里用一句话描述这个方法的作用) 
* @param @param out_trade_no 提交给第三方的订单编号
* @param @param order_money
* @param @param goods_name
* @param @return  参数说明 
* @return String    返回类型 
* @throws 
*/
private String getUpmpParams(String out_trade_no, double order_money, String goods_name) {
    
        /**
         * 组装请求报文
         */
        Map<String, String> data = new HashMap<String, String>();
        // 版本号
        data.put("version", "5.0.0");
        // 字符集编码 默认"UTF-8"
        data.put("encoding", "UTF-8");
        // 签名方法 01 RSA
        data.put("signMethod", "01");
        // 交易类型 01-消费
        data.put("txnType", "01");
        // 交易子类型 01:自助消费 02:订购 03:分期付款
        data.put("txnSubType", "01");
        // 业务类型
        data.put("bizType", "000201");
        // 渠道类型，07-PC，08-手机
        data.put("channelType", "08");
        // 前台通知地址 ，控件接入方式无作用
        //data.put("frontUrl", "http://localhost:8080/ACPTest/acp_front_url.do");
        // 后台通知地址
        data.put("backUrl", SysConfig.getValue("app_upmp_notify_url_yjg"));
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        data.put("accessType", "0");
        // 商户号码，请改成自己的商户号
        data.put("merId",com.meiqi.app.pay.upmp.UpmpConfig.MER_ID);
        // 商户订单号，8-40位数字字母
        data.put("orderId", out_trade_no);
        // 订单发送时间，取系统时间
        data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 交易金额，单位分
        String amount=Arith.mulToString(order_money, 100);//转换成分
        int index=amount.indexOf(".");
        if(index!=-1){
          //去掉小数点后面的0
            amount=amount.substring(0, index);
        }
        data.put("txnAmt", amount);
        // 交易币种
        data.put("currencyCode", "156");
        // 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
        // data.put("reqReserved", "透传信息");
        // 订单描述，可不上送，上送时控件中会显示该信息
        // data.put("orderDesc", "订单描述");

        data = UpmpUtil.signData(data);

        // 交易请求url 从配置文件读取
        String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();

        Map<String, String> resmap = UpmpUtil.submitUrl(data, requestAppUrl);
        
        Map<String, String> result=new HashMap<String, String>();
        result.put("tn", resmap.get("tn"));
        result.put("out_trade_no", out_trade_no);

        return JSONObject.toJSONString(result);
}

	/**
	 * 得到传递给快钱的参数
	 * @param orderSn
	 * @param orderPrice
	 * @param productName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getKuaiQianParams(String orderSn, String orderPrice, String productName){
        StringBuffer sb = new StringBuffer();
        sb.append("kuaishua://www.99bill.com/pay");
        sb.append("?urlTye=M003");
        sb.append("&orderId=" + orderSn);
        sb.append("&orderAmt=" + orderPrice);
        try {
			sb.append("&productName=" + URLEncoder.encode(productName, "UTF-8"));
			sb.append("&brushType=" + URLEncoder.encode("5", "UTF-8"));
	        sb.append("&callbackUri=" + URLEncoder.encode("MeiQiKuaiQian:", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.getMessage();
		}
        return sb.toString();
    }

	@SuppressWarnings("unchecked")
	public String getWXCodeUrl(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		//得到前台传递的请求参数
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		if(!paramMap.containsKey("orderSn")){
			return "{\"code\":\"1\",\"msg\":\"统一下单失败，缺少订单编号\"}";
		}
		
		String orderSn=paramMap.get("orderSn").toString().trim();
		if("".equals(orderSn)){
			return "{\"code\":\"1\",\"msg\":\"统一下单失败，缺少订单编号\"}";
		}
		String currentPayMoney=null;
		if(paramMap.containsKey("amount")){
			//付款金额
	        currentPayMoney=(String) paramMap.get("amount");//单位元
	        currentPayMoney=payService2.doubleString2IntStringFee(currentPayMoney);//转换为分
		}
		
		
		String ip="58.220.17.162";
		try{
			InetAddress addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress().toString();//获得本机IP
		}catch(Exception e){
		}
		
		return weChatPayService.yjgAppToPay(orderSn, ip, "Native", null, null,currentPayMoney);
	}
}
