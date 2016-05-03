package com.meiqi.liduoo.fastweixin.api.response;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 获取用户已领取卡券接口
 * 
 * @author FrankGui
 */
public class CardDepositCodeResponse extends BaseResponse {
	@JSONField(name = "succ_code")
	private List<String> succCode; // 成功个数
	@JSONField(name = "duplicate_code")
	private List<String> duplicateCode; // 重复导入的code会自动被过滤。
	@JSONField(name = "fail_code")
	private List<String> failCode;
	/**
	 * @return the succCode
	 */
	public List<String> getSuccCode() {
		return succCode;
	}
	/**
	 * @param succCode the succCode to set
	 */
	public void setSuccCode(List<String> succCode) {
		this.succCode = succCode;
	}
	/**
	 * @return the duplicateCode
	 */
	public List<String> getDuplicateCode() {
		return duplicateCode;
	}
	/**
	 * @param duplicateCode the duplicateCode to set
	 */
	public void setDuplicateCode(List<String> duplicateCode) {
		this.duplicateCode = duplicateCode;
	}
	/**
	 * @return the failCode
	 */
	public List<String> getFailCode() {
		return failCode;
	}
	/**
	 * @param failCode the failCode to set
	 */
	public void setFailCode(List<String> failCode) {
		this.failCode = failCode;
	}


}
