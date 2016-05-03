package com.meiqi.openservice.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.functions.CHARSMARRY;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;
import com.meiqi.openservice.commons.util.Tool;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月12日 上午9:44:03 
 * 类说明  初始化传入的规则
 */
@Service
public class InitRuleAction extends BaseAction{

	@SuppressWarnings("unchecked")
	public String initRule(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		
		ResponseInfo respInfo = new ResponseInfo();
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		
		String ruleName = paramMap.get("ruleName").toString();
		JSONObject jsonObject = null;
		if(!StringUtils.isEmpty(paramMap.get("param").toString())){
			jsonObject = JSONObject.parseObject(paramMap.get("param").toString());
		}
		
		Tool tool = new Tool();
		Log log =  LogFactory.getLog("request");
		List<Map<String,String>> mapList = null;
		try {
			mapList = (List<Map<String, String>>) tool.getRuleResult(ruleName, jsonObject, log, "InitRuleAction initRule", ruleName);
		} catch (Exception e) {
			LogUtil.error("查询"+ruleName+"失败！");
			LogUtil.error("InitRuleAction initRule"+ruleName+"param is: "+jsonObject+" 规则名为: "+ruleName);
			respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("查询"+ruleName+"失败！");
			return JSON.toJSONString(respInfo);
		}
		CHARSMARRY.map.clear();
		for (int i = 0; i < mapList.size(); i++) {
			CHARSMARRY.map.put(mapList.get(i).get("ftags").toString(), i);
		}
		
		respInfo.setCode(DsResponseCodeData.SUCCESS.code);
		respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
		return JSON.toJSONString(respInfo);
		
	}
}
