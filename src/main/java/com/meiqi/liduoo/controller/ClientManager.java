package com.meiqi.liduoo.controller;

/**
 * 当前Session中的渠道、粉丝信息
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午3:24:25
 */
public class ClientManager {

	private static final ThreadLocal<ClientInfo> client = new ThreadLocal<ClientInfo>();
	
	public static ClientInfo get() {
		ClientInfo ci = client.get();
		if (ci == null) {
			ci = new ClientInfo();
			set(ci);
		}
		return ci;
	}

	public static void set(ClientInfo clientInfo) {
		client.set(clientInfo);
	}

	public static void setOpenId(String openId) {
		get().setOpenId(openId);
	}

	public static  void setChannelId(int chanelId) {
		get().setChannelId(chanelId);
	}
}
