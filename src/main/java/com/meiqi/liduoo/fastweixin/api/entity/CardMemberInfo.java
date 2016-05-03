package com.meiqi.liduoo.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 卡券概况数据
 * 
 * @author FrankGui
 */
public class CardMemberInfo extends BaseDataCube {
	@JSONField(name = "view_cnt")
	private Integer viewCnt;
	@JSONField(name = "view_user")
	private Integer viewUser;
	@JSONField(name = "receive_cnt")
	private Integer receiveCnt;
	@JSONField(name = "receive_user")
	private Integer receiveUser;
	@JSONField(name = "verify_cnt")
	private Integer verifyCnt;
	@JSONField(name = "verify_user")
	private Integer verifyUser;
	@JSONField(name = "active_user")
	private Integer activeUser;
	@JSONField(name = "total_user")
	private Integer totalUser;
	@JSONField(name = "total_receive_user")
	private Integer totalReceiveUser;
	/**
	 * @return the viewCnt
	 */
	public Integer getViewCnt() {
		return viewCnt;
	}
	/**
	 * @param viewCnt the viewCnt to set
	 */
	public void setViewCnt(Integer viewCnt) {
		this.viewCnt = viewCnt;
	}
	/**
	 * @return the viewUser
	 */
	public Integer getViewUser() {
		return viewUser;
	}
	/**
	 * @param viewUser the viewUser to set
	 */
	public void setViewUser(Integer viewUser) {
		this.viewUser = viewUser;
	}
	/**
	 * @return the receiveCnt
	 */
	public Integer getReceiveCnt() {
		return receiveCnt;
	}
	/**
	 * @param receiveCnt the receiveCnt to set
	 */
	public void setReceiveCnt(Integer receiveCnt) {
		this.receiveCnt = receiveCnt;
	}
	/**
	 * @return the receiveUser
	 */
	public Integer getReceiveUser() {
		return receiveUser;
	}
	/**
	 * @param receiveUser the receiveUser to set
	 */
	public void setReceiveUser(Integer receiveUser) {
		this.receiveUser = receiveUser;
	}
	/**
	 * @return the verifyCnt
	 */
	public Integer getVerifyCnt() {
		return verifyCnt;
	}
	/**
	 * @param verifyCnt the verifyCnt to set
	 */
	public void setVerifyCnt(Integer verifyCnt) {
		this.verifyCnt = verifyCnt;
	}
	/**
	 * @return the verifyUser
	 */
	public Integer getVerifyUser() {
		return verifyUser;
	}
	/**
	 * @param verifyUser the verifyUser to set
	 */
	public void setVerifyUser(Integer verifyUser) {
		this.verifyUser = verifyUser;
	}
	/**
	 * @return the activeUser
	 */
	public Integer getActiveUser() {
		return activeUser;
	}
	/**
	 * @param activeUser the activeUser to set
	 */
	public void setActiveUser(Integer activeUser) {
		this.activeUser = activeUser;
	}
	/**
	 * @return the totalUser
	 */
	public Integer getTotalUser() {
		return totalUser;
	}
	/**
	 * @param totalUser the totalUser to set
	 */
	public void setTotalUser(Integer totalUser) {
		this.totalUser = totalUser;
	}
	/**
	 * @return the totalReceiveUser
	 */
	public Integer getTotalReceiveUser() {
		return totalReceiveUser;
	}
	/**
	 * @param totalReceiveUser the totalReceiveUser to set
	 */
	public void setTotalReceiveUser(Integer totalReceiveUser) {
		this.totalReceiveUser = totalReceiveUser;
	}

}
