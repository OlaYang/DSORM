package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.util.LogUtil;

@Service
public class PayGiveRedPacketAction extends BaseAction{

	@Autowired
	private IDataAction dataAction;

  	@Autowired
  	private IMushroomAction mushroomAction;
  	
  	@Autowired
  	private SmsAction smsAction;

  	@SuppressWarnings("unchecked")
	public String payGiveRedPacket(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo){
	    String result = "";
	    RuleServiceResponseData responseData = null;
	    ResponseInfo respInfo = new ResponseInfo();
	    Map<String,Object> paramMap1 = new HashMap<String,Object>();
	    List<Action> newActions = new ArrayList<Action>();
	    List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
	    DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
	    Map<String,Object> param = new HashMap<String,Object>();
	    JSONObject goods_serialNumber = new JSONObject();
	    JSONObject goods_use_start_time = new JSONObject();
	    JSONObject goods_use_end_time = new JSONObject();
	    JSONObject goods_bonus_name = new JSONObject();
	
	    Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
	    String ourOrderSn = (String)paramMap.get("order_sn");
        String[] array = ourOrderSn.split("up");
        String order_sn = array[0];//自己系统内部的订单编号
        String orderType = "";
        if(null != paramMap.get("order_type")){
        	orderType = (String) paramMap.get("order_type");
        }else{
        	orderType = "2";//普通订单
        	if(array.length>=3){
        		orderType = array[2];
        	}
        }
	    DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
	    String serviceName_esc_pay_log = "COM_HSV1_pay";// 获取订单总金额，已付金额的service
        serviceReqInfo.setServiceName(serviceName_esc_pay_log);
        Map<String,Object> param3 = new HashMap<String, Object>();
        param3.put("ordersn", order_sn);
        param3.put("type", orderType);
        serviceReqInfo.setParam(param3);
        serviceReqInfo.setNeedAll("1");
        String data =dataAction.getData(serviceReqInfo,"");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        LogUtil.info("PayGiveRedPacketAction COM_HSV1_pay param is: " + param3);
	    LogUtil.info("PayGiveRedPacketAction COM_HSV1_pay result is: " + data);
	    if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
	      LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay param is: " + param3);
	      LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay result is: " + data);
	      respInfo.setCode(DsResponseCodeData.ERROR.code);
	      respInfo.setDescription("查询订单错误！");
	      return JSON.toJSONString(respInfo);
	    }
	    JSONObject jsonBaseRespInfo = new JSONObject();
	    List<Map<String,String>> mapList3 = new ArrayList<Map<String,String>>();
	    mapList3 = responseData.getRows();
	    if(mapList3 != null && mapList3.size() > 0){
	    	double orderMoney = Double.valueOf(mapList3.get(0).get("order_amount").toString());// 订单总金额
            double paidMoney = Double.valueOf(mapList3.get(0).get("paid").toString());;// 订单已付金额
            if(orderMoney <= paidMoney){
            	dsManageReqInfo.setNeedAll("1");
        	    dsManageReqInfo.setServiceName("YJG_HSV1_SendBonusMessage");
        	    param.put("order_sn", order_sn);
        	    dsManageReqInfo.setParam(param);
        	    String resultData = this.dataAction.getData(dsManageReqInfo, "");
        	    responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        	    LogUtil.info("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage param is: " + param);
        	    LogUtil.info("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage result is: " + resultData);
        	    if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        	      LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage param is: " + param);
        	      LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage result is: " + resultData);
        	      respInfo.setCode(DsResponseCodeData.ERROR.code);
        	      respInfo.setDescription("查询订单商品错误！");
        	      return JSON.toJSONString(respInfo);
        	    }
        	    String shipping_status = "";
        	    mapList = responseData.getRows();
        	    if ((mapList != null) && (mapList.size() > 0)) {
        	      for (int i = 0; i < mapList.size(); i++) {
        	        String serialNumber = "";
        	        String use_start_time = "";
        	        String use_end_time = "";
        	        String bonus_name = "";
        	
        	        String flag = (String)mapList.get(i).get("flag");
        	        String title = (String)mapList.get(i).get("title");
        	        String bonus_id = (String)mapList.get(i).get("bonus_id");
        	        String goods_id = (String)mapList.get(i).get("goods_id");
        	        String goods_number = (String)mapList.get(i).get("goods_number");
        	        String user_id = (String)mapList.get(i).get("user_id");
        	        String mobile = (String)mapList.get(i).get("mobile");
        	        String rec_id = (String)mapList.get(i).get("rec_id");
        	        if ("1".equals(flag)) {
        	          List<Map<String,String>> mapList1 = new ArrayList<Map<String,String>>();
        	          DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
        	          Map<String,Object> param1 = new HashMap<String,Object>();
        	          param1.put("bonus_id", bonus_id);
        	          dsManageReqInfo1.setNeedAll("1");
        	          dsManageReqInfo1.setServiceName("HMJ_HSV1_bonus_list");
        	          dsManageReqInfo1.setParam(param1);
        	          String resultData1 = this.dataAction.getData(dsManageReqInfo1, "");
        	          responseData = DataUtil.parse(resultData1, RuleServiceResponseData.class);
        	          LogUtil.info("PayGiveRedPacketAction HMJ_HSV1_bonus_list param is: " + param1);
        	          LogUtil.info("PayGiveRedPacketAction HMJ_HSV1_bonus_list result is: " + resultData1);
        	          if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        	            LogUtil.error("PayGiveRedPacketAction HMJ_HSV1_bonus_list param is: " + param1);
        	            LogUtil.error("PayGiveRedPacketAction HMJ_HSV1_bonus_list result is: " + resultData1);
        	            respInfo.setCode(DsResponseCodeData.ERROR.code);
        	            respInfo.setDescription("查询红包错误！");
        	            return JSON.toJSONString(respInfo);
        	          }
        	          mapList1 = responseData.getRows();
        	          if ((mapList1 != null) && (mapList1.size() > 0)) {
        	            use_start_time = (String)mapList1.get(0).get("use_start_time");
        	            use_end_time = (String)mapList1.get(0).get("use_end_time");
        	            bonus_name = (String)mapList1.get(0).get("bonus_name");
        	            List<Map<String,String>> mapList2 = new ArrayList<Map<String,String>>();
        	            DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
        	            Map<String,Object> param2 = new HashMap<String,Object>();
        	            param2.put("order_sn", order_sn);
        	            dsManageReqInfo2.setNeedAll("1");
        	            dsManageReqInfo2.setServiceName("YJG_HSV1_SendBonusGoodsStatus");
        	            dsManageReqInfo2.setParam(param2);
        	            String resultData2 = this.dataAction.getData(dsManageReqInfo2, "");
        	            responseData = DataUtil.parse(resultData2, RuleServiceResponseData.class);
        	            LogUtil.info("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus param is: " + param2);
        	            LogUtil.info("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus result is: " + resultData2);
        	            if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
        	              LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus param is: " + param2);
        	              LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus result is: " + resultData2);
        	              respInfo.setCode(DsResponseCodeData.ERROR.code);
        	              respInfo.setDescription("查询订单状态错误！");
        	              return JSON.toJSONString(respInfo);
        	            }
        	            mapList2 = responseData.getRows();
        	            if ((mapList2 != null) && (mapList2.size() > 0)) {
        	              shipping_status = (String)mapList2.get(0).get("shipping_status");
        	              try {
        	                for (int j = 0; j < Integer.parseInt(goods_number); j++) {
        	                  String str = CodeUtils.getBonusCode();
        	                  Action action = new Action();
        	                  action.setServiceName("test_ecshop_ecs_bonus_sequence");
        	                  Map<String,Object> setMap = new HashMap<String,Object>();
        	                  action.setType("C");
        	                  setMap.put("bonus_id", bonus_id);
        	                  setMap.put("sequence_sn", str);
        	                  setMap.put("create_time", "$UnixTime");
        	                  setMap.put("get_time", "$UnixTime");
        	                  setMap.put("get_id", user_id);
        	                  setMap.put("bonus_status", Integer.valueOf(3));
        	                  setMap.put("cell_phone", mobile);
        	                  action.setSet(setMap);
        	                  newActions.add(action);
        	
        	                  if (serialNumber != ""){
        	                	  serialNumber = serialNumber + "," + str;
        	                  }else{
        	                	  serialNumber = str;
        	                  }
        	                }
        	              }
        	              catch (Exception e) {
        	                LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage param is: " + param);
        	                LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage result is: " + resultData);
        	                respInfo.setCode(DsResponseCodeData.ERROR.code);
        	                respInfo.setDescription("系统错误！");
        	                return JSON.toJSONString(respInfo);
        	              }
        	
        	              Action action1 = new Action();
        	              action1.setServiceName("test_ecshop_ecs_order_goods");
        	              Map<String,Object> setMap1 = new HashMap<String,Object>();
        	              action1.setType("U");
        	              setMap1.put("status", Integer.valueOf(1));
        	              setMap1.put("bonus_id", bonus_id);
        	              action1.setSet(setMap1);
        	              Where where = new Where();
        	              where.setPrepend("and");
        	              List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        	              SqlCondition sqlCondition = new SqlCondition();
        	              sqlCondition.setKey("rec_id");
        	              sqlCondition.setOp("=");
        	              sqlCondition.setValue(rec_id);
        	
        	              conditions.add(sqlCondition);
        	              where.setConditions(conditions);
        	              action1.setWhere(where);
        	              newActions.add(action1);
        	            }else {
        	              LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus mapList2 length is: " + mapList2.size());
        	              LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus param is: " + param2);
        	              LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusGoodsStatus result is: " + resultData2);
        	              respInfo.setCode(DsResponseCodeData.ERROR.code);
        	              respInfo.setDescription("查询订单状态无数据！");
        	              return JSON.toJSONString(respInfo);
        	            }
        	          } else {
        	            LogUtil.error("PayGiveRedPacketAction HMJ_HSV1_bonus_list mapList1 length is: " + mapList1.size());
        	            LogUtil.error("PayGiveRedPacketAction HMJ_HSV1_bonus_list param is: " + param1);
        	            LogUtil.error("PayGiveRedPacketAction HMJ_HSV1_bonus_list result is: " + resultData1);
        	            respInfo.setCode(DsResponseCodeData.ERROR.code);
        	            respInfo.setDescription("查询红包无数据！");
        	            return JSON.toJSONString(respInfo);
        	          }
        	        }else if("2".equals(flag)){
        	        	LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage param is: " + param);
        	            LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage result is: " + resultData);
        	            respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        	            respInfo.setDescription(title);
        	            return JSON.toJSONString(respInfo);
        	        }else if("0".equals(flag)){
						LogUtil.error("OrederAction YJG_HSV1_SendBonusMessage param is: " + param);
						LogUtil.error("OrederAction YJG_HSV1_SendBonusMessage result is: " + resultData);
						respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        	            respInfo.setDescription("");
        	            return JSON.toJSONString(respInfo);
        	        }
        	        goods_serialNumber.put(goods_id, serialNumber);
        	        goods_use_start_time.put(goods_id, use_start_time);
        	        goods_use_end_time.put(goods_id, use_end_time);
        	        goods_bonus_name.put(goods_id, bonus_name);
        	      }
        	
