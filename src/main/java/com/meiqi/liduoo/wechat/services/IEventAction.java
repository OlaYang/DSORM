package com.meiqi.liduoo.wechat.services;

import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseEvent;
import com.meiqi.liduoo.fastweixin.message.req.LocationEvent;
import com.meiqi.liduoo.fastweixin.message.req.MenuEvent;
import com.meiqi.liduoo.fastweixin.message.req.QrCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.ScanCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendMessageEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendPicsInfoEvent;
import com.meiqi.liduoo.fastweixin.message.req.TemplateMsgEvent;

public interface IEventAction {
	BaseMsg handleDefaultEvent(BaseEvent event);
	/**
	 * 处理添加关注事件，有需要时子类重写
	 *
	 * @param event
	 *            添加关注事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleSubscribe(BaseEvent event);
	/**
	 * 处理扫描二维码事件，有需要时子类重写
	 *
	 * @param event
	 *            扫描二维码事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleQrCodeEvent(QrCodeEvent event);
	/**
	 * 处理地理位置事件，有需要时子类重写
	 *
	 * @param event
	 *            地理位置事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleLocationEvent(LocationEvent event);
	/**
	 * 处理菜单点击事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单点击事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleMenuClickEvent(MenuEvent event);
	/**
	 * 处理菜单跳转事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单跳转事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleMenuViewEvent(MenuEvent event);
	/**
	 * 处理菜单扫描推事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单扫描推事件对象
	 * @return 响应的消息对象
	 */
	BaseMsg handleScanCodeEvent(ScanCodeEvent event);

	/**
	 * 处理菜单弹出相册事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单弹出相册事件
	 * @return 响应的消息对象
	 */
	BaseMsg handlePSendPicsInfoEvent(SendPicsInfoEvent event);
	/**
	 * 处理模版消息发送事件，有需要时子类重写
	 *
	 * @param event
	 *            菜单弹出相册事件
	 * @return 响应的消息对象
	 */
	BaseMsg handleTemplateMsgEvent(TemplateMsgEvent event);
	/**
	 * 接收群发消息的回调方法
	 *
	 * @param event
	 *            群发回调方法
	 * @return 响应消息对象
	 */
	BaseMsg callBackAllMessage(SendMessageEvent event);
	/**
	 * 处理取消关注事件，有需要时子类重写
	 *
	 * @param event
	 *            取消关注事件对象
	 * @return 响应消息对象
	 */
	BaseMsg handleUnsubscribe(BaseEvent event);
}
