package com.heb.liquidsky.oauth.service;

import java.util.logging.Logger;

public class OAuthServiceFactory {
	private static final Logger logger = Logger.getLogger(OAuthServiceFactory.class.getName());
	
	/**
	 * Return the OAuth service provider class by service name 
	 * @param serviceName the name of the OAuth service provider.
	 * @return OAuth service provider class
	 */
	static public OAuthService getOAuthService(String serviceName) {
		OAuthService oauthService = null;
		switch (serviceName) {
		case "gigya":
			oauthService = new GigyaOAuthService();
			break;
		default:
			logger.severe("No OAuth service provider was specified.   Authentication services will not work!");
		}
		return oauthService;
	}
}
