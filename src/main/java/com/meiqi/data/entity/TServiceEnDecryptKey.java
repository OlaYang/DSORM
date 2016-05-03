package com.meiqi.data.entity;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-11-28
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 * 加解密函数ENDECRYPT私钥po
 */
public class TServiceEnDecryptKey implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
    private Integer versionID;
    private String privateKey;
    private Integer state;

   

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersionID() {
		return versionID;
	}

	public void setVersionID(Integer versionID) {
		this.versionID = versionID;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String makeMapKey(Long versionID, Long state) {
        StringBuilder sb = new StringBuilder();
        sb.append(versionID);
        sb.append("_");
        sb.append(state);
        return sb.toString();
    }
}
