package com.meiqi.liduoo.fastweixin.api.response;

/**
 * @author peiyu
 */
public class GetSignatureResponse extends BaseResponse {
	private String appId;
    private String nonceStr;
    private long   timestamp;
    private String url;
    private String signature;

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String noncestr) {
        this.nonceStr = noncestr;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
