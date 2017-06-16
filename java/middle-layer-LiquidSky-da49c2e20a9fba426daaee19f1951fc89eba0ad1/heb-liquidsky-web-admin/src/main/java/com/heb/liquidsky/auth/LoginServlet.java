package com.heb.liquidsky.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow.Builder;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static final Collection<String> SCOPES = Arrays.asList("email", "profile");
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private GoogleAuthorizationCodeFlow flow;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		String state = new BigInteger(130, new SecureRandom()).toString(32);	// prevent request forgery
		req.getSession().setAttribute("state", state);

		// Check for a loginDestination in the request so we can send the user there if it exists.
		if (req.getAttribute("loginDestination") != null) {
			req.getSession().setAttribute("loginDestination", (String) req.getAttribute("loginDestination"));
			logger.log(Level.FINE, "Login destination " + (String) req.getAttribute("loginDestination"));
		} else {
			req.getSession().setAttribute("loginDestination", "/admin/index.jsp");
		}

		Builder authBuilder = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT,
				JSON_FACTORY,
				getServletContext().getInitParameter("adminweb.clientID"),
				getServletContext().getInitParameter("adminweb.clientSecret"),
				SCOPES);

		//force password re-entry.
		authBuilder.setApprovalPrompt("force");

		flow = authBuilder.build();
		String url = flow.newAuthorizationUrl()
						.setRedirectUri(getServletContext().getInitParameter("adminweb.callback"))
						.setState(state) // Prevent request forgery
						.build();
		
		resp.sendRedirect(url);
	}
}