package com.meiqi.liduoo.base.exception;

/**
 * 
 * @author FrankGui
 *
 */
public class IllegalJsonResultException extends RuntimeException {

	private static final long serialVersionUID = -7659045015641423257L;

	public IllegalJsonResultException() {
		super();
	}

	public IllegalJsonResultException(String s) {
		super(s);
	}

	public IllegalJsonResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalJsonResultException(Throwable cause) {
		super(cause);
	}

}
