package com.meiqi.openservice.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.dsmanager.po.mushroom.req.ActionReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * 购物车
 *
 */
@Service
public class OpenCartAction extends BaseAction{
	@Autowired
	private IDataAction dataAction;
	@Autowired
	private IMushroomAction mushroomAction;
	private static final ObjectMapper JSON_SERIALIZER = new ObjectMapper();
	
	public String addCart(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		String content = repInfo.getParam();
		JSONObject jsonObject=JSONObject.parseObject(content);
		
		//建立mushroom json信息包
		ActionReqInfo actionReqInfo = new ActionReqInfo();
		try {
			//解析成mushroom实体类actions
			actionReqInfo= JSON_SERIALIZER.readValue(jsonObject.get("param").toString(), ActionReqInfo.class);
			actionReqInfo.getActions().size();
			List<Action> actions=actionReqInfo.getActions();
			int max=actions.size();
			List<Action> newActions=new ArrayList<Action>();
			Map<String, Long> cartMap=new HashMap<String, Long>();
			long userId=0;
			for(int i=0;i<max;i++){
				Action action=actions.get(i);
				Map<String, Object> set=action.getSet();
				//获取userId
				if(0==userId&&0==cartMap.size()){
					userId=Long.parseLong(set.get("user_id").toString());
					if(0==userId){
						throw new Exception("缺少用户信息！");
					}
					//从规则引擎获取用户购物车信息
					getCartByUserFromData(userId,cartMap);
				}
				String suit_id="0";
				if(set.containsKey("suit_id")){
					suit_id=set.get("suit_id").toString();
				}
				
				String goods_id=set.get("goods_id").toString();
				String suidGoodKey=suit_id+"_"+goods_id;
				long goods_number=Long.parseLong(set.get("goods_number").toString());
				//如果购物车已经有同样商品,更新报文中商品数量和操作类型
				//如果购物没有则使用原报文
				if(cartMap.containsKey(suidGoodKey)){
					action.setType("U");
					long cartgoodsNum=cartMap.get(suidGoodKey);
					String goodsNum=String.valueOf(cartgoodsNum+goods_number);
					set.put("goods_number", goodsNum);
					action.setSet(set);
					Where where=new Where();
					where.setPrepend("and");
					List<SqlCondition> conditions=new ArrayList<SqlCondition>();
					SqlCondition sqlCondition1=new SqlCondition();
					sqlCondition1.setKey("user_id");
					sqlCondition1.setOp("=");
					sqlCondition1.setValue(userId);
					conditions.add(sqlCondition1);
					SqlCondition sqlCondition2=new SqlCondition();
					sqlCondition2.setKey("goods_id");
					sqlCondition2.setOp("=");
					sqlCondition2.setValue(goods_id);
					conditions.add(sqlCondition2);
					where.setConditions(conditions);
					action.setWhere(where);
				}
				newActions.add(action);
				
			}
			actionReqInfo.setActions(newActions);
			JSONObject newJsonObject=(JSONObject) JSONObject.toJSON(actionReqInfo);
			jsonObject.put("param", newJsonObject);
			DsManageReqInfo dsReqInfo = DataUtil.parse(jsonObject.toJSONString(), DsManageReqInfo.class);
			return mushroomAction.offer(dsReqInfo,request,response);
		} catch (Exception e) {
			e.printStackTrace();
			return "报文错误！";
		}
	}
	
	//从规则中获取购物车信息
	private void getCartByUserFromData(long userId,Map<String,Long> cartMap){
		DsManageReqInfo dsManageReqInfo=new DsManageReqInfo();
		dsManageReqInfo.setServiceName("HMJ_HSV1_ShopCart");
		dsManageReqInfo.setNeedAll("1");
		Map<String,Object> param=new HashMap<String, Object>();
		param.put("user_id",userId);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo,"");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class); 
		List<Map<String, String>> rows=responseData.getRows();
		for(Map<String,String> row:rows){
			String goods_id=row.get("goods_id");
			String suit_id=row.get("suit_id");
			long goods_number=Long.parseLong(row.get("goods_number"));
			cartMap.put(suit_id+"_"+goods_id, goods_number);
		}
	}
}
