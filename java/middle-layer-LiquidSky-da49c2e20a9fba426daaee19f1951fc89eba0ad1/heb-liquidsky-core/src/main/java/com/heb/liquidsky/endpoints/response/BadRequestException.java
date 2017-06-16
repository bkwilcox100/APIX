package com.heb.liquidsky.endpoints.response;

import javax.servlet.http.HttpServletResponse;

public class BadRequestException extends ServiceException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = HttpServletResponse.SC_BAD_REQUEST;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable t) {
		super(message, t);
	}

	public BadRequestException(String message, String messageCode) {
		super(message, messageCode);
	}

	public BadRequestException(String message, String messageCode, Throwable t) {
		super(message, messageCode, t);
	}

	public int getStatusCode() {
		return CODE;
	}
}
