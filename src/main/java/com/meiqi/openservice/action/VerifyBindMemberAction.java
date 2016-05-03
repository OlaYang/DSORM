package com.meiqi.openservice.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.util.LogUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年11月9日 下午5:35:29 
 * 类说明   判断是否进入绑定会员的界面还是会员界面
 */
@Service
public class VerifyBindMemberAction extends  BaseAction{

	@Autowired
	private IDataAction dataAction;
	
	@SuppressWarnings("unchecked")
	public String isBindMember(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		
		String resultData = "";
		ResponseInfo respInfo = new ResponseInfo();
		Map<String, String> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		Map<String, Object> param = new HashMap<String, Object>();
		String union_id = paramMap.get("union_id");
		
		dsManageReqInfo.setNeedAll("1");
		dsManageReqInfo.setServiceName("YJG_HSV1_McUsers");
		param.put("union_id", union_id);
		dsManageReqInfo.setParam(param);
		
		resultData = dataAction.getData(dsManageReqInfo, "");
		
		RuleServiceResponseData responseData = null;
		responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
		if (!DsResponseCodeData.SUCCESS.code.equals(responseData.getCode())) {
	        return resultData;
	    }
		List<Map<String,String>> mapList=responseData.getRows();
		
		LogUtil.info("VerifyBindMember YJG_HSV1_McUsers return length is:"+mapList.size());
		
		if(mapList != null && mapList.size() > 0){
			String is_flag = "";
			String phone = "";
			for (Map<String, String> responseMap : mapList) {
				is_flag = responseMap.get("is_flag");
				if(!StringUtils.isEmpty(responseMap.get("mobile_phone"))){
					phone = responseMap.get("mobile_phone");
				}
			}
			DsManageReqInfo dsManageReqInfo1 = new DsManageReqInfo();
			Map<String, Object> param1 = new HashMap<String, Object>();
			dsManageReqInfo1.setNeedAll("1");
			if("0".equals(is_flag)){
				param1.put("ad_code", "card_ad");
				param1.put("enabled", "1");
				dsManageReqInfo1.setServiceName("T_BUV1_LejjAdCode");
				dsManageReqInfo1.setParam(param1);
			}else if("1".equals(is_flag)){
				param1.put("third_party_id", union_id);
				dsManageReqInfo1.setServiceName("YJG_HSV1_UsersInfo");
				dsManageReqInfo1.setParam(param1);
			}
			resultData = dataAction.getData(dsManageReqInfo1, "");
			JSONObject jsonObject = JSONObject.parseObject(resultData);
			if (!DsResponseCodeData.SUCCESS.code.equals(jsonObject.get("code"))) {
		        return resultData;
		    }
			JSONArray array = jsonObject.getJSONArray("rows");
			if(array != null && array.size() > 0){
				array.getJSONObject(0).put("phone", phone);
				array.getJSONObject(0).put("is_flag",is_flag);
			}	
			return jsonObject.toJSONString();
		}else{
			respInfo.setCode(DsResponseCodeData.NO_DATA.code);
            respInfo.setDescription(DsResponseCodeData.NO_DATA.description);
            return JSON.toJSONString(respInfo);
		}
	} 
}
