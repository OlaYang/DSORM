package com.meiqi.liduoo.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 卡券概况数据
 * 
 * @author FrankGui
 */
public class CardCardInfo extends CardBizuinInfo {
	@JSONField(name = "card_id")
	private String cardId;
	@JSONField(name = "card_type")
	private Integer cardType;

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
	 * @return the cardType
	 */
	public Integer getCardType() {
		return cardType;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

}
