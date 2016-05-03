/**
 * Copyright (c) meiqi
 * 百度推广相关api接口实现
 * @Title: GetCampaignAction.java 
 * @author wanghuanwei
 * @since 2015.7.13
 * @Desciption 实现百度推广（搜索推广和网盟推广）相关API
 * 
 * */
package com.meiqi.liduoo.wechat.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IMemcacheAction;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.DataUtil;

@Service
public class GetCacheResultAction extends BaseAction {
	@Autowired
	private IMemcacheAction cache;

	public Object get(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
		// 提取请求参数参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(), Map.class);
		String cacheKey = (String) param.get("cacheKey");
		return cache.getCache(cacheKey);
	}
}