        	      Action action2 = new Action();
        	      action2.setServiceName("test_ecshop_ecs_order_info");
        	      Map<String,Object> setMap2 = new HashMap<String,Object>();
        	      action2.setType("U");
        	      setMap2.put("shipping_status", shipping_status);
        	      action2.setSet(setMap2);
        	      Where where1 = new Where();
        	      where1.setPrepend("and");
        	      List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
        	      SqlCondition sqlCondition1 = new SqlCondition();
        	      sqlCondition1.setKey("order_sn");
        	      sqlCondition1.setOp("=");
        	      sqlCondition1.setValue(order_sn);
        	
        	      conditions1.add(sqlCondition1);
        	      where1.setConditions(conditions1);
        	      action2.setWhere(where1);
        	      newActions.add(action2);
        	
        	      paramMap1.put("actions", newActions);
        	      paramMap1.put("transaction", Integer.valueOf(1));
        	      DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        	      dsReqInfo.setServiceName("MUSH_Offer");
        	      dsReqInfo.setParam(paramMap1);
        	      result = this.mushroomAction.offer(dsReqInfo);
        	      JSONObject job = JSONObject.parseObject(result);
        	      LogUtil.info("PayGiveRedPacketAction set param is: " + paramMap1);
        	      LogUtil.info("PayGiveRedPacketAction set result is: " + result);
        	      
