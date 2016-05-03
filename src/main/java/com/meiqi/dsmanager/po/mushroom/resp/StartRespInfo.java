package com.meiqi.dsmanager.po.mushroom.resp;

import com.meiqi.dsmanager.po.ResponseBaseData;


/** 获取一个全局事务号 返回实体
 * User: 
 * Date: 13-11-29
 * Time: 上午11:54
 */
public class StartRespInfo extends ResponseBaseData {
	/**
	 * 获取到的事务号
	 */
    private String transactionNum;

    public String getTransactionNum() {
        return transactionNum;
    }

    public void setTransactionNum(String transactionNum) {
        this.transactionNum = transactionNum;
    }
}
