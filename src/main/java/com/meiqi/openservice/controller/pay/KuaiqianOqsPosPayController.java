package com.meiqi.openservice.controller.pay;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RespectBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.ConfigFileUtil;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.action.pay.kuaiqianpos.CerEncode;
import com.meiqi.openservice.action.pay.kuaiqianpos.Pkipair;
import com.meiqi.openservice.commons.util.StringUtils;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月10日 下午3:40:41 
 * 类说明  快钱pos机支付
 */
@RequestMapping(value="/oqs")
@Controller
public class KuaiqianOqsPosPayController {
	
	private static final Log LOG =  LogFactory.getLog("pay");
	
	private static Properties  properties1 = new Properties();
	
	@Autowired
	private IDataAction dataAction;
	 
	
	/**
	 * 快钱pos机支付接口
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/pay/getOqsPay")
	@RespectBinding
	public String getOqsPay(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		
		properties1 = ConfigFileUtil.propertiesReader("sysConfig.properties");
        String kuaiqian_pos_merchantName = properties1.getProperty("kuaiqian_pos_merchantName");
        
		String orderSn = request.getParameter("orderId");
		String reqTime = request.getParameter("reqTime");
		String merchantId = request.getParameter("merchantId");
		String terminalId = request.getParameter("terminalId");
		String ext1 = request.getParameter("ext1");
		String ext2 = request.getParameter("ext2");
		String MAC = request.getParameter("MAC");
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId",orderSn);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "reqTime",reqTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		LOG.info("接收到的所有变量为:" + orderSn+","+reqTime+","+merchantId+","+terminalId+","+ext1+","+ext2+","+MAC);
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
        
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_BUV1_tmp_paylog");
		param.put("third_order_sn", orderSn);
		param.put("pay_type", Constants.PAY_TYPE.KUAIQIAN_OQS);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		RuleServiceResponseData responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog result is: "+result1);
			LOG.error("查询流水号支付金额失败！");
	    }
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		mapList = responseData.getRows();
		if(null == mapList || mapList.size() == 0){
			LOG.error("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("KuaiqianOqsPosPayController getOpsPay YJG_BUV1_tmp_paylog result is: "+result1);
			LOG.error("查询流水号支付金额无数据！");
		}
		
		CerEncode ce = new CerEncode();
		boolean flag = ce.enCodeByCer(merchantSignMsgVal, MAC);
		String responseCode = "";
		if (flag) {
			responseCode = "00";
			LOG.info("快钱pos机验证通过,flag is "+flag+" ,param is "+merchantSignMsgVal+","+MAC);
		} else {
			responseCode = "56";
			LOG.error("flag is "+flag+" ,param is "+merchantSignMsgVal+","+MAC);
			LOG.error("快钱pos机验证不通过");
		}
		
		String order_amount = mapList.get(0).get("order_amount");
		if(StringUtils.isEmpty(order_amount)){
			order_amount = "0.00";
		}
		
		//查到数据进行拼接
		String signXML =  "<MessageContent>"
				+ "<reqTime>"
				+ reqTime
				+ "</reqTime>"							   
				+ "<respTime>"+getDate()+"</respTime>"
				+ "<responseCode>"
				+ responseCode
				+ "</responseCode>"
				+ "<message>"
				+ "<orderId>"
				+ orderSn
				+ "</orderId>"
				+ "<merchantId>"
				+ merchantId
				+ "</merchantId>"
				+ "<merchantName>"+kuaiqian_pos_merchantName+"</merchantName>"
				+ "<amt>"+Double.valueOf(order_amount)+"</amt>"
				+ "<amt2></amt2>"
				+ "<amt3></amt3>"
				+ "<amt4></amt4>"
				+ "<ext>"
				+ "<userdata1>"
				+ "<value>"+orderSn+"</value>"
				+ "<chnName>交易流水号</chnName>"
				+ "</userdata1>"
				+ "<userdata2>"
				+ "<value>"+order_amount+"</value>"
				+ "<chnName>支付金额</chnName>"
				+ "</userdata2>"
				+ "<userdata4>"
				+ "<value>"+orderSn+"</value>"
				+ "<chnName>交易流水号</chnName>"
				+ "</userdata4>"
				+ "<userdata5>"
				+ "<value>"+order_amount+"</value>"
				+ "<chnName>支付金额</chnName>"
				+ "</userdata5>"
				+ "</ext>"
				+"<desc></desc>"
				+ "</message>" + "</MessageContent>";
		Pkipair pk = new Pkipair();
		String signMAC = pk.signMsg(signXML);
		
		//最终返回到pos上的报文
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<ResponseMessage>" + "<MAC>"
				+ signMAC.replaceAll("\n","")//signMAC.replaceAll("\r\n","")
				+ "</MAC>"
				+ signXML
				+ "</ResponseMessage>";
		try {
			BufferedWriter outW = new BufferedWriter(response.getWriter());
			outW.write(xml);
			outW.flush();
			outW.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		LOG.info(xml);
		return null;
		
	}

	/**
	 * 拼接验签的参数
	 * @param returnStr
	 * @param paramId
	 * @param paramValue
	 * @return
	 */
	public String appendParam(String returnStr, String paramId,String paramValue){
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + paramId + "=" + paramValue;
			}
		} else {
			if (!paramValue.equals("")) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}
	
	/**
	 * 得到当前时间（年月日时分秒）
	 * @return
	 */
	public String getDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = df.format(new Date());
		return str;
	}
}
