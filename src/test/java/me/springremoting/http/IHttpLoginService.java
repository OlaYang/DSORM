package me.springremoting.http;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午2:44:56
 * @discription 用户登录
 */
public interface IHttpLoginService {

	/**
	 * 
	 * @description:接受登录信息，调用业务处理
	 * @param HttpServletRequest：登录信息
	 * @return:String 返回结果
	 */
	public String login(String reqStr);
}
