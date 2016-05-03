package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.meiqi.util.LogUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年10月13日 下午1:47:46 
 * 类说明 
 * 合并购物车商品和删除购物车商品
 */
@Service
public class shoppingMergerAction extends  BaseAction{

	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private IMushroomAction mushroomAction;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String mergerShopping(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		
		RuleServiceResponseData responseData = null;
		ResponseInfo respInfo = new ResponseInfo();
		String result = "";
		String flag = "";
		String message = "";
		
		String str1 = repInfo.getParam();
		List<Map<String,Object>> list = null;
		try {
			list = DataUtil.parse(str1, List.class);
		} catch (Exception e) {
			list = new ArrayList<Map<String,Object>>();
			Map<String,Object> map = DataUtil.parse(str1, Map.class);
			list.add(map);
		}
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		if(list.size() == 1 && !"2".equals(list.get(0).get("setType"))){
			Map<String, Object> paramMap = list.get(0);

			String setType = (String) paramMap.get("setType");
			String type = (String) paramMap.get("type");
			String site_id = paramMap.get("site_id") == null?"0":(String)paramMap.get("site_id");
			
			if(setType.equals("0")){
				DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
				Map<String, Object> param=new HashMap<String, Object>();
				String loginType = (String) paramMap.get("loginType");
				JSONObject goodsSessionJson = null;
				JSONObject goods_user_id = null;
				
				DsManageReqInfo dsManageReqInfo2 = new DsManageReqInfo();
				dsManageReqInfo2.setServiceName("HMJ_HSV1_ShopCartJionJudge");
				dsManageReqInfo2.setNeedAll("1");
				Map<String, Object> param2=new HashMap<String, Object>();
				if(loginType.equals("0")){
					goodsSessionJson = (JSONObject) paramMap.get("goods_session_id");
					Iterator iter = goodsSessionJson.entrySet().iterator(); 
					String session_id = "";
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    session_id = (String) entry.getValue(); 
					    break;
					} 
					param2.put("session_id", session_id);
				}else if(loginType.equals("1")){
					goods_user_id = (JSONObject) paramMap.get("goods_user_id");
					Iterator iter = goods_user_id.entrySet().iterator(); 
					String user_id = "";
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    user_id = (String) entry.getValue(); 
					    break;
					} 
					param2.put("user_id",user_id);
					param2.put("site_id",site_id);
				}
				dsManageReqInfo2.setParam(param2);
				result = dataAction.getData(dsManageReqInfo2, "");
				responseData = DataUtil.parse(result, RuleServiceResponseData.class);
				LogUtil.info("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge param is: "+JSONObject.toJSONString(dsManageReqInfo2));
				LogUtil.info("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge result is: "+result);
				if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge param is: "+JSONObject.toJSONString(dsManageReqInfo2));
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("购物车数量查询失败！");
					return JSON.toJSONString(respInfo);
			    }
				List<Map<String,String>> mapList2=responseData.getRows();
				if(null != mapList2 && mapList2.size() > 0){
					flag = mapList2.get(0).get("is_flag");
					message = mapList2.get(0).get("message");
				}else{
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge result length is: "+mapList2.size());
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge param is: "+JSONObject.toJSONString(dsManageReqInfo2));
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCartJionJudge result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("购物车数量查询为空！");
					return JSON.toJSONString(respInfo);
				}
				
				if("0".equals(flag)){
					JSONObject jsonObject = new JSONObject();
					JSONArray rows = new JSONArray();
					JSONObject obj = new JSONObject();
					obj.put("is_flag", flag);
					obj.put("message", message);
					obj.put("is_flag", flag);
					obj.put("message", message);
					rows.add(obj);
					jsonObject.put("rows", rows);
					jsonObject.put("code", DsResponseCodeData.SUCCESS.code);
					jsonObject.put("description", DsResponseCodeData.SUCCESS.description);
					return jsonObject.toJSONString();
				}
				
