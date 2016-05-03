package com.meiqi.openservice.bean;
/**
 * 微信登录实体
 * @author meiqidr
 *
 */
public class ThirdLoginInfo {
	private String code;  //绑定的用户code
	private long userId=0;  //绑定的用户id
	private String from="";  //请求来源
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
