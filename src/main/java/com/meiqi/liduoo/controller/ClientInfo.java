package com.meiqi.liduoo.controller;

import java.io.Serializable;

/**
 * 微信请求客户端的相关渠道、粉丝信息
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午3:23:58
 */
public class ClientInfo implements Serializable {

	private static final long serialVersionUID = -7920569995996716604L;

	private String openId = null;
	private int channelId = -1;
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

}
