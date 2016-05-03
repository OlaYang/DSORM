package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年11月18日 下午3:44:37 
 * 类说明  商城后台供应链接口
 */
@Service
public class SupplyChainAction extends BaseAction{

	private static final Log LOG =  LogFactory.getLog("supply");
	
	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private IMushroomAction mushroomAction;
	
	@SuppressWarnings("unchecked")
	public String transform(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
	    
	    // RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSON.toJSONString(respInfo);
        }
        
		String resultData = "";
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		JSONObject jsonObject = new JSONObject();
		
		String type = paramMap.get("type").toString();
		JSONArray rows = new JSONArray();
		JSONObject obj = new JSONObject();
		if("74".equals(type)){
			LOG.info("transformInStore  param is: "+repInfo.getParam());
			String transformOutStore = transformOutStore(paramMap);
			JSONObject job = JSONObject.parseObject(transformOutStore);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				return transformOutStore;
		    }
			JSONArray array =  job.getJSONArray("results");
			String delivery_id = array.getJSONObject(0).get("generateKey").toString();
			String eo_id = array.getJSONObject(2).get("generateKey").toString();
			
			String transformInStore = transformInStore(paramMap,job.get("ship_sn").toString(),job.get("transactionNum").toString());
			JSONObject job1 = JSONObject.parseObject(transformInStore);
			if (!DsResponseCodeData.SUCCESS.code.equals(job1.get("code"))) {
				String rollBackTransactionNum = rollBackTransactionNum(job.get("transactionNum").toString());
				LOG.info("rollBackTransactionNum result is: "+rollBackTransactionNum+" and transactionNum is: "+job.get("transactionNum").toString());
				return transformInStore;
		    }
			JSONArray array1 =  job1.getJSONArray("results");
			String carry_id = array1.getJSONObject(0).get("generateKey").toString();
			String ei_id = array1.getJSONObject(0).get("generateKey").toString();
			
			obj.put("delivery_id", delivery_id);
			obj.put("eo_id", eo_id);
			obj.put("carry_id", carry_id);
			obj.put("ei_id", ei_id);
			rows.add(obj);
			jsonObject.put("rows", rows);
			
