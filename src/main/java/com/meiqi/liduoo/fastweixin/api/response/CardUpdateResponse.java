package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 修改卡券返回值
 * 
 * @author FrankGui
 */
public class CardUpdateResponse extends BaseResponse {
	@JSONField(name = "send_check")
	private boolean sendCheck;

	/**
	 * @return the sendCheck
	 */
	public boolean isSendCheck() {
		return sendCheck;
	}

	/**
	 * @param sendCheck
	 *            the sendCheck to set
	 */
	public void setSendCheck(boolean sendCheck) {
		this.sendCheck = sendCheck;
	}

}
