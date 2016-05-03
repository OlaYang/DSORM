package com.meiqi.liduoo.fastweixin.api.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.meiqi.liduoo.fastweixin.util.JSONUtil;

/**
 * 抽象实体类
 *
 * @author peiyu
 */
public abstract class BaseModel implements Model {
	@Override
	public String toJsonString() {
		return JSONUtil.toJson(this);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