				dsManageReqInfo.setNeedAll("1");
				if (type.equals("0")) {
					dsManageReqInfo.setServiceName("YJG_HSV1_JoinCart");
					JSONObject goodsSuitJson = (JSONObject) paramMap.get("goods_suit_id");
					param.put("goods_suit_id", DataUtil.parse(goodsSuitJson.toJSONString(), Map.class));

					JSONObject goodsNumJson = (JSONObject) paramMap.get("goods_num_id");
					param.put("goods_num_id", DataUtil.parse(goodsNumJson.toJSONString(), Map.class));

					String goods_id = (String) paramMap.get("goods_id");
					param.put("goods_id", goods_id);

					param.put("loginType", loginType);

					JSONObject goodsSelectJson = (JSONObject) paramMap.get("goods_selected");
					param.put("goods_selected", DataUtil.parse(goodsSelectJson.toJSONString(), Map.class));

					if (loginType.equals("0")) {
						param.put("goods_session_id", DataUtil.parse(goodsSessionJson.toJSONString(), Map.class));
					} else if (loginType.equals("1")) {
						param.put("goods_user_id", DataUtil.parse(goods_user_id.toJSONString(), Map.class));
					}
					param.put("site_id",site_id);
				} else if (type.equals("1")) {
					String session_id = "";
					String user_id = "";
					Cookie[] cookie = request.getCookies();
					if (cookie.length > 0) {
						int count = 0;
						for (int i = 0; i < cookie.length; i++) {
							Cookie cook = cookie[i];
							if (cook.getName().equalsIgnoreCase("JSESSIONID")) {
								session_id = cook.getName();
								count++;
							}
							if (cook.getName().equalsIgnoreCase("uid")) {
								user_id = cook.getName();
								count++;
							}
							if (count == 2) {
								break;
							}
						}
					}

					dsManageReqInfo.setServiceName("HMJ_HSV1_JoinCart");
					param.put("user_id", session_id);
					param.put("session_id", user_id);
					param.put("site_id",site_id);
				}

