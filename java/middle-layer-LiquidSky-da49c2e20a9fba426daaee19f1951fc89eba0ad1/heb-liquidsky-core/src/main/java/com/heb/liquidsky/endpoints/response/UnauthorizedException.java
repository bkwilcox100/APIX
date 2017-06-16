package com.heb.liquidsky.endpoints.response;

import javax.servlet.http.HttpServletResponse;

public class UnauthorizedException extends ServiceException {

	private static final long serialVersionUID = -2331135734482607030L;
	private static final int CODE = HttpServletResponse.SC_UNAUTHORIZED;

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, String messageCode) {
		super(message, messageCode);
	}

	public int getStatusCode() {
		return CODE;
	}
}
