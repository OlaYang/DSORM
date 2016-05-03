package com.meiqi.liduoo.fastweixin.message;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.liduoo.fastweixin.message.util.MessageBuilder;

/**
 * @author peiyu
 */
public class ImageMsg extends BaseMsg {
	private static final long serialVersionUID = 5520461853685119773L;
	private String mediaId;

	public ImageMsg() {
		this.setMsgType(RespType.IMAGE);
	}

	public ImageMsg(String mediaId) {
		this.mediaId = mediaId;
		this.setMsgType(RespType.IMAGE);
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
		mb.addData("MsgType", RespType.IMAGE);
		mb.append("<Image>\n");
		mb.addData("MediaId", mediaId);
		mb.append("</Image>\n");
		mb.surroundWith("xml");
		return mb.toString();
	}

	public boolean isValidMsg() {
		return StringUtils.isNotEmpty(mediaId) && super.isValidMsg();
	}
}
