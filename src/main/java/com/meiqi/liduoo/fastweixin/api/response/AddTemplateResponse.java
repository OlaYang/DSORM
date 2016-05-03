package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 添加模版响应
 */
public class AddTemplateResponse extends BaseResponse {
	private static final long serialVersionUID = 7189991847209969524L;
	/**
	 * 模版id
	 */
	@JSONField(name = "template_id")
	private String templateId;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
}
