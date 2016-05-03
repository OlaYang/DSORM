package com.meiqi.liduoo.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meiqi.liduoo.base.services.IChannelService;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseEvent;
import com.meiqi.liduoo.fastweixin.message.req.BaseReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.ImageReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LinkReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.LocationEvent;
import com.meiqi.liduoo.fastweixin.message.req.LocationReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.MenuEvent;
import com.meiqi.liduoo.fastweixin.message.req.QrCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.ScanCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendMessageEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendPicsInfoEvent;
import com.meiqi.liduoo.fastweixin.message.req.TemplateMsgEvent;
import com.meiqi.liduoo.fastweixin.message.req.TextReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VideoReqMsg;
import com.meiqi.liduoo.fastweixin.message.req.VoiceReqMsg;
import com.meiqi.liduoo.fastweixin.servlet.WeixinControllerSupport;
import com.meiqi.liduoo.wechat.services.IEventService;
import com.meiqi.liduoo.wechat.services.IMessageService;

@RestController
// @Controller
@RequestMapping(value = "/weixin")

public class WeixinController extends WeixinControllerSupport {
	private static final Logger log = Logger.getLogger(WeixinController.class);

	@Autowired
	private IChannelService channelService;

	@Autowired
	private IMessageService msgService;
	@Autowired
	private IEventService eventService;

	/**
	 * 设置TOKEN，用于绑定微信服务器
	 */
	@Override
	protected String getToken() {
		return channelService.getChannelProperty(channelId, "WECHAT_TOKEN");
	}

	/**
	 * 使用安全模式时设置：APPID.不再强制重写，有加密需要时自行重写该方法
	 */
	@Override
	protected String getAppId() {
		return channelService.getChannelProperty(channelId, "WECHAT_APPID");
	}

	/**
	 * 使用安全模式时设置：密钥。不再强制重写，有加密需要时自行重写该方法
	 */
	@Override
	protected String getAESKey() {
		return channelService.getChannelProperty(channelId, "WECHAT_AESKEY");
	}

	/**
	 * 处理文本消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleTextMsg(TextReqMsg msg) {
		return msgService.handleTextMsg(msg);
	}

	@Override
	protected BaseMsg handleDefaultEvent(BaseEvent event) {
		return eventService.handleDefaultEvent(event);
	}
	@Override
	protected BaseMsg handleDefaultEvent(BaseEvent event,Map<String, Object> reqMap) {
		return eventService.handleDefaultEvent(event,reqMap);
	}
	@Override
	protected BaseMsg handleDefaultMsg(BaseReqMsg msg) {
		return msgService.handleDefaultMsg(msg);
	}

	/**
	 * 处理添加关注事件，有需要时子类重写
	 *
	 * @param event
	 *            添加关注事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleSubscribe(BaseEvent event) {
		return eventService.handleSubscribe(event);
	}

	/**
	 * 处理图片消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleImageMsg(ImageReqMsg msg) {
		return msgService.handleImageMsg(msg);
	}

	/**
	 * 处理语音消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleVoiceMsg(VoiceReqMsg msg) {
		return msgService.handleVoiceMsg(msg);
	}

	/**
	 * 处理视频消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleVideoMsg(VideoReqMsg msg) {
		return msgService.handleVideoMsg(msg);
	}

	/**
	 * 处理小视频消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleShortVideoMsg(VideoReqMsg msg) {
		return msgService.handleShortVideoMsg(msg);
	}

	/**
	 * 处理地理位置消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleLocationMsg(LocationReqMsg msg) {
		return msgService.handleLocationMsg(msg);
	}

	/**
	 * 处理链接消息，有需要时子类重写
	 *
	 * @param msg
	 *            请求消息对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleLinkMsg(LinkReqMsg msg) {
		return msgService.handleLinkMsg(msg);
	}

	/**
	 * 处理扫描二维码事件，有需要时子类重写
	 *
	 * @param event
	 *            扫描二维码事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleQrCodeEvent(QrCodeEvent event) {
		return eventService.handleQrCodeEvent(event);
	}

	/**
	 * 处理地理位置事件，有需要时子类重写
	 *
	 * @param event
	 *            地理位置事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleLocationEvent(LocationEvent event) {
		return eventService.handleLocationEvent(event);
	}

	/**
	 * 处理菜单点击事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单点击事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleMenuClickEvent(MenuEvent event) {
		return eventService.handleMenuClickEvent(event);
	}

	/**
	 * 处理菜单跳转事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单跳转事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleMenuViewEvent(MenuEvent event) {
		return eventService.handleMenuViewEvent(event);
	}

	/**
	 * 处理菜单扫描推事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单扫描推事件对象
	 * @return 响应的消息对象
	 */
	@Override
	protected BaseMsg handleScanCodeEvent(ScanCodeEvent event) {
		return eventService.handleScanCodeEvent(event);
	}

	/**
	 * 处理菜单弹出相册事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单弹出相册事件
	 * @return 响应的消息对象
	 */
	@Override
	protected BaseMsg handlePSendPicsInfoEvent(SendPicsInfoEvent event) {
		return eventService.handlePSendPicsInfoEvent(event);
	}

	/**
	 * 处理模版消息发送事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单弹出相册事件
	 * @return 响应的消息对象
	 */
	@Override
	protected BaseMsg handleTemplateMsgEvent(TemplateMsgEvent event) {
		return eventService.handleTemplateMsgEvent(event);
	}

	/**
	 * 接收群发消息的回调方法
	 *
	 * @param event
	 *            群发回调方法
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg callBackAllMessage(SendMessageEvent event) {
		return eventService.callBackAllMessage(event);
	}

	/**
	 * 处理取消关注事件，有需要时子类重写
	 *
	 * @param event
	 *            取消关注事件对象
	 * @return 响应消息对象
	 */
	@Override
	protected BaseMsg handleUnsubscribe(BaseEvent event) {
		return eventService.handleUnsubscribe(event);
	}

}