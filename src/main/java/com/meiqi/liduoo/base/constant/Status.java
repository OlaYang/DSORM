package com.meiqi.liduoo.base.constant;

public enum Status {
		// 目前针对刊物、商户、应用、开发者都使用下列的状态编码
		COMMON_STATUS_NEW(0, "新建"),
		COMMON_STATUS_TO_VERIFY(1, "待审核"),
		COMMON_STATUS_VERIFIED(2, "已审核"),
		COMMON_STATUS_VERIFY_FAILED(3, "审核未通过"),
		COMMON_STATUS_NOTICE(4, "公示期"),
		COMMON_STATUS_INVALID(5, "已失效"),
		COMMON_STATUS_UNPUBLISHED(6, "已下架"),
		COMMON_STATUS_WAIT_VALID(7, "待生效"),
		COMMON_STATUS_RUNNING(8, "正在运行"),
		COMMON_STATUS_PUBLISHED(9, "已发布"),
		COMMON_STATUS_ENDED(16, "已结束"),
		COMMON_STATUS_TO_CANCEL(20, "待取消"),
		COMMON_STATUS_CANCELED(21, "已取消"),
		COMMON_STATUS_CANCEL_FAILED(22, "取消失败");

	private int code = 0;
	private String label = "";

	Status(int code, String label) {
		this.setCode(code);
		this.setLabel(label);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
