package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.offer.Action;
import com.meiqi.dsmanager.po.mushroom.offer.SqlCondition;
import com.meiqi.dsmanager.po.mushroom.offer.Where;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;

/**
 * 后台订单管理
 * 
 * @author meiqidr
 *
 */
@Service
public class BackGroupOrderAction extends BaseAction {
	@Autowired
	private IMushroomAction mushroomAction;
	@Autowired
	private IDataAction dataAction;

	public String deleteGoodsToOrder(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 申明一个返回实体。
		ResponseInfo respInfo = new ResponseInfo();
		JSONArray jsonArray=JSONArray.parseArray(repInfo.getParam());
		
		if(0==jsonArray.size()){
			respInfo.setCode("1");
			respInfo.setDescription("缺少参数！");
			return JSONObject.toJSONString(respInfo);
		}
		
		// 调用统一方法，验证必要参数
		String type = "d";
		verification(respInfo, jsonArray, type);
		if ("1".equals(respInfo.getCode())) { // 如果=1，有必要字段验证失败
			return JSONObject.toJSONString(respInfo);
		}// 获取操作人
		
		
		
		JSONObject param=jsonArray.getJSONObject(0);
		
		String actionUser = param.getString("action_user");
		String shopId=param.getString("shop_id");
		// 开始封装 参数，往商品订单表中添加商品数据
		// 封装一个DsManageReqInfo实体，封装请求
		DsManageReqInfo reqInfo = new DsManageReqInfo();
		Map<String, Object> ruleParam = new HashMap<String, Object>();
		// 重算订单价格
		// 订单id
		String orderId = param.getString("order_id");
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		JSONArray ruleJsonArray = getDataByRule(
				"YJG_HSV1_MyOrderBackgroundDiscount", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		JSONObject resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 获取老的订单金额
		String oldOrderAmount = resultJsonObejct.getString("order_amount");
		if (null == oldOrderAmount || "".equals(oldOrderAmount)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取订单金额，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		
		int jsonArraySize=jsonArray.size();
		reqInfo.setServiceName("MUSH_Offer");
		List<Action> actions = new ArrayList<Action>();
		StringBuilder goodsIds=new StringBuilder();
		for(int i=0;i<jsonArraySize;i++){
			param=jsonArray.getJSONObject(i);
			// 取出商品id，后面会用
			String goodsId = param.getString("goods_id");
			goodsIds.append(goodsId).append(",");
			String recId = param.getString("rec_id");
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_order_goods");// 设置mushroom调用的服务号
			action.setType("U");// 设置set操作类型
			param.clear();
			param.put("status", "2");
			action.setSet(param);
			SqlCondition condition = new SqlCondition();
			condition.setKey("rec_id");
			condition.setOp("=");
			condition.setValue(recId);
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			conditions.add(condition);

			Where where = new Where();
			where.setConditions(conditions);
			where.setPrepend("and");

			action.setWhere(where);
			actions.add(action);
		}
		
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("actions", actions);
		reqParam.put("transaction", 1);// 设置开启事务
		
		reqInfo.setParam(reqParam);
		
		String result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}
		return orderDeal(reqInfo, respInfo, actions, reqParam,
				ruleParam, orderId, actionUser, goodsIds.toString(),
				oldOrderAmount, type,shopId,"","","");
	}

	public String updateGoodsToOrder(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 申明一个返回实体。
		ResponseInfo respInfo = new ResponseInfo();
		
		JSONArray jsonArray=JSONArray.parseArray(repInfo.getParam());
		
		if(0==jsonArray.size()){
			respInfo.setCode("1");
			respInfo.setDescription("缺少参数！");
			return JSONObject.toJSONString(respInfo);
		}
		// 调用统一方法，验证必要参数
		String type = "u";
		verification(respInfo, jsonArray, type);
		if ("1".equals(respInfo.getCode())) { // 如果=1，有必要字段验证失败
			return JSONObject.toJSONString(respInfo);
		}
		// 获取操作人
		JSONObject param=jsonArray.getJSONObject(0);
		
		String actionUser = param.getString("action_user");
		String shopId=param.getString("shop_id");
		String goods_number=param.getString("goods_number");
		// 开始封装 参数，往商品订单表中添加商品数据
		// 封装一个DsManageReqInfo实体，封装请求
		DsManageReqInfo reqInfo = new DsManageReqInfo();
		Map<String, Object> ruleParam = new HashMap<String, Object>();
		// 重算订单价格
		// 订单id
		String orderId =  param.getString("order_id");
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		JSONArray ruleJsonArray = getDataByRule(
				"YJG_HSV1_MyOrderBackgroundDiscount", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		JSONObject resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 获取老的订单金额
		String oldOrderAmount = resultJsonObejct.getString("order_amount");
		if (null == oldOrderAmount || "".equals(oldOrderAmount)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取订单金额，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		
		int jsonArraySize=jsonArray.size();
		reqInfo.setServiceName("MUSH_Offer");
		List<Action> actions = new ArrayList<Action>();
		StringBuilder goodsIds=new StringBuilder();
		for(int i=0;i<jsonArraySize;i++){
			param=jsonArray.getJSONObject(i);
			// 取商品数量
			String goodsNumber = param.getString("goods_number");
			
			// 取出商品id，后面会用
			String goodsId = param.getString("goods_id");
			String recId = param.getString("rec_id");
			goodsIds.append(goodsId).append(",");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_order_goods");// 设置mushroom调用的服务号
			action.setType("U");// 设置set操作类型
			Map<String, Object> setMap=new HashMap<String, Object>();
			setMap.put("goods_number", goodsNumber);
			action.setSet(setMap);
			SqlCondition condition = new SqlCondition();
			condition.setKey("rec_id");
			condition.setOp("=");
			condition.setValue(recId);
			List<SqlCondition> conditions = new ArrayList<SqlCondition>();
			conditions.add(condition);

			Where where = new Where();
			where.setConditions(conditions);
			where.setPrepend("and");

			action.setWhere(where);
			actions.add(action);
		}
		
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("actions", actions);
		reqParam.put("transaction", 1);// 设置开启事务
		reqInfo.setParam(reqParam);

		String result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}
		return orderDeal(reqInfo, respInfo, actions, reqParam,
				ruleParam, orderId, actionUser, goodsIds.toString(),
				oldOrderAmount, type,shopId,"","",goods_number);
	}

	// 新增商品到订单中
	public String addGoodsToOrder(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 申明一个返回实体。
		ResponseInfo respInfo = new ResponseInfo();
		
		JSONArray jsonArray=JSONArray.parseArray(repInfo.getParam());
		
		if(0==jsonArray.size()){
			respInfo.setCode("1");
			respInfo.setDescription("缺少参数！");
			return JSONObject.toJSONString(respInfo);
		}
		// 调用统一方法，验证必要参数
		String type = "c";
		verification(respInfo, jsonArray, type);
		if ("1".equals(respInfo.getCode())) { // 如果=1，有必要字段验证失败
			return JSONObject.toJSONString(respInfo);
		}
		
		
		JSONObject param=jsonArray.getJSONObject(0);
		
		String actionUser = param.getString("action_user");
		String shopId=param.getString("shop_id");
		String informSn=param.getString("inform_sn");
		// 开始封装 参数，往商品订单表中添加商品数据
		// 封装一个DsManageReqInfo实体，封装请求
		DsManageReqInfo reqInfo = new DsManageReqInfo();

		// 获取之前的订单价格

		Map<String, Object> ruleParam = new HashMap<String, Object>();
		// 重算订单价格
		// 订单id
		String orderId = param.getString("order_id");
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		JSONArray ruleJsonArray = getDataByRule(
				"YJG_HSV1_MyOrderBackgroundDiscount", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		JSONObject resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 获取老的订单金额
		String oldOrderAmount = resultJsonObejct.getString("order_amount");
		if (null == oldOrderAmount || "".equals(oldOrderAmount)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取订单金额，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		String detail_id = param.getString("detail_id");
		ruleParam.clear();
		ruleParam.put("detail_id", detail_id);
		JSONArray ruleJsonArray1 = getDataByRule(
				"HMJ_BUV1_templatedetail", ruleParam);
		if (null == ruleJsonArray1 || ruleJsonArray1.size() == 0) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_templatedetail无法获取信息，模板明细Id:"
					+ detail_id);
			return JSONObject.toJSONString(respInfo);
		}
		
		
		
		int jsonArraySize=jsonArray.size();
		reqInfo.setServiceName("MUSH_Offer");
		List<Action> actions = new ArrayList<Action>();
		StringBuilder goodsIds=new StringBuilder();
		String is_separate_flag="";
		for(int i=0;i<jsonArraySize;i++){
			param=jsonArray.getJSONObject(i);
			param.remove("action_user");
			param.remove("inform_sn");
			if(param.containsKey("is_separate_flag")){
				is_separate_flag=param.getString("is_separate_flag");
				param.remove("is_separate_flag");
			}
			// 取出商品id，后面会用
			goodsIds.append(param.getString("goods_id")).append(",");
			
			Action action = new Action();
			action.setServiceName("test_ecshop_ecs_order_goods");// 设置mushroom调用的服务号
			action.setType("C");// 设置set操作类型
			param.put("status", 0);
			param.put("add_time", "$UnixTime");
			action.setSet(param);
			actions.add(action);
		}
		
		int num = 1;
		for (int y = 0; y < ruleJsonArray1.size(); y++) {
			String fist_unit = ruleJsonArray1.getJSONObject(y).getString("fist_unit");
			String fist_fee = ruleJsonArray1.getJSONObject(y).getString("fist_fee");
			String extend_unit = ruleJsonArray1.getJSONObject(y).getString("extend_unit");
			String extend_fee = ruleJsonArray1.getJSONObject(y).getString("extend_fee");
			String amount_percent = ruleJsonArray1.getJSONObject(y).getString("amount_percent");
			String lowest_fee = ruleJsonArray1.getJSONObject(y).getString("lowest_fee");
			String freight_type = ruleJsonArray1.getJSONObject(y).getString("freight_type");
			String template_id = ruleJsonArray1.getJSONObject(y).getString("template_id");

			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_order_goods_detail");// 设置mushroom调用的服务号
			action1.setType("C");// 设置set操作类型
			JSONObject  param1 = new JSONObject();
			param1.put("rec_id", "$-"+num+".generateKey");
			param1.put("fist_unit", fist_unit);
			param1.put("fist_fee", fist_fee);
			param1.put("extend_unit", extend_unit);
			param1.put("extend_fee", extend_fee);
			param1.put("amount_percent", amount_percent);
			param1.put("lowest_fee", lowest_fee);
			param1.put("freight_type", freight_type);
			param1.put("template_id",template_id);
			action1.setSet(param1);
			actions.add(action1);
			num = num + 1;
		}
		
		Map<String, Object> reqParam = new HashMap<String, Object>();
		reqParam.put("actions", actions);
		reqParam.put("transaction", 1);// 设置开启事务
		reqInfo.setParam(reqParam);
		String result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}

		
		return orderDeal(reqInfo, respInfo, actions, reqParam,
				ruleParam, orderId, actionUser, goodsIds.toString(),
				oldOrderAmount, type,shopId,is_separate_flag,informSn,"");

	}

	private String orderDeal(DsManageReqInfo reqInfo, ResponseInfo respInfo,
			List<Action> actions,  Map<String, Object> reqParam,
			Map<String, Object> ruleParam, String orderId, String actionUser,
			String goodsIds,  String oldOrderAmount,
			String type,String shopId,String is_separate_flag,String inform_sn,String goods_number) {
		Action action=new Action();
		// 进行规则操作，获取最新的订单状态
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		JSONArray ruleJsonArray = getDataByRule("HMJ_BUV1_ORDER_NEW", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取订单信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		JSONObject resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 从规则返回中取订单状态
		String orderStatus = resultJsonObejct.getString("order_status");
		if (null == orderStatus || "".equals(orderStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取订单状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		// 从规则返回中取发货状态
		String shippingStatus = resultJsonObejct.getString("shipping_status");
		if (null == shippingStatus || "".equals(shippingStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取发货状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		// 从规则返回中取发货状态
		String payStatus = resultJsonObejct.getString("pay_status");
		if (null == payStatus || "".equals(payStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取付款状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		// 继续复用第一步的mushroom对象，减少对象生成
		actions.clear();
		action.setServiceName("test_ecshop_ecs_order_action");
		action.setType("C");
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleParam.put("action_user", actionUser);
		ruleParam.put("order_status", orderStatus);
		ruleParam.put("shipping_status", shippingStatus);
		ruleParam.put("pay_status", payStatus);
		if ("c".equalsIgnoreCase(type)) {
			ruleParam.put("action_note", "新增了商品ID:" + goodsIds);
		} else if ("u".equalsIgnoreCase(type)) {
			ruleParam.put("action_note", "修改了商品ID:" + goodsIds);
		} else if ("d".equalsIgnoreCase(type)) {
			ruleParam.put("action_note", "删除了商品ID:" + goodsIds);
		}

		ruleParam.put("log_time", "$UnixTime");
		action.setSet(ruleParam);
		actions.add(action);
		reqParam.put("actions", actions);
		reqInfo.setParam(reqParam);
		
		String result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}

		// 取detail_id和rec_detail_id
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleParam.put("goods_status", 1);
		ruleJsonArray = getDataByRule("HMJ_BUV1_ORDER_NEW2", ruleParam);
		JSONObject recDetailJsonObj = new JSONObject();
		StringBuilder detailIdStringBuilder = new StringBuilder();
		if (null == ruleJsonArray) {// 如果为空，则没有数据,与数据-刘涛预定的是传入0
			recDetailJsonObj.put("0", "0");
			detailIdStringBuilder.append("0");
		}else{
			for (int i = 0; i < ruleJsonArray.size(); i++) {
				resultJsonObejct = ruleJsonArray.getJSONObject(i);
				String rec_id = resultJsonObejct.getString("rec_id");
				if (null == rec_id || "".equals(rec_id)) {
					respInfo.setCode("1");
					respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW2无法获取rec_id，订单号:"
							+ orderId);
					return JSONObject.toJSONString(respInfo);
				}
				String detail_id = resultJsonObejct.getString("detail_id");
				if (null == detail_id || "".equals(detail_id)) {
					respInfo.setCode("1");
					respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW2无法获取detail_id，订单号:"
							+ orderId);
					return JSONObject.toJSONString(respInfo);
				}
				recDetailJsonObj.put(rec_id, detail_id);
				if ((i + 1) == ruleJsonArray.size()) {
					detailIdStringBuilder.append(detail_id);
				} else {
					detailIdStringBuilder.append(detail_id).append(",");
				}
			}
		}
		
		
		

		// 通过规则取运费和商品金额
		ruleParam.clear(); // 清理对象中的元素，重新封装元素
		ruleParam.put("order_id", orderId);
		ruleParam.put("type", "1");
		ruleParam.put("rec_detail_id", recDetailJsonObj.toJSONString());
		ruleParam.put("detail_id", detailIdStringBuilder.toString());

		ruleJsonArray = getDataByRule("HMJ_HSV1_ShopCart_Order", ruleParam); //
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_HSV1_ShopCart_Order无法获取订单信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 从规则返回中取订单状态
		String feeAmout = resultJsonObejct.getString("fee_amout");// 取运费
		if (null == feeAmout || "".equals(feeAmout)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_HSV1_ShopCart_Order无法获取运费，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		String goodsTotalMoney = resultJsonObejct
				.getString("goods_total_money");// 取运费
		if (null == goodsTotalMoney || "".equals(goodsTotalMoney)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_HSV1_ShopCart_Order无法获取商品总金额，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		// 重新跑订单活动（1）
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleJsonArray = getDataByRule("YJG_BUV1_Order_discount", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_BUV1_Order_discount无法获取信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		StringBuilder bonusIds = new StringBuilder();
		int ruleJsonArraySize = ruleJsonArray.size();
		for (int i = 0; i < ruleJsonArray.size(); i++) {
			resultJsonObejct = ruleJsonArray.getJSONObject(i);
			// 从规则返回中取值
			String bonusId = resultJsonObejct.getString("bonus_id");// 取运费
			if (null == bonusId || "".equals(bonusId)) { // 查询返回没有订单状态
				respInfo.setCode("1");
				respInfo.setDescription("规则YJG_BUV1_Order_discount无法获取活动id，订单号:"
						+ orderId);
				return JSONObject.toJSONString(respInfo);
			}
			if (ruleJsonArraySize == (i + 1)) {
				bonusIds.append(bonusId);
			} else {
				bonusIds.append(bonusId).append(",");
			}
		}

		// 重跑订单活动（2）
		ruleParam.clear();
		ruleParam.put("bonus_id", bonusIds.toString());
		ruleParam.put("order_id", orderId);
		ruleJsonArray = getDataByRule("YJG_HSV1_OrderBackActityCP", ruleParam);
		double disBonus = 0;
		if (null != ruleJsonArray) {// 如果不为空，计算活动优惠价
			ruleJsonArraySize = ruleJsonArray.size();

			for (int i = 0; i < ruleJsonArraySize; i++) {
				resultJsonObejct = ruleJsonArray.getJSONObject(i);
				// 从规则返回中取值
				String disBonusId = resultJsonObejct.getString("dis_money_1");// 取订单折扣金额
				if (null == disBonusId || "".equals(disBonusId)) { // 查询返回没有订单状态
					respInfo.setCode("1");
					respInfo.setDescription("规则YJG_HSV1_OrderBackActityCP无法获取订单折扣金额，订单号:"
							+ orderId + "活动id:" + bonusIds);
					return JSONObject.toJSONString(respInfo);
				}
				disBonus += Double.parseDouble(disBonusId);
			}
		}

		// 重跑红包活动和折扣码活动
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleJsonArray = getDataByRule("YJG_HSV1_OrderBackBonusCP", ruleParam);
		double bonus_discount = 0, discount = 0;

		List<String> sequenceIds = new ArrayList<String>();
		if (null != ruleJsonArray) {// 如果不为空，计算红包和折扣活动
			for (int i = 0; i < ruleJsonArray.size(); i++) {
				resultJsonObejct = ruleJsonArray.getJSONObject(i);
				String disRedMoney = resultJsonObejct.getString("dis_money_1");// 取订单折扣金额
				if (null == disRedMoney) { // 查询返回没有订单状态
					respInfo.setCode("1");
					respInfo.setDescription("规则YJG_HSV1_OrderBackBonusCP无法获取红包折扣金额，订单号:"
							+ orderId);
					return JSONObject.toJSONString(respInfo);
				}

				String sequenceType = resultJsonObejct
						.getString("sequence_type");// 取红包序列类型 0 红包 1 折扣码
				if (null == sequenceType || "".equals(sequenceType)) {
					respInfo.setCode("1");
					respInfo.setDescription("规则YJG_HSV1_OrderBackBonusCP无法获取红包序列类型，订单号:"
							+ orderId);
					return JSONObject.toJSONString(respInfo);
				}

				String sequenceId = resultJsonObejct.getString("sequence_id");
				if (null == sequenceId || "".equals(sequenceId)) {
					respInfo.setCode("1");
					respInfo.setDescription("规则YJG_HSV1_OrderBackBonusCP无法获取红包序列号，订单号:"
							+ orderId);
					return JSONObject.toJSONString(respInfo);
				}

				if ("1".equals(sequenceType) || !"".equals(disRedMoney)) {
					discount += Double.parseDouble(disRedMoney);
				} else if ("0".equals(sequenceType) || !"".equals(sequenceType)) {
					bonus_discount += Double.parseDouble(disRedMoney);
				}

				if ("0".equals(disRedMoney)) {
					sequenceIds.add(sequenceId);
				}
			}
		}

		//写订单运费
		actions.clear();
		Action tempAction = new Action();
		tempAction.setServiceName("test_ecshop_ecs_order_info");
		Map<String, Object> tempParam = new HashMap<String, Object>();
		tempParam.put("shipping_fee", feeAmout);
		tempAction.setSet(tempParam);
		List<SqlCondition> tempConditions = new ArrayList<SqlCondition>();
		SqlCondition tempCondition = new SqlCondition();
		tempCondition.setKey("order_id");
		tempCondition.setOp("=");
		tempCondition.setValue(orderId);
		tempConditions.add(tempCondition);
		Where tempWhere = new Where();
		tempWhere.setConditions(tempConditions);
		tempWhere.setPrepend("and");
		tempAction.setWhere(tempWhere);
		tempAction.setType("U");
		actions.add(tempAction);
		
		
		// 更新订单折扣表
		action.setServiceName("test_ecshop_ecs_order_discount");
		ruleParam.clear();
		ruleParam.put("discount", discount);
		ruleParam.put("bonus_discount", bonus_discount);
		ruleParam.put("order_discount", disBonus);
		List<SqlCondition> conditions = new ArrayList<SqlCondition>();
		SqlCondition condition = new SqlCondition();
		condition.setKey("order_id");
		condition.setOp("=");
		condition.setValue(orderId);
		Where where = new Where();
		where.setPrepend("and");
		conditions.add(condition);
		where.setConditions(conditions);

		action.setWhere(where);
		action.setSet(ruleParam);
		action.setType("U");
		actions.add(action);
		reqParam.put("actions", actions);
		reqInfo.setParam(reqParam);
		
		result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}

		// 重算订单价格
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleJsonArray = getDataByRule("YJG_HSV1_MyOrderBackgroundDiscount",
				ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		resultJsonObejct = ruleJsonArray.getJSONObject(0);

		String updateDiscount = resultJsonObejct.getString("update_discount");// 取总折扣价
		if (null == updateDiscount || "".equals(updateDiscount)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取总折扣价，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		String orderAmount = resultJsonObejct.getString("order_amount");// 取总金额
		if (null == orderAmount || "".equals(orderAmount)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取订单总金额，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		String payStatus1 = resultJsonObejct.getString("pay_status");// 取付款状态
		if (null == payStatus1 || "".equals(payStatus1)) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取付款状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		
		String commodity_discount = resultJsonObejct.getString("commodity_discount");// 取付款状态
		if (null == commodity_discount) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取批发补充折扣，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		
		String order_status1 = resultJsonObejct.getString("order_status");// 取付款状态
		if (null == order_status1) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取订单状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		
		String shipping_status1 = resultJsonObejct.getString("shipping_status");// 取付款状态
		if (null == shipping_status1) {
			respInfo.setCode("1");
			respInfo.setDescription("规则YJG_HSV1_MyOrderBackgroundDiscount无法获取发货状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		

		// 更新订单信息表、写日志和更新红包表
		// 更新订单表
		actions.clear();
		ruleParam.clear();
		ruleParam.put("order_amount", orderAmount);
		ruleParam.put("preferent", updateDiscount);
		ruleParam.put("pay_status", payStatus1);
		ruleParam.put("goods_amount", goodsTotalMoney);
		ruleParam.put("commodity_discount", commodity_discount);
		ruleParam.put("order_status", order_status1);
		ruleParam.put("shipping_status", shipping_status1);
		action.setSet(ruleParam);
		action.setType("U");
		action.setServiceName("test_ecshop_ecs_order_info");
		// 更新红包表
		if (sequenceIds.size()>0) {
			tempAction.setServiceName("test_ecshop_ecs_bonus_sequence");
			tempParam.clear();
			tempParam.put("user_time", "0");
			tempParam.put("user_id", "0");
			tempParam.put("order_id", "0");
			tempParam.put("bonus_status", "0");
			tempAction.setSet(tempParam);
			tempConditions.clear();
			tempCondition.setKey("sequence_id");
			tempCondition.setOp("in");
			tempCondition.setValue(sequenceIds.toArray());
			tempConditions.add(tempCondition);
			tempWhere.setConditions(tempConditions);
			tempWhere.setPrepend("and");
			tempAction.setWhere(tempWhere);
			tempAction.setType("U");
			actions.add(tempAction);
		}
		conditions.clear();
		condition.setKey("order_id");
		condition.setValue(orderId);
		condition.setOp("=");
		conditions.add(condition);
		where.setConditions(conditions);
		where.setPrepend("and");
		action.setWhere(where);
		actions.add(action);
		reqParam.put("actions", actions);
		reqInfo.setParam(reqParam);
		System.out.println(JSONObject.toJSONString(reqInfo));
		result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}

		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleJsonArray = getDataByRule("HMJ_BUV1_ORDER_NEW", ruleParam);
		if (null == ruleJsonArray) {// 如果为空，则没有数据
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取订单信息，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		resultJsonObejct = ruleJsonArray.getJSONObject(0);
		// 从规则返回中取订单状态
		orderStatus = resultJsonObejct.getString("order_status");
		if (null == orderStatus || "".equals(orderStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取订单状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}

		// 从规则返回中取发货状态
		shippingStatus = resultJsonObejct.getString("shipping_status");
		if (null == shippingStatus || "".equals(shippingStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取发货状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		// 从规则返回中取发货状态
		payStatus = resultJsonObejct.getString("pay_status");
		if (null == payStatus || "".equals(payStatus)) { // 查询返回没有订单状态
			respInfo.setCode("1");
			respInfo.setDescription("规则HMJ_BUV1_ORDER_NEW无法获取付款状态，订单号:"
					+ orderId);
			return JSONObject.toJSONString(respInfo);
		}
		actions.clear();
		// 写入订单日志
		ruleParam.clear();
		ruleParam.put("order_id", orderId);
		ruleParam.put("action_user", actionUser);
		ruleParam.put("order_status", orderStatus);
		ruleParam.put("shipping_status", shippingStatus);
		ruleParam.put("pay_status", payStatus);
		ruleParam.put("action_note", "订单金额从" + oldOrderAmount + "变为"
				+ orderAmount);
		ruleParam.put("log_time", "$UnixTime");
		action.setServiceName("test_ecshop_ecs_order_action");
		action.setType("C");
		action.setSet(ruleParam);
		actions.add(action);
		if("u".equalsIgnoreCase(type)){
			Action action1 = new Action();
			action1.setServiceName("test_ecshop_ecs_order_action");
			Map<String, Object> setMap1 = new HashMap<String, Object>();
			action1.setType("C");
			setMap1.put("order_id", orderId);
			setMap1.put("action_user", actionUser);
			setMap1.put("order_status", orderStatus);
			setMap1.put("shipping_status", shippingStatus);
			setMap1.put("pay_status", payStatus);
			setMap1.put("action_note", "修改了商品ID:" + goodsIds+"的数量为"+goods_number);
			setMap1.put("log_time", "$UnixTime");
			action1.setSet(setMap1);
			actions.add(action1);
		}
		//需求 #12850
		if("1".equals(is_separate_flag)){
			Action action1=new Action();
			action1.setType("c");
			action1.setServiceName("test_ecshop_ecs_delivery_inform");
			Map<String,Object> map1=new HashMap<String, Object>();
			map1.put("inform_sn", inform_sn);
			map1.put("order_id", orderId);
			map1.put("shop_id", shopId);
			map1.put("add_time", "$UnixTime");
			action1.setSet(map1);
			actions.add(action1);
			
			Action action2=new Action();
			action2.setType("u");
			action2.setServiceName("test_ecshop_ecs_order_goods");
			Map<String,Object> map2=new HashMap<String, Object>();
			map2.put("inform_id", "$-1.generateKey");
			action2.setSet(map2);
			Where where2=new Where();
			where2.setPrepend("and");
			List<SqlCondition> sqlConditionList=new ArrayList<SqlCondition>();
			SqlCondition sqlCondition1=new SqlCondition();
			sqlCondition1.setKey("order_id");
			sqlCondition1.setOp("=");
			sqlCondition1.setValue(orderId);
			SqlCondition sqlCondition2=new SqlCondition();
			sqlCondition2.setKey("shop_id");
			sqlCondition2.setOp("=");
			sqlCondition2.setValue(shopId);
			sqlConditionList.add(sqlCondition1);
			sqlConditionList.add(sqlCondition2);
			where2.setConditions(sqlConditionList);
			action2.setWhere(where2);
			actions.add(action2);
			
			Action action3=new Action();
			action3.setType("c");
			action3.setServiceName("test_ecshop_ecs_order_action");
			Map<String,Object> map3=new HashMap<String, Object>();
			map3.put("order_id", orderId);
			map3.put("action_user", actionUser);
			map3.put("order_status", orderStatus);
			map3.put("shipping_status", shippingStatus);
			map3.put("pay_status", payStatus);
			map3.put("action_note", "【发货单"+inform_sn+"】新增了商品id："+goodsIds);
			map3.put("log_time", "$UnixTime");
			map3.put("inform_id", "$-2.generateKey");
			action3.setSet(map3);
			actions.add(action3);
		}
		reqParam.put("actions", actions);
		reqInfo.setParam(reqParam);
		result1 = mushroomAction.offer(reqInfo);
		resultJsonObejct = JSONObject.parseObject(result1);// 将执行结果转换为json对象，方便后续处理
		if (!resultJsonObejct.containsKey("code")
				|| !"0".equals(resultJsonObejct.getString("code"))) {// 如果返回报文没有code或者code不为0，则错误
			respInfo.setCode("1");
			respInfo.setDescription(resultJsonObejct.getString("description"));
			return JSONObject.toJSONString(respInfo);
		}
		respInfo.setCode("0");
		respInfo.setDescription("成功");
		return JSONObject.toJSONString(respInfo);
	}

	// 从规则获取数据
	private JSONArray getDataByRule(String serviceName,
			Map<String, Object> param) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName(serviceName);
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setParam(param);
		
		String resultData = dataAction.getData(dsManageReqInfo, "");
		JSONObject JB = JSONObject.parseObject(resultData);
		if (JB.containsKey("code") && "0".equals(JB.get("code"))) {
			JSONArray jbArray = JB.getJSONArray("rows");
			if (0 < jbArray.size()) {
				return jbArray;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 验证参数
	 * 
	 * @return
	 */
	private void verification(ResponseInfo respInfo, JSONArray jsonArray,String type) {
		int jsonArraySize=jsonArray.size();
		for(int i=0;i<jsonArraySize;i++){
			JSONObject param=jsonArray.getJSONObject(i);
			
			if (!param.containsKey("order_id")) { // 验证订单
				respInfo.setCode("1");
				respInfo.setDescription("缺少order_id参数！");
			} else if (!param.containsKey("goods_id")) { // 验证商品id
				respInfo.setCode("1");
				respInfo.setDescription("缺少goods_id参数！");
			} else if (!param.containsKey("goods_sn")) { // 验证商品id
				respInfo.setCode("1");
				respInfo.setDescription("缺少goods_sn参数！");
			} else if (!param.containsKey("goods_number")) { // 验证商品数量
				respInfo.setCode("1");
				respInfo.setDescription("缺少goods_number参数！");
			} else if (!param.containsKey("shop_id")) { // 验证店铺id
				respInfo.setCode("1");
				respInfo.setDescription("缺少shop_id参数！");
			} else if (!param.containsKey("action_user")) { // 验证店铺id
				respInfo.setCode("1");
				respInfo.setDescription("缺少action_user参数！");
			}
			
			if("c".equalsIgnoreCase(type)){
				if (!param.containsKey("trade_price")) { // 验证拿货价
					respInfo.setCode("1");
					respInfo.setDescription("缺少trade_price参数！");
				}else if (!param.containsKey("goods_weight")) { // 验证商品总量
					respInfo.setCode("1");
					respInfo.setDescription("缺少goods_weight参数！");
				}else if (!param.containsKey("goods_volume")) { // 验证商品体积
					respInfo.setCode("1");
					respInfo.setDescription("缺少goods_volume参数！");
				}else if (!param.containsKey("shipping_name")) { // 验证商品配送名称
					respInfo.setCode("1");
					respInfo.setDescription("缺少shipping_name参数！");
				} else if (!param.containsKey("detail_id")) { // 验证商品配送模板id
					respInfo.setCode("1");
					respInfo.setDescription("缺少detail_id参数！");
				} else if (!param.containsKey("act_id")) { // 验证折扣id
					respInfo.setCode("1");
					respInfo.setDescription("缺少act_id参数！");
				} else if (!param.containsKey("discount")) { // 验证折扣
					respInfo.setCode("1");
					respInfo.setDescription("缺少discount参数！");
				} else if (!param.containsKey("shop_price")) { // 验证本店价格
					respInfo.setCode("1");
					respInfo.setDescription("缺少shop_price参数！");
				} else if (!param.containsKey("goods_price")) { // 验证商品执行价
					respInfo.setCode("1");
					respInfo.setDescription("缺少goods_price参数！");
				} else if (!param.containsKey("goods_name")) { // 验证商品名称
					respInfo.setCode("1");
					respInfo.setDescription("缺少goods_name参数！");
				} 
			}
			
			if ("u".equalsIgnoreCase(type) || "d".equalsIgnoreCase(type)) {
				if (!param.containsKey("rec_id")) { // 验证rec_id
					respInfo.setCode("1");
					respInfo.setDescription("缺少rec_id参数！");
				}
			}
			
		}
		
	}

	
}
