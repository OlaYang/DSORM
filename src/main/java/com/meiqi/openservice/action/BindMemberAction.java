package com.meiqi.openservice.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
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
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.util.LogUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年11月9日 下午6:21:29 
 * 类说明  绑定会员操作
 */
@Service
public class BindMemberAction extends BaseAction{

	@Autowired
	private IDataAction dataAction;
	
	@Autowired
	private IMushroomAction  mushroomAction;
	
	@SuppressWarnings("unchecked")
	public String bindMember(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		String resultData = "";
		ResponseInfo respInfo = new ResponseInfo();
		Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		
		String union_id = paramMap.get("union_id");
		String phone = paramMap.get("phone");
		String code = paramMap.get("code");
		String real_name = paramMap.get("real_name");
		String verify_code = paramMap.get("verify_code");
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_McUsers");
		param.put("union_id", union_id);
		param.put("phone", phone);
		param.put("code", code);
		dsManageReqInfo.setParam(param);
		
		resultData = dataAction.getData(dsManageReqInfo, "");
		LogUtil.info("BindMember YJG_HSV1_McUsers param is: "+paramMap);
		LogUtil.info("BindMember YJG_HSV1_McUsers result is: "+resultData);
		RuleServiceResponseData responseData = null;
		List<Map<String,String>> mapList= new ArrayList<Map<String,String>>();
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
			LogUtil.error("BindMember YJG_HSV1_McUsers return is: "+resultData);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("申请加入失败！");
			return JSON.toJSONString(respInfo);
	    }
		 
