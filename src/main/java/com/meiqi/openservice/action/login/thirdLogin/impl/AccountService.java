package com.meiqi.openservice.action.login.thirdLogin.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.login.LoginAction;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.config.Constants;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.service.IDownFileService;
import com.meiqi.openservice.service.IUploadFileService;
import com.meiqi.util.LogUtil;

/**
 * 三方登录成功后的帐号处理
 * 
 * @author duanran
 *
 */
@Service
public class AccountService {
	@Autowired
	private IDataAction dataAction;
	@Autowired
	private IMushroomAction mushroomAction;
	@Autowired
	private IDownFileService downFileService;
	@Autowired
	private IUploadFileService uploadFileService;
	@Autowired
	private LoginAction loginAction;

	/**
	 * 获取到第三方id后的与我方数据帐号绑定操作
	 * 
	 * @param thirdId
	 *            三方id QQ openid 微信 union_id
	 * @param type
	 *            登录平台 QQ 微信 and so on return map为null表示没有该用户
	 */
	public Map<String, String> loginUser(String thirdId, String type,
			long userId) {
		String resultData = "";
		// 判断是否有userID
		// 带有userid表示 是进行绑定操作，使用userid对应的用户信息，此登陆操作会把三方id信息绑定该userid
		if (0 < userId) {
			resultData = getDataFromData(String.valueOf(userId), "");
		} else {// 使用三方id登录则只更新登录信息
			resultData = getDataFromData(thirdId, type);
		}

		LogUtil.info("accept third login resultData=" + resultData);
		// 封装规则中得到的参数
		RuleServiceResponseData responseData = DataUtil.parse(resultData,
				RuleServiceResponseData.class);
		// 从对象中回去参数信息
		List<Map<String, String>> rows = responseData.getRows();
		// 如果在规则中找不到信息，返回null
		if (0 == rows.size()) {
			return null;
		}
		// 遍历结果
		Map<String, String> map = null;
		for (int i = 0; i < rows.size(); i++) {
			map = rows.get(i);
			if ("true".equals(map.get("is_transfer_row"))) {
				break;
			} else {
				// 如果遍历到最后都没有满足 true条件那么则取第一条数据
				if ((i + 1) == rows.size()) {
					map = rows.get(0);
				}
			}
		}

		// 将使用查询到的用户id赋值给userid，以便进行update条件
		userId = Long.parseLong(map.get("user_id"));
		// 声明一个map 封装更新的参数
		Map<String, Object> set = new HashMap<String, Object>();
		set.put("last_time", "$Date"); // 设置最后一次登录时间
		set.put("visit_count", "$EP.visit_count+1"); // 设置登录次数+1

		// 判断用户信息的三方id是否和数据相同，不同则更新到当前的三方id
		// 另一个作用，如果使用指定userid登陆，此处便可以绑定三方id
		if (Constants.LOGIN_3RD.LOGIN_TYPE_QQ.equals(type)) { // 如果是qq登录
			if (!thirdId.equals(map.get("open_id"))) {
				set.put("open_id", thirdId); // 如果qq登录的用户openid不符合已存在的，则更新
			}
		} else if (Constants.LOGIN_3RD.LOGIN_TYPE_WECHAT.equals(type)) { // 如果是微信登录
			if (!thirdId.equals(map.get("third_party_id"))) {
				set.put("third_party_id", thirdId); // 如果微信登录的用户unid不符合已存在的，则更新
			}
		}
		// 调用update操作
		updateUser(set, userId);
		return map;
	}

