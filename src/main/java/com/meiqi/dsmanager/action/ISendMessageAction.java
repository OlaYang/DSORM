package com.meiqi.dsmanager.action;


/**
 * 
 * @author fangqi
 * @date 2015年6月27日 上午10:00:41
 * @discription
 */
public interface ISendMessageAction {

	/**
	 * 
	 * @description:发送短信
	 * @param content
	 * @return:String
	 */
	public String sendMessage(String content);
}
