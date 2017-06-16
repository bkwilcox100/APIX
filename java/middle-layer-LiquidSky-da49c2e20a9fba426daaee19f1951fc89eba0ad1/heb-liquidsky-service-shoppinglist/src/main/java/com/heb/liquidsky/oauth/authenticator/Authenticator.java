package com.heb.liquidsky.oauth.authenticator;

import javax.servlet.http.HttpServletRequest;

import com.heb.liquidsky.endpoints.auth.User;

public abstract interface Authenticator {
	public abstract User authenticate(HttpServletRequest paramHttpServletRequest);
}
