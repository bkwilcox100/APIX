package com.heb.liquidsky.oauth.authenticator;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.heb.liquidsky.Constants;
import com.heb.liquidsky.endpoints.auth.User;
import com.heb.liquidsky.endpoints.response.UnauthorizedException;
import com.heb.liquidsky.oauth.service.OAuthService;
import com.heb.liquidsky.oauth.service.OAuthServiceFactory;

/**
 * This class is used by the Google Cloud endpoints to authenticate the OAuth token via the Gigya SSO solution
 * @author bhewett
 *
 */
public class GigyaAuthenticator implements Authenticator {
	public final static OAuthService gigyaService = OAuthServiceFactory.getOAuthService(Constants.OAUTH_PROVIDER);
	private static final Logger logger = Logger.getLogger(GigyaAuthenticator.class.getName());

	@Override
	/**
	 * Validate the user via the Gigya SSO service.
	 */
	public User authenticate(HttpServletRequest request) {
		User user = null;
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			String[] authHeaderAry = authHeader.split(" ");
			if (authHeaderAry.length >= 2) {
				String token = authHeaderAry[1];
				try {
				// apply your Facebook/Twitter/OAuth2 authentication
					user = gigyaService.getUserProfile(token);
				} catch (UnauthorizedException ue) {
					logger.warning("User is unauthorized: " + ue.getMessage());
				}
			} else {
				logger.warning("Invalid Auth token!");
			}
		}
		return user;
	}
}