		mapList = responseData.getRows();
		if(mapList != null && mapList.size() > 0){
			String isCode = "";
			String user_id = "";
			String operate = "";
			String use_user_id = "";
			String is_designer = "";
			String is_invite = "";
			String role_id = "";
			for (Map<String, String> responseMap : mapList) {
				isCode = responseMap.get("isCode");
				user_id = responseMap.get("user_id");
				operate = responseMap.get("operate");
				use_user_id = responseMap.get("use_user_id");
				is_designer = responseMap.get("is_designer");
				is_invite = responseMap.get("is_invite");
				role_id = responseMap.get("roleId");
				break;
			}
			
			//判断邀约码是否有效
			if("1".equals(isCode)){
				DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
				Map<String, Object> param1 = new HashMap<String, Object>();

				dsManageReqInfo1.setNeedAll("1");
				dsManageReqInfo1.setServiceName("IPAD_HSV1_code");
				param1.put("type", "0");
				param1.put("phone", phone);
				param1.put("code", verify_code);
				dsManageReqInfo1.setParam(param1);
				
				resultData = dataAction.getData(dsManageReqInfo1, "");
				responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
				if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
					LogUtil.error("BindMember IPAD_HSV1_code param is : "+param1);
					LogUtil.error("BindMember IPAD_HSV1_code result is : "+resultData);
					respInfo.setCode(DsResponseCodeData.ERROR.code);
					respInfo.setDescription("验证验证码失败！");
					return JSON.toJSONString(respInfo);
			    }
				mapList = responseData.getRows();
				if(mapList != null && mapList.size() > 0){
					String code_is_flag = "";
					String code_id = "";
					for (Map<String, String> responseMap : mapList) {
						code_id = responseMap.get("code_id");
						code_is_flag = responseMap.get("is_flag");
						break;
					}
					//如果验证码有效则更新验证码状态
					if("1".equals(code_is_flag)){
						Action action = new Action();
						action.setServiceName("MeiqiServer_ecs_messaging_send");
						Map<String, Object> setMap = new HashMap<String, Object>();
						action.setType("U");
						setMap.put("is_valid", "0");
						action.setSet(setMap);
						Where where = new Where();
						where.setPrepend("and");
						List<SqlCondition> conditions = new ArrayList<SqlCondition>();
						SqlCondition sqlCondition = new SqlCondition();
						sqlCondition.setKey("id");
						sqlCondition.setOp("=");
						sqlCondition.setValue(code_id);
						conditions.add(sqlCondition);
						where.setConditions(conditions);
						action.setWhere(where);
						newActions.add(action);
					}else{
						LogUtil.error("BindMember IPAD_HSV1_code return code_is_flag is:"+ code_is_flag);
						respInfo.setCode(DsResponseCodeData.CODE_NOT_RIGHT.code);
						respInfo.setDescription(DsResponseCodeData.CODE_NOT_RIGHT.description);
						return JSON.toJSONString(respInfo);
					}
					
					
				}else{
					LogUtil.error("BindMember IPAD_HSV1_code result is : "+resultData);
					respInfo.setCode(DsResponseCodeData.NO_DATA.code);
		            respInfo.setDescription(DsResponseCodeData.NO_DATA.description);
		            return JSON.toJSONString(respInfo);
				}
			}else{
				LogUtil.error("BindMember YJG_HSV1_McUsers param is: "+paramMap);
				LogUtil.error("BindMember YJG_HSV1_McUsers result is: "+resultData);
				LogUtil.error("BindMember isCode is: "+isCode+" and union_id is "+union_id);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription("邀约码无效！");
				return JSON.toJSONString(respInfo);
			}
			if("1".equals(operate)){
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_users");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("C");
				setMap.put("user_name", phone);
				setMap.put("real_name", real_name);
				setMap.put("mobile_phone", phone);
				setMap.put("third_party_id", union_id);
				setMap.put("from", "5");
				setMap.put("reg_time", "$UnixTime");
				setMap.put("last_login", "$UnixTime");
				setMap.put("source", "2");
				setMap.put("is_validated", "1");
				setMap.put("role_id", role_id);
				action.setSet(setMap);
				newActions.add(action);
			}else if("2".equals(operate)){
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_users");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("U");
				setMap.put("real_name", real_name);
				setMap.put("mobile_phone", phone);
				setMap.put("third_party_id", union_id);
				setMap.put("is_validated", "1");
				setMap.put("role_id", role_id);
				action.setSet(setMap);
				Where where = new Where();
				where.setPrepend("and");
				List<SqlCondition> conditions = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition = new SqlCondition();
				sqlCondition.setKey("user_id");
				sqlCondition.setOp("=");
				sqlCondition.setValue(user_id);
				conditions.add(sqlCondition);
				where.setConditions(conditions);
				action.setWhere(where);
				newActions.add(action);
			}else if("0".equals(operate)){
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_users");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("U");
				setMap.put("user_name", phone);
				setMap.put("real_name", real_name);
				setMap.put("mobile_phone", phone);
				setMap.put("third_party_id", union_id);
				setMap.put("role_id", role_id);
				setMap.put("source", "2");
				setMap.put("is_validated", "1");
				action.setSet(setMap);
				Where where = new Where();
				where.setPrepend("and");
				List<SqlCondition> conditions = new ArrayList<SqlCondition>();
				SqlCondition sqlCondition = new SqlCondition();
				sqlCondition.setKey("user_id");
				sqlCondition.setOp("=");
				sqlCondition.setValue(user_id);
				conditions.add(sqlCondition);
				where.setConditions(conditions);
				action.setWhere(where);
				newActions.add(action);
			}
			if("0".equals(is_designer)){
				Action action = new Action();
				action.setServiceName("test_ecshop_ecs_designer_info");
				Map<String, Object> setMap = new HashMap<String, Object>();
				action.setType("C");
				if("1".equals(operate)){
					setMap.put("user_id", "$-1.generateKey");
				}else{
					setMap.put("user_id", user_id);
				}
				setMap.put("real_name", real_name);
				setMap.put("creat_time", "$UnixTime");
				setMap.put("contact_phone", phone);
				setMap.put("status", "2");
				setMap.put("settle_source", "4");
				action.setSet(setMap);
				newActions.add(action);
				
				Action action1 = new Action();
				action1.setServiceName("test_ecshop_ecs_aplayaduit_info");
				Map<String, Object> setMap1 = new HashMap<String, Object>();
				action1.setType("C");
				if("1".equals(operate)){
					setMap1.put("user_id", "$-2.generateKey");
				}else{
					setMap1.put("user_id", user_id);
				}
				setMap1.put("role", "2");
				setMap1.put("add_time", "ecs_aplayaduit_info");
				setMap1.put("enter_id", "$-1.generateKey");
				setMap1.put("check_time", "ecs_aplayaduit_info");
				setMap1.put("status", "2");
				action1.setSet(setMap1);
				newActions.add(action1);
			}
			paramMap1.put("actions", newActions);
			paramMap1.put("transaction",1);
			DsManageReqInfo dsReqInfo = new DsManageReqInfo();
			dsReqInfo.setServiceName("MUSH_Offer");
			dsReqInfo.setParam(paramMap1);
			
