	package com.heb.liquidsky.oauth.service;

import com.heb.liquidsky.endpoints.auth.User;
import com.heb.liquidsky.endpoints.response.UnauthorizedException;
import com.heb.liquidsky.oauth.model.OAuthRequest;
import com.heb.liquidsky.oauth.model.OAuthResponse;

public interface OAuthService {
	/**
	 * Get an Oauth token from the underlying security service.
	 * @param oAuthRequest the authorization code needed to access the OAuth token 
	 * @return
	 * @throws UnauthorizedException
	 */
	public OAuthResponse getOAuthToken (OAuthRequest oAuthRequest) throws UnauthorizedException;
	
	public OAuthResponse getAuthCode () throws UnauthorizedException;
	
	public User getUserProfile (String token) throws UnauthorizedException;
}
