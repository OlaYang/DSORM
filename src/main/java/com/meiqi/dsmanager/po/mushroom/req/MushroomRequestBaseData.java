package com.meiqi.dsmanager.po.mushroom.req;

/**
 * 请求mushroom的基本报文
* @ClassName: MushroomRequestBaseData 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author duanran
* @date 2015年6月23日 下午3:25:24 
*
 */
public class MushroomRequestBaseData {
	/*
	 * 绑定的事务号
	 */
	private String transactionNum;

	public String getTransactionNum() {
		return transactionNum;
	}

	public void setTransactionNum(String transactionNum) {
		this.transactionNum = transactionNum;
	}
	
}
