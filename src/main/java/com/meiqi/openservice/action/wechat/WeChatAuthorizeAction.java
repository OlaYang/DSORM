package com.meiqi.openservice.action.wechat;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.wechat.service.IWeChatAuthorizerService;

/**
 * 公众号第三方平台微信授权管理
 * @author duanran
 *
 */
@Service
public class WeChatAuthorizeAction extends BaseAction{
	@Autowired
	private IDataAction dataAction;
	@Autowired
	private IWeChatAuthorizerService weChatAuthorizerService;
	public String authorize(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		String microNo="";
		ResponseInfo responseInfo=new ResponseInfo();
		JSONObject jsonObject=new JSONObject();
		if(isAuthorize(microNo)){
			weChatAuthorizerService.getPublicInfo("");
		}else{
			responseInfo.setCode(DsResponseCodeData.SUCCESS.code);
			responseInfo.setDescription(DsResponseCodeData.SUCCESS.description);
			jsonObject.put("url", weChatAuthorizerService.getAuthorizerUrl());
		}
		responseInfo.setObject(jsonObject);
		return JSONObject.toJSONString(responseInfo);
	}
	
	private boolean isAuthorize(String microNo){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("MC_BUV1_AccountInfo_count");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("micro_no", microNo);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getInnerData(dsManageReqInfo);
		RuleServiceResponseData responseData = DataUtil
				.parse(resultData, RuleServiceResponseData.class);
		List<Map<String,String>> rows=responseData.getRows();
		int total=Integer.parseInt(rows.get(0).get("total"));
		return total>0?true:false;
	}
}
