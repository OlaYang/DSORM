/**
 * 
 */
package com.meiqi.liduoo.fastweixin.api.config;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class WxProperty implements Serializable {
	private static final long serialVersionUID = 2972968157007738707L;
	@JSONField(name = "fproperty")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JSONField(name = "fvalue")
	private String value;
}