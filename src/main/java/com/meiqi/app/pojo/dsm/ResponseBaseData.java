package com.meiqi.app.pojo.dsm;

public class ResponseBaseData {
	/**
	 * 成功或者失败。1表示失败，0表示成功
	 */
	private String code;
	/**
	 * 成功或者失败描述信息
	 */
	private String description;

	public ResponseBaseData() {

	}

	public ResponseBaseData(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
