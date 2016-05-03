package com.meiqi.openservice.bean.sort;

import java.util.List;

public class AttrInfo {
	private String attr_name;
	private List<RespUrlInfo> attr_val;
	public AttrInfo(String attr_name,List<RespUrlInfo> attr_val){
		this.attr_name=attr_name;
		this.attr_val=attr_val;
	}
	public String getAttr_name() {
		return attr_name;
	}
	public void setAttr_name(String attr_name) {
		this.attr_name = attr_name;
	}
	public List<RespUrlInfo> getAttr_val() {
		return attr_val;
	}
	public void setAttr_val(List<RespUrlInfo> attr_val) {
		this.attr_val = attr_val;
	}
}
