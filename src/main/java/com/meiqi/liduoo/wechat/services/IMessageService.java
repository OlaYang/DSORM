package com.meiqi.liduoo.wechat.services;

import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.ImageReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LinkReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LocationReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.TextReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VideoReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VoiceReqMsg;

public interface IMessageService {
	/**
	 * 处理默认消息
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleDefaultMsg(BaseReqMsg msg);
	/**
	 * 处理文本消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleTextMsg(TextReqMsg msg);
	/**
	 * 处理图片消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleImageMsg(ImageReqMsg msg);
	/**
	 * 处理语音消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleVoiceMsg(VoiceReqMsg msg);
	/**
	 * 处理视频消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleVideoMsg(VideoReqMsg msg);
	/**
	 * 处理小视频消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleShortVideoMsg(VideoReqMsg msg);

	/**
	 * 处理地理位置消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleLocationMsg(LocationReqMsg msg);
	/**
	 * 处理链接消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	BaseMsg handleLinkMsg(LinkReqMsg msg);

}
