package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 充值券点接口返回信息
 * 
 * @author FrankGui
 */
public class CardRechargeResponse extends BaseResponse {
	@JSONField(name = "qrcode_url")
	private String qrcodeUrl;
	@JSONField(name = "order_id")
	private String orderId;
	@JSONField(name = "qrcode_buffer")
	private String qrcodeBuffer;
	/**
	 * @return the qrcodeUrl
	 */
	public String getQrcodeUrl() {
		return qrcodeUrl;
	}
	/**
	 * @param qrcodeUrl the qrcodeUrl to set
	 */
	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the qrcodeBuffer
	 */
	public String getQrcodeBuffer() {
		return qrcodeBuffer;
	}
	/**
	 * @param qrcodeBuffer the qrcodeBuffer to set
	 */
	public void setQrcodeBuffer(String qrcodeBuffer) {
		this.qrcodeBuffer = qrcodeBuffer;
	}
	

}
