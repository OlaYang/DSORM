package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 批量查询卡券列表返回信息
 * 
 * @author FrankGui
 */
public class CardGetPayPriceResponse extends BaseResponse {
	@JSONField(name = "price")
	private double price;
	@JSONField(name = "order_id")
	private String orderId;
	@JSONField(name = "free_coin")
	private double freeCoin;
	@JSONField(name = "pay_coin")
	private double payCoin;

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
