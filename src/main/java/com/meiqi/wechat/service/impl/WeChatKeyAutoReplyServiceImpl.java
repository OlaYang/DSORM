package com.meiqi.wechat.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.wechat.service.IWeChatKeyAutoReplyService;

@Service
public class WeChatKeyAutoReplyServiceImpl implements IWeChatKeyAutoReplyService{

	@Autowired
	private IDataAction dataAction;
	
	@Override
	public String keyAutoReply(String microNo, String isAttent,String key) {
		// TODO Auto-generated method stub
		JSONArray replyArray = getReplyFromDB(microNo, isAttent, key);
		return null;
	}
	
	private JSONArray getReplyFromDB(String microNo,String isAttent,String key){
		DsManageReqInfo dsManageReqInfo = new DsManageReqInfo();
		dsManageReqInfo.setServiceName("MC_HSV1_KeyAutoReply");
		dsManageReqInfo.setNeedAll("1");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("micro_no",microNo);
		param.put("is_attent",isAttent);
		param.put("key",key);
		dsManageReqInfo.setParam(param);
		String resultData = dataAction.getInnerData(dsManageReqInfo);
		JSONObject resultDataJson=JSONObject.parseObject(resultData);
		if(resultDataJson.containsKey("rows")){
			return resultDataJson.getJSONArray("rows");
		}else{
			return null;
		}
	}
}
