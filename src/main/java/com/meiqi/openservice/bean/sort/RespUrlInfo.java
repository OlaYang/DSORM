package com.meiqi.openservice.bean.sort;

public class RespUrlInfo {
	//连接名称
	private String name;
	//url地址
	private String url;
	//是否被选中
	private String is_select;
	
	public RespUrlInfo(){
	}
	public RespUrlInfo(String name,String url,String is_select){
		this.name=name;
		this.url=url;
		this.is_select=is_select;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIs_select() {
		return is_select;
	}
	public void setIs_select(String is_select) {
		this.is_select = is_select;
	}
}
