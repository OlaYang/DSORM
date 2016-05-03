package com.meiqi.liduoo.fastweixin.api.response;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.liduoo.fastweixin.api.entity.Card;

/**
 * 获取用户已领取卡券接口
 * 
 * @author FrankGui
 */
public class CardGetUserCardsResponse extends BaseResponse {
	@JSONField(name = "card_list")
	private CardData cards;

	/**
	 * @return the cards
	 */
	public CardData getCards() {
		return cards;
	}

	/**
	 * @param cards
	 *            the cards to set
	 */
	public void setCards(CardData cards) {
		this.cards = cards;
	}

	public class CardData implements Serializable {
		private Card[] SimpleCard;

		public Card[] getSimpleCard() {
			return SimpleCard;
		}

		public void setSimpleCard(Card[] simpleCard) {
			SimpleCard = simpleCard;
		}
	}

	public class SimpleCard implements Serializable {
		private String code;
		@JSONField(name = "card_list")
		private String card_id;

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
		 * @return the card_id
		 */
		public String getCard_id() {
			return card_id;
		}

		/**
		 * @param card_id
		 *            the card_id to set
		 */
		public void setCard_id(String card_id) {
			this.card_id = card_id;
		}
	}

}
