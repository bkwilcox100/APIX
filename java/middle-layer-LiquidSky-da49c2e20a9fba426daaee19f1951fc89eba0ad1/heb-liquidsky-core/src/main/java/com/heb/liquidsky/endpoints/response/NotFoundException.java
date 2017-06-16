package com.heb.liquidsky.endpoints.response;

import javax.servlet.http.HttpServletResponse;

public class NotFoundException extends ServiceException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = HttpServletResponse.SC_NOT_FOUND;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, String messageCode) {
		super(message, messageCode);
	}

	public int getStatusCode() {
		return CODE;
	}
}
