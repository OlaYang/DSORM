package com.meiqi.openservice.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.RsaKeyTools;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月5日 下午1:50:31 
 * 类说明  缓存action  针对缓存做操作的action
 */
@Service
public class MemcacheAction extends BaseAction{

	private static final Log LOG =  LogFactory.getLog("memcache");
	
	@Autowired
    private IMemcacheAction     memcacheService;
	
	/**
	 * 将传入的key和value存入到memcache中
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String setKeyAndValue(HttpServletRequest request,HttpServletResponse response,RepInfo repInfo){
		ResponseInfo respInfo = new ResponseInfo();
		boolean rsaVerify = RsaKeyTools.doRSAVerify(request, response, repInfo);
        if (!rsaVerify) {
            respInfo.setCode(DsResponseCodeData.ILLEGAL_OPERATION.code);
            respInfo.setDescription(DsResponseCodeData.ILLEGAL_OPERATION.description);
            return JSONObject.toJSONString(respInfo);
        }
		Map<String,Object> paramMap = DataUtil.parse(repInfo.getParam(), Map.class);
		String customType = (String)paramMap.get("customType");//取到业务类型，方便记录日志，是什么业务在存入缓存
		JSONArray jsonArray = JSONArray.parseArray(paramMap.get("arrays").toString());
		LOG.info("传入的参数为： "+JSONObject.parseObject(repInfo.getParam()));
		if(null == jsonArray){
			LOG.error("传入的参数为： "+JSONObject.parseObject(repInfo.getParam()));
	    	respInfo.setCode(DsResponseCodeData.ERROR.code);
			respInfo.setDescription("传入的写入参数为空！");
			return JSON.toJSONString(respInfo);
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jo =  jsonArray.getJSONObject(i);
			String key = jo.get("key").toString();
			String value = jo.get("value").toString();
			String holdTime = jo.get("holdTime").toString();
			LOG.info("存入"+key+","+value+",业务类型为: "+customType);
		    boolean flag = memcacheService.putCache(key, value, Long.valueOf(holdTime));
		    if(!flag){
		    	LOG.error("存入"+key+","+value+"到memcache失败"+",业务类型为: "+customType);
		    	LOG.error("传入的参数为： "+JSONObject.parseObject(repInfo.getParam()));
		    	respInfo.setCode(DsResponseCodeData.ERROR.code);
				respInfo.setDescription(DsResponseCodeData.ERROR.description);
				return JSON.toJSONString(respInfo);
		    }
		}
		respInfo.setCode(DsResponseCodeData.SUCCESS.code);
		respInfo.setDescription(DsResponseCodeData.SUCCESS.description);
		return JSON.toJSONString(respInfo);
		
	}
}
