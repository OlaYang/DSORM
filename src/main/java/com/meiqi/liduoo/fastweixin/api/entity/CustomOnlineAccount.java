package com.meiqi.liduoo.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 客服帐号对象
 *
 * @author FrankGui
 */
public class CustomOnlineAccount extends BaseModel {
//	 "kf_account": "test1@test", 
//     "status": 1, 
//     "kf_id": "1001", 
//     "auto_accept": 0, 
//     "accepted_case": 1
    @JSONField(name = "kf_account")
    private String accountName;

    @JSONField(name = "status")
    private String status;

    public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getKfid() {
		return kfid;
	}
	public void setKfid(String kfid) {
		this.kfid = kfid;
	}
	public String getAutoAccept() {
		return autoAccept;
	}
	public void setAutoAccept(String autoAccept) {
		this.autoAccept = autoAccept;
	}
	public String getAcceptedCase() {
		return acceptedCase;
	}
	public void setAcceptedCase(String acceptedCase) {
		this.acceptedCase = acceptedCase;
	}
	@JSONField(name = "kf_id")
    private String kfid;

    @JSONField(name = "auto_accept")
    private String autoAccept;
    @JSONField(name = "accepted_case")
    private String acceptedCase;
    
}
