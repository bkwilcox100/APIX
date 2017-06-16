package com.heb.liquidsky.spring.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.heb.liquidsky.endpoints.OAuthEndpoint;
import com.heb.liquidsky.endpoints.response.ServiceException;
import com.heb.liquidsky.oauth.model.OAuthRequest;
import com.heb.liquidsky.oauth.model.OAuthResponse;

@RestController
@RequestMapping(value="/_ah/api/oauth/v1")
public class OAuthServlet {

	private static final String PARAMETER_AUTH_CODE = "auth_code";
	private static final OAuthEndpoint OAUTH_ENDPOINT = new OAuthEndpoint();

	@RequestMapping(value="/token", method=RequestMethod.POST)
	protected Map<String, Object> getOAuthToken(HttpServletRequest request) throws IOException, ServiceException {
		String authCode = request.getParameter(PARAMETER_AUTH_CODE);
		JsonReader jsonReader = new JsonReader(request.getReader());
		OAuthRequest oauthRequest = new Gson().fromJson(jsonReader, OAuthRequest.class);
		OAuthResponse oauthResponse = OAUTH_ENDPOINT.getOAuthToken(oauthRequest, authCode);
		String json = new Gson().toJson(oauthResponse);
		Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
		return new Gson().fromJson(json, mapType);
	}
}