        	      if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code")) && (result.indexOf("sequence_sn") != -1)) {
        	    	  payGiveRedPacket(request, response, repInfo);
          	      }
        	      
        	      if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
        	        LogUtil.error("PayGiveRedPacketAction set  param is: " + paramMap1);
        	        LogUtil.error("PayGiveRedPacketAction set  result is: " + result);
        	        respInfo.setCode(DsResponseCodeData.ERROR.code);
        	        respInfo.setDescription("写入数据失败！");
        	        return JSON.toJSONString(respInfo);
        	      }
        	
        	      for (int y = 0; y < mapList.size(); y++){
        	    	  String goods_id1 = mapList.get(y).get("goods_id");
        	    	  String mobile1 = (String)mapList.get(y).get("mobile");
        	    	  String serialNumber1 = "";
        	    	  /*String use_start_time1 = "";
        	    	  String use_end_time1 = "";
        	    	  String bonus_name1 = "";*/
        	    	  Iterator<String> iterator = goods_serialNumber.keySet().iterator();
        	    	  while (iterator.hasNext()) {
        					String key = iterator.next();
        					String value = goods_serialNumber.getString(key);
        					if(key.equals(goods_id1)){
        						serialNumber1 = value;
        						break;
        					}
        				}
        	    	  /*Iterator<String> iterator1 = goods_use_start_time.keySet().iterator();
        	    	  while (iterator1.hasNext()) {
        					String key = iterator1.next();
        					String value = goods_use_start_time.getString(key);
        					if(key.equals(goods_id1)){
        						use_start_time1 = value;
        						break;
        					}
        				}
        	    	  Iterator<String> iterator2 = goods_use_end_time.keySet().iterator();
        	    	  while (iterator2.hasNext()) {
        					String key = iterator2.next();
        					String value = goods_use_end_time.getString(key);
        					if(key.equals(goods_id1)){
        						use_end_time1 = value;
        						break;
        					}
        				}
        	    	  Iterator<String> iterator3 = goods_bonus_name.keySet().iterator();
        	    	  while (iterator3.hasNext()) {
        					String key = iterator3.next();
        					String value = goods_bonus_name.getString(key);
        					if(key.equals(goods_id1)){
        						bonus_name1 = value;
        						break;
        					}
        				}*/
        	    	  RepInfo repInfo1 = new RepInfo();
        	    	  repInfo1.setAction("smsAction");
        	    	  repInfo1.setMethod("sendMsg");
        	    	  String smsParam = "{\"website\":\"0\",\"sequence_sn\":\""+serialNumber1+"\",\"phone_number\":\""+mobile1+
	    			  			"\",\"template_name_en\":\"hbgm\",\"smsType\":\"32\"}";
        	    	  repInfo1.setParam(smsParam);
        	    	  String sendMsg = smsAction.sendMsg(request, response, repInfo1);
        			  jsonBaseRespInfo = (JSONObject) JSONObject.parseObject(sendMsg);
        			  if("1".equals(jsonBaseRespInfo.get("code"))){
        				  LogUtil.error("PayGiveRedPacketAction send is fail and smsParam is: "+smsParam);
        			  }
        	      }
        	    }else{
        	      LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage mapList length is: " + mapList.size());
        	      LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage param is: " + param);
        	      LogUtil.error("PayGiveRedPacketAction YJG_HSV1_SendBonusMessage result is: " + resultData);
        	      respInfo.setCode(DsResponseCodeData.ERROR.code);
        	      respInfo.setDescription("查询订单商品无数据！");
        	      return JSON.toJSONString(respInfo);
        	    }
            }else{
            	LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay orderMoney is: " +orderMoney+",paidMoney is: "+paidMoney);
    			LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay param3 is: " + param3);
    			LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay result is: " + data);
    			respInfo.setCode(DsResponseCodeData.SUCCESS.code);
    			respInfo.setDescription("订单未完全付款！");
    			return JSON.toJSONString(respInfo);
            }
	    }else{
			LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay mapList3 length is: " + mapList3.size());
			LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay param3 is: " + param3);
			LogUtil.error("PayGiveRedPacketAction COM_HSV1_pay result is: " + data);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单无数据！");
			return JSON.toJSONString(respInfo);
	    }
	    respInfo.setCode(DsResponseCodeData.SUCCESS.code);
	    respInfo.setDescription(jsonBaseRespInfo.getString("description"));
	    return JSON.toJSONString(respInfo);
	  }
  	
}