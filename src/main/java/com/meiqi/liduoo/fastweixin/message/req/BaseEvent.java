package com.meiqi.liduoo.fastweixin.message.req;

public class BaseEvent extends BaseReq {

    private String eventType;

    public BaseEvent() {
        setMsgType(ReqType.EVENT);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
   
}
