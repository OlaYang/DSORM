package com.meiqi.openservice.commons.exception;

public class OpenServiceException extends Exception {

    private static final long serialVersionUID = -4121745796541399704L;

    public OpenServiceException() {
		super();
	}

	public OpenServiceException(String message) {
		super(message);
	}

	public OpenServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpenServiceException(Throwable cause) {
		super(cause);
	}
}
