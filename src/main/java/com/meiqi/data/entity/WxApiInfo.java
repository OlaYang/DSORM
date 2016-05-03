package com.meiqi.data.entity;

import java.io.Serializable;

/**
 * 
 */
public class WxApiInfo implements Serializable {
	private static final long serialVersionUID = 6780009367435182484L;
	private Integer apiId;
	private String apiName;
	private String apiUrl;// '接口请求地址',
	private String apiComment;// '接口功能概述',
	private String urlType;// 'URL类型（post、get、java-class)'
	private String apiType;// '接口类型 G 查询 U 更新 C 新增 D删除...',
	private Long addTime;// '接口添加时间',
	private String userName;// '接口配置添加人',
	private Integer apiGroupId;// '接口归属组',
	private Integer isAccessToken = 1;// '是否需要验证'

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WxApiInfo))
			return false;

		WxApiInfo apiInfo = (WxApiInfo) o;

		if (apiId != null ? !apiId.equals(apiInfo.apiId) : apiInfo.apiId != null)
			return false;
		if (apiName != null ? !apiName.equals(apiInfo.apiName) : apiInfo.apiName != null)
			return false;
		if (apiUrl != null ? !apiUrl.equals(apiInfo.apiUrl) : apiInfo.apiUrl != null)
			return false;
		if (apiGroupId != null ? !apiGroupId.equals(apiInfo.apiGroupId) : apiInfo.apiGroupId != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = apiId != null ? apiId.hashCode() : 0;
		result = 31 * result + (apiName != null ? apiName.hashCode() : 0);
		result = 31 * result + (apiUrl != null ? apiUrl.hashCode() : 0);
		result = 31 * result + (apiGroupId != null ? apiGroupId.hashCode() : 0);
		result = 31 * result + (urlType != null ? urlType.hashCode() : 0);
		result = 31 * result + (apiType != null ? apiType.hashCode() : 0);
		result = 31 * result + (isAccessToken != null ? isAccessToken.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "WxApiInfo{" + 
				"apiId=" + apiId + 
				", apiName=" + apiName + 
				", apiUrl=" + apiUrl +
				", urlType=" + urlType + 
				", apiType=" + apiType + 
				", apiGroupId=" + apiGroupId +  
				", isAccessToken=" + isAccessToken+
				 ", addTime=" + addTime + 
				 ", userName=" + userName + '}';
		//@formatter:on
	}

	public Integer getApiId() {
		return apiId;
	}

	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiComment() {
		return apiComment;
	}

	public void setApiComment(String apiComment) {
		this.apiComment = apiComment;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getApiGroupId() {
		return apiGroupId;
	}

	public void setApiGroupId(Integer apiGroupId) {
		this.apiGroupId = apiGroupId;
	}

	public Integer getIsAccessToken() {
		return isAccessToken;
	}

	public void setIsAccessToken(Integer isAccessToken) {
		this.isAccessToken = isAccessToken;
	}
}
