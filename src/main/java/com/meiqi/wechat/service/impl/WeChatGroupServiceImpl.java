package com.meiqi.wechat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.util.HttpUtil;
import com.meiqi.wechat.service.IWeChatGroupService;
import com.meiqi.wechat.tools.PublicWeChat;

/**
 * 微信分组管理
 * @author duanran
 *
 */

@Service
public class WeChatGroupServiceImpl implements IWeChatGroupService{
	@Autowired
	private PublicWeChat publicWeChat;

	@Override
	public JSONObject createGroup(JSONObject groupJson) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/create?access_token="+ACCESS_TOKEN, groupJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject getGroups() {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpGet("https://api.weixin.qq.com/cgi-bin/groups/get?access_token="+ACCESS_TOKEN, null, 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject getGroupByOpenId(String openId) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/getid?access_token="+ACCESS_TOKEN, "{\"openid\":\""+openId+"\"}", 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject updateGroup(JSONObject groupJson) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/update?access_token="+ACCESS_TOKEN, groupJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject deleteGroup(String groupId) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/delete?access_token="+ACCESS_TOKEN, "{\"group\":{\"id\":"+groupId+"}}", 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject moveUserToGroup(JSONObject groupJson) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token="+ACCESS_TOKEN, groupJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

	@Override
	public JSONObject moveUsersToGroup(JSONObject groupJson) {
		String ACCESS_TOKEN=publicWeChat.getPublicAccessToken();
		String result=HttpUtil.httpPostData("https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token="+ACCESS_TOKEN, groupJson.toJSONString(), 3000);
		if(null==result||"".equals(result)){
			return null;
		}else{
			return JSONObject.parseObject(result);
		}
	}

}
