package com.meiqi.liduoo.fastweixin.message;

import com.meiqi.app.common.utils.StringUtils;

/**
 * 提交至微信的图文消息素材
 * ====================================================================
 * 上海聚攒软件开发有限公司
 * --------------------------------------------------------------------
 * @author Nottyjay
 * @version 1.0.beta
 * ====================================================================
 */
public class MpNewsMsg extends BaseMsg {
	private static final long serialVersionUID = -8124124143777436035L;
	private String mediaId;

    public MpNewsMsg() {
        this.setMsgType(RespType.MPNEWS);
    }

    public MpNewsMsg(String mediaId) {
        this.mediaId = mediaId;
        this.setMsgType(RespType.MPNEWS);
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
    public boolean isValidMsg() {
		return StringUtils.isNotEmpty(mediaId) && super.isValidMsg();
	}
}
