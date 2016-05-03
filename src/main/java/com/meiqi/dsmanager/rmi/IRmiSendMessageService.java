package com.meiqi.dsmanager.rmi;

/**
 * 
 * @author fangqi
 * @date 2015年6月27日 上午10:12:21
 * @discription
 */
public interface IRmiSendMessageService {

	/**
	 * 
	 * @description:发送短信
	 * @param content
	 * @return:String
	 */
	public String sendMessage(String content);
}
