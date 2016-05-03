package com.meiqi.liduoo.fastweixin.message;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.app.common.utils.StringUtils;

/**
 * 提交至微信的卡券纤细
 * 
 * @author FrankGui
 * @version 1.0
 */
public class WxCardMsg extends BaseMsg {
	private static final long serialVersionUID = -7679964341643622679L;
	@JSONField(name = "card_id")
	private String cardId;
	@JSONField(name = "card_ext")
	private CardExt cardExt;

	public WxCardMsg() {
		this.setMsgType(RespType.WXCARD);
	}

	public WxCardMsg(String cardId) {
		this.cardId = cardId;
		this.setMsgType(RespType.WXCARD);
	}

	public boolean isValidMsg() {
		return StringUtils.isNotEmpty(cardId) && super.isValidMsg();
	}

	public class CardExt implements Serializable {
		private String code;
		private String openid;
		private long timestamp;
		private String signature;

		/**
		 * @return the code
		 */
		public String getCode() {
			return code;
		}

		/**
		 * @param code
		 *            the code to set
		 */
		public void setCode(String code) {
			this.code = code;
		}

		/**
		 * @return the openid
		 */
		public String getOpenid() {
			return openid;
		}

		/**
		 * @param openid
		 *            the openid to set
		 */
		public void setOpenid(String openid) {
			this.openid = openid;
		}

		/**
		 * @return the timestamp
		 */
		public long getTimestamp() {
			return timestamp;
		}

		/**
		 * @param timestamp
		 *            the timestamp to set
		 */
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		/**
		 * @return the signature
		 */
		public String getSignature() {
			return signature;
		}

		/**
		 * @param signature
		 *            the signature to set
		 */
		public void setSignature(String signature) {
			this.signature = signature;
		}
	}

	/**
	 * @return the cardId
	 */
	public String getCardId() {
		return cardId;
	}

	/**
	 * @param cardId
	 *            the cardId to set
	 */
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	/**
	 * @return the cardExt
	 */
	public CardExt getCardExt() {
		return cardExt;
	}

	/**
	 * @param cardExt
	 *            the cardExt to set
	 */
	public void setCardExt(CardExt cardExt) {
		this.cardExt = cardExt;
	}
}
