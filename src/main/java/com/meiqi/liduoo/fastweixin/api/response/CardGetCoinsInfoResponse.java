package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 查询券点余额接口返回信息
 * 
 * @author FrankGui
 */
public class CardGetCoinsInfoResponse extends BaseResponse {
	@JSONField(name = "total_coin")
	private double totalCoin;
	@JSONField(name = "free_coin")
	private double freeCoin;
	@JSONField(name = "pay_coin")
	private double payCoin;

	/**
	 * @return the totalCoin
	 */
	public double getTotalCoin() {
		return totalCoin;
	}

	/**
	 * @param totalCoin
	 *            the totalCoin to set
	 */
	public void setTotalCoin(double totalCoin) {
		this.totalCoin = totalCoin;
	}

	/**
	 * @return the freeCoin
	 */
	public double getFreeCoin() {
		return freeCoin;
	}

	/**
	 * @param freeCoin
	 *            the freeCoin to set
	 */
	public void setFreeCoin(double freeCoin) {
		this.freeCoin = freeCoin;
	}

	/**
	 * @return the payCoin
	 */
	public double getPayCoin() {
		return payCoin;
	}

	/**
	 * @param payCoin
	 *            the payCoin to set
	 */
	public void setPayCoin(double payCoin) {
		this.payCoin = payCoin;
	}

}