			String result = mushroomAction.offer(dsReqInfo);
			JSONObject job = JSONObject.parseObject(result);
			if (!DsResponseCodeData.SUCCESS.code.equals(job.get("code"))) {
				LogUtil.error("BindMember MUSHROOM param is:"+dsReqInfo);
				LogUtil.error("BindMember MUSHROOM result is:"+result);
				respInfo.setCode(DsResponseCodeData.ERROR.code);
                respInfo.setDescription("申请加入失败！");
                return JSON.toJSONString(respInfo);
		    }
			if("0".equals(is_invite) && StringUtils.isNotEmpty(user_id)){
				LogUtil.info("BindMember is_invite is: "+ is_invite+",and user_id is: "+user_id+" and result is: "+result);
				Map<String, Object> setMap = new HashMap<String, Object>();
				setMap.put("use_user_id", user_id);
				setMap.put("send_user_id", use_user_id);
				setMap.put("receive_phone", phone);
				setMap.put("use_time", "$UnixTime");
                String operateResult="";
                operateResult=addInviteCode(setMap,0,operateResult);
                if(!"".equals(operateResult)){
                	LogUtil.error("BindMember test_ecshop_lejj_invite_code result is"+ operateResult);
                    respInfo.setCode(DsResponseCodeData.ERROR.code);
                    respInfo.setDescription("U码重复！");
                    return JSON.toJSONString(respInfo);
                }
			}
			
		}else{
			LogUtil.error("BindMember YJG_HSV1_McUsers param is: "+paramMap);
			LogUtil.error("BindMember YJG_HSV1_McUsers return is: "+resultData);
			respInfo.setCode(DsResponseCodeData.NO_DATA.code);
            respInfo.setDescription(DsResponseCodeData.NO_DATA.description);
            return JSON.toJSONString(respInfo);
		}
		respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
        return JSON.toJSONString(respInfo);
	}
	
	public String addInviteCode(Map<String,Object> set,int i,String operateResult){
		List<Action> newActions=new ArrayList<Action>();
		Map<String,Object> paramMap1 = new HashMap<String, Object>();
		Action action = new Action();
		set.put("code", com.meiqi.app.common.utils.CodeUtils.getInviteCode());
		action.setServiceName("test_ecshop_lejj_invite_code");
		action.setType("C");
		action.setSet(set);
		newActions.add(action);
		paramMap1.put("actions", newActions);
		paramMap1.put("transaction",1);
		DsManageReqInfo dsReqInfo = new DsManageReqInfo();
		dsReqInfo.setParam(paramMap1);
		String result = mushroomAction.offer(dsReqInfo);
		JSONObject job = JSONObject.parseObject(result);
        i++;
        int try_num=10;
        if (!"0".equals(job.get("code"))) {
            operateResult=result;
            if(i<=try_num){
                //如果添加失败，那么重试10次
            	addInviteCode(set,i,operateResult);
            }
        }else{
            operateResult="";
        }
        LogUtil.info("BindMember addInviteCode param is: "+paramMap1);
        return operateResult;
	    }
}
