package com.meiqi.liduoo.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 卡券信息
 * 
 * --------------------------------------------------------------------
 * 
 * @author FrankGui
 */
public class Card extends BaseModel {
	@JSONField(name = "code")
	private String code;

	@JSONField(name = "card_id")
	private String cardId;
	@JSONField(name = "begin_time")
	private long beginTime;
	@JSONField(name = "end_time")
	private long endTime;
	/**
	 * 当前code对应卡券的状态，
	 * 
	 * <pre>
	 * NORMAL 正常 
	 * CONSUMED 已核销 
	 * EXPIRE 已过期 
	 * GIFTING 转赠中 
	 * GIFT_TIMEOUT 转赠超时 
	 * DELETE 已删除，
	 * UNAVAILABLE 已失效；
	 * </pre>
	 */
	@JSONField(name = "user_card_status")
	private String userCardStatus;

	@JSONField(name = "canConsume")
	private boolean can_consume;

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
	 * @return the beginTime
	 */
	public long getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime
	 *            the beginTime to set
	 */
	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userCardStatus
	 */
	public String getUserCardStatus() {
		return userCardStatus;
	}

	/**
	 * @param userCardStatus
	 *            the userCardStatus to set
	 */
	public void setUserCardStatus(String userCardStatus) {
		this.userCardStatus = userCardStatus;
	}

	/**
	 * @return the can_consume
	 */
	public boolean isCan_consume() {
		return can_consume;
	}

	/**
	 * @param can_consume
	 *            the can_consume to set
	 */
	public void setCan_consume(boolean can_consume) {
		this.can_consume = can_consume;
	}

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

}
