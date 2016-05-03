package com.meiqi.dsmanager.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.dsmanager.action.IUserListAction;
import com.meiqi.dsmanager.po.rule.userlist.UserListReqInfo;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;

@Controller
@RequestMapping("/service")
public class UserListController {

	@Autowired
	private IUserListAction userListAction;
	
	@ResponseBody
	@RequestMapping("/userlist")
	public String getUserList(HttpServletRequest request) throws IOException{
		HttpMethod method = HttpMethod.valueOf(request.getMethod());
		String content = "";
		if (HttpMethod.GET.equals(method)) {
			content = DataUtil.getNoKeyParamValue(request, content);
		} else {
			content = DataUtil.inputStream2String(request.getInputStream());
		}
		String decodeContent = "";
		try {
			decodeContent = URLDecoder.decode(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.error(e.getMessage());
		}
		UserListReqInfo reqInfo = DataUtil.parse(decodeContent, UserListReqInfo.class);
		return userListAction.getUserList(reqInfo);
	}
}
