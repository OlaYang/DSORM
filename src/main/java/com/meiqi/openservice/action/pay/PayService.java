package com.meiqi.openservice.action.pay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.config.Constants;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.commons.util.DataUtil;

@Service
public class PayService {
	@Autowired
	private IDataAction dataAction;
	
	public static LRUMap<String, String> paying = new LRUMap<String, String>(10000);
	
	/**
	 * 根据订单编号，获取订单信息
	 * @param orderId
	 * @return
	 */
	public Map<String,String> getOrderInfoFromData(String orderId,String orderType){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("COM_HSV1_pay");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("ordersn", orderId);
		if(null==orderType){
			orderType="2";
		}
		param.put("type", orderType);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		List<Map<String, String>> rows=responseData.getRows();
		if(Constants.GetResponseCode.SUCCESS.equals(responseData.getCode())){
			if(null==rows||0==rows.size()){
				return null;
			}
			return rows.get(0);
		}
		return null;
	}
	
	/**
	 * double型金额 10.11 元 转 int型金额 1011分
	 * @return
	 */
	public String doubleString2IntStringFee(String doubleStringFee){
		 int index = doubleStringFee.indexOf(".");  
	     int length = doubleStringFee.length();  
	     Long amLong = 0l;  
	     if(index == -1){  
	            amLong = Long.parseLong(doubleStringFee+"00");  
	        }else if(length - index >= 3){  
	            amLong = Long.parseLong((doubleStringFee.substring(0,index+3)).replace(".",""));  
	        }else if(length - index == 2){  
	            amLong = Long.parseLong((doubleStringFee.substring(0,index+2)).replace(".","")+0);  
	        }else{  
	            amLong = Long.parseLong((doubleStringFee.substring(0,index+1)).replace(".","")+"00");  
	        }  
	        return amLong.toString();  
	}
	
	/**
	 * int型金额 1011分 转 DOUBLE型金额 10.11元
	 * @return
	 */
	public String intString2DoubleStringFee(String intStringFee){
		 return BigDecimal.valueOf(Long.parseLong(intStringFee)).divide(new BigDecimal(100)).toString();
	}
	
}
