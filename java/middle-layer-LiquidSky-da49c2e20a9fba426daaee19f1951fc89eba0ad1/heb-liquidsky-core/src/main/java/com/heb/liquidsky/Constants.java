package com.heb.liquidsky;

import com.heb.liquidsky.common.CloudUtil;

/**
 * Contains the client IDs and scopes for allowed clients consuming the API.
 */
public class Constants {
	public static final String OAUTH_PROVIDER = "gigya";
	public static final String WEB_CLIENT_ID = CloudUtil.getProperty("OAUTH_WEB_CLIENT_ID");
	public static final String CLIENT_SECRET = CloudUtil.getProperty("OAUTH_CLIENT_SECRET");
	public static final String OAUTH_EXPIRATION_SECS = CloudUtil.getProperty("OAUTH_EXPIRATION", "0");
	
	//  These define expected keys in some legacy response messages
	public static final String RESPONSE_ERROR_KEY = "ERROR";
	public static final String RESPONSE_SUCCESS_KEY = "SUCCESS";
	public static final String RESPONSE_EXCEPTION_KEY = "EXCEPTION";
	public static final String RESPONSE_MESSAGE_KEY = "MESSAGE";
}