	public Map<String, String> getUserByUserName(String userName) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("HMJ_BUV1_userdetail");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userName);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = DataUtil.parse(resultData,
				RuleServiceResponseData.class);
		// 从对象中回去参数信息
		List<Map<String, String>> rows = responseData.getRows();
		// 如果在规则中找不到信息，返回null
		if (0 == rows.size()) {
			return null;
		}
		// 取出第一条信息
		Map<String, String> map = rows.get(0);
		return map;
	}

	/**
	 * 更新用户信息，
	 * 
	 * @param jsonObject
	 * @param type
	 * @param id
	 */
	public void updateUser(JSONObject jsonObject, String type, String id) {
		DsManageReqInfo actionReqInfo = new DsManageReqInfo();
		actionReqInfo.setServiceName("MUSH_Offer");
		actionReqInfo.setFormat("json");
		List<Action> actions = new ArrayList<Action>();
		Action action = new Action();
		action.setServiceName("test_ecshop_ecs_users");
		action.setType("U");
		actions.add(action);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("transaction", 1);
		param.put("actions", actions);
		actionReqInfo.setParam(param);

		Map<String, Object> set = new HashMap<String, Object>();
		// 微信用户
		if (Constants.LOGIN_3RD.LOGIN_TYPE_WECHAT.equals(type)) {
			String unionid = jsonObject.getString("unionid");
			set.put("user_name", unionid);
			// 微信中1=男 2=女 我方系统0=男 1-女
			Object sexCode = jsonObject.get("sex");
			if (null != sexCode) {
				set.put("sex", 1 == jsonObject.getInteger("sex") ? 0 : 1);
			}
			set.put("source", 2);
			set.put("third_party_id", unionid);
			set.put("avatar", jsonObject.getString("headimgurl"));
		} else if (Constants.LOGIN_3RD.LOGIN_TYPE_QQ.equals(type)) {// qq登录
			String open_id = jsonObject.getString("open_id");
			set.put("user_name", open_id);
			// 微信中1=男 2=女 我方系统0=男 1-女
			Object sexCode = jsonObject.get("gender");
			if (null != sexCode) {
				set.put("sex", "男".equals(jsonObject.getString("gender")) ? 0
						: 1);
			}
			set.put("source", 1);
			set.put("open_id", open_id);
			set.put("avatar", jsonObject.getString("figureurl"));
		}
		// 相同属性
		set.put("role_id", 0);
		set.put("alias", jsonObject.getString("nickname"));
		set.put("last_time", "$Date");
		set.put("reg_time", "$UnixTime");
		set.put("from", jsonObject.getInteger("from"));

		// 获取城市信息
		String cityName = jsonObject.getString("city");
		if (null != cityName) {
			if (!"".equals(cityName.trim())) {
				long cityId = getCityIdByCityNameFromData(cityName);
				if (0 < cityId) {
					set.put("region_id", cityId);
				}
			}
		}
		action.setSet(set);
		Where where = new Where();
		where.setPrepend("and");
		List<SqlCondition> cons = new ArrayList<SqlCondition>();
		SqlCondition con = new SqlCondition();
		con.setKey("user_id");
		con.setOp("=");
		con.setValue(id);
		cons.add(con);
		where.setConditions(cons);
		action.setWhere(where);
		mushroomAction.offer(actionReqInfo);
	}

	/**
	 * 新增用户 jsonObject 需要add的参数 type类型 qq 微信
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void addUser(JSONObject jsonObject, String type) {
		DsManageReqInfo actionReqInfo = new DsManageReqInfo();
		actionReqInfo.setServiceName("MUSH_Offer");
		actionReqInfo.setFormat("json");
		List<Action> actions = new ArrayList<Action>();
		Action action = new Action();
		action.setServiceName("test_ecshop_ecs_users");
		action.setType("C");
		actions.add(action);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("transaction", 1);
		param.put("actions", actions);
		actionReqInfo.setParam(param);

		Map<String, Object> set = new HashMap<String, Object>();
		// 微信用户
		if (Constants.LOGIN_3RD.LOGIN_TYPE_WECHAT.equals(type)) {
			String unionid = jsonObject.getString("unionid");
			set.put("user_name", unionid);
			// 微信中1=男 2=女 我方系统0=男 1-女
			Object sexCode = jsonObject.get("sex");
			if (null != sexCode) {
				set.put("sex", 1 == jsonObject.getInteger("sex") ? 0 : 1);
			}
			set.put("source", 2);
			set.put("third_party_id", unionid);
			String headPath = headImgDeal(jsonObject.getString("headimgurl"));
			if (null != headPath) {
				set.put("avatar", headPath);
			}
		} else if (Constants.LOGIN_3RD.LOGIN_TYPE_QQ.equals(type)) {// qq登录
			String open_id = jsonObject.getString("open_id");
			set.put("user_name", open_id);
			// 微信中1=男 2=女 我方系统0=男 1-女
			Object sexCode = jsonObject.get("gender");
			if (null != sexCode) {
				set.put("sex", "男".equals(jsonObject.getString("gender")) ? 0
						: 1);
			}
			set.put("source", 1);
			set.put("open_id", open_id);
			String headPath = headImgDeal(jsonObject.getString("figureurl"));
			if (null != headPath) {
				set.put("avatar", headPath);
			}
		}
		// 相同属性
		set.put("role_id", 0);
		set.put("alias", jsonObject.getString("nickname"));
		set.put("last_time", "$Date");
		set.put("reg_time", "$UnixTime");
		set.put("from", jsonObject.getInteger("from"));

		// 获取城市信息
		String cityName = jsonObject.getString("city");
		if (null != cityName) {
			if (!"".equals(cityName.trim())) {
				long cityId = getCityIdByCityNameFromData(cityName);
				if (0 < cityId) {
					set.put("region_id", cityId);
				}
			}
		}
		action.setSet(set);
		mushroomAction.offer(actionReqInfo);
	}

	// 头像处理
	private String headImgDeal(String url) {
		String filePath = downFileService.downPicture(url);
		if (null != filePath) {
			JSONObject headJson = uploadFileService
					.uploadToFileServer(filePath);
			downFileService.deleteFile(filePath);
			if (null != headJson) {
				return headJson.getString("path");
			}
		}
		return null;
	}

	/**
	 * 使用mushroom更新用户信息
	 * 
	 * @param set
	 * @param userId
	 */
	private void updateUser(Map<String, Object> set, long userId) {
		DsManageReqInfo actionReqInfo = new DsManageReqInfo();
		actionReqInfo.setServiceName("MUSH_Offer");
		actionReqInfo.setFormat("json");
		List<Action> actions = new ArrayList<Action>();

		Action action = new Action();
		action.setServiceName("test_ecshop_ecs_users");
		action.setType("U");
		actions.add(action);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("transaction", 1);
		param.put("actions", actions);
		actionReqInfo.setParam(param);
		action.setSet(set);
		Where where = new Where();
		where.setPrepend("and");
		List<SqlCondition> sqlConditions = new ArrayList<SqlCondition>();
		SqlCondition sqlCondition = new SqlCondition();
		sqlCondition.setOp("=");
		sqlCondition.setKey("user_id");
		sqlCondition.setValue(userId);
		sqlConditions.add(sqlCondition);
		where.setConditions(sqlConditions);
		action.setWhere(where);
		mushroomAction.offer(actionReqInfo);
	}

	/**
	 * 使用规则获取城市id，传入城市名
	 * 
	 * @param cityName
	 * @return
	 */
	private long getCityIdByCityNameFromData(String cityName) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("HMJ_BUV1_freight");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("region_name", cityName);
		dsManageReqInfo.setParam(param);
		String dataString = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(dataString, RuleServiceResponseData.class);
		List<Map<String, String>> rows = responseData.getRows();
		if (0 == rows.size()) {
			return 0;
		} else {
			Map<String, String> row = rows.get(0);
			return Long.parseLong(row.get("region_id"));
		}
	}

	/**
	 * 从规则引擎获取数据 通过qq openid
	 * 
	 * @param selectId
	 *            查询的id userid QQ的openid 微信的uninonid
	 * @return
	 */
	private String getDataFromData(String selectId, String type) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("HMJ_BUV1_userdetail");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		// 判断查询条件类型
		if (Constants.LOGIN_3RD.LOGIN_TYPE_QQ.equals(type)) {
			param.put("open_id", selectId);
		} else if (Constants.LOGIN_3RD.LOGIN_TYPE_WECHAT.equals(type)) {
			param.put("third_party_id", selectId);
		} else {
			param.put("user_id", selectId);
		}
		dsManageReqInfo.setParam(param);
		return dataAction.getData(dsManageReqInfo, "");
	}

	/**
	 * 使用规则登录。返回登录报文
	 * 
	 * @param selectId
	 * @param type
	 * @return
	 */
	public Map<String, String> loginFromData(String userId) {
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("T_HSV_ThirdLogin");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_id", userId);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData resp = DataUtil.parse(resultData,
				RuleServiceResponseData.class);
		if (DsResponseCodeData.SUCCESS.code.equals(resp.getCode())) {
			Map<String, String> result = resp.getRows().get(0);
			return result;
		} else {
			return null;
		}
	}

	public ResponseInfo IPAD_HSV1_wxLogin(Map<String, Object> paramMap,String type,String sourceFrom) {
		ResponseInfo respInfo = new ResponseInfo();
		if (!paramMap.containsKey("union_id")) {
			respInfo.setCode("1");
			respInfo.setDescription("登录失败，缺少必要参数union_id！");
			return respInfo;
		}
		
		String union_id = String.valueOf(paramMap.get("union_id"));
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("IPAD_HSV1_wxLogin");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("type", type);
		param.put("sourceFrom", sourceFrom);
		param.put("union_id", union_id);
		if (paramMap.containsKey("userName")) {
			String userName = String.valueOf(paramMap.get("userName"));
			String password = String.valueOf(paramMap.get("password"));
			if ("".equals(userName)) {
				respInfo.setCode("1");
				respInfo.setDescription("登录失败，规则IPAD_HSV1_wxLogin参数userName不能为空！");
				return respInfo;
			}

			if ("".equals(password)) {
				respInfo.setCode("1");
				respInfo.setDescription("登录失败，规则IPAD_HSV1_wxLogin参数password不能为空！");
				return respInfo;
			}
			param.put("userName", userName);
			param.put("password", password);
			
		}
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getData(dsManageReqInfo, "");
		RuleServiceResponseData responseData = null;
		responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		List<Map<String, String>> rows = responseData.getRows();
		if (rows.size() > 0) {
			Map<String, String> row = rows.get(0);
			String is_bind = row.get("is_bind");
			String login=row.get("login");
			if ("0".equals(is_bind)&&"".equals(login)) {
				respInfo.setCode("0");
				respInfo.setDescription("登录失败，规则IPAD_HSV1_wxLogin判定is_bind=0！");
				respInfo.setRows(rows);
				return respInfo;
			} else {
				if("0".equals(login)){
					respInfo.setCode("1");
					respInfo.setDescription(row.get("if_login"));
//					respInfo.setRows(rows);
					return respInfo;
				}
				String userName = row.get("user_name");
				if (!"".equals(userName)) {
					row = loginAction.getUserInfo(userName, type);
					respInfo.setCode("0");
					respInfo.setDescription("登录成功！");
					rows.clear();
					rows.add(row);
					respInfo.setRows(rows);
					return respInfo;
				}
			}
		} else {
			respInfo.setCode("1");
			respInfo.setDescription("登录失败，规则IPAD_HSV1_wxLogin返回信息为空！");
			return respInfo;
		}
		respInfo.setCode("1");
		respInfo.setDescription("登录失败！");
		return respInfo;

	}

	// public void login
}