				dsManageReqInfo.setParam(param);
				String resultData = dataAction.getData(dsManageReqInfo, "");
				responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
				LogUtil.info("ShoppingMergerAction YJG_HSV1_JoinCart param is:"+ JSONObject.toJSONString(dsManageReqInfo));
				LogUtil.info("ShoppingMergerAction YJG_HSV1_JoinCart result is:"+ resultData);
				if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
					if("0".equals(type)){
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart param is:"+ JSONObject.toJSONString(dsManageReqInfo));
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart result is:"+ resultData);
					}else if("1".equals(type)){
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart param is:"+ JSONObject.toJSONString(dsManageReqInfo));
						LogUtil.error("ShoppingMergerAction HMJ_HSV1_JoinCart result is:"+ resultData);
					}
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("合并购物车失败！");
					return JSON.toJSONString(respInfo);
			    }
				
				List<Map<String,String>> mapList=responseData.getRows();
				
				if(mapList!=null && mapList.size() > 0){
					if(type.equals("0")){
						for (Map<String, String> responseMap : mapList) {
							String update_num = responseMap.get("update_num");
							String is_flag = responseMap.get("is_flag");
							if("1".equals(is_flag)){
								respInfo.setCode(DsResponseCodeData.ERROR.code);
								respInfo.setDescription("该商品购买数量总和超过库存，请检查购物车！");
								return JSON.toJSONString(respInfo);
							}
							
							Action action = new Action();
							action.setServiceName("test_ecshop_ecs_cart");
							Map<String, Object> setMap = new HashMap<String, Object>();
							if(Integer.parseInt(update_num) > 0){
								action.setType("U");
								setMap.put("goods_number", responseMap.get("update_num"));
								setMap.put("suit_num", responseMap.get("suit_num"));
								setMap.put("selected", responseMap.get("selected"));
								action.setSet(setMap);
								Where where = new Where();
								where.setPrepend("and");
								List<SqlCondition> conditions = new ArrayList<SqlCondition>();
								SqlCondition sqlCondition = new SqlCondition();
								sqlCondition.setKey("goods_id");
								sqlCondition.setOp("=");
								sqlCondition.setValue(responseMap.get("goods_id"));
								
								SqlCondition sqlCondition1 = new SqlCondition();
								sqlCondition1.setKey("suit_id");
								sqlCondition1.setOp("=");
								sqlCondition1.setValue(responseMap.get("suit_id"));
								
								SqlCondition sqlCondition2 = new SqlCondition();
								sqlCondition2.setOp("=");
								if(loginType.equals("0")){
									sqlCondition2.setKey("session_id");
									sqlCondition2.setValue(responseMap.get("session_id"));
								}else if(loginType.equals("1")){
									sqlCondition2.setKey("user_id");
									sqlCondition2.setValue(responseMap.get("user_id"));
								}
								
								conditions.add(sqlCondition);
								conditions.add(sqlCondition1);
								conditions.add(sqlCondition2);
								where.setConditions(conditions);
								action.setWhere(where);
							}else{
								action.setType("C");
								setMap.put("goods_id", responseMap.get("goods_id"));
								if(loginType.equals("0")){
									setMap.put("session_id", responseMap.get("session_id"));
								}else if(loginType.equals("1")){
									setMap.put("user_id", responseMap.get("user_id"));
								}
								setMap.put("goods_name", responseMap.get("goods_name"));
								setMap.put("goods_number", responseMap.get("goods_num"));
								setMap.put("suit_id", responseMap.get("suit_id"));
								setMap.put("suit_num", responseMap.get("suit_num"));
								action.setSet(setMap);
							}
							newActions.add(action);
						}
						
					}else if(type.equals("1")){
						for (Map<String, String> responseMap : mapList) {
							String is_delate = responseMap.get("is_delate");
							String is_update = responseMap.get("is_update");
							String is_update_userid = responseMap.get("is_update_userid");
							
							Action action = new Action();
							action.setServiceName("test_ecshop_ecs_cart");
							Map<String, Object> setMap = new HashMap<String, Object>();
							
							if(is_delate.equals("1")){
								action.setType("D");
								Where where = new Where();
								where.setPrepend("and");
								List<SqlCondition> conditions = new ArrayList<SqlCondition>();
								SqlCondition sqlCondition = new SqlCondition();
								sqlCondition.setKey("rec_id");
								sqlCondition.setOp("=");
								sqlCondition.setValue(responseMap.get("rec_id"));
								conditions.add(sqlCondition);
								where.setConditions(conditions);
								action.setWhere(where);
							}else if(is_update.equals("1")){
								action.setType("U");
								setMap.put("goods_number", responseMap.get("same_goods_num"));
								setMap.put("suit_num", responseMap.get("update_suit_num"));
								action.setSet(setMap);
								Where where = new Where();
								where.setPrepend("and");
								List<SqlCondition> conditions = new ArrayList<SqlCondition>();
								SqlCondition sqlCondition = new SqlCondition();
								sqlCondition.setKey("rec_id");
								sqlCondition.setOp("=");
								sqlCondition.setValue(responseMap.get("rec_id"));
								conditions.add(sqlCondition);
								where.setConditions(conditions);
								action.setWhere(where);
							}else if(is_update_userid.equals("1")){
								action.setType("U");
								Where where = new Where();
								setMap.put("user_id", responseMap.get("user_id"));
								action.setSet(setMap);
								where.setPrepend("and");
								List<SqlCondition> conditions = new ArrayList<SqlCondition>();
								SqlCondition sqlCondition = new SqlCondition();
								sqlCondition.setKey("rec_id");
								sqlCondition.setOp("=");
								sqlCondition.setValue(responseMap.get("rec_id"));
								conditions.add(sqlCondition);
								where.setConditions(conditions);
								action.setWhere(where);
							}
							newActions.add(action);
						}

					}
					
					paramMap1.put("actions", newActions);
					paramMap1.put("transaction",1);
					DsManageReqInfo dsReqInfo = new DsManageReqInfo();
					dsReqInfo.setServiceName("MUSH_Offer");
					dsReqInfo.setParam(paramMap1);
					String str = mushroomAction.offer(dsReqInfo);
					JSONObject job = JSONObject.parseObject(str);
					LogUtil.info("ShoppingMergerAction set shopCart param is: "+JSONObject.toJSONString(dsReqInfo));
					LogUtil.info("ShoppingMergerAction set shopCart result is: "+str);
					if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
						LogUtil.error("ShoppingMergerAction set shopCart param is: "+JSONObject.toJSONString(dsReqInfo));
						LogUtil.error("ShoppingMergerAction set shopCart result is: "+str);
						respInfo.setCode(DsResponseCodeData.ERROR.code);
		                respInfo.setDescription("申请加入失败！");
		                return JSON.toJSONString(respInfo);
				    }
				}else{
					if("0".equals(type)){
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart param is:"+ JSONObject.toJSONString(dsManageReqInfo));
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart result is:"+ resultData);
					}else if("1".equals(type)){
						LogUtil.error("ShoppingMergerAction YJG_HSV1_JoinCart param is:"+ JSONObject.toJSONString(dsManageReqInfo));
						LogUtil.error("ShoppingMergerAction HMJ_HSV1_JoinCart result is:"+ resultData);
					}
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("合并购物车无数据！");
					return JSON.toJSONString(respInfo);
				}
				
				DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
				dsManageReqInfo1.setServiceName("HMJ_HSV1_ShopCart");
				dsManageReqInfo1.setNeedAll("1");
				Map<String, Object> param1=new HashMap<String, Object>();
				if(loginType.equals("0")){
					goodsSessionJson = (JSONObject) paramMap.get("goods_session_id");
					Iterator iter = goodsSessionJson.entrySet().iterator(); 
					String session_id = "";
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    session_id = (String) entry.getValue(); 
					    break;
					} 
					param1.put("session_id", session_id);
				}else if(loginType.equals("1")){
					JSONObject goodsUserJson = (JSONObject) paramMap.get("goods_user_id");
					Iterator iter = goodsUserJson.entrySet().iterator(); 
					String user_id = "";
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    user_id = (String) entry.getValue(); 
					    break;
					} 
					param1.put("user_id",user_id);
				}
				param1.put("site_id", site_id);
				dsManageReqInfo1.setParam(param1);
				result = dataAction.getData(dsManageReqInfo1, "");
				responseData = DataUtil.parse(result, RuleServiceResponseData.class);
				LogUtil.info("ShoppingMergerAction HMJ_HSV1_ShopCart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
				LogUtil.info("ShoppingMergerAction HMJ_HSV1_ShopCart result is: "+result);
				if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCart result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("购物车展示失败！");
					return JSON.toJSONString(respInfo);
			    }
				List<Map<String, String>> maplist3 = responseData.getRows();
				if(null == maplist3 || maplist3.size() == 0){
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCart param is: "+JSONObject.toJSONString(dsManageReqInfo1));
					LogUtil.error("ShoppingMergerAction HMJ_HSV1_ShopCart result is: "+result);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("购物车展示无数据！");
					return JSON.toJSONString(respInfo);
				}
				responseData.getRows().get(0).put("is_flag", flag);
				responseData.getRows().get(0).put("message", message);
				result = JSONObject.toJSONString(responseData);
			}else if(setType.equals("1")){
				Action action = new Action();
				action.setType("D");
				action.setServiceName("test_ecshop_ecs_cart");
				Where where = new Where();
				where.setPrepend("and");
				List<SqlCondition> conditions = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition = new SqlCondition();
				sqlCondition.setKey("rec_id");
				sqlCondition.setOp("in");
				sqlCondition.setValue(paramMap.get("rec_id").toString().split(","));;
				conditions.add(sqlCondition);
				where.setConditions(conditions);
				action.setWhere(where);
				newActions.add(action);
				
				paramMap1.put("actions", newActions);
				paramMap1.put("transaction",1);
				DsManageReqInfo dsReqInfo = new DsManageReqInfo();
				dsReqInfo.setParam(paramMap1);
				result = mushroomAction.offer(dsReqInfo);
			}
		}else if(list.size()>1 || "2".equals(list.get(0).get("setType"))){
			result = updateShopCart(list);
		}else{
			LogUtil.error("ShoppingMergerAction list is: "+list+" and str1 is: "+str1);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("请求失败！");
			return JSON.toJSONString(respInfo);
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private String updateShopCart(List<Map<String,Object>> list){
		ResponseInfo respInfo = new ResponseInfo();
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		for (int y = 0; y < list.size(); y++) {
			Map<String, Object> paramMap = list.get(y);
			if( paramMap.get("suit") != null ){
				JSONArray array = JSONArray.parseArray(paramMap.get("suit").toString());
				for (int i = 0; i < array.size(); i++) {
					JSONObject jo = array.getJSONObject(i);
					JSONObject jo1 = (JSONObject)jo.get("rec_goods_number");
					Iterator iter = jo1.entrySet().iterator(); 
					while (iter.hasNext()) { 
					    Map.Entry entry = (Map.Entry) iter.next(); 
					    Action action = new Action();
						action.setType("U");
						action.setServiceName("test_ecshop_ecs_cart");
						Map<String, Object> setMap = new HashMap<String, Object>();
						setMap.put("goods_number", (String) entry.getValue());
						setMap.put("suit_num", jo.get("suit_num").toString());
						setMap.put("selected", paramMap.get("selected"));
						action.setSet(setMap);
						Where where = new Where();
						where.setPrepend("and");
						List<SqlCondition> conditions = new ArrayList<SqlCondition>();
						SqlCondition sqlCondition = new SqlCondition();
						sqlCondition.setKey("rec_id");
						sqlCondition.setOp("=");
						sqlCondition.setValue((String)entry.getKey());;
						conditions.add(sqlCondition);
						where.setConditions(conditions);
						action.setWhere(where);
						newActions.add(action);
					}
				}
			}
			if(paramMap.get("goods_number") != null){
				JSONObject jsonObject = (JSONObject)paramMap.get("goods_number");
				Iterator iter = jsonObject.entrySet().iterator(); 
				while (iter.hasNext()) { 
				    Map.Entry entry = (Map.Entry) iter.next(); 
				    Action action = new Action();
					action.setType("U");
					action.setServiceName("test_ecshop_ecs_cart");
					Map<String, Object> setMap = new HashMap<String, Object>();
					setMap.put("goods_number", (String) entry.getValue());
					setMap.put("selected", paramMap.get("selected"));
					action.setSet(setMap);
					Where where = new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions = new ArrayList<SqlCondition>();
					SqlCondition sqlCondition = new SqlCondition();
					sqlCondition.setKey("rec_id");
					sqlCondition.setOp("=");
					sqlCondition.setValue((String)entry.getKey());;
					conditions.add(sqlCondition);
					where.setConditions(conditions);
					action.setWhere(where);
					newActions.add(action);
				}
			}
		}
		paramMap1.put("actions", newActions);
		paramMap1.put("transaction",1);
		DsManageReqInfo dsReqInfo = new DsManageReqInfo();
		dsReqInfo.setParam(paramMap1);
		String result = mushroomAction.offer(dsReqInfo);
		JSONObject job = JSONObject.parseObject(result);
		LogUtil.info("ShoppingMergerAction test_ecshop_ecs_cart U param is: "+JSONObject.toJSONString(dsReqInfo));
		LogUtil.info("ShoppingMergerAction test_ecshop_ecs_cart U result is: "+result);
		if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
			LogUtil.error("ShoppingMergerAction test_ecshop_ecs_cart U param is: "+JSONObject.toJSONString(dsReqInfo));
			LogUtil.error("ShoppingMergerAction test_ecshop_ecs_cart U result is: "+result);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("修改失败！");
			
			return JSON.toJSONString(respInfo);
		}
		return result;
	}
}
