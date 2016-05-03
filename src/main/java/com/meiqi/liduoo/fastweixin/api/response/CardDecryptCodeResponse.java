package com.meiqi.liduoo.fastweixin.api.response;

/**
 * 查询卡券返回值
 * 
 * @author FrankGui
 */
public class CardDecryptCodeResponse extends BaseResponse {

	private String code;

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
