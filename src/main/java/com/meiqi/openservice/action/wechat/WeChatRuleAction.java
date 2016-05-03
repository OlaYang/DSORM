package com.meiqi.openservice.action.wechat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.action.GetAction;
import com.meiqi.openservice.bean.RepInfo;

//微信请求规则服务

@Service
public class WeChatRuleAction extends BaseAction{
	@Autowired
	private GetAction getAction;
	
	private int allQueue=1;
	private int countQueue=0;
	public String queue(HttpServletRequest request,HttpServletResponse response, RepInfo repInfo){
		if(allQueue==countQueue){
			return null;
		}
		countQueue++;
		String getActionMsg=getAction.get(request, response, repInfo);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		countQueue--;
		return getActionMsg;
	}
}
