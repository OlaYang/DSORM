package com.meiqi.openservice.action.wechat;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.config.Constants;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.wechat.service.IWeChatGroupService;

@Service
public class WeChatGroupAction extends BaseAction{
	@Autowired
	private IWeChatGroupService weChatGroupService;
	@Autowired
	private IDataAction dataAction;
	@Autowired
	private IMushroomAction mushroomAction;
	
	/**
	 * 同步用户分组
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	public String SynchronizeGroup(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		String microNo="youjiagougw";
		RuleServiceResponseData responseData = new RuleServiceResponseData();
		//调用微信接口获取所有分组
		JSONObject groupObject=weChatGroupService.getGroups();
		if(groupObject.containsKey("groups")){
			//解析报文，获取分组的集合
			JSONArray groupJSONArray=groupObject.getJSONArray("groups");
			int groupJSONArraySize=groupJSONArray.size();
			JSONObject tempJson=null;
			Map<String,JSONObject> dataMap=getGroupByData(microNo);
			Map<String,JSONObject> weChatMap=new HashMap<String, JSONObject>();
			
			// 调用mushroom 
	    	DsManageReqInfo actionReqInfo = new DsManageReqInfo();
	    	actionReqInfo.setServiceName("MUSH_Offer");
	    	List<Action> actions = new ArrayList<Action>();
	    	
	    	//新增和修改封装
			for(int i=0;i<groupJSONArraySize;i++){
				tempJson=groupJSONArray.getJSONObject(i);
				if(dataMap.containsKey(tempJson.getString("id"))){
					Action action = new Action();
					action.setServiceName("test_ecshop_mc_group");
					action.setType("U");
					Map<String, Object> set = new HashMap<String, Object>();
					set.put("group_id", tempJson.getString("id"));
					set.put("group_name", tempJson.getString("name"));
					set.put("count", tempJson.getString("count"));
					action.setSet(set);
					Where where = new Where();
			        where.setPrepend("and");
			        List<SqlCondition> cons = new ArrayList<SqlCondition>();
			        SqlCondition con = new SqlCondition();
			        con.setKey("group_id");
			        con.setOp("=");
			        con.setValue(tempJson.getString("id"));
			        cons.add(con);
			        where.setConditions(cons);
			        action.setWhere(where);
			        actions.add(action);
				}else{
					Action action = new Action();
					action.setServiceName("test_ecshop_mc_group");
					action.setType("C");
					Map<String, Object> set = new HashMap<String, Object>();
					set.put("group_id", tempJson.getString("id"));
					set.put("group_name", tempJson.getString("name"));
					set.put("count", tempJson.getString("count"));
					set.put("micro_no", microNo);
					action.setSet(set);
			        actions.add(action);
				}
				weChatMap.put(tempJson.getString("id"), tempJson);
			}
			
		   Iterator iter = dataMap.entrySet().iterator();
		   while(iter.hasNext()){
			   Map.Entry entry = (Map.Entry) iter.next();
			   String key = entry.getKey().toString();
			   if(!weChatMap.containsKey(key)){
				    Action action = new Action();
					action.setServiceName("test_ecshop_mc_group");
					action.setType("D");
					Where where = new Where();
			        where.setPrepend("and");
			        List<SqlCondition> cons = new ArrayList<SqlCondition>();
			        SqlCondition con = new SqlCondition();
			        con.setKey("group_id");
			        con.setOp("=");
			        con.setValue(tempJson.getString("id"));
			        cons.add(con);
			        where.setConditions(cons);
			        action.setWhere(where);
			        actions.add(action);
			   }
		   }
			Map<String,Object> param=new HashMap<String, Object>();
	        param.put("actions", actions);
	        param.put("transaction", 1);
	        actionReqInfo.setParam(param);
	        String res=mushroomAction.offer(actionReqInfo);
	        SetServiceResponseData actionResponse= DataUtil.parse(res, SetServiceResponseData.class);
	        if(Constants.SetResponseCode.SUCCESS.equals(actionResponse.getCode())){ //如果持久化成功
	        	responseData.setCode(DsResponseCodeData.SUCCESS.code);
	        	responseData.setDescription(DsResponseCodeData.SUCCESS.description);
	        }else{
		        responseData.setCode(DsResponseCodeData.ERROR.code);
	        	responseData.setDescription(DsResponseCodeData.ERROR.description);
	        }
		}else{
			responseData.setCode(DsResponseCodeData.ERROR.code);
        	responseData.setDescription(DsResponseCodeData.ERROR.description);
		}
		
		return JSON.toJSONString(responseData);
	}
	
	
	/**
	 * 从规则获取对应微信号的分组信息
	 * @return
	 */
	private Map<String,JSONObject> getGroupByData(String microNo){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("MC_BUV1_Group");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("micro_no",microNo);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getInnerData(dsManageReqInfo);
		JSONObject resultDataJson=JSONObject.parseObject(resultData);
		Map<String,JSONObject> groupDataMap=new HashMap<String, JSONObject>();
		if(resultDataJson.containsKey("rows")){
			JSONArray jsonArray=resultDataJson.getJSONArray("rows");
			int jsonArraySize=jsonArray.size();
			JSONObject tempJson=null;
			for(int i=0;i<jsonArraySize;i++){
				tempJson=jsonArray.getJSONObject(i);
				groupDataMap.put(tempJson.getString("group_id"), tempJson);
			}
		}
		return groupDataMap;
	}
	
}
