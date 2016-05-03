package com.meiqi.app.pojo.dsm.action;

import java.util.Map;
import java.util.Set;

import com.meiqi.app.pojo.dsm.ResponseBaseData;

public class SetServiceResponseData extends ResponseBaseData{
	//mapSet封装发生了修改的db和table
	private Map<String,Set<String>> mapSet;
	//mushroom中执行的事务号
	private String transactionNum;

	public String getTransactionNum() {
		return transactionNum;
	}

	public void setTransactionNum(String transactionNum) {
		this.transactionNum = transactionNum;
	}

	public Map<String, Set<String>> getMapSet() {
		return mapSet;
	}

	public void setMapSet(Map<String, Set<String>> mapSet) {
		this.mapSet = mapSet;
	}
	
}
