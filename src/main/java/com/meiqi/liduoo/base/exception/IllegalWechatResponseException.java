package com.meiqi.liduoo.base.exception;

/**
 * 
 * @author FrankGui
 *
 */
public class IllegalWechatResponseException extends RuntimeException {
	private static final long serialVersionUID = -4088479281790712566L;
	private String code = "-1";

	public IllegalWechatResponseException() {
		super();
	}

	public IllegalWechatResponseException(String s) {
		super(s);
	}

	public IllegalWechatResponseException(String code, String s) {
		this(s);
		this.code = code;
	}

	public IllegalWechatResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalWechatResponseException(String code, String message, Throwable cause) {
		this(message, cause);
		this.code = code;
	}

	public IllegalWechatResponseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}
