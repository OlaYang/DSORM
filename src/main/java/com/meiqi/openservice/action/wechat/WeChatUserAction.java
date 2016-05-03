package com.meiqi.openservice.action.wechat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.wechat.service.IWeChatUserService;


@Service
public class WeChatUserAction extends BaseAction{
	
	@Autowired
	private IWeChatUserService weChatUserService;
	
	/**
	 * 用户管理
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 */
	public String userManger(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		weChatUserService.getUser("");
		return "";
	}
}
