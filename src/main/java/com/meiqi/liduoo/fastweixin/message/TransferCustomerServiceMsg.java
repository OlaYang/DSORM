package com.meiqi.liduoo.fastweixin.message;

import com.meiqi.liduoo.fastweixin.message.util.MessageBuilder;

public class TransferCustomerServiceMsg extends BaseMsg {
	private static final long serialVersionUID = -4644187430980966760L;

    public TransferCustomerServiceMsg() {
        this.setMsgType(RespType.TRANSFER_CUSTOM_SERVICE);
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.TRANSFER_CUSTOM_SERVICE);
        mb.surroundWith("xml");
        return mb.toString();
    }
}
