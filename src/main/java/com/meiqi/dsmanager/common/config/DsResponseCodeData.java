package com.meiqi.dsmanager.common.config;

public enum DsResponseCodeData {
//
//	 NOT_SET_DATASOURCE("1","数据源未配置"),
//	 NOT_SET_STYLE("2","该数据源未设置样式"),
//	 NO_DATA("3","无数据"),
//	 STYLE_CONVERT_ERROR("4","样式转换错误！"),
//	 SUCCESS("5","成功"),
//	 ERROR("6","失败");
	 
	 LS_DATASOURCE("1","该数据源已经失效"),
	 NOT_SET_DATASOURCE("1","数据源未配置"),
	 NOT_SET_STYLE("1","该数据源未设置样式"),
	 NO_DATA("0","无数据"),
	 STYLE_CONVERT_ERROR("1","样式转换错误！"),
	 SUCCESS("0","成功"),
	 ERROR("1","失败"),
	 MISSING_PARAM("1", "缺少必要参数"),
	 FUNCTION_PARAM("1","参数个数不匹配"),
	 PWD_NOT_RIGHT("1","密码不正确"),
	 SMS_CODE_NOT_RIGHT("1","短信验证码不正确"),
	 REGISTER_CODE_NOT_RIGHT("1","注册码不正确"),
	 PHONE_IS_EMPTY("1","手机号不能为空"),
	 USER_IS_EXIST("1","用户已经存在"),
	 USER_IS_NOT_EXIST("1","用户不存在"),
	 USER_TYPE_NOT_RIGHT("1","用户类型不正确"),
	 EMAIL_IS_EMPTY("1","邮箱不能为空"),
	 CODE_IS_EMPTY("1","验证码不能为空"),
	 CODE_TYPE_NOT_RIGHT("1","验证码类型不正确"),
	 EMAIL_IS_EXIST("1","邮箱已经存在"),
	 CODE_NOT_RIGHT("1","验证码不正确"),
	 USER_IDENTIFER_IS_EMPTY("1","用户名、邮箱或电话号码不能为空"),
	 NOT_SET_DATASOURCE_TYPE("1","数据源类型未配置"),
	 REQINFO_NOT_RIGHT("1","请求报文不正确，包含特殊字符,get请求请做URL编码"),
	 SMS_TEMPLATE_NOT_EXIST("1","短信模板不存在"),
	 ILLEGAL_OPERATION("1","非法操作"),
	 OLD_PWD_NOT_RIGHT("1","旧密码不正确"),
	 SMS_SEND_PHONE_LIMIT("2","当前手机号操作太频繁，请休息一会再试！"),
	 RULE_SIZE_GT_TEN("1","错误，规则个数大于10"),
	 USERNAME_OR_PWD_NOT_RIGHT("1","用户名或密码不正确");
	
	private DsResponseCodeData(String code,String description){
		this.code=code;
		this.description=description;
	}
	public String code;
	public String description;
}
