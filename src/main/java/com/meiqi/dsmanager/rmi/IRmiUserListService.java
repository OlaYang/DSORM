package com.meiqi.dsmanager.rmi;
/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午7:20:08
 * @discription
 */
public interface IRmiUserListService {
	
	/**
	 * 
	 * @description:获取用户列表
	 * @param reqStr 查询条件
	 * @return:String
	 */
	public String getUserList(String reqStr);
}
