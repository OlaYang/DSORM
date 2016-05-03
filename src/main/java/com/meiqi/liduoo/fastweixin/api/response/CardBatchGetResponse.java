package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 批量查询卡券列表返回信息
 * 
 * @author FrankGui
 */
public class CardBatchGetResponse extends BaseResponse {
	@JSONField(name = "total_num")
	private int totalNum;
	@JSONField(name = "card_id_list")
	private String[] cardIdList;

	/**
	 * @return the totalNum
	 */
	public int getTotalNum() {
		return totalNum;
	}

	/**
	 * @param totalNum
	 *            the totalNum to set
	 */
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	/**
	 * @return the cardIdList
	 */
	public String[] getCardIdList() {
		return cardIdList;
	}

	/**
	 * @param cardIdList
	 *            the cardIdList to set
	 */
	public void setCardIdList(String[] cardIdList) {
		this.cardIdList = cardIdList;
	}

}
