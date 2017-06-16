package com.heb.liquidsky.endpoints;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.heb.liquidsky.Constants;
import com.heb.liquidsky.endpoints.response.BadRequestException;
import com.heb.liquidsky.endpoints.response.UnauthorizedException;
import com.heb.liquidsky.oauth.model.OAuthRequest;
import com.heb.liquidsky.oauth.model.OAuthResponse;
import com.heb.liquidsky.oauth.service.OAuthService;
import com.heb.liquidsky.oauth.service.OAuthServiceFactory;

/**
 * OAuth Endpoints are used to authorize the current user via the SSO provider using the OAuth 2.0 protocol.
 * @author bhewett
 *
 */
public class OAuthEndpoint {
	private static final Logger logger = Logger.getLogger(ShoppingListInterface.class.getName());
	private static final OAuthService oauthService = OAuthServiceFactory.getOAuthService(Constants.OAUTH_PROVIDER);
	

	/**
	 * The auth code returned by the 
	 * EXAMPLE CALL:  GET http://localhost:8080/_ah/api/oauth/v1/token?auth_token=
	 * @param authCode
	 * @return
	 * @throws UnauthorizedException
	 */
	public OAuthResponse getOAuthToken (OAuthRequest oauthRequest, String auth_code) throws UnauthorizedException, BadRequestException {
		OAuthResponse oauthRepsonse = null;
		
		if (StringUtils.isBlank(auth_code) && StringUtils.isBlank(oauthRequest.getAuthCode())) {
			logger.warning("Missing required OAuth Code");
			throw new BadRequestException("authCode is a required parameter.");
		} else {
			oauthRepsonse = oauthService.getOAuthToken(oauthRequest);
			if (oauthRepsonse == null) {
				throw new UnauthorizedException("Unable to get OAuth token from the OAuth service.");
			}
		}
		
		return oauthRepsonse;
	}
	
}
