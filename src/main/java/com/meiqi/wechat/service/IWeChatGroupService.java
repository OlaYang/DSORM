package com.meiqi.wechat.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信分组
 * @author duanran
 *
 */
public interface IWeChatGroupService {
	//创建分组
	public JSONObject createGroup(JSONObject groupJson);
	
	//获取所有分组信息
	public JSONObject getGroups();
	
	//获取用户所在的分组
	public JSONObject getGroupByOpenId(String openId);
		
	//更新分组名称
	public JSONObject updateGroup(JSONObject groupJson);
	
	//删除分组
	public JSONObject deleteGroup(String groupId);
	
	//移动某个用户到分组
	public JSONObject moveUserToGroup(JSONObject groupJson);
	
	//移动多个用户到分组
	public JSONObject moveUsersToGroup(JSONObject groupJson);
}
