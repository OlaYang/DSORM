package com.meiqi.liduoo.wechat.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.meiqi.data.util.LogUtil;
import com.meiqi.liduoo.fastweixin.message.BaseMsg;
import com.meiqi.liduoo.fastweixin.message.req.BaseEvent;
import com.meiqi.liduoo.fastweixin.message.req.LocationEvent;
import com.meiqi.liduoo.fastweixin.message.req.MenuEvent;
import com.meiqi.liduoo.fastweixin.message.req.QrCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.ScanCodeEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendMessageEvent;
import com.meiqi.liduoo.fastweixin.message.req.SendPicsInfoEvent;
import com.meiqi.liduoo.fastweixin.message.req.TemplateMsgEvent;
import com.meiqi.liduoo.wechat.services.IEventService;

@Service("eventService")
public class EventServiceImpl extends AbstractWeChatService implements IEventService {

	@Override
	public BaseMsg handleDefaultEvent(BaseEvent event) {
		LogUtil.info("EventServiceImpl.handleDefaultEvent.1: event="+event);
		return invokeService(event,"EVENT_" + event.getEventType(),null);
	}
	@Override
	public BaseMsg handleSubscribe(BaseEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleQrCodeEvent(QrCodeEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleLocationEvent(LocationEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleMenuClickEvent(MenuEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleMenuViewEvent(MenuEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleScanCodeEvent(ScanCodeEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handlePSendPicsInfoEvent(SendPicsInfoEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleTemplateMsgEvent(TemplateMsgEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg callBackAllMessage(SendMessageEvent event) {
		return handleDefaultEvent(event);
	}

	@Override
	public BaseMsg handleUnsubscribe(BaseEvent event) {
		return handleDefaultEvent(event);
	}
	/* (non-Javadoc)
	 * @see com.meiqi.liduoo.wechat.services.IEventService#handleDefaultEvent(java.util.Map)
	 */
	@Override
	public BaseMsg handleDefaultEvent(BaseEvent event,Map<String, Object> reqMap) {
		LogUtil.info("EventServiceImpl.handleDefaultEvent.2: event="+event);
		LogUtil.info("EventServiceImpl.handleDefaultEvent.2: reqMap="+reqMap);
		return invokeService(event,"EVENT_" + event.getEventType(),reqMap);
	}

}
