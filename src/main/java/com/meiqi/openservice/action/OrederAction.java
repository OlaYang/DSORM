package com.meiqi.openservice.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meilele.datalayer.common.utils.HttpExecutor;
import com.meiqi.app.common.config.ConfigFileUtil;
import com.meiqi.app.common.utils.CodeUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.ListUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.RsaKeyTools;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * 订单相关
 * @author 作者 xubao 
 * @version 创建时间：2015年12月1日 下午3:57:11 
 * 类说明 
 */
@Service
public class OrederAction extends BaseAction{

	private static final Log LOG =  LogFactory.getLog("order");
	
	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private IMushroomAction mushroomAction;
	
	/**
	 * 生成订单
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String createOrder(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		String result = "";
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		RuleServiceResponseData responseData = null;
		List<Action> newActions=new ArrayList<Action>();
		ResponseInfo respInfo = new ResponseInfo();
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList1= new ArrayList<Map<String,String>>();
		List<Map<String,String>> mapList2= new ArrayList<Map<String,String>>();
		//得到前台传递的请求参数
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		//将执行规则的参数封装成对象
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		JSONObject recSuitJson = (JSONObject) paramMap.get("rec_suit_id");
		JSONObject recNumJson = (JSONObject) paramMap.get("rec_num_id");
		JSONObject recDetailJson = (JSONObject) paramMap.get("rec_detail_id");
		String user_id = (String) paramMap.get("user_id");
		String detail_id = (String) paramMap.get("detail_id");
		String code = "";
		if(!StringUtils.isEmpty((String)paramMap.get("code"))){
			code = (String)paramMap.get("code");
		}
		String bonus_id = "";
		if(!StringUtils.isEmpty((String) paramMap.get("bonus_id"))){
			bonus_id = (String) paramMap.get("bonus_id");
		}
		String sequence_sn = "";
		if(!StringUtils.isEmpty((String) paramMap.get("sequence_sn"))){
			sequence_sn = (String) paramMap.get("sequence_sn");
		}
		String address_id = (String) paramMap.get("address_id");
		String order_amount = (String) paramMap.get("order_amount");
		JSONObject rec_shipping_name = (JSONObject) paramMap.get("rec_shipping_name");
		JSONObject rec_depot_id = (JSONObject) paramMap.get("rec_depot_id");
		JSONObject rec_depot_area = (JSONObject) paramMap.get("rec_depot_area");
		String best_time = "";
		if(!StringUtils.isEmpty((String) paramMap.get("best_time"))){
			best_time = (String) paramMap.get("best_time");
		}
		String order_note = "";
		if(!StringUtils.isEmpty((String) paramMap.get("order_note"))){
			order_note = (String) paramMap.get("order_note");
		}
		String order_source = (String) paramMap.get("order_source");
		String rec_id = (String) paramMap.get("rec_id");
		String is_to_store = (String)paramMap.get("is_to_store");
		String site_id = paramMap.get("site_id") == null?"0":(String)paramMap.get("site_id");
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_CreatOrder");
		param.put("rec_suit_id", DataUtil.parse(recSuitJson.toJSONString(),Map.class));
		param.put("rec_num_id", DataUtil.parse(recNumJson.toJSONString(),Map.class));
		param.put("rec_detail_id", DataUtil.parse(recDetailJson.toJSONString(),Map.class));
		param.put("user_id", user_id);
		param.put("detail_id", detail_id);
		param.put("code", code);
		param.put("bonus_id", bonus_id);
		param.put("sequence_sn", sequence_sn);
		param.put("address_id", address_id);
		param.put("order_amount", order_amount);
		param.put("rec_shipping_name", DataUtil.parse(rec_shipping_name.toJSONString(),Map.class));
		param.put("best_time", best_time);
		param.put("order_note", order_note);
		param.put("order_source", order_source);
		param.put("rec_id", rec_id);
		param.put("site_id", site_id);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		LOG.info("OrederAction YJG_HSV1_CreatOrder param is: "+ JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("OrederAction YJG_HSV1_CreatOrder result is: "+ resultData);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("OrederAction YJG_HSV1_CreatOrder param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction YJG_HSV1_CreatOrder result is: "+ resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单回写失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		RuleServiceResponseData responseData1 = null;
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("HMJ_BUV1_USER_ADDRESS");
		param1.put("address_id", address_id);
		param1.put("site_id", site_id);
		dsManageReqInfo1.setParam(param1);
		String resultData1 = dataAction.getData(dsManageReqInfo1, "");
		responseData1 = DataUtil.parse(resultData1, RuleServiceResponseData.class);
		LOG.info("OrederAction HMJ_BUV1_USER_ADDRESS param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("OrederAction HMJ_BUV1_USER_ADDRESS result is: "+ resultData1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData1.getCode())) {
				LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
				LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS result is: "+ resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询用户地址失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList1 = responseData1.getRows();
		
		
		RuleServiceResponseData responseData2 = null;
		DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
		Map<String, Object> param2 = new HashMap<String, Object>();
		dsManageReqInfo2.setNeedAll("1");
		dsManageReqInfo2.setServiceName("YJG_HSV1_CreatOrderRcevieGoods");
		param2.put("rec_suit_id", DataUtil.parse(recSuitJson.toJSONString(),Map.class));
		param2.put("rec_num_id", DataUtil.parse(recNumJson.toJSONString(),Map.class));
		param2.put("rec_detail_id", DataUtil.parse(recDetailJson.toJSONString(),Map.class));
		param2.put("rec_shipping_name", DataUtil.parse(rec_shipping_name.toJSONString(),Map.class));
		param2.put("rec_depot_id", DataUtil.parse(rec_depot_id==null?"":rec_depot_id.toJSONString(),Map.class));
		param2.put("rec_depot_area", DataUtil.parse(rec_depot_area==null?"":rec_depot_area.toJSONString(),Map.class));
		param2.put("rec_id", rec_id);
		param2.put("user_id", user_id);
		param2.put("site_id", site_id);
		dsManageReqInfo2.setParam(param2);
		String resultData2 = dataAction.getData(dsManageReqInfo2, "");
		responseData2 = DataUtil.parse(resultData2, RuleServiceResponseData.class);
		LOG.info("OrederAction YJG_HSV1_CreatOrderRcevieGoods param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
		LOG.info("OrederAction YJG_HSV1_CreatOrderRcevieGoods result is: "+ resultData2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData2.getCode())) {
				LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
				LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods result is: "+ resultData2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单商品失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList2 = responseData2.getRows();
		String order_sn = "";
		if(null != mapList && mapList.size() > 0 && null != mapList1 && mapList1.size() > 0 && null != mapList2 && mapList2.size() > 0){
			String is_flag = mapList.get(0).get("is_flag");
			int back_user_id = Integer.parseInt(mapList.get(0).get("back_user_id"));
			double order_discount = Double.valueOf(mapList.get(0).get("order_discount"));
			double bonus_discount = Double.valueOf(mapList.get(0).get("bonus_discount"));
			double code_discount = Double.valueOf(mapList.get(0).get("code_discount"));
			int bonus_id1 = Integer.parseInt(mapList.get(0).get("bonus_id"));
			int use_user_id = Integer.parseInt(mapList.get(0).get("use_user_id"));
			int u_bonus_id = Integer.parseInt(mapList.get(0).get("u_bonus_id"));
			if("1".equals(is_flag)){
				String goods_amount = mapList.get(0).get("goods_amount");
				String fee_amount = mapList.get(0).get("fee_amount");
				String order_amount1 = mapList.get(0).get("order_amount");
				String total_discount = mapList.get(0).get("total_discount");
				String role_id = mapList.get(0).get("role_id");
				String department = mapList.get(0).get("department");
				String best_time_1 = mapList.get(0).get("best_time_1");
				String order_source1 = mapList.get(0).get("order_source");
				String order_note1 = mapList.get(0).get("order_note");
				String is_first = mapList.get(0).get("is_first");
				String shop_rec_id = mapList.get(0).get("shop_rec_id");
				String order_type = mapList.get(0).get("order_type");
				String commodity_discount = mapList.get(0).get("commodity_discount");
				
				String country = mapList1.get(0).get("country");
				String province = mapList1.get(0).get("province");
				String city = mapList1.get(0).get("city");
				String district = mapList1.get(0).get("district");
				String address = mapList1.get(0).get("address");
				String zipcode = mapList1.get(0).get("zipcode");
				String tel = mapList1.get(0).get("tel");
				String consignee = mapList1.get(0).get("consignee");
				String email = mapList1.get(0).get("email");
				String mobile = mapList1.get(0).get("mobile");
				String sign_building = mapList1.get(0).get("sign_building");
				
				int num = 1;
				order_sn = getNumber();
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_order_info");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("C");
				setMap.put("order_sn", order_sn);
				setMap.put("user_id", user_id);
				setMap.put("order_status", 0);
				setMap.put("shipping_status", 0);
				setMap.put("pay_status", 0);
				setMap.put("consignee", consignee);
				setMap.put("country", country);
				setMap.put("province", province);
				setMap.put("city", city);
				setMap.put("district", district);
				setMap.put("address", address);
				setMap.put("zipcode", zipcode);
				setMap.put("tel", tel);
				setMap.put("mobile", mobile);
				setMap.put("email", email);
				setMap.put("best_time", best_time_1);
				setMap.put("sign_building", sign_building);
				setMap.put("pay_note", order_note1);
				setMap.put("goods_amount", goods_amount);
				setMap.put("shipping_fee", fee_amount);
				setMap.put("order_amount", order_amount1);
				setMap.put("order_source", order_source1);
				setMap.put("add_time", "$UnixTime");
				setMap.put("preferent", total_discount);
				setMap.put("department_id", department);
				setMap.put("order_type", order_type);
				setMap.put("commodity_discount", commodity_discount);
				setMap.put("is_to_store", is_to_store);
				action.setSet(setMap);
				newActions.add(action);
				
				if(back_user_id > 0 && Integer.parseInt(user_id) > 0){
					Action action3 = new Action();
					action3.setServiceName("test_ecshop_ecs_order_belong");
					Map<String, Object> setMap3 = new HashMap<String, Object>();
					action3.setType("C");
					setMap3.put("order_id", "$-1.generateKey");
					setMap3.put("user_id", user_id);
					setMap3.put("role_id", role_id);
					setMap3.put("is_first", is_first);
					setMap3.put("back_user_id", back_user_id);
					action3.setSet(setMap3);
					newActions.add(action3);
					num = num+1;
				}
				
				if(order_discount > 0 || bonus_discount > 0 || code_discount > 0 || bonus_id1 > 0 ){
					Action action4 = new Action();
					action4.setServiceName("test_ecshop_ecs_order_discount");
					Map<String, Object> setMap4 = new HashMap<String, Object>();
					action4.setType("C");
					setMap4.put("order_id", "$-"+num+".generateKey");
					setMap4.put("discount", code_discount);
					setMap4.put("order_discount", order_discount);
					setMap4.put("bonus_discount", bonus_discount);
					setMap4.put("bonus_id", bonus_id1);
					action4.setSet(setMap4);
					newActions.add(action4);
					num = num+1;
				}
				
				if(!StringUtils.isEmpty(sequence_sn) && bonus_discount > 0){
					Action action5 = new Action();
					action5.setServiceName("test_ecshop_ecs_bonus_sequence");
					Map<String, Object> setMap5 = new HashMap<String, Object>();
					action5.setType("U");
					setMap5.put("order_id", "$-"+num+".generateKey");
					setMap5.put("user_id", user_id);
					setMap5.put("bonus_status", 1);
					setMap5.put("user_time", "$UnixTime");
					setMap5.put("use_money", bonus_discount);
					action5.setSet(setMap5);
					Where where = new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition = new SqlCondition();
					sqlCondition.setKey("sequence_sn");
					sqlCondition.setOp("=");
					sqlCondition.setValue(sequence_sn);;
					conditions.add(sqlCondition);
					where.setConditions(conditions);
					action5.setWhere(where);
					newActions.add(action5);
					num = num+1;
					new UpdateWeixinBonusStatus(sequence_sn).start();
				}
				
				if(use_user_id > 0 && u_bonus_id > 0){
					Action action6 = new Action();
					action6.setServiceName("test_ecshop_ecs_bonus_sequence");
					Map<String, Object> setMap6 = new HashMap<String, Object>();
					action6.setType("C");
					setMap6.put("order_id", "$-"+num+".generateKey");
					setMap6.put("bonus_id", bonus_id1);
					setMap6.put("sequence_sn", CodeUtils.getBonusCode());
					setMap6.put("create_time", "$UnixTime");
					setMap6.put("get_time", "$UnixTime");
					setMap6.put("get_id", "use_user_id");
					setMap6.put("sequence_type", 1);
					setMap6.put("user_id", user_id);
					setMap6.put("bonus_status", 1);
					setMap6.put("user_time", "$UnixTime");
					action6.setSet(setMap6);
					newActions.add(action6);
					num = num+1;
				}
				
				Action action7 = new Action();
				action7.setServiceName("test_ecshop_ecs_order_action");
				Map<String, Object> setMap7 = new HashMap<String, Object>();
				action7.setType("C");
				setMap7.put("order_id", "$-"+num+".generateKey");
				setMap7.put("action_user", "admin");
				setMap7.put("order_status", 0);
				setMap7.put("shipping_status", 0);
				setMap7.put("pay_status", 0);
				setMap7.put("action_note", "订单已提交，请尽快付款");
				setMap7.put("log_time", "$UnixTime");
				action7.setSet(setMap7);
				newActions.add(action7);
				num = num + 1;
				for (int i = 0; i < mapList2.size(); i++) {
					String goods_id1 = mapList2.get(i).get("goods_id");
					String goods_sn = mapList2.get(i).get("goods_sn");
					String goods_name = mapList2.get(i).get("goods_name");
					String shop_price_1 = mapList2.get(i).get("shop_price_1");
					String shop_id = mapList2.get(i).get("shop_id");
					String commodity_price_1 = mapList2.get(i).get("commodity_price_1");
					String effect_price = mapList2.get(i).get("effect_price");
					String privilege = mapList2.get(i).get("privilege");
					int act_id = Integer.parseInt(mapList2.get(i).get("act_id"));
					String fist_unit = mapList2.get(i).get("fist_unit");
					String fist_fee = mapList2.get(i).get("fist_fee");
					String extend_unit = mapList2.get(i).get("extend_unit");
					String extend_fee = mapList2.get(i).get("extend_fee");
					String lowest_fee = mapList2.get(i).get("lowest_fee");
					String amount_percent = mapList2.get(i).get("amount_percent");
					String suit_number = mapList2.get(i).get("suit_number");
					String shipping_name = mapList2.get(i).get("shipping_name");
					String detail_id1 = mapList2.get(i).get("detail_id");
					String suit_id = mapList2.get(i).get("suit_id");
					String goods_num = mapList2.get(i).get("goods_num");
					String goods_volume = mapList2.get(i).get("goods_volume");
					String goods_weight = mapList2.get(i).get("goods_weight");
					String update_act_num = mapList2.get(i).get("update_act_num");
					String template_id = mapList2.get(i).get("template_id") == null?"":mapList2.get(i).get("template_id");
					String freight_type = mapList2.get(i).get("freight_type") == null?"":mapList2.get(i).get("freight_type");
					String depot_id = mapList2.get(i).get("depot_id") == null?"":mapList2.get(i).get("depot_id");
					String depot_area = mapList2.get(i).get("depot_area") == null?"":mapList2.get(i).get("depot_area");

					Action action1 = new Action();
					action1.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap1 = new HashMap<String, Object>();
					action1.setType("C");
					setMap1.put("order_id", "$-"+num+".generateKey");
					setMap1.put("goods_id", goods_id1);
					setMap1.put("goods_name", goods_name);
					setMap1.put("goods_sn", goods_sn);
					setMap1.put("goods_number", goods_num);
					setMap1.put("goods_price", effect_price);
					setMap1.put("suit_id", suit_id);
					setMap1.put("shipping_name", shipping_name);
					setMap1.put("detail_id", detail_id1);
					setMap1.put("act_id", act_id);
					setMap1.put("discount", privilege);
					setMap1.put("shop_price", shop_price_1);
					setMap1.put("shop_id", shop_id);
					setMap1.put("suit_number", suit_number);
					setMap1.put("trade_price", commodity_price_1);
					setMap1.put("goods_volume", goods_volume);
					setMap1.put("goods_weight", goods_weight);
					setMap1.put("depot_id", depot_id);
					setMap1.put("depot_area", depot_area);
					action1.setSet(setMap1);
					newActions.add(action1);
					
					Action action2 = new Action();
					action2.setServiceName("test_ecshop_ecs_order_goods_detail");
					Map<String, Object> setMap2 = new HashMap<String, Object>();
					action2.setType("C");
					setMap2.put("rec_id", "$-1.generateKey");
					setMap2.put("fist_unit", fist_unit);
					setMap2.put("fist_fee", fist_fee);
					setMap2.put("extend_unit", extend_unit);
					setMap2.put("extend_fee", extend_fee);
					setMap2.put("amount_percent", amount_percent);
					setMap2.put("lowest_fee", lowest_fee);
					action2.setSet(setMap2);
					setMap2.put("template_id", template_id);
					setMap2.put("freight_type", freight_type);
					newActions.add(action2);
					
					if(act_id > 0){
						Action action9 = new Action();
						action9.setServiceName("test_ecshop_ecs_activity_goods");
						Map<String, Object> setMap9 = new HashMap<String, Object>();
						action9.setType("U");
						setMap9.put("sale_number", update_act_num);
						action9.setSet(setMap9);
						Where where9 = new Where();
						where9.setPrepend("and");
						List<SqlCondition> conditions9 = new ArrayList<SqlCondition>();
						SqlCondition sqlCondition = new SqlCondition();
						sqlCondition.setKey("act_id");
						sqlCondition.setOp("=");
						sqlCondition.setValue(act_id);
						SqlCondition sqlCondition1 = new SqlCondition();
						sqlCondition1.setKey("goods_id");
						sqlCondition1.setOp("=");
						sqlCondition1.setValue(goods_id1);

						conditions9.add(sqlCondition);
						conditions9.add(sqlCondition1);
						where9.setConditions(conditions9);
						action9.setWhere(where9);
						newActions.add(action9);
						num = num+1;
					}
					num = num + 2;
				}
				
				Action action8 = new Action();
				action8.setType("D");
				action8.setServiceName("test_ecshop_ecs_cart");
				Where where1 = new Where();
				where1.setPrepend("and");
				List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition1 = new SqlCondition();
				sqlCondition1.setKey("rec_id");
				sqlCondition1.setOp("in");
				sqlCondition1.setValue(shop_rec_id.split(","));;
				conditions1.add(sqlCondition1);
				where1.setConditions(conditions1);
				action8.setWhere(where1);
				newActions.add(action8);
				
				paramMap1.put("actions", newActions);
				paramMap1.put("transaction",1);
				DsManageReqInfo dsReqInfo = new DsManageReqInfo();
				dsReqInfo.setServiceName("MUSH_Offer");
				dsReqInfo.setParam(paramMap1);
				result = mushroomAction.offer(dsReqInfo);
				JSONObject job = JSONObject.parseObject(result);
				LOG.info("OrederAction set param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.info("OrederAction set result is: "+result);
				if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
					LOG.error("OrederAction set  param is: "+JSONObject.toJSONString(dsReqInfo));
					LOG.error("OrederAction set  result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
	                respInfo.setDescription("写入数据失败！");
	                return JSON.toJSONString(respInfo);
			    }
			}else{
				LOG.error("OrederAction YJG_HSV1_CreatOrder is_flag is: "+ is_flag);
				LOG.error("OrederAction YJG_HSV1_CreatOrder param is: "+ JSONObject.toJSONString(dsManageReqInfo));
				LOG.error("OrederAction YJG_HSV1_CreatOrder result is: "+ resultData);
				LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
				LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS result is: "+ resultData1);
				LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
				LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods result is: "+ resultData2);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("不能生成订单！");
				return JSON.toJSONString(respInfo);
			}
		}else{
			LOG.error("OrederAction YJG_HSV1_CreatOrder param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction YJG_HSV1_CreatOrder result is: "+ resultData);
			LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("OrederAction HMJ_BUV1_USER_ADDRESS result is: "+ resultData1);
			LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.error("OrederAction YJG_HSV1_CreatOrderRcevieGoods result is: "+ resultData2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("生成订单失败！");
			return JSON.toJSONString(respInfo);
		}
		JSONObject jsonObject = new JSONObject();
		JSONArray rows = new JSONArray();
		JSONObject obj = new JSONObject();
		JSONObject job = JSONObject.parseObject(result);
		JSONArray array =  job.getJSONArray("results");
		String order_id = array.getJSONObject(0).get("generateKey").toString();
		obj.put("order_id", order_id);
		obj.put("order_sn", order_sn);
		rows.add(obj);
		jsonObject.put("rows", rows);
		jsonObject.put("code", "0");
		jsonObject.put("description", "成功");
		return jsonObject.toJSONString();
		
		
	}
	
	/**
	 * 生成发货通知单
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String createDeliverGoods(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		
		/*// RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSON.toJSONString(respInfo);
        }else{
            RsaKeyTools.doRSA(request, response,repInfo);
        }*/
		
		String result = "";
		List<Action> newActions=new ArrayList<Action>();
		ResponseInfo respInfo = new ResponseInfo();
		
		Map<String,String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		String order_id = paramMap.get("order_id");
		String user_name = paramMap.get("user_name");
		String user_id = paramMap.get("user_id");
		String site_id = paramMap.get("site_id") == null?"0":(String)paramMap.get("site_id");
		if(!StringUtils.isEmpty(user_id)){
			DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
			Map<String, Object> param2 = new HashMap<String, Object>();
			dsManageReqInfo2.setNeedAll("1");
			dsManageReqInfo2.setServiceName("HMJ_BUV1_USERS");
			param2.put("user_id", user_id);
			param2.put("site_id", site_id);
			dsManageReqInfo2.setParam(param2);
			String resultData = dataAction.getData(dsManageReqInfo2, "");
			RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
			LOG.info("OrederAction createDeliverGoods HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.info("OrederAction createDeliverGoods HMJ_BUV1_USERS result is: "+ resultData);
			if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
				LOG.error("OrederAction createDeliverGoods HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
				LOG.error("OrederAction createDeliverGoods HMJ_BUV1_USERS result is: "+ resultData);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("app查询用户失败！");
				return JSON.toJSONString(respInfo);
		    }
			List<Map<String,String>> mapList = responseData.getRows();
			if(null != mapList && mapList.size() > 0){
				user_name = mapList.get(0).get("user_name");
			}else{
				LOG.error("OrederAction createDeliverGoods HMJ_BUV1_USERS param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
				LOG.error("OrederAction createDeliverGoods HMJ_BUV1_USERS result is: "+ resultData);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("app查询用户无数据！");
				return JSON.toJSONString(respInfo);
			}
		}
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("HMJ_BUV1_ORDER_NEW");
		param.put("order_id", order_id);
		param.put("site_id", site_id);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		LOG.info("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW param is: "+ JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW result is: "+ resultData);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW result is: "+ resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单失败！");
			return JSON.toJSONString(respInfo);
	    }
		List<Map<String,String>> mapList = responseData.getRows();
		
		DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
		Map<String, Object> param1 = new HashMap<String, Object>();
		dsManageReqInfo1.setNeedAll("1");
		dsManageReqInfo1.setServiceName("YJG_HSV1_OrderBackCreatDelivery");
		param1.put("order_id", order_id);
		param1.put("site_id", site_id);
		dsManageReqInfo1.setParam(param1);
		String resultData1 = dataAction.getData(dsManageReqInfo1, "");
		RuleServiceResponseData responseData1 = DataUtil.parse(resultData1, RuleServiceResponseData.class);
		LOG.info("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
		LOG.info("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery result is: "+ resultData1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData1.getCode())) {
			LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery result is: "+ resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单回写失败！");
			return JSON.toJSONString(respInfo);
	    }
		List<Map<String,String>> mapList1 = responseData1.getRows();
		
		
		Tool tool = new Tool();
		List<Map<String,String>> mapList2 = null;
		try {
			mapList2 = (List<Map<String, String>>) tool.getRuleResult("YJG_HSV1_JudgeOrderGoods", param1, LOG, "OrederAction createDeliverGoods", "订单商品的bom_id");
		} catch (Exception e) {
			LOG.error("createDeliverGoods YJG_HSV1_JudgeOrderGoods param is: "+ param1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("订单商品的bom_id失败！");
			return JSON.toJSONString(respInfo);
		}
		
		if(null != mapList && mapList.size() > 0 && null != mapList1 && mapList1.size() > 0){
			String order_status = mapList.get(0).get("order_status");
			String shipping_status = mapList.get(0).get("shipping_status");
			String pay_status = mapList.get(0).get("pay_status");
			
			Action action5 = new Action();
			action5.setServiceName("test_ecshop_ecs_order_info");
			Map<String, Object> setMap5 = new HashMap<String, Object>();
			action5.setType("U");
			setMap5.put("is_fast", 1);
			action5.setSet(setMap5);
			Where where = new Where();
			where.setPrepend("and");
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			SqlCondition sqlCondition = new SqlCondition();
			sqlCondition.setKey("order_id");
			sqlCondition.setOp("=");
			sqlCondition.setValue(order_id);
			conditions.add(sqlCondition);
			where.setConditions(conditions);
			action5.setWhere(where);
			newActions.add(action5);
			
			Action action4 = new Action();
			action4.setServiceName("test_ecshop_ecs_order_action");
			Map<String, Object> setMap4 = new HashMap<String, Object>();
			action4.setType("C");
			setMap4.put("order_id", order_id);
			setMap4.put("action_user", user_name);
			setMap4.put("order_status", order_status);
			setMap4.put("shipping_status", shipping_status);
			setMap4.put("pay_status", pay_status);
			setMap4.put("action_note", "设置了尽快发货");
			setMap4.put("log_time", "$UnixTime");
			action4.setSet(setMap4);
			newActions.add(action4);
			
			
			String is_separate = mapList1.get(0).get("is_separate");
			if(!"1".equals(is_separate)){
				for (int i = 0; i < mapList1.size(); i++) {
					String shop_id = mapList1.get(i).get("shop_id");
					String order_sn = mapList1.get(i).get("order_sn");
					if(StringUtils.isEmpty(is_separate)){
						LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery is_separate is: "+ is_separate);
						LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
						LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery result is: "+ resultData1);
						respInfo.setCode(DsResponseCodeData.ERROR.code);
						respInfo.setDescription("字段is_separate为空！");
						return JSON.toJSONString(respInfo);
					}
					
					Action action = new Action();
					action.setServiceName("test_ecshop_ecs_delivery_inform");
					Map<String, Object> setMap = new HashMap<String, Object>();
					action.setType("C");
					if (shop_id.length() == 3) {
						shop_id = "0" + shop_id;
					} else if (shop_id.length() == 2) {
						shop_id = "00" + shop_id;
					} else if (shop_id.length() == 1) {
						shop_id = "000" + shop_id;
					}
					setMap.put("inform_sn", order_sn + shop_id + (String.valueOf(new Random().nextInt(900) + 100)));
					setMap.put("order_id", order_id);
					setMap.put("shop_id", shop_id);
					setMap.put("add_time", "$UnixTime");
					action.setSet(setMap);
					newActions.add(action);

					Action action1 = new Action();
					action1.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap1 = new HashMap<String, Object>();
					action1.setType("U");
					setMap1.put("inform_id", "$-1.generateKey");
					action1.setSet(setMap1);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("order_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(order_id);
					SqlCondition sqlCondition2 = new SqlCondition();
					sqlCondition2.setKey("shop_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(shop_id);
					
					conditions1.add(sqlCondition1);
					conditions1.add(sqlCondition2);
					where1.setConditions(conditions1);
					action1.setWhere(where1);
					newActions.add(action1);

					Action action2 = new Action();
					action2.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap2 = new HashMap<String, Object>();
					action2.setType("C");
					setMap2.put("order_id", order_id);
					setMap2.put("action_user", user_name);
					setMap2.put("order_status", order_status);
					setMap2.put("shipping_status", shipping_status);
					setMap2.put("pay_status", pay_status);
					setMap2.put("action_note", "生成了发货通知单");
					setMap2.put("log_time", "$UnixTime");
					setMap2.put("inform_id", "$-2.generateKey");
					action2.setSet(setMap2);
					newActions.add(action2);

					Action action3 = new Action();
					action3.setServiceName("test_ecshop_ecs_order_info");
					Map<String, Object> setMap3 = new HashMap<String, Object>();
					action3.setType("U");
					setMap3.put("is_separate", 1);
					action3.setSet(setMap3);
					Where where3 = new Where();
					where3.setPrepend("and");
					List<SqlCondition> conditions3 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition3 = new SqlCondition();
					sqlCondition3.setKey("order_id");
					sqlCondition3.setOp("=");
					sqlCondition3.setValue(order_id);
					conditions3.add(sqlCondition3);
					where3.setConditions(conditions3);
					action3.setWhere(where3);
					newActions.add(action3);
					
				}
				
				for (int i = 0; i < mapList2.size(); i++) {
					String bom_id = mapList2.get(i).get("bom_id");
					String rec_id = mapList2.get(i).get("rec_id");
					Action action1 = new Action();
					action1.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap1 = new HashMap<String, Object>();
					action1.setType("U");
					setMap1.put("bom_id", bom_id);
					action1.setSet(setMap1);
					Where where1 = new Where();
					where1.setPrepend("and");
					List<SqlCondition> conditions1 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1 = new SqlCondition();
					sqlCondition1.setKey("rec_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(rec_id);
					
					conditions1.add(sqlCondition1);
					where1.setConditions(conditions1);
					action1.setWhere(where1);
					newActions.add(action1);
				}
			};
			
			
			Map<String,Object> paramMap1 = new HashMap<String, Object>();
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			result = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(result);
			LOG.info("OrederAction createDeliverGoods set param is: "+JSONObject.toJSONString(dsReqInfo));
			LOG.info("OrederAction createDeliverGoods set result is: "+result);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LOG.error("OrederAction createDeliverGoods set param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.error("OrederAction createDeliverGoods set result is: "+result);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("生成发货通知单写入失败！");
                return JSON.toJSONString(respInfo);
		    }
			DsManageReqInfo dsManageReqInfo3 = new DsManageReqInfo();
			Map<String, Object> param3 = new HashMap<String, Object>();
			dsManageReqInfo3.setNeedAll("1");
			dsManageReqInfo3.setServiceName("WX_HSV1_OrderGoodsSendMicro");
			param3.put("order_id", order_id);
			param3.put("site_id", site_id);
			dsManageReqInfo3.setParam(param3);
			String resultData3 = dataAction.getData(dsManageReqInfo3, "");
			RuleServiceResponseData responseData3 = DataUtil.parse(resultData3, RuleServiceResponseData.class);
			LOG.info("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
			LOG.info("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro result is: "+ resultData1);
			if (!DsResponseCodeData.SUCCESS.code.equals(responseData3.getCode())) {
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro result is: "+ resultData1);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("查询发送微信失败！");
				return JSON.toJSONString(respInfo);
		    }
			List<Map<String,String>> mapList3 = responseData3.getRows();
			if(null == mapList3 || mapList3.size() == 0){
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro result is: "+ resultData1);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("查询发送微信无数据！");
				return JSON.toJSONString(respInfo);
			}
			
			if(!"0".equals(mapList3.get(0).get("execute_result"))){
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
				LOG.error("OrederAction createDeliverGoods WX_HSV1_OrderGoodsSendMicro result is: "+ resultData1);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("发送微信消息失败！");
				return JSON.toJSONString(respInfo);
			}
			
			
		}else{
			LOG.error("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW result length is: "+ mapList.size());
			LOG.error("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction createDeliverGoods HMJ_BUV1_ORDER_NEW result is: "+ resultData);
			LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery result length is: "+ mapList1.size());
			LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery param is: "+ JSONObject.toJSONString(dsManageReqInfo1));
			LOG.error("OrederAction createDeliverGoods YJG_HSV1_OrderBackCreatDelivery result is: "+ resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("生成发货通知单失败！");
			return JSON.toJSONString(respInfo);
		}
		respInfo.setCode(DsResponseCodeData.SUCCESS.code);
		respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
		return JSON.toJSONString(respInfo);
		
	}
	
	/**
	 * 得到年月日时分+5位随机数用来作为订单编号
	 * @return
	 */
	private String getNumber(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		String str = df.format(new Date());
		Random rdm = new Random();
		str += rdm.nextInt(9999)+10000;
		return str;
		
	}
	
	class UpdateWeixinBonusStatus extends Thread {
		private String sequence_sn;
    	
		public UpdateWeixinBonusStatus(String sequence_sn) {
			super();
			this.sequence_sn = sequence_sn;
		}

		public void run() {
			DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
			Map<String, Object> param = new HashMap<String, Object>();
			dsManageReqInfo.setNeedAll("1");
			dsManageReqInfo.setServiceName("YJG_HSV1_BonusSequenceIsWX");
			param.put("sequence_sn", sequence_sn);
			dsManageReqInfo.setParam(param);
			String resultData = dataAction.getData(dsManageReqInfo, "");
			RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
			LOG.info("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.info("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX result is: "+ resultData);
			if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
				LOG.error("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX param is: "+ JSONObject.toJSONString(dsManageReqInfo));
				LOG.error("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX result is: "+ resultData);
		    }
			List<Map<String,String>> mapList = responseData.getRows();
			if(null != mapList && mapList.size() > 0){
				String is_wx = mapList.get(0).get("is_wx");
				if(!StringUtils.isEmpty(is_wx) && !"0".equals(is_wx)){
					Properties propertiesReader = ConfigFileUtil.propertiesReader("sysConfig.properties");
					String property = propertiesReader.getProperty("update_weixin_bonusstatus_url");
					String serviceName = mapList.get(0).get("name");
					String parameter = mapList.get(0).get("parameter");
					String url = property+"?{\"action\":\"getAction\",\"method\":\"get\",\"param\": {\"serviceName\":\""+serviceName+"\",\"param\": "+parameter+"}}";
					try {
						String result = HttpExecutor.get(url);
						LOG.info("请求微信通道修改红包序列号状态的请求参数为： "+url);
						LOG.info("请求微信通道修改红包序列号状态的结果为： "+result);
					} catch (IOException e) {
						LOG.info("请求微信通道异常");
						StringBuilder errMsg=new StringBuilder();
						errMsg.append(" Error Detail：");
						errMsg.append(""+e.getMessage());
						LOG.info(errMsg.toString());
					}
				}
				
			}else{
				LOG.error("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX result length is: "+ mapList.size());
				LOG.error("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX param is: "+ JSONObject.toJSONString(dsManageReqInfo));
				LOG.error("OrederAction UpdateWeixinBonusStatus YJG_HSV1_BonusSequenceIsWX result is: "+ resultData);
			}
		}
	}
	
	/**
     * 查询商家订单信息
    * @Title: queryOrUpdateOrderForShop 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param request
    * @param @param response
    * @param @param repInfo
    * @param @return  参数说明 
    * @return Object    返回类型 
    * @throws
     */
    @SuppressWarnings("unchecked")
	public Object queryOrUpdateOrderForShop(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
        
        
        // RSA授权认证
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return respInfo;
        }
        
        ResponseInfo respInfo=new ResponseInfo();
        String contentInfo = repInfo.getParam();
        if (StringUtils.isEmpty(contentInfo)) {
            return "请求参数为空";
        }
        String paramStr=repInfo.getParam();
        Map<String,Object> params=DataUtil.parse(paramStr,Map.class);
        
        String user_name=params.get("user_name")==null?"":params.get("user_name").toString();
        if(StringUtils.isEmpty(user_name)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("user_name不能为空");
            return respInfo;
        }
        
        String password=params.get("password")==null?"":params.get("password").toString();
        if(StringUtils.isEmpty(password)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("password不能为空");
            return respInfo;
        }
        
        String site_id=params.get("site_id")==null?"":params.get("site_id").toString();
        if(StringUtils.isEmpty(site_id)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("site_id不能为空");
            return respInfo;
        }
        
        //验证用户名密码
        DsManageReqInfo serviceReqInfo1=new DsManageReqInfo();
        serviceReqInfo1.setServiceName("T_BUV1_show_shop");
        Map<String,Object> p=new HashMap<String, Object>();
        p.put("user_name", user_name);
        p.put("password", password);
        p.put("site_id", site_id);
        serviceReqInfo1.setParam(p);
        serviceReqInfo1.setNeedAll("1");
        String resultData =dataAction.getData(serviceReqInfo1,"");
        RuleServiceResponseData resp = DataUtil.parse(resultData, RuleServiceResponseData.class);
        String shop_id="";
        if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
            List<Map<String,String>> list=resp.getRows();
            if(ListUtil.notEmpty(list)){
                Map<String, String> result = list.get(0);
                shop_id=result.get("show_shop");
            }else{
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
                return respInfo;
            }
        }else{
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
            return respInfo;
        } 
        
        if(StringUtils.isEmpty(shop_id)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("当前用户对应的shop_id为空");
            return respInfo;
        }
        
        params.put("shop_id", shop_id);
        
        String type=params.get("type")==null?"":params.get("type").toString();
        if(StringUtils.isEmpty(type)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("type不能为空");
            return respInfo;
        }
        if("1".equals(type)){
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName("YJG_HSV1_Select_OrderInfo");
            serviceReqInfo.setParam(params);
            serviceReqInfo.setNeedAll("1");
            String data =dataAction.getData(serviceReqInfo,"");
            return data;
        }else if("2".equals(type)){
            DsManageReqInfo serviceReqInfo=new DsManageReqInfo();
            serviceReqInfo.setServiceName("YJG_HSV1_JavaGoodsStatus");
            serviceReqInfo.setParam(params);
            serviceReqInfo.setNeedAll("1");
            String data =dataAction.getData(serviceReqInfo,"");
            return data;
        }
        return respInfo;
    }   
    
    /**
     * 新增发货通知单
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
	public String addDeliverGoods(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
    	
    	// RSA授权认证
    	ResponseInfo respInfo = new ResponseInfo();
        boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(respInfo);
        }
        
        String contentInfo = repInfo.getParam();
        if (StringUtils.isEmpty(contentInfo)) {
            return "请求参数为空";
        }
        String paramStr=repInfo.getParam();
        Map<String,Object> params=DataUtil.parse(paramStr,Map.class);
        
        String user_name=params.get("user_name")==null?"":params.get("user_name").toString();
        if(StringUtils.isEmpty(user_name)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("user_name不能为空");
            return JSONObject.toJSONString(respInfo);
        }
        
        String password=params.get("password")==null?"":params.get("password").toString();
        if(StringUtils.isEmpty(password)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("password不能为空");
            return JSONObject.toJSONString(respInfo);
        }
        
        String site_id=params.get("site_id")==null?"":params.get("site_id").toString();
        if(StringUtils.isEmpty(site_id)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("site_id不能为空");
            return JSONObject.toJSONString(respInfo);
        }
        
        //验证用户名密码
        DsManageReqInfo serviceReqInfo1=new DsManageReqInfo();
        serviceReqInfo1.setServiceName("T_BUV1_show_shop");
        Map<String,Object> p=new HashMap<String, Object>();
        p.put("user_name", user_name);
        p.put("password", password);
        p.put("site_id", site_id);
        serviceReqInfo1.setParam(p);
        serviceReqInfo1.setNeedAll("1");
        String resultData =dataAction.getData(serviceReqInfo1,"");
        RuleServiceResponseData resp = DataUtil.parse(resultData, RuleServiceResponseData.class);
        String show_shop="";
        if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
            List<Map<String,String>> list=resp.getRows();
            if(ListUtil.notEmpty(list)){
                Map<String, String> result = list.get(0);
                show_shop=result.get("show_shop");
            }else{
                respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
                return JSONObject.toJSONString(respInfo);
            }
        }else{
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
            return JSONObject.toJSONString(respInfo);
        } 
        
        if(StringUtils.isEmpty(show_shop)){
            respInfo.setCode(DsResponseCodeData.ERROR.code);
            respInfo.setDescription("当前用户对应的show_shop为空");
            return JSONObject.toJSONString(respInfo);
        }
    	
        String get_inform_id = params.get("get_inform_id") == null ? "" : params.get("get_inform_id").toString();
        String get_rec_id = params.get("get_rec_id") == null ? "" : params.get("get_rec_id").toString();
        String admin_user_id = params.get("admin_user_id") == null ? "" : params.get("admin_user_id").toString();
        String admin_user_name = params.get("admin_user_name") == null ? "" : params.get("admin_user_name").toString();
        String servicer_id = params.get("service_id") == null ? "" : params.get("service_id").toString();
        String service_name = params.get("service_name") == null ? "" : params.get("service_name").toString();
        String waybill_sn = params.get("waybill_sn") == null ? "" : params.get("waybill_sn").toString();
        String contact_user = params.get("contact_user") == null ? "" : params.get("contact_user").toString();
        String contact_phone = params.get("contact_phone") == null ? "" : params.get("contact_phone").toString();
        String note = params.get("note") == null ? "" : params.get("note").toString();
        String operation = params.get("operation") == null ? "" : params.get("operation").toString();
        
        String result = "";
        RuleServiceResponseData responseData = null;
        DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("HMJ_HSV2_JavaShopDeliver");
		param.put("get_inform_id", get_inform_id);
		param.put("get_rec_id", get_rec_id);
		param.put("java_shop", show_shop);
		param.put("operation", operation);
		dsManageReqInfo.setParam(param);
		String resultData1 = dataAction.getData(dsManageReqInfo, "");
		
		responseData = DataUtil.parse(resultData1, RuleServiceResponseData.class);
		LOG.info("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver param is: "+ JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver result is: "+ resultData1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver param is: "+ JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver result is: "+ resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询卖家信息失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList = responseData.getRows();
		
		RuleServiceResponseData responseData2 = null;
		DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
		Map<String, Object> param2 = new HashMap<String, Object>();
		List<Map<String,String>> mapList2= new ArrayList<Map<String,String>>();
		
		dsManageReqInfo2.setNeedAll("1");
		dsManageReqInfo2.setServiceName("HMJ_BUV1_order_saleafter");
		param2.put("rec_id", get_rec_id);
		param2.put("inform_id", get_inform_id);
		dsManageReqInfo2.setParam(param2);
		String resultData2 = dataAction.getData(dsManageReqInfo2, "");
		responseData2 = DataUtil.parse(resultData2, RuleServiceResponseData.class);
		LOG.info("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
		LOG.info("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter result is: "+ resultData2);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData2.getCode())) {
			LOG.error("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.error("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter result is: "+ resultData2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单商品失败！");
			return JSON.toJSONString(respInfo);
	    }
		mapList2 = responseData2.getRows();
		
		List<Action> newActions=new ArrayList<Action>();
		
		if(null != mapList && mapList.size() > 0 && null != mapList2 && mapList2.size() > 0){
			String in_flag = mapList.get(0).get("in_flag");
			String in_prompt = mapList.get(0).get("in_prompt");
			if("1".equals(in_flag)){
				String order_status_id = mapList.get(0).get("order_status_id");
				String pay_status_id = mapList.get(0).get("pay_status_id");
				String shipping_status_id = mapList.get(0).get("shipping_status_id");
				String delivery_sn = mapList.get(0).get("delivery_sn");
				String need_update = mapList.get(0).get("need_update");
				String update_shipping = mapList.get(0).get("update_shipping");
				String order_id = mapList.get(0).get("order_id");
				String order_sn = mapList.get(0).get("order_sn");
				String shop_id = mapList.get(0).get("shop_id");
				String user_id = mapList.get(0).get("user_id");
				String consignee = mapList.get(0).get("consignee");
				String email = mapList.get(0).get("email");
				String province = mapList.get(0).get("province");
				String city = mapList.get(0).get("city");
				String district = mapList.get(0).get("district");
				String address = mapList.get(0).get("address");
				String best_time = mapList.get(0).get("best_time");
				String zipcode = mapList.get(0).get("zipcode");
				String tel = mapList.get(0).get("tel");
				String mobile = mapList.get(0).get("mobile");
				String sign_building = mapList.get(0).get("sign_building");
				String to_buyer = mapList.get(0).get("to_buyer");
				
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_delivery_order");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("C");
				setMap.put("delivery_sn", delivery_sn);
				setMap.put("order_sn", order_sn);
				setMap.put("order_id", order_id);
				setMap.put("add_time", "$UnixTime");
				setMap.put("shipping_name", "物流");
				setMap.put("user_id", user_id);
				setMap.put("consignee", consignee);
				setMap.put("address", address);
				setMap.put("province", province);
				setMap.put("city", city);
				setMap.put("district", district);
				setMap.put("sign_building", sign_building );
				setMap.put("email", email);
				setMap.put("zipcode", zipcode);
				setMap.put("tel", tel);
				setMap.put("mobile", mobile);
				setMap.put("best_time", best_time);
				setMap.put("postscript", to_buyer);
				if("1".equals(operation)){
					setMap.put("status", 0);
				}else if("2".equals(operation)){
					setMap.put("status", 2);
				}
				setMap.put("servicer_id", servicer_id);
				setMap.put("waybill_sn", waybill_sn);
				setMap.put("delivery_direction", 0);
				setMap.put("creater_id", admin_user_id);
				setMap.put("type", 7);
				setMap.put("service_name", service_name);
				setMap.put("contact_user", contact_user);
				setMap.put("contact_phone", contact_phone);
				setMap.put("note", note);
				setMap.put("shop_id", shop_id);
				setMap.put("inform_id", get_inform_id);
				setMap.put("delivery_time", "$UnixTime");
				action.setSet(setMap);
				newActions.add(action);
				
				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_delivery_log");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("delivery_id", "$-1.generateKey");
				setMap3.put("action_user", admin_user_name);
				setMap3.put("action_time", "$UnixTime");
				if("1".equals(operation)){
					setMap3.put("action_content", admin_user_name+"提交备货");
				}else if("2".equals(operation)){
					setMap3.put("action_content", admin_user_name+"发货出库");
				}
				action3.setSet(setMap3);
				newActions.add(action3);
				
				int num = 2;
				for (int i = 0; i < mapList2.size(); i++) {
					String goods_id = mapList2.get(i).get("goods_id");
					String goods_name = mapList2.get(i).get("goods_name");
					String goods_sn = mapList2.get(i).get("goods_sn");
					String goods_number = mapList2.get(i).get("goods_number");
					String rec_id = mapList2.get(i).get("rec_id");
					
					Action action4 = new Action();
					action4.setServiceName("test_ecshop_ecs_delivery_goods");
					Map<String, Object> setMap4 = new HashMap<String, Object>();
					action4.setType("C");
					setMap4.put("delivery_id", "$-"+num+".generateKey");
					setMap4.put("goods_id", goods_id);
					setMap4.put("goods_name", goods_name);
					setMap4.put("goods_sn", goods_sn);
					setMap4.put("send_number", goods_number);
					setMap4.put("order_rec_id", rec_id);
					action4.setSet(setMap4);
					newActions.add(action4);
					
					Action action5 = new Action();
					action5.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap5 = new HashMap<String, Object>();
					action5.setType("U");
					if("1".equals(operation)){
						setMap5.put("status", 4);
					}else if("2".equals(operation)){
						setMap5.put("status", 1);
					}
					action5.setSet(setMap5);
					Where where = new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition = new SqlCondition();
					sqlCondition.setKey("rec_id");
					sqlCondition.setOp("=");
					sqlCondition.setValue(rec_id);;
					conditions.add(sqlCondition);
					where.setConditions(conditions);
					action5.setWhere(where);
					newActions.add(action5);
					
					Action action6 = new Action();
					action6.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap6 = new HashMap<String, Object>();
					action6.setType("C");
					setMap6.put("order_id", order_id);
					setMap6.put("action_user", admin_user_name);
					setMap6.put("order_status", order_status_id );
					setMap6.put("shipping_status", shipping_status_id);
					setMap6.put("pay_status", pay_status_id );
					if("1".equals(operation)){
						setMap6.put("action_note", admin_user_name+"修改了商品"+goods_id+"状态，状态改为 备货中");
					}else if("2".equals(operation)){
						setMap6.put("action_note", admin_user_name+"修改了商品"+goods_id+"状态，状态改为 已发货");
					}
					setMap6.put("log_time", "$UnixTime");
					action6.setSet(setMap6);
					newActions.add(action6);
					
					num = num + 3;
				}
				if("1".equals(need_update)){
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_order_info");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("U");
					setMap7.put("shipping_status", update_shipping );
					setMap7.put("shipping_time", "$UnixTime");
					action7.setSet(setMap7);
					Where where7 = new Where();
					where7.setPrepend("and");
					List<SqlCondition> conditions7 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition7 = new SqlCondition();
					sqlCondition7.setKey("order_id");
					sqlCondition7.setOp("=");
					sqlCondition7.setValue(order_id);;
					conditions7.add(sqlCondition7);
					where7.setConditions(conditions7);
					action7.setWhere(where7);
					newActions.add(action7);
					
					Action action2 = new Action();
					action2.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap2 = new HashMap<String, Object>();
					action2.setType("C");
					setMap2.put("order_id", order_id);
					setMap2.put("action_user", admin_user_name);
					setMap2.put("order_status", order_status_id);
					setMap2.put("shipping_status", update_shipping);
					setMap2.put("pay_status", pay_status_id);
					setMap2.put("action_note", admin_user_name+"修改了订单的发货状态 为 已发货");
					setMap2.put("log_time", "$UnixTime");
					action2.setSet(setMap2);
					newActions.add(action2);
					
				}
				
				Action action8 = new Action();
				action8.setServiceName("test_ecshop_ecs_order_action");
				Map<String, Object> setMap8 = new HashMap<String, Object>();
				action8.setType("C");
				setMap8.put("order_id", order_id);
				setMap8.put("action_user", admin_user_name);
				setMap8.put("order_status", order_status_id);
				setMap8.put("shipping_status", update_shipping);
				setMap8.put("pay_status", pay_status_id);
				if("1".equals(operation)){
					setMap8.put("action_note", admin_user_name+"生成了一个商品发货单，发货单状态为新建");
				}else if("2".equals(operation)){
					setMap8.put("action_note", admin_user_name+"生成了一个商品发货单，发货单状态为已发货");
				}
				setMap8.put("log_time", "$UnixTime");
				setMap8.put("inform_id", get_inform_id);
				action8.setSet(setMap8);
				newActions.add(action8);
				
				Map<String,Object> paramMap1 = new HashMap<String, Object>();
				paramMap1.put("actions", newActions);
				paramMap1.put("transaction",1);
				DsManageReqInfo dsReqInfo = new DsManageReqInfo();
				dsReqInfo.setServiceName("MUSH_Offer");
				dsReqInfo.setParam(paramMap1);
				result = mushroomAction.offer(dsReqInfo);
				JSONObject job = JSONObject.parseObject(result);
				LOG.info("OrederAction addDeliverGoods set param is: "+JSONObject.toJSONString(dsReqInfo));
				LOG.info("OrederAction addDeliverGoods set result is: "+result);
				if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
					LOG.error("OrederAction addDeliverGoods set  param is: "+JSONObject.toJSONString(dsReqInfo));
					LOG.error("OrederAction addDeliverGoods set  result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
	                respInfo.setDescription("发货通知单新增写入数据失败！");
	                return JSON.toJSONString(respInfo);
			    }
				
			}else{
				return in_prompt;
			}
		}else{
			LOG.error("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver result is: " + mapList.size());
			LOG.error("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver param is: " + JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction addDeliverGoods HMJ_HSV2_JavaShopDeliver result is: " + resultData1);
			LOG.error("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter result is: " + mapList2.size());
			LOG.error("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter param is: "+ JSONObject.toJSONString(dsManageReqInfo2));
			LOG.error("OrederAction addDeliverGoods HMJ_BUV1_order_saleafter result is: "+ resultData2);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("新增发货通知单失败！");
			return JSON.toJSONString(respInfo);
		}
		return result;
    }
    
    /**
     * 编辑发货通知单
     * @param request
     * @param response
     * @param repInfo
     * @return
     */
	@SuppressWarnings("unchecked")
	public String editDeliverGoods(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

		// RSA授权认证
		ResponseInfo respInfo = new ResponseInfo();
		boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
		if (!rsaVerify) {
			respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
			respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
			return JSONObject.toJSONString(respInfo);
		}
		String contentInfo = repInfo.getParam();
		if (StringUtils.isEmpty(contentInfo)) {
			return "请求参数为空";
		}
		String paramStr = repInfo.getParam();
		Map<String, Object> params = DataUtil.parse(paramStr, Map.class);

		String user_name = params.get("user_name") == null ? "" : params.get("user_name").toString();
		if (StringUtils.isEmpty(user_name)) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("user_name不能为空");
			return JSONObject.toJSONString(respInfo);
		}

		String password = params.get("password") == null ? "" : params.get("password").toString();
		if (StringUtils.isEmpty(password)) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("password不能为空");
			return JSONObject.toJSONString(respInfo);
		}

		String site_id = params.get("site_id") == null ? "" : params.get("site_id").toString();
		if (StringUtils.isEmpty(site_id)) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("site_id不能为空");
			return JSONObject.toJSONString(respInfo);
		}

		// 验证用户名密码
		DsManageReqInfo serviceReqInfo1 = new DsManageReqInfo();
		serviceReqInfo1.setServiceName("T_BUV1_show_shop");
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("user_name", user_name);
		p.put("password", password);
		p.put("site_id", site_id);
		serviceReqInfo1.setParam(p);
		serviceReqInfo1.setNeedAll("1");
		String resultData = dataAction.getData(serviceReqInfo1, "");
		RuleServiceResponseData resp = DataUtil.parse(resultData, RuleServiceResponseData.class);
		String show_shop = "";
		if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
			List<Map<String, String>> list = resp.getRows();
			if (ListUtil.notEmpty(list)) {
				Map<String, String> result = list.get(0);
				show_shop = result.get("show_shop");
			} else {
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
				return JSONObject.toJSONString(respInfo);
			}
		} else {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("请检查传入的参数user_name,password,site_id是否正确");
			return JSONObject.toJSONString(respInfo);
		}

		if (StringUtils.isEmpty(show_shop)) {
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("当前用户对应的show_shop为空");
			return JSONObject.toJSONString(respInfo);
		}

		String get_delivery_id = params.get("get_delivery_id") == null ? "" : params.get("get_delivery_id").toString();
        //String admin_user_id = params.get("admin_user_id") == null ? "" : params.get("admin_user_id").toString();
        String admin_user_name = params.get("admin_user_name") == null ? "" : params.get("admin_user_name").toString();
        String servicer_id = params.get("service_id") == null ? "" : params.get("service_id").toString();
        String service_name = params.get("service_name") == null ? "" : params.get("service_name").toString();
        String waybill_sn = params.get("waybill_sn") == null ? "" : params.get("waybill_sn").toString();
        String contact_user = params.get("contact_user") == null ? "" : params.get("contact_user").toString();
        String contact_phone = params.get("contact_phone") == null ? "" : params.get("contact_phone").toString();
        String note = params.get("note") == null ? "" : params.get("note").toString();

		String result = "";
		RuleServiceResponseData responseData = null;
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();

		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV2_JavaShopAffirm");
		param.put("get_delivery_id", get_delivery_id);
		param.put("java_shop", show_shop);
		dsManageReqInfo.setParam(param);
		String resultData1 = dataAction.getData(dsManageReqInfo, "");

		responseData = DataUtil.parse(resultData1, RuleServiceResponseData.class);
		LOG.info("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm param is: " + JSONObject.toJSONString(dsManageReqInfo));
		LOG.info("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm result is: " + resultData1);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LOG.error("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm param is: " + JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm result is: " + resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询订单信息失败！");
			return JSON.toJSONString(respInfo);
		}
		mapList = responseData.getRows();
		List<Action> newActions = new ArrayList<Action>();

		if (null != mapList && mapList.size() > 0 ) {
			String affirm_flag = mapList.get(0).get("affirm_flag");
			String affirm_prompt = mapList.get(0).get("affirm_prompt");
			if ("1".equals(affirm_flag)) {
				//String delivery_sn = mapList.get(0).get("delivery_sn");
				//String order_sn = mapList.get(0).get("order_sn");
				String order_id = mapList.get(0).get("order_id");
				String order_status = mapList.get(0).get("order_status");
				String shipping_status = mapList.get(0).get("shipping_status");
				String pay_status = mapList.get(0).get("pay_status");
				String need_update = mapList.get(0).get("need_update");
				String update_shipping = mapList.get(0).get("update_shipping");
				String order_rec_id = mapList.get(0).get("order_rec_id");
				String goods_id_str = mapList.get(0).get("goods_id_str");
				String[] split = order_rec_id.split(",");
				
				
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_delivery_order");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("U");
				setMap.put("status", 2);
				setMap.put("servicer_id", servicer_id);
				setMap.put("waybill_sn", waybill_sn);
				setMap.put("service_name", service_name);
				setMap.put("contact_user", contact_user);
				setMap.put("contact_phone", contact_phone);
				setMap.put("note", note);
				setMap.put("delivery_time", "$UnixTime");
				action.setSet(setMap);
				Where where = new Where();
				where.setPrepend("and");
				List<SqlCondition> conditions = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition = new SqlCondition();
				sqlCondition.setKey("delivery_id");
				sqlCondition.setOp("=");
				sqlCondition.setValue(get_delivery_id);;
				conditions.add(sqlCondition);
				where.setConditions(conditions);
				action.setWhere(where);
				newActions.add(action);

				Action action3 = new Action();
				action3.setServiceName("test_ecshop_ecs_delivery_log");
				Map<String, Object> setMap3 = new HashMap<String, Object>();
				action3.setType("C");
				setMap3.put("delivery_id", get_delivery_id);
				setMap3.put("action_user", admin_user_name);
				setMap3.put("action_time", "$UnixTime");
				setMap3.put("action_content", "出库单确认发货");
				action3.setSet(setMap3);
				newActions.add(action3);

				for (int i = 0; i < split.length; i++) {

					Action action5 = new Action();
					action5.setServiceName("test_ecshop_ecs_order_goods");
					Map<String, Object> setMap5 = new HashMap<String, Object>();
					action5.setType("U");
					setMap5.put("status", 1);
					action5.setSet(setMap5);
					Where where5 = new Where();
					where5.setPrepend("and");
					List<SqlCondition> conditions5 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition5 = new SqlCondition();
					sqlCondition5.setKey("rec_id");
					sqlCondition5.setOp("=");
					sqlCondition5.setValue(split[i]);
					conditions5.add(sqlCondition5);
					where5.setConditions(conditions5);
					action5.setWhere(where5);
					newActions.add(action5);
				}
				
				Action action6 = new Action();
				action6.setServiceName("test_ecshop_ecs_order_action");
				Map<String, Object> setMap6 = new HashMap<String, Object>();
				action6.setType("C");
				setMap6.put("order_id", order_id);
				setMap6.put("action_user", admin_user_name);
				setMap6.put("order_status", order_status);
				setMap6.put("shipping_status", shipping_status);
				setMap6.put("pay_status", pay_status);
				setMap6.put("action_note", admin_user_name + "修改了商品" + goods_id_str + "状态，状态改为已发货");
				setMap6.put("log_time", "$UnixTime");
				action6.setSet(setMap6);
				newActions.add(action6);
				
				if ("1".equals(need_update)) {
					Action action7 = new Action();
					action7.setServiceName("test_ecshop_ecs_order_info");
					Map<String, Object> setMap7 = new HashMap<String, Object>();
					action7.setType("U");
					setMap7.put("shipping_status", update_shipping);
					setMap7.put("shipping_time", "$UnixTime");
					action7.setSet(setMap7);
					Where where7 = new Where();
					where7.setPrepend("and");
					List<SqlCondition> conditions7 = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition7 = new SqlCondition();
					sqlCondition7.setKey("order_id");
					sqlCondition7.setOp("=");
					sqlCondition7.setValue(order_id);
					conditions7.add(sqlCondition7);
					where7.setConditions(conditions7);
					action7.setWhere(where7);
					newActions.add(action7);

					Action action2 = new Action();
					action2.setServiceName("test_ecshop_ecs_order_action");
					Map<String, Object> setMap2 = new HashMap<String, Object>();
					action2.setType("C");
					setMap2.put("order_id", order_id);
					setMap2.put("action_user", admin_user_name);
					setMap2.put("order_status", order_status);
					setMap2.put("shipping_status", update_shipping);
					setMap2.put("pay_status", pay_status);
					setMap2.put("action_note", admin_user_name + "修改了订单的发货状态 为 已发货");
					setMap2.put("log_time", "$UnixTime");
					action2.setSet(setMap2);
					newActions.add(action2);

				}

				Map<String, Object> paramMap1 = new HashMap<String, Object>();
				paramMap1.put("actions", newActions);
				paramMap1.put("transaction", 1);
				DsManageReqInfo dsReqInfo = new DsManageReqInfo();
				dsReqInfo.setServiceName("MUSH_Offer");
				dsReqInfo.setParam(paramMap1);
				result = mushroomAction.offer(dsReqInfo);
				JSONObject job = JSONObject.parseObject(result);
				LOG.info("OrederAction editDeliverGoods set param is: " + JSONObject.toJSONString(dsReqInfo));
				LOG.info("OrederAction editDeliverGoods set result is: " + result);
				if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
					LOG.error("OrederAction editDeliverGoods set  param is: " + JSONObject.toJSONString(dsReqInfo));
					LOG.error("OrederAction editDeliverGoods set  result is: " + result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("编辑发货通知单新增写入数据失败！");
					return JSON.toJSONString(respInfo);
				}

			} else {
				return affirm_prompt;
			}
		}else{
			LOG.error("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm result is: " + mapList.size());
			LOG.error("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm param is: " + JSONObject.toJSONString(dsManageReqInfo));
			LOG.error("OrederAction editDeliverGoods YJG_HSV2_JavaShopAffirm result is: " + resultData1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("编辑发货通知单失败！");
			return JSON.toJSONString(respInfo);
		}
		return result;
	}
}