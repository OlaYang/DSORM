package com.meiqi.liduoo.fastweixin.message;

import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.liduoo.fastweixin.message.util.MessageBuilder;

public final class TextMsg extends BaseMsg {
	private static final long serialVersionUID = 7415491928583372843L;
	private StringBuilder contentBuilder;

    public TextMsg() {
        contentBuilder = new StringBuilder();
        this.setMsgType(RespType.TEXT);
    }

    /**
	 * @param fromUserName
	 * @param toUserName
	 */
	public TextMsg(String fromUserName, String toUserName) {
		super(fromUserName, toUserName);
		contentBuilder = new StringBuilder();
        this.setMsgType(RespType.TEXT);
	}

	public TextMsg(String content) {
        setContent(content);
    }

    public String getContent() {
        return contentBuilder.toString();
    }

    public void setContent(String content) {
        contentBuilder = new StringBuilder(content);
    }

    public TextMsg add(String text) {
        contentBuilder.append(text);
        return this;
    }

    public TextMsg addln() {
        return add("\n");
    }

    public TextMsg addln(String text) {
        contentBuilder.append(text);
        return addln();
    }

    public TextMsg addLink(String text, String url) {
        contentBuilder.append("<a href=\"").append(url).append("\">")
                .append(text).append("</a>");
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("Content", contentBuilder.toString().trim());
        mb.addData("MsgType", RespType.TEXT);
        mb.surroundWith("xml");
        return mb.toString();
    }

	public boolean isValidMsg() {
		return StringUtils.isNotEmpty(contentBuilder.toString().trim()) && super.isValidMsg();
	}
}