			String commitTransactionNum = commitTransactionNum(job.get("transactionNum").toString());
			LOG.info("transform is success");
			LOG.info("commitTransactionNum result is: "+commitTransactionNum+" and transactionNum is: "+job.get("transactionNum").toString());
		}else if("4".equals(type) || "6".equals(type)){
			String allotInStore = allotInStore(paramMap);
			JSONObject job = JSONObject.parseObject(allotInStore);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				return allotInStore;
		    }
			JSONArray array =  job.getJSONArray("results");
			String carry_id = array.getJSONObject(0).get("generateKey").toString();
			String ei_id = array.getJSONObject(2).get("generateKey").toString();
			obj.put("carry_id", carry_id);
			obj.put("ei_id", ei_id);
			rows.add(obj);
			jsonObject.put("rows", rows);
		}else if("5".equals(type)){
			String adjustOutStore = adjustOutStore(paramMap);
			JSONObject job = JSONObject.parseObject(adjustOutStore);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				return adjustOutStore;
		    }
			JSONArray array =  job.getJSONArray("results");
			String eo_id = array.getJSONObject(2).get("generateKey").toString();
			obj.put("eo_id", eo_id);
			rows.add(obj);
			jsonObject.put("rows", rows);
		}else if("1".equals(type) || "2".equals(type) || "3".equals(type) || "106".equals(type)){
			String deliverGoods = deliverGoods(paramMap);
			JSONObject job = JSONObject.parseObject(deliverGoods);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				return deliverGoods;
		    }
			JSONArray array =  job.getJSONArray("results");
			String eo_id = array.getJSONObject(2).get("generateKey").toString();
			obj.put("eo_id", eo_id);
			rows.add(obj);
			jsonObject.put("rows", rows);
		}
		
		jsonObject.put("code", "0");
		jsonObject.put("description", "成功");
		resultData = jsonObject.toJSONString();
		return resultData;
		
	}
	
	
	/**
	 * 转换入库
	 * @param map 前台传过来的请求参数
	 * @param ship_sn 转换出库查询出来的发货单号
	 * @param transactionNum  全局事务号
	 * @return
	 */
	private String transformInStore(Map<String,Object> map,String ship_sn,String transactionNum){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String depot_id = map.get("depot_id").toString();
		String user_name = map.get("user_name").toString();
		String bom_id = map.get("bom_id").toString();
		String add_num = map.get("add_num").toString();
		String putin_time = map.get("best_time").toString();
		String relate_sn = ship_sn;
		String user_id = map.get("user_id").toString();
		String is_Split = map.get("is_Split").toString();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_JavaNotice_Info");
		param.put("type", 7);
		param.put("depot_id", depot_id);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("transformInStore YJG_HSV1_JavaNotice_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("transformInStore YJG_HSV1_JavaNotice_Info result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("transformInStore YJG_HSV1_JavaNotice_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("transformInStore YJG_HSV1_JavaNotice_Info result is: "+result1);
			LOG.info("transformInStore is false");
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("转换入库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("YJG_HSV1_JavaBom_Goods");
		param1.put("depot_id", depot_id);
		param1.put("bom_id", bom_id);
		param1.put("add_num", Integer.parseInt(add_num));
		param1.put("is_Split", is_Split);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("transformInStore YJG_HSV1_JavaBom_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("transformInStore YJG_HSV1_JavaBom_Goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("transformInStore YJG_HSV1_JavaBom_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("transformInStore YJG_HSV1_JavaBom_Goods result is: "+result2);
			LOG.info("transformInStore is false");
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("转换入库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 3;
		int num1 = 2;
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String carry_sn = mapList.get(0).get("carry_sn");
			String in_sn = mapList.get(0).get("in_sn");
			String status = mapList.get(0).get("status");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_send_notice");
			Map<String, Object> setMap = new HashMap<String, Object>();
			action.setType("C");
			setMap.put("carry_sn", carry_sn);
			setMap.put("depot_id", Integer.parseInt(depot_id));
			setMap.put("status", Integer.parseInt(status));
			setMap.put("putin_time", Integer.parseInt(putin_time));
			setMap.put("add_time", "$UnixTime");
			setMap.put("user_id", Integer.parseInt(user_id));
			setMap.put("type", 7);
			setMap.put("relate_sn", relate_sn);
			action.setSet(setMap);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_supply_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("receipt_sn", carry_sn);
			setMap1.put("type", 1);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "添加转换入库单");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_indepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("in_sn", in_sn);
			setMap2.put("type", 7);
			setMap2.put("carry_id", "$-2.generateKey");
			setMap2.put("depot_id", depot_id);
			setMap2.put("in_time", "$UnixTime");
			setMap2.put("status", 1);
			setMap2.put("add_time", "$UnixTime");
			setMap2.put("user_id", user_id);
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				
				String goods_id = mapList1.get(i).get("goods_id");
				String add_num1 = mapList1.get(i).get("add_num");
				if(StringUtils.isEmpty(add_num1)){
					add_num1 = "0";
				}
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_notice_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("carry_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", goods_id);
				setMap3.put("real_num", Integer.parseInt(add_num1));
				setMap3.put("bom_id", bom_id);
				action3.setSet(setMap3);
				newActions.add(action3);
				
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_indepot_goods");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("ei_id", "$-"+num1+".generateKey");
				setMap4.put("in_number", Integer.parseInt(add_num1));
				setMap4.put("bom_id", bom_id);
				setMap4.put("goods_id", goods_id);
				setMap4.put("status", 0);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				action5.setType("C");
				setMap5.put("stgd_id", "$-1.generateKey");
				setMap5.put("receipt_sn", carry_sn);
				setMap5.put("type", 0);
				setMap5.put("in_number", Integer.parseInt(add_num1));
				setMap5.put("operation_time", "$UnixTime");
				setMap5.put("note", "生成转换入出库单(主商品入库)");
				setMap5.put("user_id", user_id);
				action5.setSet(setMap5);
				newActions.add(action5);
				
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject.getJSONArray("rows");
				String obligate_number = "0";
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					obligate_number = jsonObject2.getString("obligate_number");
					action6.setType("U");
					setMap6.put("obligate_number", Integer.parseInt(obligate_number)+Integer.parseInt(add_num1));
					action6.setSet(setMap6);
					Where where = new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition = new SqlCondition();
					sqlCondition.setKey("depot_id");
					sqlCondition.setOp("=");
					sqlCondition.setValue(depot_id);
					
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("goods_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(goods_id);
					
					conditions.add(sqlCondition);
					conditions.add(sqlCondition1);
					where.setConditions(conditions);
					action6.setWhere(where);
				}else{
					action6.setType("C");
					setMap6.put("depot_id", depot_id);
					setMap6.put("goods_id", goods_id);
					setMap6.put("obligate_number", Integer.parseInt(add_num1));
					action6.setSet(setMap6);
				}
				newActions.add(action6);
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_stock_goods_action");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("C");
				setMap8.put("depot_id", depot_id);
				setMap8.put("goods_id", goods_id);
				setMap8.put("business_sn", carry_sn);
				setMap8.put("procedure_sn", in_sn);
				setMap8.put("before_number", obligate_number);
				setMap8.put("in_number", Integer.parseInt(add_num1));
				setMap8.put("obligate_number", Integer.parseInt(obligate_number)+Integer.parseInt(add_num1));
				setMap8.put("add_time", "$UnixTime");
				action8.setSet(setMap8);
				newActions.add(action8);
				
				num = num+5;
				num1 = num1+5;
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			paramMap1.put("transactionNum", transactionNum);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("transformInStore set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("transformInStore set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("transformInStore set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("transformInStore set  result is: "+str);
				LOG.info("transformInStore is false");
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("转换入库失败！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("transformInStore YJG_HSV1_JavaNotice_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("transformInStore YJG_HSV1_JavaNotice_Info result is: "+result1);
			LOG.error("transformInStore YJG_HSV1_JavaBom_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("transformInStore YJG_HSV1_JavaBom_Goods result is: "+result2);
			LOG.error("transformInStore mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
			LOG.info("transformInStore is false");
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("转换入库失败！");
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 转换出库
	 * @param map 前端传入的请求参数
	 * @return 
	 */
	private String transformOutStore(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();

		String depot_id = map.get("depot_id").toString();
		String user_name = map.get("user_name").toString();
		String bom_id = map.get("bom_id").toString();
		String add_num = map.get("add_num").toString();
		String user_id = map.get("user_id").toString();
		String is_Split = map.get("is_Split").toString();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_JavaDeliver_BOM");
		param.put("type", 4);
		param.put("depot_id", depot_id);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("transformOutStore YJG_HSV1_JavaDeliver_BOM param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("transformOutStore YJG_HSV1_JavaDeliver_BOM result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("transformOutStore YJG_HSV1_JavaDeliver_BOM param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("transformOutStore YJG_HSV1_JavaDeliver_BOM result is: "+result1);
			LOG.info("transformOutStore is false");
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("转换出库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("YJG_HSV1_JavaBomPart");
		param1.put("bom_id", bom_id);
		param1.put("add_num", Integer.parseInt(add_num));
		param1.put("is_Split", is_Split);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("transformOutStore YJG_HSV1_JavaBomPart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("transformOutStore YJG_HSV1_JavaBomPart result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("transformOutStore YJG_HSV1_JavaBomPart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("transformOutStore YJG_HSV1_JavaBomPart result is: "+result2);
			LOG.info("transformOutStore is false");
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("转换出库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 3;
		int num1 = 2;
		
		String transactionNum = getTransactionNum("test_ecshop_ecs_delivery_order");
		
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String province_id = mapList.get(0).get("province_id");
			String city_id = mapList.get(0).get("city_id");
			String district_id = mapList.get(0).get("district_id");
			String address= mapList.get(0).get("address");
			String shipping_name = mapList.get(0).get("shipping_name");
			String ship_sn = mapList.get(0).get("ship_sn");
			String out_sn = mapList.get(0).get("out_sn");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_delivery_order");
			Map<String, Object> setMap = new HashMap<String, Object>();
			
			action.setType("C");
			setMap.put("delivery_sn", ship_sn);
			setMap.put("add_time", "$UnixTime");
			setMap.put("shipping_name", shipping_name);
			setMap.put("consignee", user_name);
			setMap.put("address", address);
			setMap.put("province", province_id);
			setMap.put("city", city_id);
			setMap.put("district", district_id);
			setMap.put("best_time", DateUtils.timeToDate(System.currentTimeMillis(), "yyyy-MM-dd"));
			setMap.put("status", 4);
			setMap.put("delivery_direction", 1);
			setMap.put("creater_id", user_id);
			setMap.put("print_status", 0);
			setMap.put("depot_id", depot_id);
			setMap.put("type", 4);
			setMap.put("allot_depot", depot_id);
			action.setSet(setMap);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_delivery_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("delivery_id", "$-1.generateKey");
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "添加转换出库单");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_outdepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("out_sn", out_sn);
			setMap2.put("type", 4);
			setMap2.put("relation_id", "$-2.generateKey");
			setMap2.put("depot_id", depot_id);
			setMap2.put("status", 0);
			setMap2.put("carriage_way", 0);
			setMap2.put("out_time", "$UnixTime");
			setMap2.put("user_id", user_id);
			setMap2.put("servicer_id", 0);
			setMap2.put("add_time", "$UnixTime");
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String part_goods_id = mapList1.get(i).get("part_goods_id");
				String goods_name = mapList1.get(i).get("goods_name");
				String goods_sn = mapList1.get(i).get("goods_sn");
				String num_total = mapList1.get(i).get("num_total");
				if(StringUtils.isEmpty(num_total)){
					num_total = "0";
				}
				
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_delivery_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("delivery_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", part_goods_id);
				setMap3.put("goods_name", goods_name);
				setMap3.put("goods_sn", goods_sn);
				setMap3.put("send_number", Integer.parseInt(num_total));
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_outdepot_goods");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("eo_id", "$-"+num1+".generateKey");
				setMap4.put("out_number", Integer.parseInt(num_total));
				setMap4.put("bom_id", bom_id);
				setMap4.put("bom_id", bom_id);
				setMap4.put("goods_id", part_goods_id);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				action5.setType("C");
				setMap5.put("stgd_id", "$-1.generateKey");
				setMap5.put("type", 1);
				setMap5.put("out_number", Integer.parseInt(num_total));
				setMap5.put("operation_time", "$UnixTime");
				setMap5.put("note", "生成转换出库出库单(主商品出库)");
				setMap5.put("user_id", user_id);
				action5.setSet(setMap5);
				newActions.add(action5);
				
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", part_goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject.getJSONArray("rows");
				JSONObject jsonObject2 = jsonArray.getJSONObject(0);
				int obligate_number = jsonObject2.getInteger("obligate_number");
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				action6.setType("U");
				setMap6.put("obligate_number", obligate_number-Integer.parseInt(num_total));
				action6.setSet(setMap6);
				Where where = new Where();
				where.setPrepend("and");
				List<SqlCondition> conditions = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition = new SqlCondition();
				sqlCondition.setKey("depot_id");
				sqlCondition.setOp("=");
				sqlCondition.setValue(depot_id);;
				
				SqlCondition sqlCondition1 = new SqlCondition();
				sqlCondition1.setKey("goods_id");
				sqlCondition1.setOp("=");
				sqlCondition1.setValue(part_goods_id);
				
				conditions.add(sqlCondition);
				conditions.add(sqlCondition1);
				where.setConditions(conditions);
				action6.setWhere(where);
				newActions.add(action6);
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_stock_goods_action");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("C");
				setMap8.put("depot_id", depot_id);
				setMap8.put("goods_id", part_goods_id);
				setMap8.put("business_sn", ship_sn);
				setMap8.put("procedure_sn", out_sn);
				setMap8.put("before_number", obligate_number);
				setMap8.put("out_number", num_total);
				setMap8.put("obligate_number", obligate_number-Integer.parseInt(num_total));
				setMap8.put("add_time", "$UnixTime");
				action8.setSet(setMap8);
				newActions.add(action8);
				
				num = num+5;
				num1 = num1+5;
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			paramMap1.put("transactionNum",transactionNum);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("transformOutStore set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("transformOutStore set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("transformOutStore set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("transformOutStore set  result is: "+str);
				LOG.info("transformOutStore is false");
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("写入数据失败！");
                return JSON.toJSONString(respInfo);
		    }
			job.put("ship_sn", ship_sn);
			job.put("transactionNum", transactionNum);
			return job.toJSONString();
			
		}else{
			LOG.error("transformOutStore YJG_HSV1_JavaDeliver_BOM param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("transformOutStore YJG_HSV1_JavaDeliver_BOM result is: "+result1);
			LOG.error("transformOutStore YJG_HSV1_JavaBomPart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("transformOutStore YJG_HSV1_JavaBomPart result is: "+result2);
			LOG.error("transformOutStore mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
			LOG.info("transformOutStore is false");
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("转换出库失败！");
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 
	 * @param map 
	 * @return  调拨入库
	 */
	private String allotInStore(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String delivery_id  = map.get("delivery_id").toString();
		String user_name = map.get("user_name").toString();
		String user_id = map.get("user_id").toString();
		String putin_time = map.get("best_time").toString();
		String type = map.get("type").toString();
		JSONObject jsonObject = (JSONObject) map.get("goods_notes");
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("HMJ_HSV2_Allot_Info");
		param.put("delivery_id", delivery_id);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("allotInStore HMJ_HSV2_Allot_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("allotInStore HMJ_HSV2_Allot_Info result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("allotInStore HMJ_HSV2_Allot_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("allotInStore HMJ_HSV2_Allot_Info result is: "+result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("调拨入库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("HMJ_BUV2_delivery_goods");
		param1.put("delivery_id", delivery_id);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("allotInStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("allotInStore HMJ_BUV2_delivery_goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("allotInStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("allotInStore HMJ_BUV2_delivery_goods result is: "+result2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("调拨入库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 3;
		int num1 = 2;
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String delivery_sn = mapList.get(0).get("delivery_sn");
			String status = mapList.get(0).get("status");
			String depot_id = mapList.get(0).get("depot_id");
			String carry_sn = mapList.get(0).get("carry_sn");
			String in_sn = mapList.get(0).get("in_sn");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_send_notice");
			Map<String, Object> setMap = new HashMap<String, Object>();
			
			action.setType("C");
			setMap.put("carry_sn", carry_sn);
			setMap.put("depot_id", Integer.parseInt(depot_id));
			setMap.put("status", Integer.parseInt(status));
			if("".equals(putin_time)){
				setMap.put("putin_time", "$UnixTime");
			}else{
				setMap.put("putin_time", Integer.parseInt(putin_time));
			}
			setMap.put("add_time", "$UnixTime");
			setMap.put("user_id", Integer.parseInt(user_id));
			setMap.put("type", type);
			setMap.put("relate_sn", delivery_sn);
			setMap.put("suppliers_id", 0);
			action.setSet(setMap);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_supply_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("receipt_sn", carry_sn);
			setMap1.put("type", 1);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "生成调拨入库单");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_indepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("in_sn", in_sn);
			setMap2.put("type", type);
			setMap2.put("carry_id", "$-2.generateKey");
			setMap2.put("depot_id", depot_id);
			setMap2.put("in_time", "$UnixTime");
			setMap2.put("status", 1);
			setMap2.put("add_time", "$UnixTime");
			setMap2.put("user_id", user_id);
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String goods_id = mapList1.get(i).get("goods_id");
				String send_number = mapList1.get(i).get("send_number");
				if(StringUtils.isEmpty(send_number)){
					send_number = "0";
				}
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_notice_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("carry_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", goods_id);
				setMap3.put("real_num", Integer.parseInt(send_number));
				Iterator<String> iterator = jsonObject.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = jsonObject.getString(key);
					if(key.equals(goods_id)){
						setMap3.put("note", value);
						break;
					}
				}
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_indepot_goods");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("ei_id", "$-"+num1+".generateKey");
				setMap4.put("in_number", Integer.parseInt(send_number));
				setMap4.put("goods_id", goods_id);
				setMap4.put("status", 0);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				action5.setType("C");
				setMap5.put("stgd_id", "$-1.generateKey");
				setMap5.put("receipt_sn", carry_sn);
				setMap5.put("type", 0);
				setMap5.put("in_number", Integer.parseInt(send_number));
				setMap5.put("operation_time", "$UnixTime");
				setMap5.put("note", "生成调拨入库单-商品入库");
				setMap5.put("user_id", user_id);
				action5.setSet(setMap5);
				newActions.add(action5);
				
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject1 = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject1.getJSONArray("rows");
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					String obligate_number = jsonObject2.getString("obligate_number");
					action6.setType("U");
					setMap6.put("obligate_number", Integer.parseInt(obligate_number)+Integer.parseInt(send_number));
					action6.setSet(setMap6);
					Where where = new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition = new SqlCondition();
					sqlCondition.setKey("depot_id");
					sqlCondition.setOp("=");
					sqlCondition.setValue(depot_id);
					
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("goods_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(goods_id);
					
					conditions.add(sqlCondition);
					conditions.add(sqlCondition1);
					where.setConditions(conditions);
					action6.setWhere(where);
				}else{
					action6.setType("C");
					setMap6.put("depot_id", depot_id);
					setMap6.put("goods_id", goods_id);
					setMap6.put("obligate_number", Integer.parseInt(send_number));
					action6.setSet(setMap6);
				}
				newActions.add(action6);
				
				num = num+4;
				num1 = num1+4;
			}
			
			Action action7 = new Action();
			action7.setServiceName("test_ecshop_ecs_delivery_order");
			Map<String, Object> setMap7 = new HashMap<String, Object>();
			action7.setType("U");
			setMap7.put("status", 4);
			action7.setSet(setMap7);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("delivery_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(delivery_id);
			
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action7.setWhere(where);
			newActions.add(action7);
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("allotInStore set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("allotInStore set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("allotInStore set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("allotInStore set  result is: "+str);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("调拨入库失败！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("allotInStore HMJ_HSV2_Allot_Info param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("allotInStore HMJ_HSV2_Allot_Info result is: "+result1);
			LOG.error("allotInStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("allotInStore HMJ_BUV2_delivery_goods result is: "+result2);
			LOG.error("allotInStore mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("调拨入库失败！");
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 调整出库
	 * @param map
	 * @return
	 */
	private String adjustOutStore(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String delivery_id  = map.get("delivery_id").toString();
		String user_name = map.get("user_name").toString();
		String user_id = map.get("user_id").toString();

		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV2_JavaSell_Delivery");
		param.put("delivery_id", delivery_id);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("adjustOutStore YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("adjustOutStore YJG_HSV2_JavaSell_Delivery result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("adjustOutStore YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("adjustOutStore YJG_HSV2_JavaSell_Delivery result is: "+result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("调整出库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("HMJ_BUV2_delivery_goods");
		param1.put("delivery_id", delivery_id);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("adjustOutStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("adjustOutStore HMJ_BUV2_delivery_goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("adjustOutStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("adjustOutStore HMJ_BUV2_delivery_goods result is: "+result2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("调整出库失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 1;
		
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String depot_id = mapList.get(0).get("depot_id");
			String out_sn = mapList.get(0).get("out_sn");
			String delivery_sn = mapList.get(0).get("delivery_sn");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_delivery_order");
			Map<String, Object> setMap = new HashMap<String, Object>();
			action.setType("U");
			setMap.put("audit_status", 3);
			setMap.put("status", 4);
			action.setSet(setMap);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("delivery_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(delivery_id);
			
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action.setWhere(where);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_delivery_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("delivery_id", delivery_id);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "调整出库审核通过");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_outdepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("out_sn", out_sn);
			setMap2.put("type", 5);
			setMap2.put("relation_id", delivery_id);
			setMap2.put("depot_id", depot_id);
			setMap2.put("status", 0);
			setMap2.put("carriage_way", 0);
			setMap2.put("servicer_id", 0);
			setMap2.put("user_id", user_id);
			setMap2.put("out_time", "$UnixTime");
			setMap2.put("add_time", "$UnixTime");
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String goods_id = mapList1.get(i).get("goods_id");
				String send_number = mapList1.get(i).get("send_number");
				String suppliers_id = mapList1.get(i).get("suppliers_id");
				if(StringUtils.isEmpty(send_number)){
					send_number = "0";
				}
				
				
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_outdepot_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("eo_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", goods_id);
				setMap3.put("suppliers_id", suppliers_id);
				setMap3.put("out_number", Integer.parseInt(send_number));
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("stgd_id", "$-1.generateKey");
				setMap4.put("receipt_sn", out_sn);
				setMap4.put("type", 1);
				setMap4.put("out_number", Integer.parseInt(send_number));
				setMap4.put("operation_time", "$UnixTime");
				setMap4.put("note", "调整出库");
				setMap4.put("user_id", user_id);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject1 = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject1.getJSONArray("rows");
				String obligate_number = "0";
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					obligate_number = jsonObject2.getString("obligate_number");
					action5.setType("U");
					setMap5.put("obligate_number", Integer.parseInt(obligate_number)-Integer.parseInt(send_number));
					action5.setSet(setMap5);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("depot_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(depot_id);
					
					SqlCondition sqlCondition2 = new SqlCondition();
					sqlCondition2.setKey("goods_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(goods_id);
					
					conditions1.add(sqlCondition2);
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action5.setWhere(where1);
				}else{
					action5.setType("C");
					setMap5.put("depot_id", depot_id);
					setMap5.put("goods_id", goods_id);
					setMap5.put("obligate_number", Integer.parseInt(send_number));
					action5.setSet(setMap5);
				}
				newActions.add(action5);
				
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_stock_goods_action");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				action6.setType("C");
				setMap6.put("depot_id", depot_id);
				setMap6.put("goods_id", goods_id);
				setMap6.put("business_sn", delivery_sn);
				setMap6.put("procedure_sn", out_sn);
				setMap6.put("before_number", obligate_number);
				setMap6.put("out_number", send_number);
				setMap6.put("obligate_number", Integer.parseInt(obligate_number)-Integer.parseInt(send_number));
				setMap6.put("add_time", "$UnixTime");
				action6.setSet(setMap6);
				newActions.add(action6);
				
				num = num+4;
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("adjustOutStore set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("adjustOutStore set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("adjustOutStore set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("adjustOutStore set  result is: "+str);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("调整出库失败！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("adjustOutStore YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("adjustOutStore YJG_HSV2_JavaSell_Delivery result is: "+result1);
			LOG.error("adjustOutStore HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("adjustOutStore HMJ_BUV2_delivery_goods result is: "+result2);
			LOG.error("adjustOutStore mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("调整出库失败！");
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 出库单发货
	 * @param map
	 * @return
	 */
	private String deliverGoods(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String delivery_id  = map.get("delivery_id").toString();
		String user_name = map.get("user_name").toString();
		String user_id = map.get("user_id").toString();
		String shipper_no  = map.get("shipper_no").toString();
		String carriage_way = map.get("carriage_way").toString();
		String servicer_id = map.get("servicer_id").toString();
		String freight_fee  = map.get("freight_fee").toString();
		String upstairs_fee = map.get("upstairs_fee").toString();
		String setup_fee = map.get("setup_fee").toString();
		String logistics_fee  = map.get("logistics_fee").toString();
		String type = map.get("type").toString();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV2_JavaSell_Delivery");
		param.put("delivery_id", delivery_id);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("出库单发货失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("HMJ_BUV2_delivery_goods");
		param1.put("delivery_id", delivery_id);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("deliverGoods HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("deliverGoods HMJ_BUV2_delivery_goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods result is: "+result2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("出库单发货失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 1;
		
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String depot_id = mapList.get(0).get("depot_id");
			String out_sn = mapList.get(0).get("out_sn");
			String shipping_status_2 = mapList.get(0).get("shipping_status_2");
			String order_id = mapList.get(0).get("order_id");
			String order_status_1 = mapList.get(0).get("order_status_1");
			String shipping_status_1 = mapList.get(0).get("shipping_status_1");
			String pay_status_1 = mapList.get(0).get("pay_status_1");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_delivery_order");
			Map<String, Object> setMap = new HashMap<String, Object>();
			action.setType("U");
			setMap.put("status", 2);
			action.setSet(setMap);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("delivery_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(delivery_id);
			
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action.setWhere(where);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_delivery_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("delivery_id", delivery_id);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "出库单发货");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_outdepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("out_sn", out_sn);
			setMap2.put("type", type);
			setMap2.put("relation_id", delivery_id);
			setMap2.put("depot_id", depot_id);
			setMap2.put("status", 0);
			setMap2.put("shipper_no", shipper_no);
			setMap2.put("carriage_way", carriage_way);
			setMap2.put("servicer_id", servicer_id);
			setMap2.put("freight_fee", freight_fee);
			setMap2.put("upstairs_fee", upstairs_fee);
			setMap2.put("setup_fee", setup_fee);
			setMap2.put("logistics_fee", logistics_fee);
			setMap2.put("user_id", user_id);
			setMap2.put("out_time", "$UnixTime");
			setMap2.put("add_time", "$UnixTime");
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String goods_id = mapList1.get(i).get("goods_id");
				String send_number = mapList1.get(i).get("send_number");
				String suppliers_id = mapList1.get(i).get("suppliers_id");
				String order_rec_id = mapList1.get(i).get("order_rec_id");
				if(StringUtils.isEmpty(send_number)){
					send_number = "0";
				}
				
				
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_outdepot_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("eo_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", goods_id);
				setMap3.put("suppliers_id", suppliers_id);
				setMap3.put("out_number", Integer.parseInt(send_number));
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("stgd_id", "$-1.generateKey");
				setMap4.put("receipt_sn", out_sn);
				setMap4.put("type", 1);
				setMap4.put("out_number", Integer.parseInt(send_number));
				setMap4.put("operation_time", "$UnixTime");
				setMap4.put("note", "出库单发货");
				setMap4.put("user_id", user_id);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject1 = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject1.getJSONArray("rows");
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					String obligate_number = jsonObject2.getString("obligate_number");
					action5.setType("U");
					setMap5.put("obligate_number", Integer.parseInt(obligate_number)-Integer.parseInt(send_number));
					action5.setSet(setMap5);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("depot_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(depot_id);
					
					SqlCondition sqlCondition2 = new SqlCondition();
					sqlCondition2.setKey("goods_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(goods_id);
					
					conditions1.add(sqlCondition2);
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action5.setWhere(where1);
				}else{
					action5.setType("C");
					setMap5.put("depot_id", depot_id);
					setMap5.put("goods_id", goods_id);
					setMap5.put("obligate_number", Integer.parseInt(send_number));
					action5.setSet(setMap5);
				}
				newActions.add(action5);
				
				if("1".equals(type)){
					Action action6 = new Action();
					action6.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap6 = new HashMap<String, Object>();
					action6.setType("U");
					setMap6.put("status", 1);
					action6.setSet(setMap6);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("rec_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(order_rec_id);
					
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action6.setWhere(where1);
					newActions.add(action6);
					
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("C");
					setMap7.put("order_id", order_id);
					setMap7.put("action_user", user_name);
					setMap7.put("order_status", order_status_1);
					setMap7.put("shipping_status", shipping_status_1);
					setMap7.put("pay_status", pay_status_1);
					setMap7.put("action_note", "修改了商品("+goods_id+")状态，状态改为已发货");
					setMap7.put("log_time", "$UnixTime");
					action7.setSet(setMap7);
					newActions.add(action7);
					
					num = num+5;
				}else{
					num = num+3;
				}
			}
			
			if("1".equals(type)){
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_order_info");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("U");
				setMap8.put("shipping_status", shipping_status_2);
				action8.setSet(setMap8);
				Where where2 = new Where();
				where2.setPrepend("and");
				List<SqlCondition> conditions2 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition2 = new SqlCondition();
				sqlCondition2.setKey("order_id");
				sqlCondition2.setOp("=");
				sqlCondition2.setValue(order_id);
				
				conditions2.add(sqlCondition2);
				where2.setConditions(conditions2);
				action8.setWhere(where2);
				newActions.add(action8);
				
				Action action9 = new Action();
				action9.setServiceName("test_ecshop_ecs_order_action");
				Map<String, Object> setMap9 = new HashMap<String, Object>();
				action9.setType("C");
				setMap9.put("order_id", order_id);
				setMap9.put("action_user", user_name);
				setMap9.put("order_status", order_status_1);
				setMap9.put("shipping_status", shipping_status_2);
				setMap9.put("pay_status", pay_status_1);
				setMap9.put("action_note", "修改了订单的状态");
				setMap9.put("log_time", "$UnixTime");
				action9.setSet(setMap9);
				newActions.add(action9);
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("deliverGoods set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("deliverGoods set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("deliverGoods set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("deliverGoods set  result is: "+str);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("出库单发货失败！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods result is: "+result2);
			LOG.error("deliverGoods mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("出库单发货失败！");
		return JSON.toJSONString(respInfo);
	}
	
	/**
	 * 扫描条形码出库与入库
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String scanBarCode(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		 // RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSON.toJSONString(respInfo);
        }
        
        String resultData = "";
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		JSONObject jsonObject = new JSONObject();
		
		JSONArray rows = new JSONArray();
		JSONObject obj = new JSONObject();
		
		if(null != paramMap.get("type")){
			String type = paramMap.get("type").toString();
			if("1".equals(type) || "2".equals(type) || "3".equals(type) || "106".equals(type)){
				String scanBarCodeOut = scanBarCodeOut(paramMap);
				JSONObject job = JSONObject.parseObject(scanBarCodeOut);
				if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
					return scanBarCodeOut;
			    }
				JSONArray array =  job.getJSONArray("results");
				String eo_id = array.getJSONObject(2).get("generateKey").toString();
				obj.put("eo_id", eo_id);
				rows.add(obj);
				jsonObject.put("rows", rows);
			}
		}else{
			String scanBarCodeIn = scanBarCodeIn(paramMap);
			JSONObject job = JSONObject.parseObject(scanBarCodeIn);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				return scanBarCodeIn;
		    }
		}
		
		jsonObject.put("code", DsResponseCodeData.SUCCESS.code);
		jsonObject.put("description", DsResponseCodeData.SUCCESS.description);
		resultData = jsonObject.toJSONString();
		return resultData;
		
	} 
	
	/**
	 * 扫描条形码出库
	 * @param map
	 * @return
	 */
	private String scanBarCodeOut(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String delivery_id  = map.get("delivery_id").toString();
		String user_name = map.get("user_name").toString();
		String user_id = map.get("user_id").toString();
		String shipper_no  = map.get("shipper_no").toString();
		String carriage_way = map.get("carriage_way").toString();
		String servicer_id = map.get("servicer_id").toString();
		String freight_fee  = map.get("freight_fee").toString();
		String upstairs_fee = map.get("upstairs_fee").toString();
		String setup_fee = map.get("setup_fee").toString();
		String logistics_fee  = map.get("logistics_fee").toString();
		String type = map.get("type").toString();
		String bar_id_str = map.get("bar_id_str").toString();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV2_JavaSell_Delivery");
		param.put("delivery_id", delivery_id);
		param.put("bar_id_str", bar_id_str);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("扫描条形码出库单查询失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("SCM_BUV1_JavaDelivery_Goods");
		param1.put("delivery_id", delivery_id);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("deliverGoods SCM_BUV1_JavaDelivery_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("deliverGoods SCM_BUV1_JavaDelivery_Goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("deliverGoods SCM_BUV1_JavaDelivery_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("deliverGoods SCM_BUV1_JavaDelivery_Goods result is: "+result2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("扫描条形码出库单商品查询失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 1;
		
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String depot_id = mapList.get(0).get("depot_id");
			String out_sn = mapList.get(0).get("out_sn");
			String shipping_status_2 = mapList.get(0).get("shipping_status_2");
			String order_id = mapList.get(0).get("order_id");
			String order_status_1 = mapList.get(0).get("order_status_1");
			String shipping_status_1 = mapList.get(0).get("shipping_status_1");
			String pay_status_1 = mapList.get(0).get("pay_status_1");
			String order_sn = mapList.get(0).get("order_sn");
			String delivery_sn = mapList.get(0).get("delivery_sn");
			String flag = mapList.get(0).get("flag");
			String prompt = mapList.get(0).get("prompt");
			if("0".equals(flag)){
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(prompt);
                return JSON.toJSONString(respInfo);
			}
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_delivery_order");
			Map<String, Object> setMap = new HashMap<String, Object>();
			action.setType("U");
			setMap.put("status", 2);
			action.setSet(setMap);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("delivery_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(delivery_id);
			
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action.setWhere(where);
			newActions.add(action);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_delivery_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("delivery_id", delivery_id);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "出库单发货");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_outdepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("out_sn", out_sn);
			setMap2.put("type", type);
			setMap2.put("relation_id", delivery_id);
			setMap2.put("depot_id", depot_id);
			setMap2.put("status", 0);
			setMap2.put("shipper_no", shipper_no);
			setMap2.put("carriage_way", carriage_way);
			setMap2.put("servicer_id", servicer_id);
			setMap2.put("freight_fee", freight_fee);
			setMap2.put("upstairs_fee", upstairs_fee);
			setMap2.put("setup_fee", setup_fee);
			setMap2.put("logistics_fee", logistics_fee);
			setMap2.put("user_id", user_id);
			setMap2.put("out_time", "$UnixTime");
			setMap2.put("add_time", "$UnixTime");
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String goods_id = mapList1.get(i).get("goods_id");
				String send_number = mapList1.get(i).get("send_number");
				String suppliers_id = mapList1.get(i).get("suppliers_id");
				String order_rec_id = mapList1.get(i).get("order_rec_id");
				String bom_id = mapList1.get(i).get("bom_id");
				if(StringUtils.isEmpty(send_number)){
					send_number= "0";
				}
				
				
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_outdepot_goods");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("eo_id", "$-"+num+".generateKey");
				setMap3.put("goods_id", goods_id);
				setMap3.put("suppliers_id", suppliers_id);
				setMap3.put("out_number", send_number);
				setMap3.put("bom_id", bom_id);
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("stgd_id", "$-1.generateKey");
				setMap4.put("receipt_sn", out_sn);
				setMap4.put("type", 1);
				setMap4.put("out_number", Integer.parseInt(send_number));
				setMap4.put("operation_time", "$UnixTime");
				setMap4.put("note", "出库单发货");
				setMap4.put("user_id", user_id);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", goods_id);
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject1 = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject1.getJSONArray("rows");
				String obligate_number = "0";
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					obligate_number = jsonObject2.getString("obligate_number");
					action5.setType("U");
					setMap5.put("obligate_number", Integer.parseInt(obligate_number)-Integer.parseInt(send_number));
					action5.setSet(setMap5);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("depot_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(depot_id);
					
					SqlCondition sqlCondition2 = new SqlCondition();
					sqlCondition2.setKey("goods_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(goods_id);
					
					conditions1.add(sqlCondition2);
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action5.setWhere(where1);
				}else{
					action5.setType("C");
					setMap5.put("depot_id", depot_id);
					setMap5.put("goods_id", goods_id);
					setMap5.put("obligate_number", Integer.parseInt(send_number));
					action5.setSet(setMap5);
				}
				newActions.add(action5);
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_stock_goods_action");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("C");
				setMap8.put("depot_id", depot_id);
				setMap8.put("goods_id", goods_id);
				setMap8.put("business_sn", delivery_sn);
				setMap8.put("procedure_sn", out_sn);
				setMap8.put("before_number", obligate_number);
				setMap8.put("out_number", send_number);
				setMap8.put("obligate_number", Integer.parseInt(obligate_number)-Integer.parseInt(send_number));
				setMap8.put("add_time", "$UnixTime");
				setMap8.put("bar_id", bar_id_str);
				action8.setSet(setMap8);
				newActions.add(action8);
				
				
				if("1".equals(type)){
					Action action6 = new Action();
					action6.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap6 = new HashMap<String, Object>();
					action6.setType("U");
					setMap6.put("status", 1);
					action6.setSet(setMap6);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("rec_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(order_rec_id);
					
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action6.setWhere(where1);
					newActions.add(action6);
					
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("C");
					setMap7.put("order_id", order_id);
					setMap7.put("action_user", user_name);
					setMap7.put("order_status", order_status_1);
					setMap7.put("shipping_status", shipping_status_1);
					setMap7.put("pay_status", pay_status_1);
					setMap7.put("action_note", "修改了商品("+goods_id+")状态，状态改为已发货");
					setMap7.put("log_time", "$UnixTime");
					action7.setSet(setMap7);
					newActions.add(action7);
					
					num = num+6;
				}else{
					num = num+4;
				}
			}

			String[] split = bar_id_str.split(",");
			for (int i = 0; i < split.length; i++) {
				Action action10 = new Action();
				action10.setServiceName("test_ecshop_ecs_barcode_track");
				Map<String, Object> setMap10 = new HashMap<String, Object>();
				action10.setType("C");
				setMap10.put("action_user", user_name);
				setMap10.put("action_time", "$UnixTime");
				setMap10.put("type", 2);
				setMap10.put("bill_id", delivery_id);
				setMap10.put("bar_id", split[i]);
				action10.setSet(setMap10);
				newActions.add(action10);
				
				Action action11 = new Action();
				action11.setServiceName("test_ecshop_ecs_barcode_info");
				Map<String, Object> setMap11 = new HashMap<String, Object>();
				action11.setType("U");
				setMap11.put("track_id", "$-1.generateKey");
				if("1".equals(type) || "6".equals(type)){
					setMap11.put("status", 3);
				}else{
					setMap11.put("status", 2);
				}
				setMap11.put("order_sn", order_sn);
				action11.setSet(setMap11);
				Where where3 = new Where();
				where3.setPrepend("and");
				List<SqlCondition> conditions3 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition3 = new SqlCondition();
				sqlCondition3.setKey("bar_id");
				sqlCondition3.setOp("=");
				sqlCondition3.setValue(split[i]);
				
				conditions3.add(sqlCondition3);
				where3.setConditions(conditions3);
				action11.setWhere(where3);
				newActions.add(action11);
			}
			
			if("1".equals(type)){
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_order_info");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("U");
				setMap8.put("shipping_status", shipping_status_2);
				action8.setSet(setMap8);
				Where where2 = new Where();
				where2.setPrepend("and");
				List<SqlCondition> conditions2 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition2 = new SqlCondition();
				sqlCondition2.setKey("order_id");
				sqlCondition2.setOp("=");
				sqlCondition2.setValue(order_id);
				
				conditions2.add(sqlCondition2);
				where2.setConditions(conditions2);
				action8.setWhere(where2);
				newActions.add(action8);
				
				Action action9 = new Action();
				action9.setServiceName("test_ecshop_ecs_order_action");
				Map<String, Object> setMap9 = new HashMap<String, Object>();
				action9.setType("C");
				setMap9.put("order_id", order_id);
				setMap9.put("action_user", user_name);
				setMap9.put("order_status", order_status_1);
				setMap9.put("shipping_status", shipping_status_2);
				setMap9.put("pay_status", pay_status_1);
				setMap9.put("action_note", "修改了订单的状态");
				setMap9.put("log_time", "$UnixTime");
				action9.setSet(setMap9);
				newActions.add(action9);
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("deliverGoods set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("deliverGoods set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("deliverGoods set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("deliverGoods set  result is: "+str);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("扫描条形码出库单写入失败！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("deliverGoods YJG_HSV2_JavaSell_Delivery result is: "+result1);
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("deliverGoods HMJ_BUV2_delivery_goods result is: "+result2);
			LOG.error("deliverGoods mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("扫描条形码出库单发货失败！");
		return JSON.toJSONString(respInfo);
	}
	
	/**
	 * 扫描条形码入库
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String scanBarCodeIn(Map<String,Object> map){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		
		String carry_id  = map.get("carry_id").toString();
		String user_name = map.get("user_name").toString();
		String user_id = map.get("user_id").toString();
		String status = map.get("status").toString();
		String bar_id_str = map.get("bar_id_str").toString();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("SCM_HSV1_JavaNotice");
		param.put("carry_id", carry_id);
		param.put("update_status", status);
		param.put("bar_id_str", bar_id_str);
		dsManageReqInfo.setParam(param);
		String result1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(result1, RuleServiceResponseData.class);
		LOG.info("allotInStore SCM_HSV1_JavaNotice param is: "+JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("allotInStore SCM_HSV1_JavaNotice result is: "+result1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("allotInStore SCM_HSV1_JavaNotice param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("allotInStore SCM_HSV1_JavaNotice result is: "+result1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("扫描条形码入库查询回写信息失败！！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("SCM_HSV2_JavaNotice_Goods");
		param1.put("carry_id", carry_id);
		param1.put("bar_id_str", bar_id_str);
		dsManageReqInfo1.setParam(param1);
		String result2 = dataAction.getData(dsManageReqInfo1, "");
		
		responseData = DataUtil.parse(result2, RuleServiceResponseData.class);
		LOG.info("allotInStore SCM_BUV2_JavaNotice_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("allotInStore SCM_BUV2_JavaNotice_Goods result is: "+result2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("allotInStore SCM_BUV2_JavaNotice_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("allotInStore SCM_BUV2_JavaNotice_Goods result is: "+result2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("扫描条形码入库单商品查询失败！！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData.getRows();
		int num = 1;
		if(mapList != null && mapList.size() > 0 && mapList1 != null && mapList1.size() > 0){
			String type = mapList.get(0).get("type");
			String delivery_id = mapList.get(0).get("delivery_id");
			String update_forecast = mapList.get(0).get("update_forecast");
			String forecast_id = mapList.get(0).get("forecast_id");
			String update_delivery = mapList.get(0).get("update_delivery");
			String depot_id = mapList.get(0).get("depot_id");
			String carry_sn = mapList.get(0).get("carry_sn");
			String in_sn = mapList.get(0).get("in_sn");
			JSONArray stockGoodsArray = JSONArray.parseArray(mapList.get(0).get("stock_goods_num"));
			String advice_id = mapList.get(0).get("advice_id");
			String advice_sn = mapList.get(0).get("advice_sn");
			String need_advice = mapList.get(0).get("need_advice");
			String advice_status = mapList.get(0).get("advice_status");
			
			
			String flag = mapList.get(0).get("flag");
			String prompt = mapList.get(0).get("prompt");
			if("0".equals(flag)){
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription(prompt);
                return JSON.toJSONString(respInfo);
			}
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_send_notice");
			Map<String, Object> setMap = new HashMap<String, Object>();
			action.setType("U");
			setMap.put("status", status);
			action.setSet(setMap);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("carry_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(carry_id);
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action.setWhere(where);
			newActions.add(action);
			
			Action action9 = new Action();
			action9.setServiceName("test_ecshop_ecs_advice_info");
			Map<String, Object> setMap9 = new HashMap<String, Object>();
			action9.setType("U");
			if("3".equals(advice_status)){
				setMap9.put("status",3);
				setMap9.put("finish_way",1);
			}else if("4".equals(advice_status)){
				setMap9.put("status",4);
			}
			action9.setSet(setMap9);
			Where where9 = new Where();
			where9.setPrepend("and");
			List<SqlCondition> conditions9 = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition9 = new SqlCondition();
			sqlCondition9.setKey("advice_id");
			sqlCondition9.setOp("=");
			sqlCondition9.setValue(advice_id);
			conditions9.add(sqlCondition9);
			where9.setConditions(conditions9);
			action9.setWhere(where9);
			newActions.add(action9);
			
			Action action10 = new Action();
			action10.setServiceName("test_ecshop_ecs_supply_log");
			Map<String, Object> setMap10 = new HashMap<String, Object>();
			action10.setType("C");
			setMap10.put("receipt_sn", advice_sn);
			setMap10.put("type", 6);
			setMap10.put("action_user", user_name);
			setMap10.put("action_time", "$UnixTime");
			if("3".equals(advice_status)){
				setMap10.put("action_content", "通过扫码入库修改通知单状态为完成");
			}else if("4".equals(advice_status)){
				setMap10.put("action_content", "通过扫码入库修改通知单状态为部分完成");
			}
			action10.setSet(setMap10);
			newActions.add(action10);
			
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_supply_log");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("receipt_sn", carry_sn);
			setMap1.put("type", 1);
			setMap1.put("action_user", user_name);
			setMap1.put("action_time", "$UnixTime");
			setMap1.put("action_content", "商品扫码入库");
			action1.setSet(setMap1);
			newActions.add(action1);
			
			Action action2 = new Action();
			action2.setServiceName("test_ecshop_ecs_indepot");
			Map<String, Object> setMap2 = new HashMap<String, Object>();
			action2.setType("C");
			setMap2.put("in_sn", in_sn);
			setMap2.put("type", type);
			setMap2.put("carry_id", carry_id);
			setMap2.put("depot_id", depot_id);
			setMap2.put("in_time", "$UnixTime");
			setMap2.put("status", 1);
			setMap2.put("add_time", "$UnixTime");
			setMap2.put("user_id", user_id);
			action2.setSet(setMap2);
			newActions.add(action2);
			
			for (int i = 0; i < mapList1.size(); i++) {
				String goods_id = mapList1.get(i).get("goods_id");
				String send_number = mapList1.get(i).get("send_number");
				String bom_id = mapList1.get(i).get("bom_id");
				String eag_rec_id = mapList1.get(i).get("eag_rec_id");
				String eng_id = mapList1.get(i).get("eng_id");
				if(StringUtils.isEmpty(send_number)){
					send_number = "0";
				}
				
				
				Action action4 = new Action();
				action4.setServiceName("test_ecshop_ecs_indepot_goods");
				Map<String, Object> setMap4 = new HashMap<String, Object>();
				action4.setType("C");
				setMap4.put("ei_id", "$-"+num+".generateKey");
				setMap4.put("in_number", send_number);
				setMap4.put("goods_id", goods_id);
				setMap4.put("status", 0);
				setMap4.put("bom_id", bom_id);
				setMap4.put("eng_id", eng_id);
				action4.setSet(setMap4);
				newActions.add(action4);
				
				Action action5 = new Action();
				action5.setServiceName("test_ecshop_ecs_stock_action");
				Map<String, Object> setMap5 = new HashMap<String, Object>();
				action5.setType("C");
				setMap5.put("stgd_id", "$-1.generateKey");
				setMap5.put("receipt_sn", carry_sn);
				setMap5.put("type", 0);
				setMap5.put("in_number", send_number);
				setMap5.put("operation_time", "$UnixTime");
				setMap5.put("note", "商品扫码入库");
				setMap5.put("user_id", user_id);
				action5.setSet(setMap5);
				newActions.add(action5);
				
				if("1".equals(need_advice)){
					Tool tool = new Tool();
					Map<String,Object> param2 = new HashMap<String, Object>();
					param2.put("eag_rec_id", eag_rec_id);
					List<Map<String,String>> mapList3 = null;
					try {
						mapList3 = (List<Map<String, String>>) tool.getRuleResult("SCM_BUV1_AdviceGoodsInfo", param2, LOG, "SupplyChainAction scanBarCodeIn", "SCM_BUV1_AdviceGoodsInfo");
					} catch (Exception e) {
						LOG.error("SupplyChainAction scanBarCodeIn param is: "+ JSONObject.toJSONString(param2));
						respInfo.setCode(DsResponseCodeData.ERROR.code);
						respInfo.setDescription("查询通知单商品的入库数量错误！");
						return JSON.toJSONString(respInfo);
					}
					String indepot_number = mapList3.get(0).get("indepot_number");
					if(StringUtils.isEmpty(indepot_number)){
						indepot_number = "0";
					}
					
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_advice_goods");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("U");
					setMap7.put("indepot_number",Integer.parseInt(indepot_number)+Integer.parseInt(send_number));
					action7.setSet(setMap7);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("eag_rec_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(eag_rec_id);
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action7.setWhere(where1);
					newActions.add(action7);
					num = num+1;
				}
				
				num = num+2;
			}
			
			for (int y = 0; y < stockGoodsArray.size(); y++) {
				JSONObject stock_goods_num = stockGoodsArray.getJSONObject(y);
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_stock_goods");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				Map<String, Object> param2 = new HashMap<String, Object>();
				
				dsManageReqInfo2.setNeedAll("1");
				dsManageReqInfo2.setServiceName("HMJ_BUV1_stockgoods2");
				param2.put("goods_id", stock_goods_num.get("goods_id"));
				param2.put("depot_id", depot_id);
				dsManageReqInfo2.setParam(param2);
				String result3 = dataAction.getData(dsManageReqInfo2, "");
				JSONObject jsonObject1 = JSONObject.parseObject(result3);
				JSONArray jsonArray = jsonObject1.getJSONArray("rows");
				String obligate_number = "0";
				if(jsonArray != null && jsonArray.size()>0){
					JSONObject jsonObject2 = jsonArray.getJSONObject(0);
					obligate_number = jsonObject2.getString("obligate_number");
					action6.setType("U");
					setMap6.put("obligate_number", Integer.parseInt(obligate_number)+Integer.parseInt(stock_goods_num.get("num_total").toString()));
					action6.setSet(setMap6);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("depot_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(depot_id);
					
					SqlCondition sqlCondition2 = new SqlCondition();
					sqlCondition2.setKey("goods_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(stock_goods_num.get("goods_id"));
					
					conditions1.add(sqlCondition2);
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action6.setWhere(where1);
				}else{
					action6.setType("C");
					setMap6.put("depot_id", depot_id);
					setMap6.put("goods_id", stock_goods_num.get("goods_id"));
					setMap6.put("obligate_number", stock_goods_num.get("num_total").toString());
					setMap6.put("add_time", "$UnixTime");
					action6.setSet(setMap6);
				}
				newActions.add(action6);
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_stock_goods_action");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("C");
				setMap8.put("depot_id", depot_id);
				setMap8.put("goods_id", stock_goods_num.get("goods_id"));
				setMap8.put("business_sn", carry_sn);
				setMap8.put("procedure_sn", in_sn);
				setMap8.put("before_number", obligate_number);
				setMap8.put("in_number", stock_goods_num.get("num_total").toString());
				setMap8.put("obligate_number", Integer.parseInt(obligate_number)+Integer.parseInt(stock_goods_num.get("num_total").toString()));
				setMap8.put("add_time", "$UnixTime");
				setMap8.put("bar_id", bar_id_str);
				action8.setSet(setMap8);
				newActions.add(action8);
			}
			
			String[] split = bar_id_str.split(",");
			for (int i = 0; i < split.length; i++) {
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_barcode_track");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("action_user", user_name);
				setMap3.put("action_time", "$UnixTime");
				setMap3.put("type", 1);
				setMap3.put("bill_id", carry_id);
				setMap3.put("bar_id", split[i]);
				action3.setSet(setMap3);
				newActions.add(action3);
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_barcode_info");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("U");
				setMap8.put("status", 1);
				setMap8.put("depot_id", depot_id);
				setMap8.put("track_id", "$-1.generateKey");
				action8.setSet(setMap8);
				Where where1 = new Where();
				where1.setPrepend("and");
				List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition1 = new SqlCondition();
				sqlCondition1.setKey("bar_id");
				sqlCondition1.setOp("=");
				sqlCondition1.setValue(split[i]);
				
				conditions1.add(sqlCondition1);
				where1.setConditions(conditions1);
				action8.setWhere(where1);
				newActions.add(action8);
			}
			
			if("1".equals(update_delivery)){
				Action action7 = new Action();
				action7.setServiceName("test_ecshop_ecs_delivery_order");
				Map<String, Object> setMap7 = new HashMap<String, Object>();
				action7.setType("U");
				setMap7.put("status", 4);
				setMap7.put("update_time", "$UnixTime");
				action7.setSet(setMap7);
				Where where1 = new Where();
				where1.setPrepend("and");
				List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition1 = new SqlCondition();
				sqlCondition1.setKey("delivery_id");
				sqlCondition1.setOp("=");
				sqlCondition1.setValue(delivery_id);
				
				conditions1.add(sqlCondition1);
				where1.setConditions(conditions1);
				action7.setWhere(where1);
				newActions.add(action7);
			}
			if("1".equals(update_forecast)){
				String[] split2 = forecast_id.split(",");
				for (int j = 0; j < split2.length; j++) {
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_sales_forecast");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("U");
					setMap7.put("status", 2);
					action7.setSet(setMap7);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("forecast_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(split2[j]);
					
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action7.setWhere(where1);
					newActions.add(action7);
				}
				
			}
			
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			String str = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(str);
			LOG.info("allotInStore set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("allotInStore set result is: "+str);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("allotInStore set  param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("allotInStore set  result is: "+str);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("扫描条形码入库写入失败！！");
                return JSON.toJSONString(respInfo);
		    }
			
			return str;
			
		}else{
			LOG.error("allotInStore SCM_HSV1_JavaNotice param is: "+JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("allotInStore SCM_HSV1_JavaNotice result is: "+result1);
			LOG.error("allotInStore SCM_BUV2_JavaNotice_Goods param is: "+JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("allotInStore SCM_BUV2_JavaNotice_Goods result is: "+result2);
			LOG.error("allotInStore mapList length is: "+mapList.size()+" and mapList1 length is: "+mapList1.size());
		}
		respInfo.setCode(DsResponseCodeData.ERROR.code);
		respInfo.setDescription("扫描条形码入库单失败！！");
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 获取全局事务号
	 * @param serviceName 服务名
	 * @return 返回全局事务号
	 */
	private String getTransactionNum(String serviceName){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		dsManageReqInfo.setFormat("json");;
		dsManageReqInfo.setServiceName("MUSH_Start");
		param.put("serviceName", serviceName);
		param.put("transactionTimeout", 10);
		dsManageReqInfo.setParam(param);
		String result = mushroomAction.start(dsManageReqInfo);
		JSONObject jsonObject = JSONObject.parseObject(result);
		return jsonObject.getString("transactionNum").toString();
		
	}
	
	/**
	 * 提交全局事务
	 * @param transactionNum 全局事务号
	 * @return
	 */
	private String commitTransactionNum(String transactionNum){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		dsManageReqInfo.setFormat("json");;
		dsManageReqInfo.setServiceName("MUSH_Commit");
		param.put("transactionNum", transactionNum);
		dsManageReqInfo.setParam(param);
		String result = mushroomAction.commit(dsManageReqInfo);
		return result;
		
	}
	
	/**
	 * 回滚全局事务
	 * @param transactionNum 全局事务号
	 * @return
	 */
	private String rollBackTransactionNum(String transactionNum){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		dsManageReqInfo.setFormat("json");;
		dsManageReqInfo.setServiceName("MUSH_Rollback");
		param.put("transactionNum", transactionNum);
		dsManageReqInfo.setParam(param);
		String result = mushroomAction.commit(dsManageReqInfo);
		return result;
		
	}
}
