package com.meiqi.dsmanager.po.rule.smsSend;

import java.util.Map;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年6月12日 上午9:15:36 
 * 类说明  发送短信参数封装实体
 */

public class SMSSend {
	
	private String phoneNumber; //发送的电话号码
	private String auth_key;  //请求中带的识别码equal SysUil.auth_key才能发送。
	private Long templateId;  //需要发送的模板id
	private Long channelId;  //需要发送的短信通道id
	private Map<String,String> param;  //模板中动态参数
	private String sendMsg; //发送到用户的信息
	private String site_id;
	private String operation; //发送短信的业务类型
    public String getSite_id() {
        return site_id;
    }
    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }
    private Integer webSite;//站点
    
	public Integer getWebSite() {
        return webSite;
    }
    public void setWebSite(Integer webSite) {
        this.webSite = webSite;
    }
    private Integer smsType;//短信业务类型
	public Integer getSmsType() {
        return smsType;
    }
    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }
    public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAuth_key() {
		return auth_key;
	}
	public void setAuth_key(String auth_key) {
		this.auth_key = auth_key;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public Map<String, String> getParam() {
		return param;
	}
	public void setParam(Map<String, String> param) {
		this.param = param;
	}
	public String getSendMsg() {
		return sendMsg;
	}
	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}
	public Long getChannelId() {
		return channelId;
	}
	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
}
