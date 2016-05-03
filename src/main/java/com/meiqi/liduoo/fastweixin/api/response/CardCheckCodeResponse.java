package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 获取用户已领取卡券接口
 * 
 * @author FrankGui
 */
public class CardCheckCodeResponse extends BaseResponse {
	@JSONField(name = "exist_code")
	private String[] existCode;
	@JSONField(name = "not_exist_code")
	private String[] notExistCode;
	/**
	 * @return the existCode
	 */
	public String[] getExistCode() {
		return existCode;
	}
	/**
	 * @param existCode the existCode to set
	 */
	public void setExistCode(String[] existCode) {
		this.existCode = existCode;
	}
	/**
	 * @return the notExistCode
	 */
	public String[] getNotExistCode() {
		return notExistCode;
	}
	/**
	 * @param notExistCode the notExistCode to set
	 */
	public void setNotExistCode(String[] notExistCode) {
		this.notExistCode = notExistCode;
	}

}
