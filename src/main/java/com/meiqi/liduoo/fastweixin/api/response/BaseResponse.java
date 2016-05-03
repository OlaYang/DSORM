package com.meiqi.liduoo.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.meiqi.data.util.LogUtil;
import com.meiqi.liduoo.base.exception.IllegalWechatResponseException;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.entity.BaseModel;
import com.meiqi.liduoo.fastweixin.api.enums.ResultType;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 微信API响应报文对象基类
 *
 * @author peiyu
 */
public class BaseResponse extends BaseModel {
	private static final long serialVersionUID = -7975779631797358755L;
	@JSONField(name = "errcode")
	private String errcode = "0";
	@JSONField(name = "errmsg")
	private String errmsg = "";

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		String result = this.errmsg;
		// 将接口返回的错误信息转换成中文，方便提示用户出错原因
		if (StrUtil.isNotBlank(this.errcode) && !ResultType.SUCCESS.getCode().toString().equals(this.errcode)) {
			ResultType resultType = ResultType.get(this.errcode);
			if (BeanUtil.nonNull(resultType)) {
				result = resultType.getDescription();
			}
		}
		return result;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	/**
	 * 简单验证微信返回消息是否正确
	 * 
	 * @param result
	 * @param throwExceptionOnError
	 * @return
	 */
	public boolean verifyWechatResponse(boolean throwExceptionOnError, ApiConfig apiConfig) {
		String code = this.getErrcode();
		String description = this.getErrmsg();
		if (code == null) {
			return true;
		}
		if (!"0".equals(code)) {
			if ("40001".equals(code) && apiConfig != null) {
				ApiConfig.removeInstance(apiConfig.getAppid(), apiConfig.getSecret());
			}
			if (throwExceptionOnError) {
				throw new IllegalWechatResponseException(code, (String) description);
			} else {
				LogUtil.warn("WeChat返回错误【已忽略】：" + this.toJsonString());
				return false;
			}
		} else {
			return true;
		}
	}
}
