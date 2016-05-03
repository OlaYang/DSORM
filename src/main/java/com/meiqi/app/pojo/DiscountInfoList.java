package com.meiqi.app.pojo;

import java.util.List;

public class DiscountInfoList {
	private int total;

	private List<DiscountInfo> disCountInfoList;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<DiscountInfo> getDisCountInfoList() {
		return disCountInfoList;
	}

	public void setDisCountInfoList(List<DiscountInfo> disCountInfoList) {
		this.disCountInfoList = disCountInfoList;
	}
	
	public void addDisCountInfo(DiscountInfo disCountInfo) {
		this.disCountInfoList.add(disCountInfo);
	}

}
