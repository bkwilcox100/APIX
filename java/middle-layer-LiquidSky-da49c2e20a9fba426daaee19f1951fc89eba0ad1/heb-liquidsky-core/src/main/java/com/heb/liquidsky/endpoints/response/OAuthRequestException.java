package com.heb.liquidsky.endpoints.response;

import javax.servlet.http.HttpServletResponse;

/**
 * Based on com.google.appengine.api.oauth.OAuthRequestException
 */
public class OAuthRequestException extends ServiceException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = HttpServletResponse.SC_UNAUTHORIZED;

	public OAuthRequestException(String message) {
		super(message);
	}

	public OAuthRequestException(String message, String messageCode) {
		super(message, messageCode);
	}

	public int getStatusCode() {
		return CODE;
	}
}