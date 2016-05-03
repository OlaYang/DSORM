package com.meiqi.liduoo.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 卡券概况数据
 * 
 * @author FrankGui
 */
public class CardBizuinInfo extends BaseDataCube {
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
	@JSONField(name = "given_cnt")
	private Integer givenCnt;
	@JSONField(name = "given_user")
	private Integer givenUser;
	@JSONField(name = "expire_cnt")
	private Integer expireCnt;
	@JSONField(name = "expire_user")
	private Integer expireUser;

	/**
	 * @return the viewCnt
	 */
	public Integer getViewCnt() {
		return viewCnt;
	}

	/**
	 * @param viewCnt
	 *            the viewCnt to set
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
	 * @param viewUser
	 *            the viewUser to set
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
	 * @param receiveCnt
	 *            the receiveCnt to set
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
	 * @param receiveUser
	 *            the receiveUser to set
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
	 * @param verifyCnt
	 *            the verifyCnt to set
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
	 * @param verifyUser
	 *            the verifyUser to set
	 */
	public void setVerifyUser(Integer verifyUser) {
		this.verifyUser = verifyUser;
	}

	/**
	 * @return the givenCnt
	 */
	public Integer getGivenCnt() {
		return givenCnt;
	}

	/**
	 * @param givenCnt
	 *            the givenCnt to set
	 */
	public void setGivenCnt(Integer givenCnt) {
		this.givenCnt = givenCnt;
	}

	/**
	 * @return the givenUser
	 */
	public Integer getGivenUser() {
		return givenUser;
	}

	/**
	 * @param givenUser
	 *            the givenUser to set
	 */
	public void setGivenUser(Integer givenUser) {
		this.givenUser = givenUser;
	}

	/**
	 * @return the expireCnt
	 */
	public Integer getExpireCnt() {
		return expireCnt;
	}

	/**
	 * @param expireCnt
	 *            the expireCnt to set
	 */
	public void setExpireCnt(Integer expireCnt) {
		this.expireCnt = expireCnt;
	}

	/**
	 * @return the expireUser
	 */
	public Integer getExpireUser() {
		return expireUser;
	}

	/**
	 * @param expireUser
	 *            the expireUser to set
	 */
	public void setExpireUser(Integer expireUser) {
		this.expireUser = expireUser;
	}

}
