package com.meiqi.liduoo.fastweixin.message.req;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;

public class BaseReq implements Serializable {
	String toUserName;
	String fromUserName;
	long createTime;
	String msgType;
	/**
	 * 渠道ID，Added by FrankGui
	 */
	int channelId;

	/**
	 * 获取关联的渠道ID
	 * 
	 * @return
	 */
	public int getChannelId() {
		return channelId;
	}

	/**
	 * 设置关联的渠道ID
	 * 
	 * @param channelId
	 */
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	/**
	 * 获取微信消息的toUserName节点数据，实际是微信的原始ID
	 * 
	 * @return
	 */
	public String getToUserName() {
		return toUserName;
	}

	/**
	 * 设置微信消息的toUserName节点数据，实际是微信的原始ID
	 * 
	 */
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	/**
	 * 获取微信消息的fromUserName节点数据，实际是粉丝的OpenID
	 * 
	 * @return
	 */
	public String getFromUserName() {
		return fromUserName;
	}

	/**
	 * 设置微信消息的fromUserName节点数据，实际是粉丝的OpenID
	 * 
	 */
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取消息类型：text/image/news....
	 * 
	 * @return
	 * @see ReqType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * 设置消息类型：text/image/news....
	 * 
	 * @see ReqType
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public Map<String, Object> toMap() {
		return CommonUtils.beanToMap(this);
	}
	public String toJsonString() {
		return JSONUtil.toJson(this);
	}
	
}
