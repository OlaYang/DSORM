package com.meiqi.liduoo.fastweixin.message;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.liduoo.fastweixin.message.util.MessageBuilder;

/**
 * @author peiyu
 */
public class VoiceMsg extends BaseMsg {
	private static final long serialVersionUID = 4729151299772410263L;
	private String mediaId;

    public VoiceMsg() {
        this.setMsgType(RespType.VOICE);
    }
    public VoiceMsg(String mediaId) {
        this.mediaId = mediaId;
        this.setMsgType(RespType.VOICE);
    }
    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.VOICE);
        mb.append("<Voice>\n");
        mb.addData("MediaId", mediaId);
        mb.append("</Voice>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }

	public boolean isValidMsg() {
		return StringUtils.isNotEmpty(mediaId) && super.isValidMsg();
	}
}
