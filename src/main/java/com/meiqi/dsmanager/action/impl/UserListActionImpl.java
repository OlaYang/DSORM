package com.meiqi.dsmanager.action.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TService;
import com.meiqi.dsmanager.action.IUserListAction;
import com.meiqi.dsmanager.po.rule.userlist.UserInfo;
import com.meiqi.dsmanager.po.rule.userlist.UserListReqInfo;
import com.meiqi.dsmanager.po.rule.userlist.UserListRespInfo;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.util.RuleExceptionUtil;

@Service
public class UserListActionImpl implements IUserListAction {
    
    @Autowired
    private RuleExceptionUtil ruleExceptionUtil;    
    
	@Override
	public String getUserList(UserListReqInfo reqInfo) {
		final String userName = reqInfo.getUserName().trim();
        final Integer page = reqInfo.getPage();
        final Integer size = reqInfo.getSize();
        final Integer start = (page - 1) * size;
        UserListRespInfo respInfo = new UserListRespInfo();
        try{
	        Map<String, Object> param = new HashMap<String, Object>();
	        if (userName.length() != 0) {
	            param.put("user_name", userName + "%");
	        } else {
	            param.put("user_name", "");
	        }
	        param.put("start", start);
	        param.put("size", size);
	
	        final TService po = Services.getService(Services.SERVICE_USER_LIST);
	        D2Data data = DataUtil.getD2Data(po, param);
	
	        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
	        UserInfo info = null;
	
	        final int len = data.getData().length;
	        for (int i = 0; i < len; i++) {
	            info = new UserInfo();
	
	            Object user_name = data.getValue("用户登录名", i);
	            Object real_name = data.getValue("用户真实姓名", i);
	            Object job_type = data.getValue("职位类型", i);
	
	            if (user_name != null) {
	                info.setUserName(String.valueOf(user_name));
	            }
	            if (real_name != null) {
	                info.setRealName(String.valueOf(real_name));
	            }
	            if (job_type != null) {
	                info.setJobType(String.valueOf(job_type));
	            }
	
	            userInfoList.add(info);
	        }
	
	        respInfo.setUserInfoList(userInfoList);
        }catch(Exception e){
        	e.printStackTrace();
        	LogUtil.error(e.getMessage());
        	ruleExceptionUtil.run(e);
        }
        return JSON.toJSONString(respInfo);
	}

}
