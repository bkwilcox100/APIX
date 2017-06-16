package com.heb.liquidsky.oauth.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSRequest;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.SigUtils;
import com.heb.liquidsky.Constants;
import com.heb.liquidsky.endpoints.auth.User;
import com.heb.liquidsky.endpoints.response.UnauthorizedException;
import com.heb.liquidsky.oauth.model.OAuthRequest;
import com.heb.liquidsky.oauth.model.OAuthResponse;

public class GigyaOAuthService implements OAuthService {
	private static final Logger logger = Logger.getLogger(GigyaOAuthService.class.getName());
	
	@Override
	public OAuthResponse getOAuthToken(OAuthRequest oAuthRequest) throws UnauthorizedException {
		try {
			if (!SigUtils.validateUserSignature(oAuthRequest.getUid(),
					oAuthRequest.getTimestamp(),  
					Constants.CLIENT_SECRET, 
					oAuthRequest.getAuthCode())) {
				throw new UnauthorizedException("Invalid OAuth request.");
			}
		} catch (UnsupportedEncodingException | InvalidKeyException e) {
			throw new UnauthorizedException("Invalid key error: " + e.getMessage());
		}
		OAuthResponse oauthResponse = new OAuthResponse();
		String method = "socialize.getToken";
		GSRequest request = new GSRequest(Constants.WEB_CLIENT_ID, Constants.CLIENT_SECRET, method, true);
		 
		// Step 2 - Adding parameters
		request.setParam("x_siteUID", oAuthRequest.getUid());  // set the "uid" parameter to user's ID
		request.setParam("grant_type", "none");  // set the "status" parameter to "I feel great"
		int sessionExpiration = !StringUtils.isBlank(Constants.OAUTH_EXPIRATION_SECS) ?
				Integer.parseInt(Constants.OAUTH_EXPIRATION_SECS) : 0;
		if (sessionExpiration > 0) {
			request.setParam("x_sessionExpiration", String.valueOf(sessionExpiration));
		}
		// Step 3 - Sending the request
 		GSResponse response = request.send();
		 
		// Step 4 - handling the request's response.
		if(response.getErrorCode() == 0) {   // SUCCESS! response status = OK  
		    logger.info("Success in getting OAuth token. ");
		    oauthResponse.setAccess_token(response.getString("access_token", null));
		    oauthResponse.setExpires_in(response.getString("expires_in", null));
		    
		} else {  // Error
		    throw new UnauthorizedException("Error on getToken: " + response.getLog());
		}		

		return oauthResponse;
	}

	@Override
	public OAuthResponse getAuthCode() throws UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserProfile(String token) throws UnauthorizedException {
		User user = null;
		if (token != null) {
			String method = "accounts.getAccountInfo";
			GSRequest request = new GSRequest(Constants.WEB_CLIENT_ID, Constants.CLIENT_SECRET, method, true);
			 
			// Step 2 - Adding parameters
			request.setParam("oauth_token", token);  // set the oauth token
			 
			// Step 3 - Sending the request
			GSResponse response = request.send();
			 
			// Step 4 - handling the request's response.
			if(response.getErrorCode() == 0) {   // SUCCESS! response status = OK  
			    logger.info("Success in getting user profile. ");
			    String id = response.getString("UID", null);
			    String email = null;
			    GSObject profile = response.getObject("profile", null);
			    if (profile != null) {
			    	email = profile.getString("email",null);
			    }
			    user = new User(id, email);
			    
			} else {  // Error
			    throw new UnauthorizedException("Error on getToken: " + response.getLog());
			}
		}
		return user;
	}

}
