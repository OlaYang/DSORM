package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.po.rule.userlist.UserListReqInfo;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午7:11:32
 * @discription 用户列表信息
 */
public interface IUserListAction {
	
	/**
	 * 
	 * @description:获取用户列表信息
	 * @param reqInfo:查询条件
	 * @return:String 返回字符串
	 */
	public String getUserList(UserListReqInfo reqInfo);
}
