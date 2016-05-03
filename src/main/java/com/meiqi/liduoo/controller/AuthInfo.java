package com.meiqi.liduoo.controller;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AuthInfo {
	private String rootKey;
	/**
	 * 是否通过了授权
	 */
	private boolean authed = false;
	/**
	 * 授权结果（微信中简单授权返回OauthGetTokenResponse，详细授权返回GetUserInfoResponse）
	 */
	private Object authResult;
	/**
	 * 网页授权时是否需要缓存规则执行结果
	 */
	private boolean needCacheResult = false;

	/**
	 * 包含网页授权功能的规则执行结果（完整结果）
	 */
	private Object serviceResult;

	/**
	 * 包含网页授权功能的规则执行结果（完整结果）
	 * 
	 * @return the serviceResult
	 */
	public synchronized Object getServiceResult() {
		return serviceResult;
	}

	/**
	 * 包含网页授权功能的规则执行结果（完整结果）
	 * 
	 * @param serviceResult
	 *            the serviceResult to set
	 */
	public synchronized void setServiceResult(Object serviceResult) {
		this.serviceResult = serviceResult;
	}

	/**
	 * 授权结果（微信中简单授权返回OauthGetTokenResponse，详细授权返回GetUserInfoResponse）
	 * 
	 * @return the authResult
	 */
	public Object getAuthResult() {
		return authResult;
	}

	/**
	 * 授权结果（微信中简单授权返回OauthGetTokenResponse，详细授权返回GetUserInfoResponse）
	 * 
	 * @param authResult
	 *            the authResult to set
	 */
	public void setAuthResult(Object authResult) {
		this.authResult = authResult;
	}

	/**
	 * 是否通过了授权
	 * 
	 * @return the authed
	 */
	public boolean isAuthed() {
		return authed;
	}

	/**
	 * 是否通过了授权
	 * 
	 * @param authed
	 *            the authed to set
	 */
	public void setAuthed(boolean authed) {
		this.authed = authed;
	}

	/**
	 * 网页授权时是否需要缓存规则执行结果
	 * 
	 * @return the needCacheResult
	 */
	public boolean isNeedCacheResult() {
		return needCacheResult;
	}

	/**
	 * 网页授权时是否需要缓存规则执行结果
	 * 
	 * @param needCacheResult
	 *            the needCacheResult to set
	 */
	public void setNeedCacheResult(boolean needCacheResult) {
		this.needCacheResult = needCacheResult;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public String getRootKey() {
		return rootKey;
	}

	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}

}
