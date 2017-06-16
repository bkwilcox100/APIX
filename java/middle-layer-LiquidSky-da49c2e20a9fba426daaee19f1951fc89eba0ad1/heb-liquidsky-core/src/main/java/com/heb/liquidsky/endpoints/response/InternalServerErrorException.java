package com.heb.liquidsky.endpoints.response;

import javax.servlet.http.HttpServletResponse;

public class InternalServerErrorException extends ServiceException {

	private static final long serialVersionUID = 8775114038918389511L;
	private static final int CODE = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	public InternalServerErrorException(String message) {
		super(message);
	}

	public InternalServerErrorException(String message, Throwable t) {
		super(message, t);
	}

	public InternalServerErrorException(String message, String messageCode) {
		super(message, messageCode);
	}

	public InternalServerErrorException(String message, String messageCode, Throwable t) {
		super(message, messageCode, t);
	}

	public int getStatusCode() {
		return CODE;
	}
}
