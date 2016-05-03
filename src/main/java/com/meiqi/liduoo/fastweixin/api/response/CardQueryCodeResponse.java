package com.meiqi.liduoo.fastweixin.api.response;

import com.meiqi.liduoo.fastweixin.api.entity.Card;

/**
 * 查询卡券返回值
 * 
 * @author FrankGui
 */
public class CardQueryCodeResponse extends BaseResponse {
	
	private Card card;

	private String openid;

	/**
	 * @return the card
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * @param card
	 *            the card to set
	 */
	public void setCard(Card card) {
		this.card = card;
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

}
