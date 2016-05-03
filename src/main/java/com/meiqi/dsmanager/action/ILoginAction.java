package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.po.rule.login.LoginReqInfo;
import com.meiqi.dsmanager.po.rule.login.LoginRespInfo;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午1:58:39
 * @discription
 */
public interface ILoginAction {
	
	/**
	 * @description:用户登录接口
	 * @param reqInfo：封装的用户登录信息，包括用户名和密码
	 * @return:LoginRespInfo
	 */
	public LoginRespInfo login(LoginReqInfo reqInfo);
}
