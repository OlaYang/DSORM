package com.meiqi.app.pojo;

import java.util.ArrayList;
import java.util.List;

public class Commissions {
    private String month;
    
    private List<CommissionInfo> commissionInfoList = new ArrayList<CommissionInfo>();

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public List<CommissionInfo> getCommissionInfoList() {
		return commissionInfoList;
	}

	public void setCommissionInfoList(List<CommissionInfo> commissionInfoList) {
		this.commissionInfoList = commissionInfoList;
	}
	
    
}
