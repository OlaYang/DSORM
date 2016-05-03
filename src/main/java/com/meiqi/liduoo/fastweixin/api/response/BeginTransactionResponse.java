package com.meiqi.liduoo.fastweixin.api.response;

/**
 * @author peiyu
 */
public class BeginTransactionResponse extends BaseResponse {

	private String transactionNum;

	public String getTransactionNum() {
		return transactionNum;
	}

	public void setTransactionNum(String transactionNum) {
		this.transactionNum = transactionNum;
	}
}
